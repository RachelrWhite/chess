package service;

import dataaccess.*;
import model.*;

public class GameService {
    private final GameDAO games;
    private final AuthDAO auth;

    public GameService(GameDAO games, AuthDAO auth) {
        this.games = games;
        this.auth = auth;
    }
    //createGame(), listGames(), joinGame() go here
    public void clear() {
        try {
            games.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException("Internal error while clearing data", e);
        }
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
            if (authToken == null || authToken.isBlank()) {
                throw new DataAccessException("unauthorized");
            }
            if (auth.getAuth(authToken) == null) {
                throw new DataAccessException("unauthorized");
            }
            if (gameName == null || gameName.isBlank()) {
                throw new DataAccessException("bad request");
            }
            return games.createGame(gameName);
    }
}
