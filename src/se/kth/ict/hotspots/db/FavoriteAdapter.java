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
        Stmt stmt = db.prepare("SELECT id, name, Y(Centroid(geom)), X(Centroid(geom)) "
                + "FROM favorite ORDER BY weight DESC");
        List<Favorite> result = new ArrayList<Favorite>();
        while (stmt.step()) {
            result.add(new Favorite(
                    stmt.column_int(0),
                    stmt.column_string(1),
                    stmt.column_double(2),
                    stmt.column_double(3)
            ));
        }
        return result;
    }

    /**
     * Save a favorite's name back to the database.
     */
    public void saveFavorite(Favorite favorite) throws jsqlite.Exception {
        Stmt stmt = db.prepare("UPDATE favorite SET name = ?2 WHERE id = ?1");
        stmt.bind(1, favorite.getId());
        stmt.bind(2, favorite.getName());
        stmt.step();
    }
}
