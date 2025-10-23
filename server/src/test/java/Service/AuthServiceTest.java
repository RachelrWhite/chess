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
    public void login_positive_validCredentials() throws DataAccessException {
        var request = new LoginRequest("link", "sword");
        var result = authService.login(request);

        assertEquals("link", result.username());
        assertNotNull(result.authToken(), "authToken should not be null");
        assertNotNull(authDAO.getAuth(result.authToken()), "token should exist in DAO");
    }

    @Test
    public void login_negative_badPassword() {
        var request = new LoginRequest("link", "wrong");
        var ex = assertThrows(DataAccessException.class, () -> authService.login(request));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    public void login_negative_missingFields() {
        var badRequest = new LoginRequest("", "");
        var ex = assertThrows(DataAccessException.class, () -> authService.login(badRequest));
        assertTrue(ex.getMessage().toLowerCase().contains("bad request"));
    }

    // ------- logout() -------

    @Test
    public void logout_positive_validToken() throws DataAccessException {
        var loginResult = authService.login(new LoginRequest("link", "sword"));
        String token = loginResult.authToken();

        assertNotNull(authDAO.getAuth(token));
        authService.logout(token);
        assertNull(authDAO.getAuth(token), "token should be deleted after logout");
    }

    @Test
    public void logout_negative_invalidToken() {
        var ex = assertThrows(DataAccessException.class,
                () -> authService.logout("not-a-real-token"));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    @Test
    public void logout_negative_blankToken() {
        var ex = assertThrows(DataAccessException.class,
                () -> authService.logout(""));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    // ------- clear() -------

    @Test
    public void clear_positive_removesAllAuths() throws DataAccessException {
        var result = authService.login(new LoginRequest("link", "sword"));
        assertNotNull(authDAO.getAuth(result.authToken()));

        authService.clear();

        assertNull(authDAO.getAuth(result.authToken()), "auth table should be empty after clear()");
    }
}

