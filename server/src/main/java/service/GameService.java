package service;

import dataaccess.*;
import model.*;
import chess.*;
import java.util.Collection;

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
        //this is for if there is no auth token
        if (authToken == null || authToken.isBlank()) {
            throw new DataAccessException("unauthorized");
        }
        var authData = auth.getAuth(authToken);
        //this is for if there is no authData in the authToken
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        String username = authData.username();

        //this is for if there is no gameID that matches their request
        if (gameID == null) {
            throw new DataAccessException("bad request");
        }
        var game = games.getGame(gameID);
        //this if for if there are no actual games with that gameID
        if (game == null) {
            throw new DataAccessException("bad request");
        }
        //this is for if there is no playerColor
        if (playerColor == null || playerColor.isBlank()) {
            return;
        }
        String color = playerColor.toUpperCase();
        //this is for if the playerColor isn't white or black
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            throw new DataAccessException("bad request");
        }

        //this is for if the color is white
        if (color.equals("WHITE")) {
            String current = game.whiteUsername();
            //cant join with the wrong username
            if (current != null && !current.equals(username)) {
                throw new DataAccessException("already taken");
            }
            //cant not have a current color
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

    public ListGamesResult listGames(String token) throws DataAccessException {
        var authData = auth.getAuth(token);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }

        Collection<GameData> gamesCollection = games.listGames();

        Collection<GameSummary> summaries = gamesCollection.stream()
                .map(g -> new GameSummary(
                        g.gameID(),
                        g.whiteUsername(),
                        g.blackUsername(),
                        g.gameName()))
                .toList();

        return new ListGamesResult(summaries);
    }

}



