package client;

import Facade.ServerFacade;
import model.AuthData;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class phase5tests {
    static ServerFacade facade;

    @BeforeAll
    static void setup() {
        facade = new ServerFacade("http://localhost:8080");
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

    }

    @Test
    void loginNegative() {

    }
    @Test
    void logoutPositive() {

    }
    @Test
    void logoutNegative() {

    }
    @Test
    void listGamesPositive() {

    }
    @Test
    void listGamesNegative() {

    }
    @Test
    void createGamePositive() {

    }
    @Test
    void createGameNegative() {

    }
    @Test
    void joinGamePositive() {

    }
    @Test
    void joinGameNegative() {

    }




}
