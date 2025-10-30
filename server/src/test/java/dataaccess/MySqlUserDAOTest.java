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

    @Test
    void createUserGetUserPositive() throws Exception {
        var u = new UserData("sam", "P@ssw0rd!", "s@mail");
        dao.createUser(u);

        var got = dao.getUser("sam");
        assertNotNull(got);
        assertEquals("sam", got.username());
        assertEquals("s@mail", got.email());

        // Should not be stored as plaintext
        assertNotEquals("P@ssw0rd!", got.password());
        assertTrue(got.password().startsWith("$2")); // bcrypt prefix
        assertTrue(BCrypt.checkpw("P@ssw0rd!", got.password()));
    }

    @Test
    void getUserUnknownReturnsNull() throws Exception {
        assertNull(dao.getUser("ghost"));
    }

    @Test
    void createUserDuplicateUsernameNegative() throws Exception {
        dao.createUser(new UserData("sam", "x", "a@mail"));
        assertThrows(DataAccessException.class,
                () -> dao.createUser(new UserData("sam", "y", "b@mail")));
    }
}
