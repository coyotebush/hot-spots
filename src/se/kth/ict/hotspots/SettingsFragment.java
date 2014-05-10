package se.kth.ict.hotspots;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
        onSharedPreferenceChanged(null, "");
        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String frequency = myPref.getString("tracking_frequent", "");
        Preference trackingF = findPreference("tracking_frequent");
        
        // This just uses milliseconds format when preferences is created for the first time
        trackingF.setSummary("Tracking frequency set to "+frequency+" milliseconds");
        Preference clearLocation = findPreference("clear_location");

        // Clear locations
        clearLocation.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Destroy location data");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Clear locations method
                        Toast.makeText(getActivity(), "bye bye locations", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
        Preference clearFavorites = findPreference("clear_favorites");
        
        // Clear favorites
        clearFavorites.setOnPreferenceClickListener(new OnPreferenceClickListener() {
            
            
            @Override
            public boolean onPreferenceClick(Preference preference) {
                
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                
                builder.setTitle("Destroy favorites data");
                builder.setMessage("Are you sure?");

                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        // Clear favorites method
                        Toast.makeText(getActivity(), "bye bye favorites", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
                return false;
            }
        });
	}
	
	
	@Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }


	@Override
    public void onPause() {
        super.onPause();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }
	

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    Toast.makeText(getActivity(), "Here is a toast for you", Toast.LENGTH_SHORT).show();
	}


	// This looks messy but is functional.
	@Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	        Preference connectionPref = findPreference(key);
	        if(key.equalsIgnoreCase("perform_updates") ) {
	            SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	            if (myPref.getBoolean("perform_updates", false)==false) {
	                Toast.makeText(getActivity(), "Tracking disabled", Toast.LENGTH_SHORT).show();
	                getActivity().stopService(new Intent(getActivity(), AlarmSetter.class));
	            } else {
	                getActivity().sendBroadcast(new Intent(getActivity(), AlarmSetter.class));
	                Toast.makeText(getActivity(), "Tracking enabled", Toast.LENGTH_SHORT).show();
	            }
	        } else if (key.equalsIgnoreCase("tracking_frequent")) {
	            String helper = sharedPreferences.getString(key, "");
	            Log.d("TAG", helper);
	            if (helper.equalsIgnoreCase("5000")) {
	               connectionPref.setSummary("Tracking frequency set to 5 seconds");
	               SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	               if (myPref.getBoolean("perform_updates", false)==true) {
    	               getActivity().stopService(new Intent(getActivity(), AlarmSetter.class));
                       getActivity().sendBroadcast(new Intent(getActivity(), AlarmSetter.class));
    	               }
	            } else if (helper.equalsIgnoreCase("30000")) {
	                   connectionPref.setSummary("Tracking frequency set to 30 seconds");
	                   SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	                   if (myPref.getBoolean("perform_updates", false)==true) {
	                       getActivity().stopService(new Intent(getActivity(), AlarmSetter.class));
	                       getActivity().sendBroadcast(new Intent(getActivity(), AlarmSetter.class));
	                       }

	            } else if (helper.equalsIgnoreCase("60000")) {
                    connectionPref.setSummary("Tracking frequency set to 60 seconds");
                    SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    if (myPref.getBoolean("perform_updates", false)==true) {
                        getActivity().stopService(new Intent(getActivity(), AlarmSetter.class));
                        getActivity().sendBroadcast(new Intent(getActivity(), AlarmSetter.class));
                        }

	            } else if (helper.equalsIgnoreCase("300000")) {
                    connectionPref.setSummary("Tracking frequency set to 5 minutes");
                    SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    if (myPref.getBoolean("perform_updates", false)==true) {
                        getActivity().stopService(new Intent(getActivity(), AlarmSetter.class));
                        getActivity().sendBroadcast(new Intent(getActivity(), AlarmSetter.class));
                        }

	            } else if (helper.equalsIgnoreCase("900000")) {
                    connectionPref.setSummary("Tracking frequency set to 15 minutes");
                    SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    if (myPref.getBoolean("perform_updates", false)==true) {
                        getActivity().stopService(new Intent(getActivity(), AlarmSetter.class));
                        getActivity().sendBroadcast(new Intent(getActivity(), AlarmSetter.class));
                        }

	            }
	        }
	    }
	}