package se.kth.ict.hotspots;

import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ListView;
import se.kth.ict.hotspots.db.DatabaseHelper;
import se.kth.ict.hotspots.db.Favorite;
import se.kth.ict.hotspots.db.FavoriteAdapter;
import se.kth.ict.hotspots.widget.FavoriteArrayAdapter;

import java.io.IOException;
import java.util.List;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sendBroadcast(new Intent(this, AlarmSetter.class));
        new LoadFavoritesTask().execute();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
}
