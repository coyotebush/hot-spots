package se.kth.ict.hotspots.db;

import jsqlite.Database;
import jsqlite.Stmt;

/**
 * Methods to interact with the "location" table.
 */
public class LocationAdapter {
    private final Database db;

    public LocationAdapter(DatabaseHelper helper) {
        db = helper.getDatabase();
    }

    public long insertLocation(String provider, double accuracy, long time, double latitude, double longitude)
            throws jsqlite.Exception {
        Stmt stmt = db.prepare("INSERT INTO location (provider, accuracy, time, geom) " +
                "VALUES (?, ?, ?, MakePoint(?, ?, 4326))");
        stmt.bind(1, provider);
        stmt.bind(2, accuracy);
        stmt.bind(3, time);
        stmt.bind(4, longitude);
        stmt.bind(5, latitude);
        stmt.step();
        return db.last_insert_rowid();
    }
}
