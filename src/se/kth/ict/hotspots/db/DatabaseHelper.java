package se.kth.ict.hotspots.db;

import android.content.Context;
import android.content.res.AssetManager;
import jsqlite.Constants;
import jsqlite.Database;

import java.io.*;

/**
 * Handles initializing and opening the database.
 */
public class DatabaseHelper {
    private Database database;

    public DatabaseHelper(Context context) throws IOException, jsqlite.Exception {
        File databaseFile = new File(context.getExternalFilesDir(null), "hotspots.sqlite");
        if (!databaseFile.exists()) {
            installSeedDatabase(databaseFile, context.getAssets());
        }

        database = new Database();
        database.open(databaseFile.getAbsolutePath(), Constants.SQLITE_OPEN_READWRITE);
    }

    public Database getDatabase() {
        return database;
    }

    private void installSeedDatabase(File dbFile, AssetManager assetManager) throws IOException {
        InputStream in = assetManager.open("seed.sqlite");
        OutputStream out = new FileOutputStream(dbFile);

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.flush();
        out.close();
    }

    protected void close() throws jsqlite.Exception {
        database.close();
    }
}
