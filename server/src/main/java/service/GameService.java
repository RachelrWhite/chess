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

    public void joinGame(String authToken, String playerColor, Integer gameID) throws DataAccessException {
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("unauthorized");
        }
        var authData = auth.getAuth(authToken);

        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }

        String username = authData.username();

        if (gameID == null) {
            throw new DataAccessException("bad request");
        }
        var game = games.getGame(gameID);
        if (game == null) {
            throw new DataAccessException("bad request");
        }

        if (playerColor == null || playerColor.isBlank()) {
            return;
        }

        String color = playerColor.toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new DataAccessException("bad request");
        }


        if (color.equals("WHITE")) {
            String current = game.whiteUsername();
            if (current != null && !current.equals(username)) {
                throw new DataAccessException("already taken");
            }

            if (current == null) {
                var updated = new GameData(game.gameID(), username, game.blackUsername(), game.gameName(), game.game());
                games.updateGame(updated);
            }
        //BLACK
        } else {
            String current = game.blackUsername();
            if (current != null && !current.equals(username)) {
                throw new DataAccessException("already taken");
            }
            if (current == null) {
                var updated = new GameData(game.gameID(), game.whiteUsername(), username, game.gameName(), game.game());
                games.updateGame(updated);
            }
        }
    }


}
