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

    public String getNearestCity(double latitude, double longitude) throws jsqlite.Exception {
        Stmt stmt = db.prepare("SELECT name FROM city WHERE PtDistWithin(geom, MakePoint(?1, ?2, 4326), 10000) "
                + "ORDER BY Distance(geom, MakePoint(?1, ?2, 4326)) LIMIT 1");
        stmt.bind(1, longitude);
        stmt.bind(2, latitude);
        if (stmt.step()) {
            return stmt.column_string(0);
        }
        return null;
    }
}
