package se.kth.ict.hotspots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import se.kth.ict.hotspots.db.DatabaseHelper;
import se.kth.ict.hotspots.db.FavoriteAdapter;
import se.kth.ict.hotspots.db.LocationAdapter;

import java.io.IOException;

/**
 * Triggered by the user interface.
 * Resets some of the database contents.
 */
public class DatabaseResetter extends BroadcastReceiver {

    /**
     * A boolean extra with this name indicates whether all location data
     * should be cleared in addition to favorites.
     */
    public static final String CLEAR_LOCATIONS = "clearLocations";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            DatabaseHelper helper = DatabaseHelper.getInstance(context);

            new FavoriteAdapter(helper).clearFavorites();
            if (intent.getBooleanExtra(CLEAR_LOCATIONS, false)) {
                new LocationAdapter(helper).clearLocations();
            }
            context.startService(new Intent(context, FavoriteUpdaterService.class));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }
    }
}
