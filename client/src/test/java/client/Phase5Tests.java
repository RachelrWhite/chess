package client;

import facade.ServerFacade;
import model.AuthData;
import model.GameData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import server.Server;

public class Phase5Tests {
    static Server server;
    static ServerFacade facade;

    @BeforeAll
    static void startServer() {
        server = new Server();

        // Prefer a random free port; Server.run(int) should return the actual port used.
        int port = server.run(0);                 // if your run(...) takes a port
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    //might not need but just in case
    @BeforeEach
    void clearDb() {
        facade.clear();
    }

    @Test
    void registerPositive() {
        var auth = facade.register("alice","pw","a@e.com");
        assertNotNull(auth);
        assertTrue(auth.authToken().length() > 5);
    }

    @Test
    void registerNegative() {
        facade.register("bob", "pw", "b@e.com");
        AuthData again = facade.register("bob", "pw", "b@e.com");
        assertNull(again); // until I add real exception handling
    }

    @Test
    void loginPositive() {
        facade.register("bob2", "pw2", "bob2@e.com");
        var auth = facade.login("bob2", "pw2");
        assertNotNull(auth);
        assertTrue(auth.authToken().length() > 5);
    }

    @Test
    void loginNegative() {
        facade.register("bob3", "pw3", "bob3@e.com");
        AuthData wrongPassword = facade.login("bob3", "wrongPassword");
        assertNull(wrongPassword);
    }

    @Test
    void logoutPositive() {
        var auth = facade.register("lo", "pw", "lo@e.com");
        assertNotNull(auth);

        // should not throw when logging out a valid token
        assertDoesNotThrow(() -> facade.logout(auth.authToken()));

        // old token should no longer work for auth-required calls
        var gamesAfter = facade.listGames(auth.authToken());
        assertTrue(gamesAfter == null || gamesAfter.isEmpty());
    }
    @Test
    void logoutNegative() {
        assertDoesNotThrow(() -> facade.logout("bad-token"));
    }
    @Test
    void listGamesPositive() {
        var auth = facade.register("lgp", "pw", "lgp@e.com");
        assertNotNull(auth);

        int id1 = facade.createGame(auth.authToken(), "Game A");
        int id2 = facade.createGame(auth.authToken(), "Game B");
        assertTrue(id1 > 0);
        assertTrue(id2 > 0);

        var games = facade.listGames(auth.authToken());
        assertNotNull(games);
        assertTrue(games.size() >= 2);

        var names = games.stream().map(GameData::gameName).toList();
        assertTrue(names.contains("Game A"));
        assertTrue(names.contains("Game B"));
    }
    @Test
    void listGamesNegative() {
        var games = facade.listGames("bad-token");
        assertTrue(games == null || games.isEmpty());
    }
    @Test
    void createGamePositive() {
        var auth = facade.register("cgp", "pw", "cgp@e.com");
        assertNotNull(auth);

        int id = facade.createGame(auth.authToken(), "My Match");
        assertTrue(id > 0);

        var games = facade.listGames(auth.authToken());
        assertNotNull(games);
        assertTrue(games.stream().anyMatch(g -> "My Match".equals(g.gameName())));
    }
    @Test
    void createGameNegative() {
        int id = facade.createGame("bad-token", "Should Fail");
        assertEquals(0, id);
    }
    @Test
    void joinGamePositive() {
        var auth = facade.register("joiner", "pw", "joiner@e.com");
        assertNotNull(auth);

        int id = facade.createGame(auth.authToken(), "Table 1");
        assertTrue(id > 0);

        // join as WHITE
        assertDoesNotThrow(() -> facade.joinGame(auth.authToken(), "WHITE", id));

        // verify the seat shows as taken by 'joiner'
        var games = facade.listGames(auth.authToken());
        var game = games.stream().filter(g -> g.gameID() == id).findFirst().orElseThrow();
        assertEquals("joiner", game.whiteUsername());
    }
    @Test
    void joinGameNegative() {
        var auth = facade.register("host", "pw", "host@e.com");
        int id = facade.createGame(auth.authToken(), "Open Table");
        assertTrue(id > 0);

        // attempt to join with a bad token → should not change seats
        assertDoesNotThrow(() -> facade.joinGame("bad-token", "BLACK", id));

        var games = facade.listGames(auth.authToken());
        var game = games.stream().filter(g -> g.gameID() == id).findFirst().orElseThrow();
        // seat should remain empty because the join failed
        assertNull(game.blackUsername());
    }

}
