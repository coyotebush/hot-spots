package se.kth.ict.hotspots.db;

import android.net.Uri;

import java.util.Locale;

/**
 * Representation of a "favorite" location.
 */
public class Favorite {
    private String name;
    private double latitude, longitude;

    public Favorite(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getLocationString() {
        return String.format(Locale.US, "%.5f %.5f", latitude, longitude);
    }

    public Uri getUri() {
        return Uri.parse(String.format(Locale.US,
                "geo:%1$.5f,%2$.5f?q=%1$.5f,%2$.5f(%3$s)",
                latitude, longitude, name));
    }
}
