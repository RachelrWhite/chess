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
}
