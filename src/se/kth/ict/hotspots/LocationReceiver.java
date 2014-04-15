package se.kth.ict.hotspots;

import com.commonsware.cwac.locpoll.LocationPollerResult;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;

public class LocationReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Bundle b=intent.getExtras();

		  LocationPollerResult locationResult = new LocationPollerResult(b);

		  Location loc=locationResult.getLocation();
		  String msg;

		  if (loc==null) {
		    loc=locationResult.getLastKnownLocation();
		    System.out.println(loc);

		    if (loc==null) {
		      msg=locationResult.getError();
		      System.out.println(msg);
		    }
		    else {
		      msg="TIMEOUT, lastKnown="+loc.toString();
		      System.out.println(msg);
		    }
		  }
		  else {
		    msg=loc.toString();
		    System.out.println(msg);
		  }

		  if (msg==null) {
		    msg="Invalid broadcast received!";
		  }
	}

	
	
}
