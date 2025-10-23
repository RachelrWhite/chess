package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserService userService;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    //register()
    @Test
    public void register_positive_createsUser() throws DataAccessException {
        var req = new RegisterRequest("alice", "password", "a@a.com");
        var res = userService.register(req);

        assertEquals("alice", res.username());
        assertNotNull(res.authToken());
        assertNotNull(authDAO.getAuth(res.authToken()));
    }

    @Test
    public void register_negative_usernameTaken() throws DataAccessException {
        var req = new RegisterRequest("bob", "password", "b@b.com");
        userService.register(req);
        var dupReq = new RegisterRequest("bob", "password", "dup@dup.com");

        var ex = assertThrows(DataAccessException.class, () -> userService.register(dupReq));
        assertTrue(ex.getMessage().toLowerCase().contains("taken"));
    }

    //clear()
    @Test
    public void clear_positive_clearsAllData() throws DataAccessException {
        var req = new RegisterRequest("chris", "password", "c@c.com");
        userService.register(req);
        assertNotNull(userDAO.getUser("chris"));

        userService.clear();

        assertNull(userDAO.getUser("chris"));
    }
}
