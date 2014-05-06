package se.kth.ict.hotspots.db;

import jsqlite.Database;
import jsqlite.Stmt;

/**
 * Methods to interact with the "favorite" table.
 */
public class FavoriteAdapter {
    private static final int METERS_PER_DEGREE = 110000;
    private final Database db;

    public FavoriteAdapter(DatabaseHelper helper) {
        db = helper.getDatabase();
    }

    public void clearFavorites() throws jsqlite.Exception {
        Stmt stmt = db.prepare("DELETE FROM favorite");
        stmt.step();
    }

    /**
     * Update the favorites table to take into account a new location entry.
     * This involves creating a new favorite based on this location and the
     * one preceding it, and/or merging favorites.
     *
     * @param locationId row ID of the new location entry
     */
    public void updateFavorites(long locationId) throws jsqlite.Exception {
        long favoriteId = createFavoriteFromLocation(locationId);
        mergeFavorites(locationId, favoriteId);
    }

    private long createFavoriteFromLocation(long locationId) throws jsqlite.Exception {
        String cityName = new CityAdapter(db).getNearestCity(locationId, CityAdapter.TEN_KILOMETERS);

        Stmt stmt = db.prepare(
                "INSERT INTO favorite (name, weight, geom, last_location)\n" +
                "SELECT ?1 name,\n" +
                " (b.time - a.time) * Pow(?3, a.time + b.time) weight,\n" +
                " ConvexHull(GUnion(\n" +
                "   Buffer(a.geom, Coalesce(a.accuracy, 10.0) / ?4),\n" +
                "   Buffer(b.geom, Coalesce(b.accuracy, 10.0) / ?4))) geom,\n" +
                " b.id last_location\n" +
                "FROM location first, location second\n" +
                "WHERE b.id = ?2 AND a.time < b.time\n" +
                "ORDER BY a.time DESC LIMIT 1"
        );
        stmt.bind(1, cityName);
        stmt.bind(2, locationId);
        stmt.bind(3, 1.00001); // TODO get this from preferences?
        stmt.bind(4, METERS_PER_DEGREE);
        stmt.step();
        return db.last_insert_rowid();
    }

    private void mergeFavorites(long locationId, long favoriteId) throws jsqlite.Exception {
        // TODO
        Stmt stmt = db.prepare("SELECT ?, ?");
        stmt.bind(1, favoriteId);
        stmt.bind(2, locationId);
        stmt.step();
    }
}
