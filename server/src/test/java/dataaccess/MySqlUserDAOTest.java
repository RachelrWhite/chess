package dataaccess;

import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;

import static org.junit.jupiter.api.Assertions.*;

class MySqlUserDAOTest {

    private MySqlDataAccess dao; // implements UserDAO

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlDataAccess();
        dao.clear();
    }

    // -------- createUser --------

    @Test
    void createUserGetUserPositiveHashVerified() throws Exception {
        var user = new UserData("sam", "P@ssw0rd!", "s@mail");
        dao.createUser(user);

        var retrieved = dao.getUser("sam");
        assertNotNull(retrieved);
        assertEquals("sam", retrieved.username());
        assertEquals("s@mail", retrieved.email());

        // Not stored as plaintext; looks like bcrypt; verifies
        assertNotEquals("P@ssw0rd!", retrieved.password());
        assertTrue(retrieved.password().startsWith("$2"));
        assertTrue(BCrypt.checkpw("P@ssw0rd!", retrieved.password()));
    }

    @Test
    void createUserDuplicateUsernameThrows() throws Exception {
        dao.createUser(new UserData("sam", "x", "a@mail"));
        assertThrows(DataAccessException.class,
                () -> dao.createUser(new UserData("sam", "y", "b@mail")));
    }

    @Test
    void createUserNullUsernameThrows() {
        assertThrows(DataAccessException.class,
                () -> dao.createUser(new UserData(null, "pw", "e@mail")));
    }

    @Test
    void createUserUsernameTooLongThrows() {
        // username VARCHAR(255); make 300 chars
        String tooLong = "u".repeat(300);
        assertThrows(DataAccessException.class,
                () -> dao.createUser(new UserData(tooLong, "pw", "e@mail")));
    }

    // -------- getUser --------

    @Test
    void getUserUnknownReturnsNull() throws Exception {
        assertNull(dao.getUser("ghost"));
    }

    // -------- clear --------

    @Test
    void clearRemovesAllRowsPositive() throws Exception {
        dao.createUser(new UserData("sam", "x", "s@mail"));
        assertNotNull(dao.getUser("sam"));

        dao.clear();
        assertNull(dao.getUser("sam"));
    }

    @Test
    void clearOnEmptyNoopEdge() {
        assertDoesNotThrow(() -> dao.clear());
        assertDoesNotThrow(() -> assertNull(dao.getUser("nobody")));
    }
}
