package se.kth.ict.hotspots;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
        onSharedPreferenceChanged(null, "");
        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String frequency = myPref.getString("tracking_frequent", "");
        Preference trackingF = findPreference("tracking_frequent");
        trackingF.setSummary("Tracking frequency set to "+frequency+" milliseconds");

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
	    Toast.makeText(getActivity(), "Here is a toast", Toast.LENGTH_SHORT).show();
	}



	 @Override
	    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	        Preference connectionPref = findPreference(key);
	        switch (key) {
	        case "perform_updates":
	            SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
	            if (myPref.getBoolean("perform_updates", false)==false) {
	                Toast.makeText(getActivity(), "Tracking disabled", Toast.LENGTH_SHORT).show();
	                break;
	            } else {
	                Toast.makeText(getActivity(), "Tracking enabled", Toast.LENGTH_SHORT).show();
	                break;
	            }
	            
	        case "tracking_frequent":
	            connectionPref.setSummary("Tracking frequency set to "+sharedPreferences.getString(key, "")+" milliseconds");
	            break;
	        // two cases below might be pointless since the magic should be done in preferences.xml 
	        case "clear_location":
	            //clear location database here
                break;
	        case "clear_favorites":
	            //clear favorites database here
	            break;
	        }
	    }
	}


