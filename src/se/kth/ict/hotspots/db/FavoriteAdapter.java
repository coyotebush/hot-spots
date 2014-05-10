package se.kth.ict.hotspots.db;

import android.util.Log;
import jsqlite.Database;
import jsqlite.Stmt;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods to interact with the "favorite" table.
 */
public class FavoriteAdapter {
    private static final int METERS_PER_DEGREE = 110000; // approximate, varies with latitude
    private static final int EPOCH = 1262304000;         // 2010-01-01, makes calculations easier
    private static final double BETA = Math.pow(2, 1.0/(86400*7)); // double in a week
    private static final String LOG_TAG = "FavoriteAdapter";

    private final Database db;

    public FavoriteAdapter(DatabaseHelper helper) {
        db = helper.getDatabase();
    }

    public List<Favorite> getTopFavorites() throws jsqlite.Exception {
        Stmt stmt = db.prepare("SELECT name, Y(Centroid(geom)), X(Centroid(geom)) "
                + "FROM favorite ORDER BY weight DESC");
        List<Favorite> result = new ArrayList<Favorite>();
        while (stmt.step()) {
            result.add(new Favorite(
                    stmt.column_string(0),
                    stmt.column_double(1),
                    stmt.column_double(2)
            ));
        }
        return result;
    }

    public void clearFavorites() throws jsqlite.Exception {
        Stmt stmt = db.prepare("DELETE FROM favorite");
        stmt.step();
    }

    /**
     * Update the favorites table to take into account all location entries
     * that have not yet been processed.
     */
    public void updateFavorites() throws jsqlite.Exception {
        synchronized (db) {
            Stmt stmt = db.prepare(
                    "SELECT id FROM location\n" +
                            "WHERE id > Coalesce((SELECT Max(last_location) FROM favorite), 0) ORDER BY time ASC"
            );
            while (stmt.step()) {
                updateFavorites(stmt.column_long(0));
            }
        }
    }

    /**
     * Update the favorites table to take into account a new location entry.
     * This involves creating a new favorite based on this location and the
     * one preceding it, and/or merging favorites.
     *
     * @param locationId row ID of the new location entry
     */
    public void updateFavorites(long locationId) throws jsqlite.Exception {
        synchronized (db) {
            Log.i(LOG_TAG, "Processing location id " + locationId);
            long favoriteId = createFavoriteFromLocation(locationId);
            mergeFavorites(locationId, favoriteId);
        }
    }

    private long createFavoriteFromLocation(long locationId) throws jsqlite.Exception {
        Stmt stmt = db.prepare(
                "INSERT INTO favorite (weight, geom, last_location) SELECT\n" +
                " (b.time - a.time) * Pow(?2, (a.time + b.time - 2 * ?3)/2) weight,\n" +
                " ConvexHull(GUnion(Buffer(a.geom, Coalesce(a.accuracy, 10.0) / ?4),\n" +
                "                   Buffer(b.geom, Coalesce(b.accuracy, 10.0) / ?4))) geom,\n" +
                " b.id last_location\n" +
                "FROM location a, location b\n" +
                "WHERE a.time < b.time AND b.id = ?1\n" +
                "ORDER BY a.time DESC LIMIT 1"
        );
        stmt.bind(1, locationId);
        stmt.bind(2, BETA); // TODO get this from preferences
        stmt.bind(3, EPOCH);
        stmt.bind(4, METERS_PER_DEGREE);
        stmt.step();
        return db.last_insert_rowid();
    }

    private void mergeFavorites(long locationId, long favoriteId) throws jsqlite.Exception {
        // If the new favorite overlaps with existing one(s),
        // and if the merge is better than each individually,
        // merge destructively into the current best one.
        Stmt stmt = db.prepare(
                "UPDATE favorite\n" +
                "SET weight =\n" +
                "  (SELECT Sum(weight) FROM favorite\n" +
                "   WHERE Intersects(geom, (SELECT geom FROM favorite WHERE id = ?1))),\n" +
                " geom =\n" +
                "  (SELECT ConvexHull(GUnion(geom)) FROM favorite\n" +
                "   WHERE Intersects(geom, (SELECT geom FROM favorite WHERE id = ?1))),\n" +
                " last_location = ?2\n" +
                "WHERE id <> ?1 AND Intersects(geom, (SELECT geom FROM favorite WHERE id = ?1))\n" +
                " AND (weight / GreatCircleLength(ExteriorRing(geom))) <\n" +
                "   (SELECT Sum(weight) / GreatCircleLength(ExteriorRing(ConvexHull(GUnion(geom)))) FROM favorite\n" +
                "    WHERE Intersects(geom, (SELECT geom FROM favorite WHERE id = ?1)))\n" +
                " AND (weight / GreatCircleLength(ExteriorRing(geom))) >=\n" +
                "   (SELECT Max(weight / GreatCircleLength(ExteriorRing(geom))) FROM favorite\n" +
                "WHERE id <> ?1 AND Intersects(geom, (SELECT geom FROM favorite WHERE id = ?1)))"
        );
        stmt.bind(1, favoriteId);
        stmt.bind(2, locationId);
        stmt.step();

        if (db.changes() > 0) {
            // If we merged, delete all but the one we updated
            stmt = db.prepare(
                    "DELETE FROM favorite\n" +
                    " WHERE Intersects(geom, (SELECT geom FROM favorite WHERE id = ?1))\n" +
                    "  AND last_location <> ?2 OR id = ?1"
            );
            stmt.bind(1, favoriteId);
            stmt.bind(2, locationId);
            stmt.step();
            Log.i(LOG_TAG, "Merged with " + db.changes() + " others");
        } else {
            // Otherwise, just add some weight to any that intersect
            stmt = db.prepare(
                    "UPDATE favorite\n" +
                    "SET weight = weight + (SELECT weight / Area(geom) FROM favorite WHERE id = ?1) *\n" +
                    "  Area(Intersection(geom, (SELECT geom FROM favorite WHERE id = ?1))),\n" +
                    " last_location = ?2\n" +
                    "WHERE id <> ?1 AND Intersects(geom, (SELECT geom FROM favorite WHERE id = ?1))"
            );
            stmt.bind(1, favoriteId);
            stmt.bind(2, locationId);
            stmt.step();

            if (db.changes() > 0) {
                // and if there were actually any intersecting favorites,
                // delete the new one again
                Log.i(LOG_TAG, "Added weight to " + db.changes() + " others, deleting");
                stmt = db.prepare("DELETE FROM favorite WHERE id = ?1");
                stmt.bind(1, favoriteId);
                stmt.step();
            } else {
                // A new favorite, give it a name
                String cityName = new CityAdapter(db).getNearestCity(locationId, CityAdapter.TEN_KILOMETERS);
                stmt = db.prepare("UPDATE favorite SET name = ?2 WHERE id = ?1");
                stmt.bind(1, favoriteId);
                stmt.bind(2, cityName);
                stmt.step();
            }
        }
    }
}
