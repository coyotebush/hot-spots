package se.kth.ict.hotspots.db;

import jsqlite.Database;
import jsqlite.Stmt;

import java.util.ArrayList;
import java.util.List;

/**
 * Methods to interact with the "favorite" table.
 */
public class FavoriteAdapter {
    private final Database db;

    public FavoriteAdapter(DatabaseHelper helper) {
        db = helper.getDatabase();
    }

    public List<Favorite> getTopFavorites() throws jsqlite.Exception {
        Stmt stmt = db.prepare("SELECT name, Y(Centroid(geom)), X(Centroid(geom)) "
                + "FROM favorite ORDER BY weight DESC");
        List<Favorite> result = new ArrayList<Favorite>();
        while (stmt.step()) {
            result.add(new Favorite(
                    stmt.column_string(0),
                    stmt.column_double(1),
                    stmt.column_double(2)
            ));
        }
        return result;
    }
}
