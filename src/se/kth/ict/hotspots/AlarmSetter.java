package se.kth.ict.hotspots;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.commonsware.cwac.locpoll.LocationPoller;
import com.commonsware.cwac.locpoll.LocationPollerParameter;

/**
 * Triggered on boot and when MainActivity starts.
 * Sets a repeating alarm to poll the device location, if not already set.
 */
public class AlarmSetter extends BroadcastReceiver {
    
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        String frequencyString = settings.getString("tracking_frequent", "");
        long frequency = Long.parseLong(frequencyString);
        Log.w("AlarmSetter", "onReceive");
        AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, LocationPoller.class);

        Bundle bundle = new Bundle();
        LocationPollerParameter parameter = new LocationPollerParameter(bundle);
        parameter.setIntentToBroadcastOnCompletion(new Intent(context, LocationReceiver.class));
        // try GPS and fall back to NETWORK_PROVIDER
        parameter.setProviders(new String[] { LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER });
        i.putExtras(bundle);


        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        mgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), frequency, pi);

        Toast.makeText(context, "Location polling every "+frequency+" milliseconds begun",
                Toast.LENGTH_LONG).show();
    }
}
