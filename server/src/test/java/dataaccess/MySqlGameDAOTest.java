package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

class MySqlGameDAOTest {

    private MySqlDataAccess dao; // implements GameDAO

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlDataAccess();
        dao.clear();
    }

    @Test
    void createGetListUpdatePositive() throws Exception {
        int id = dao.createGame("Friday Blitz");
        assertTrue(id > 0);

        // get
        GameData g = dao.getGame(id);
        assertNotNull(g);
        assertEquals(id, g.gameID());
        assertEquals("Friday Blitz", g.gameName());
        assertNull(g.whiteUsername());
        assertNull(g.blackUsername());
        assertNotNull(g.game()); // ChessGame object

        // list
        Collection<GameData> list = dao.listGames();
        assertTrue(list.stream().anyMatch(x -> x.gameID() == id));

        // update (claim white)
        var updated = new GameData(g.gameID(), "sam", g.blackUsername(), g.gameName(), g.game());
        dao.updateGame(updated);

        var after = dao.getGame(id);
        assertEquals("sam", after.whiteUsername());
        assertEquals(g.gameName(), after.gameName());
    }

    @Test
    void getBadIdReturnsNull() throws Exception {
        assertNull(dao.getGame(999_999));
    }

    @Test
    void updateBadIdThrows() {
        var bogus = new GameData(888_888, null, null, "X", new chess.ChessGame());
        var ex = assertThrows(DataAccessException.class, () -> dao.updateGame(bogus));
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    void createNullNameThrows() {
        assertThrows(DataAccessException.class, () -> dao.createGame(null)); // NOT NULL column
    }
    @Test
    void listGamesEmptyReturnsEmpty() throws Exception {
        var list = dao.listGames();
        assertNotNull(list);
        assertTrue(list.isEmpty());
    }
}
