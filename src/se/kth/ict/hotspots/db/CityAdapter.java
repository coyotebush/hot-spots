package se.kth.ict.hotspots.db;

import jsqlite.Database;
import jsqlite.Stmt;

/**
 * Methods to interact with the "city" table.
 */
public class CityAdapter {
    private final Database db;

    public CityAdapter(DatabaseHelper helper) {
        db = helper.getDatabase();
    }

    /**
     * Find the nearest city to a given set of coordinates.
     *
     * @param latitude    WGS84 latitude of the location of interest
     * @param longitude   WGS84 longitude of the location of interest
     * @param maxDistance Maximum radius, in meters, within which to search for cities.
     *                    Smaller values give faster queries but are more likely to return no
     *                    results at all. A value on the order of 10000 is probably suitable.
     * @return Name of the nearest city, or null if no cities found within maxDistance.
     * @throws jsqlite.Exception
     */
    public String getNearestCity(double latitude, double longitude, double maxDistance)
            throws jsqlite.Exception {
        Stmt stmt = db.prepare(
                "SELECT name FROM city WHERE PtDistWithin(geom, MakePoint(?1, ?2, 4326), ?3) "
                        + "ORDER BY Distance(geom, MakePoint(?1, ?2, 4326)) LIMIT 1"
        );
        stmt.bind(1, longitude);
        stmt.bind(2, latitude);
        stmt.bind(3, maxDistance);
        if (stmt.step()) {
            return stmt.column_string(0);
        }
        return null;
    }
}
