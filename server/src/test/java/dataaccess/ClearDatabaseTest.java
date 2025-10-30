package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClearDatabaseTest {

    private MySqlDataAccess dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlDataAccess();
        dao.clear();
    }

    @Test
    void clearRemovesAllRowsPositive() throws Exception {
        // Seed all three tables
        dao.createUser(new UserData("sam", "p", "s@mail"));
        int gid = dao.createGame("G1");
        dao.createAuth(new AuthData("tkn1", "sam"));

        // Sanity: present before clear
        assertNotNull(dao.getUser("sam"));
        assertNotNull(dao.getGame(gid));
        assertNotNull(dao.getAuth("tkn1"));

        // Clear everything
        dao.clear();

        // All gone
        assertNull(dao.getUser("sam"));
        assertNull(dao.getGame(gid));
        assertNull(dao.getAuth("tkn1"));
    }

    @Test
    void clearOnEmptyIsNoopNegativeEdge() {
        // Should not throw even when already empty
        assertDoesNotThrow(() -> dao.clear());
        // Still empty
        assertDoesNotThrow(() -> {
            assertNull(dao.getUser("nobody"));
            assertNull(dao.getGame(42));
            assertNull(dao.getAuth("nope"));
        });
    }
}

