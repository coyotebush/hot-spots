package se.kth.ict.hotspots;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;

import jsqlite.Exception;

import se.kth.ict.hotspots.db.DatabaseHelper;
import se.kth.ict.hotspots.db.LocationAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.commonsware.cwac.locpoll.LocationPollerResult;

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
      DatabaseHelper helper;
	try {
	      System.out.println(loc.getAltitude());

		helper = DatabaseHelper.getInstance(context);
		new LocationAdapter(helper).insertLocation(loc);
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
}