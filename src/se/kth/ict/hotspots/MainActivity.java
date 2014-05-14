package se.kth.ict.hotspots;

import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import se.kth.ict.hotspots.db.DatabaseHelper;
import se.kth.ict.hotspots.db.Favorite;
import se.kth.ict.hotspots.db.FavoriteAdapter;
import se.kth.ict.hotspots.widget.FavoriteArrayAdapter;
import se.kth.ict.hotspots.widget.PromptDialogFragment;

import java.io.IOException;
import java.util.List;

public class MainActivity extends ListActivity implements AdapterView.OnItemLongClickListener {

    private FavoritesUpdatedReceiver favoritesUpdatedReceiver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getListView().setOnItemLongClickListener(this);
        sendBroadcast(new Intent(this, AlarmSetter.class));
        new LoadFavoritesTask().execute();
        favoritesUpdatedReceiver = new FavoritesUpdatedReceiver();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(favoritesUpdatedReceiver, new IntentFilter(FavoriteUpdaterService.FAVORITES_UPDATED));
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(favoritesUpdatedReceiver);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Favorite favorite = (Favorite) l.getItemAtPosition(position);
        if (favorite != null) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(favorite.getUri());
            startActivity(intent);
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> l, View view, int position, long id) {
        final Favorite favorite = (Favorite) l.getItemAtPosition(position);
        if (favorite != null) {
            PromptDialogFragment dialog = new PromptDialogFragment(R.string.title_rename,
                    R.string.button_rename, favorite.getName());
            dialog.setListener(new PromptDialogFragment.PromptDialogListener() {
                @Override
                public void onPromptDialogResult(PromptDialogFragment dialog, String value) {
                    favorite.setName(value);
                    new RenameFavoriteTask().execute(favorite);
                }
            });
            dialog.show(getFragmentManager(), "dialog");
            return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.action_settings:
    		Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
    		startActivity(intent);
    		return true;
    	default:
            return super.onOptionsItemSelected(item);	
    	}
    	
    }

    private class LoadFavoritesTask extends AsyncTask<Void, Void, List<Favorite>> {
        @Override
        protected List<Favorite> doInBackground(Void... params) {
            try {
                DatabaseHelper helper = DatabaseHelper.getInstance(MainActivity.this);
                return new FavoriteAdapter(helper).getTopFavorites();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (jsqlite.Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Favorite> favorites) {
            if (favorites != null) {
                setListAdapter(new FavoriteArrayAdapter(MainActivity.this, favorites));
            }
        }
    }

    private class RenameFavoriteTask extends AsyncTask<Favorite, Void, Void> {
        @Override
        protected Void doInBackground(Favorite... params) {
            try {
                DatabaseHelper helper = DatabaseHelper.getInstance(MainActivity.this);
                new FavoriteAdapter(helper).saveFavorite(params[0]);
            } catch (jsqlite.Exception e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            new LoadFavoritesTask().execute();
        }
    }

    private class FavoritesUpdatedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            new LoadFavoritesTask().execute();
        }
    }
}
