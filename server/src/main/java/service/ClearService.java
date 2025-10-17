package service;

import dataaccess.*;
import model.*;

public class ClearService {
    private final UserDAO users;
    private final AuthDAO auth;
    private final GameDAO game;

    public ClearService(UserDAO users, AuthDAO auth, GameDAO game) {
        this.users = users;
        this.auth = auth;
        this.game = game;
    }
    // clear method goes here
}
