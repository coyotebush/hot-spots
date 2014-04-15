package se.kth.ict.hotspots.db;

import android.content.Context;
import android.content.res.AssetManager;
import jsqlite.Constants;
import jsqlite.Database;

import java.io.*;

/**
 * Holds a connection to the database, and handles initializing and opening it.
 * Since SQLite connections are threadsafe, this class is implemented as a singleton.
 */
public class DatabaseHelper {
    private static final String DATABASE_FILENAME = "hotspots.sqlite";
    private static final String SEED_FILENAME = "seed.sqlite";

    private static DatabaseHelper instance = null;

    private Database database;

    /**
     * Instantiate a DatabaseHelper by opening a connection to the database, creating it from the
     * "seed" first if necessary.
     *
     * @param context A Context, to obtain file paths and assets
     * @throws IOException       if creating the database fails
     * @throws jsqlite.Exception if opening the database fails
     */
    private DatabaseHelper(Context context) throws IOException, jsqlite.Exception {
        File databaseFile = new File(context.getExternalFilesDir(null), DATABASE_FILENAME);
        if (!databaseFile.exists()) {
            installSeedDatabase(databaseFile, context.getAssets());
        }

        database = new Database();
        database.open(databaseFile.getAbsolutePath(), Constants.SQLITE_OPEN_READWRITE);
    }

    /**
     * Gets the singleton instance of DatabaseHelper, instantiating one first if necessary.
     *
     * @param context A Context for use in initializing the database connection
     * @return the DatabaseHelper singleton instance
     * @throws IOException       if creating the database fails
     * @throws jsqlite.Exception if opening the database fails
     */
    public static DatabaseHelper getInstance(Context context) throws IOException, jsqlite.Exception {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    /**
     * @return the jsqlite.Database connection managed by this DatabaseHelper
     */
    public Database getDatabase() {
        return database;
    }

    /**
     * Copy the "seed" database from the application's assets into the filesystem.
     *
     * @param dbFile       Path where the database should be placed.
     * @param assetManager The AssetManager for the application's assets
     * @throws IOException
     */
    private void installSeedDatabase(File dbFile, AssetManager assetManager) throws IOException {
        InputStream in = assetManager.open(SEED_FILENAME);
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

}
