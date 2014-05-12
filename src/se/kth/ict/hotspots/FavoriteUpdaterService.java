package se.kth.ict.hotspots;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import se.kth.ict.hotspots.db.DatabaseHelper;
import se.kth.ict.hotspots.db.FavoriteAdapter;

import java.io.IOException;

/**
 * Runs {@link FavoriteAdapter#updateFavorites()} (or {@link FavoriteAdapter#updateFavorites(long)})
 * in the background.
 */
public class FavoriteUpdaterService extends IntentService {

    /**
     * A long extra with this name in a triggering Intent indicates
     * the ID of a specific location to incorporate into the favorites model.
     * If not set, all previously unprocessed locations are processed.
     */
    public static final String LOCATION_ID = "locationId";

    public FavoriteUpdaterService() {
        super("FavoriteUpdaterService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            DatabaseHelper helper = DatabaseHelper.getInstance(this);
            FavoriteAdapter favoriteAdapter = new FavoriteAdapter(helper);
            long locationId = intent.getLongExtra(LOCATION_ID, -1);
            Log.i(getClass().getSimpleName(), "updating for " + locationId);
            if (locationId > 0) {
                favoriteAdapter.updateFavorites(locationId);
            } else {
                favoriteAdapter.updateFavorites();
            }
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
