package se.kth.ict.hotspots;

import java.io.IOException;

import se.kth.ict.hotspots.db.CityAdapter;
import se.kth.ict.hotspots.db.DatabaseHelper;
import se.kth.ict.hotspots.db.LocationAdapter;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;

public class MainActivity extends ListActivity {

	private static final int PERIOD=15*60*1000; // 15 minutes
	private PendingIntent pi=null;
	private AlarmManager mgr=null;	
    
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String city = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getInstance(this);

            city = new CityAdapter(helper).getNearestCity(59.403, 17.941, 10000);

            Location demoLocation = new Location("demo");
            demoLocation.setLatitude(59.321);
            demoLocation.setLongitude(18.073);
            new LocationAdapter(helper).insertLocation(demoLocation);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (jsqlite.Exception e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                new String[] { "Home", city != null ? city : "Work" });
        setListAdapter(adapter);
        
    	mgr=(AlarmManager)getSystemService(ALARM_SERVICE);
    	Intent i=new Intent(this, LocationPoller.class);

    	Bundle bundle = new Bundle();
    	LocationPollerParameter parameter = new LocationPollerParameter(bundle);
    	parameter.setIntentToBroadcastOnCompletion(new Intent(this, LocationReceiver.class));
    	// try GPS and fall back to NETWORK_PROVIDER
    	parameter.setProviders(new String[] {LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER});
    	parameter.setTimeout(30000);
    	i.putExtras(bundle);


    	pi=PendingIntent.getBroadcast(this, 0, i, 0);
    	mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
    			SystemClock.elapsedRealtime(),
    			PERIOD,
    			pi);

    	Toast
    	.makeText(this,
    	"Location polling every 15 minutes begun",
    	Toast.LENGTH_LONG)
    	.show();
    	  }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
