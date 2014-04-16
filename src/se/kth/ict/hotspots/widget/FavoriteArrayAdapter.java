package se.kth.ict.hotspots.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import se.kth.ict.hotspots.db.Favorite;

import java.util.List;

/**
 * Builds list items for Favorite objects.
 */
public class FavoriteArrayAdapter extends ArrayAdapter<Favorite> {
    private static final int RESOURCE = android.R.layout.simple_list_item_2;

    public FavoriteArrayAdapter(Context context, List<Favorite> favoriteList) {
        super(context, RESOURCE, favoriteList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Favorite favorite = getItem(position);
        View view = View.inflate(getContext(), RESOURCE, null);
        ((TextView) view.findViewById(android.R.id.text1)).setText(favorite.getName());
        ((TextView) view.findViewById(android.R.id.text2)).setText(favorite.getLocationString());
        return view;
    }
}
