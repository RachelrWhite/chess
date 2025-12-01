package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MySqlAuthDAOTest {

    private MySqlDataAccess dao; // implements AuthDAO and UserDAO

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlDataAccess();
        dao.clear();

        // Create users to satisfy the FK auth.username -> user.username
        dao.createUser(new UserData("sam", "password", "sam@mail"));
        dao.createUser(new UserData("pam", "password", "pam@mail"));
    }

    @Test
    void createGetDeletePositive() throws Exception {
        var a = new AuthData("tkn-abc123", "sam");
        dao.createAuth(a);

        var got = dao.getAuth("tkn-abc123");
        assertNotNull(got);
        assertEquals("tkn-abc123", got.authToken());
        assertEquals("sam", got.username());

        dao.deleteAuth("tkn-abc123");
        assertNull(dao.getAuth("tkn-abc123")); // getAuth returns null when missing
    }

    @Test
    void getUnknownTokenReturnsNull() throws Exception {
        assertNull(dao.getAuth("no-such-token"));
    }

    @Test
    void deleteUnknownTokenThrows() {
        var ex = assertThrows(DataAccessException.class, () -> dao.deleteAuth("nope"));
        assertTrue(ex.getMessage().toLowerCase().contains("not found"));
    }

    @Test
    void createDuplicateTokenNegative() throws Exception {
        // First insert ok (FK satisfied: "sam" exists)
        dao.createAuth(new AuthData("dup", "sam"));

        // Second insert uses same token -> PK/unique violation, should throw
        assertThrows(DataAccessException.class, () -> dao.createAuth(new AuthData("dup", "pam")));
    }
    @Test
    void createAuthForUnknownUserThrows() {
        assertThrows(DataAccessException.class,
                () -> dao.createAuth(new AuthData("tkn-ghost", "ghost")));
    }

}
