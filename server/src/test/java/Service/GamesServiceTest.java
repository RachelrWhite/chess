package service;

import dataaccess.*;
import model.*;
import org.junit.jupiter.api.*;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.*;

public class GamesServiceTest {

    private GameService gameService;
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;

    // Common seed data
    private String userToken;
    private String userName = "zelda";

    @BeforeEach
    public void setup() throws DataAccessException {
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        gameService = new GameService(gameDAO, authDAO);

        // Seed a valid auth
        userToken = "tok-123";
        authDAO.createAuth(new AuthData(userToken, userName));
    }



    @Test
    public void createGamePositiveCreatesAndReturnsId() throws DataAccessException {
        int id = gameService.createGame(userToken, "First Game");

        assertTrue(id > 0, "gameID should be positive");
        var stored = gameDAO.getGame(id);
        assertNotNull(stored, "game should exist in DAO");
        assertEquals("First Game", stored.gameName());
        assertNull(stored.whiteUsername());
        assertNull(stored.blackUsername());
    }

    @Test
    public void createGameNegativeUnauthorized() {
        var ex = assertThrows(DataAccessException.class,
                () -> gameService.createGame("bad-token", "G"));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }


    @Test
    public void listGamesPositiveReturnsAll() throws DataAccessException {
        int g1 = gameService.createGame(userToken, "A");
        int g2 = gameService.createGame(userToken, "B");

        var result = gameService.listGames(userToken);

        Collection<GameSummary> games = result.games();
        assertEquals(2, games.size());
        assertTrue(games.stream().anyMatch(s -> s.gameID() == g1 && s.gameName().equals("A")));
        assertTrue(games.stream().anyMatch(s -> s.gameID() == g2 && s.gameName().equals("B")));
    }

    @Test
    public void listGamesNegativeUnauthorized() {
        var ex = assertThrows(DataAccessException.class,
                () -> gameService.listGames("not-a-token"));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }



    @Test
    public void joinGamePositiveClaimWhiteWhenEmpty() throws DataAccessException {
        int id = gameService.createGame(userToken, "Joinable");
        gameService.joinGame(userToken, "WHITE", id);

        var updated = gameDAO.getGame(id);
        assertEquals(userName, updated.whiteUsername());
        assertNull(updated.blackUsername());
    }

    @Test
    public void joinGamePositiveBlankColorNoChange() throws DataAccessException {
        int id = gameService.createGame(userToken, "Observer-ish");
        var before = gameDAO.getGame(id);

        gameService.joinGame(userToken, "", id);

        var after = gameDAO.getGame(id);
        assertEquals(before.whiteUsername(), after.whiteUsername());
        assertEquals(before.blackUsername(), after.blackUsername());
    }

    @Test
    public void joinGameNegativeAlreadyTakenSameColor() throws DataAccessException {
        int id = gameService.createGame(userToken, "Clash");
        gameService.joinGame(userToken, "WHITE", id);

        String otherToken = "tok-456";
        authDAO.createAuth(new AuthData(otherToken, "link"));

        var ex = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(otherToken, "WHITE", id));
        assertTrue(ex.getMessage().toLowerCase().contains("already"));
    }

    @Test
    public void joinGameNegativeBadColor() throws DataAccessException {
        int id = gameService.createGame(userToken, "BadColor");
        var ex = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(userToken, "PURPLE", id));
        assertTrue(ex.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    public void joinGameNegativeMissingOrBadGameId() {
        var ex1 = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(userToken, "WHITE", null));
        assertTrue(ex1.getMessage().toLowerCase().contains("bad request"));

        var ex2 = assertThrows(DataAccessException.class,
                () -> gameService.joinGame(userToken, "WHITE", 999_999));
        assertTrue(ex2.getMessage().toLowerCase().contains("bad request"));
    }

    @Test
    public void joinGameNegativeUnauthorized() throws DataAccessException {
        int id = gameService.createGame(userToken, "AuthNeeded");
        var ex = assertThrows(DataAccessException.class,
                () -> gameService.joinGame("nope", "WHITE", id));
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"));
    }

    // -------- clear (bonus positive) --------

    @Test
    public void clearPositiveErasesGames() throws DataAccessException {
        int id = gameService.createGame(userToken, "ToBeCleared");
        assertNotNull(gameDAO.getGame(id));

        gameService.clear();

        assertNull(gameDAO.getGame(id), "games should be gone after clear()");
    }
}
