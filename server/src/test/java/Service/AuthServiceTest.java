package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class AuthServiceTest {

    private AuthService authService;
    private MemoryAuthDAO authDAO;
    private MemoryUserDAO userDAO;

    @BeforeEach
    public void setup() throws DataAccessException {
        authDAO = new MemoryAuthDAO();
        userDAO = new MemoryUserDAO();
        authService = new AuthService(authDAO, userDAO);

        // Seed a user to test login/logout
        userDAO.createUser(new UserData("link", "sword", "link@hyrule.com"));
    }

    // ------- login() -------

    @Test
    public void loginPositiveValidCredentials() throws DataAccessException {
        var request = new LoginRequest("link", "sword");
        var result = authService.login(request);

        assertEquals("link", result.username());
        assertNotNull(result.authToken(), "authToken should not be null");
        assertNotNull(authDAO.getAuth(result.authToken()), "token should exist in DAO");
    }

    @Test
    public void loginNegativeBadPassword() {
        var request = new LoginRequest("link", "wrong");
        var ex = assertThrows(DataAccessException.class, () -> authService.login(request));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    public void loginNegativeMissingFields() {
        var badRequest = new LoginRequest("", "");
        var ex = assertThrows(DataAccessException.class, () -> authService.login(badRequest));
        assertTrue(ex.getMessage().toLowerCase().contains("bad request"));
    }



    @Test
    public void logoutPositiveValidToken() throws DataAccessException {
        var loginResult = authService.login(new LoginRequest("link", "sword"));
        String token = loginResult.authToken();

        assertNotNull(authDAO.getAuth(token));
        authService.logout(token);
        assertNull(authDAO.getAuth(token), "token should be deleted after logout");
    }

    @Test
    public void logoutNegativeInvalidToken() {
        var ex = assertThrows(DataAccessException.class,
                () -> authService.logout("not-a-real-token"));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    public void logoutNegativeBlankToken() {
        var ex = assertThrows(DataAccessException.class,
                () -> authService.logout(""));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }



    @Test
    public void clearPositiveRemovesAllAuths() throws DataAccessException {
        var result = authService.login(new LoginRequest("link", "sword"));
        assertNotNull(authDAO.getAuth(result.authToken()));

        authService.clear();

        assertNull(authDAO.getAuth(result.authToken()), "auth table should be empty after clear()");
    }
}
