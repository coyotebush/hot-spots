package se.kth.ict.hotspots;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import se.kth.ict.hotspots.db.CityAdapter;
import se.kth.ict.hotspots.db.DatabaseHelper;
import se.kth.ict.hotspots.db.LocationAdapter;

import java.io.IOException;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String city = null;
        try {
            DatabaseHelper helper = new DatabaseHelper(this);
            city = new CityAdapter(helper).getNearestCity(59.403, 17.941);
            new LocationAdapter(helper).insertLocation("demo", 0, 0, 59.321, 18.073);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                new String[] { "Home", city != null ? city : "Work" });
        setListAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
