package se.kth.ict.hotspots.db;

import jsqlite.Database;
import jsqlite.Stmt;

/**
 * Methods to interact with the "city" table.
 */
public class CityAdapter {
    /**
     * A suitable value for the maxDistance argument to getNearestCity.
     */
    public static final double TEN_KILOMETERS = 10000;

    private final Database db;

    public CityAdapter(DatabaseHelper helper) {
        db = helper.getDatabase();
    }

    public CityAdapter(Database db) {
        this.db = db;
    }

    /**
     * Find the nearest city to a given location record.
     *
     * @param locationId  row ID of a record in the "location" table.
     * @param maxDistance Maximum radius, in meters, within which to search for cities.
     *                    Smaller values give faster queries but are more likely to return no
     *                    results at all. A value on the order of 10000 is probably suitable.
     * @return Name of the nearest city, or null if no cities found within maxDistance.
     * @throws jsqlite.Exception
     */
    public String getNearestCity(long locationId, double maxDistance)
            throws jsqlite.Exception {
        Stmt stmt = db.prepare(
                "SELECT name FROM city JOIN location ON PtDistWithin(city.geom, location.geom, ?2) "
                        + "WHERE location.id = ?1 ORDER BY Distance(city.geom, location.geom) LIMIT 1"
        );
        stmt.bind(1, locationId);
        stmt.bind(2, maxDistance);
        if (stmt.step()) {
            return stmt.column_string(0);
        }
        return null;
    }
}
