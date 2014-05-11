package se.kth.ict.hotspots.db;

import android.location.Location;
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

    /**
     * Insert a location record into the database.
     *
     * @param location Location object whose data will be inserted
     * @return ID of inserted row
     * @throws jsqlite.Exception
     */
    public long insertLocation(Location location)
            throws jsqlite.Exception {
        Stmt stmt = db.prepare("INSERT INTO location (provider, time, accuracy, geom) " +
                "VALUES (?, ?, ?, MakePoint(?, ?, 4326))");
        stmt.bind(1, location.getProvider());
        stmt.bind(2, location.getTime() / 1000);
        stmt.bind(3, location.getAccuracy());
        stmt.bind(4, location.getLongitude());
        stmt.bind(5, location.getLatitude());
        stmt.step();
        return db.last_insert_rowid();
    }
}
