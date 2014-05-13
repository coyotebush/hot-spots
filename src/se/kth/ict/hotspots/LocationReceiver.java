package se.kth.ict.hotspots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import com.commonsware.cwac.locpoll.LocationPollerResult;
import se.kth.ict.hotspots.db.DatabaseHelper;
import se.kth.ict.hotspots.db.LocationAdapter;

import java.io.IOException;

public class LocationReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
   
      
      Bundle b=intent.getExtras();
      
      LocationPollerResult locationResult = new LocationPollerResult(b);
      
      Location loc=locationResult.getLocation();
      String msg;

      if (loc==null) {
        loc=locationResult.getLastKnownLocation();
        if (loc==null) {
          msg=locationResult.getError();
        }
        else {
          msg="TIMEOUT, lastKnown="+loc.toString();

        }
      }
      else {
        msg=loc.toString();
      }
      
      if (msg==null) {
        msg="Invalid broadcast received!";
      }
      Log.i("LocationReceiver", msg);

      if (loc != null) {
          Intent favoriteIntent = new Intent(context, FavoriteUpdaterService.class);
          try {
              DatabaseHelper helper = DatabaseHelper.getInstance(context);
              long locationId = new LocationAdapter(helper).insertLocation(loc);
              favoriteIntent.putExtra(FavoriteUpdaterService.LOCATION_ID, locationId);
          } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          } catch (jsqlite.Exception e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }
          context.startService(favoriteIntent);
      }
  }
}