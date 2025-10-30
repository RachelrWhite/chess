package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class UserService {
    private final UserDAO users;
    private final AuthDAO auth;

    public UserService(UserDAO users, AuthDAO auth) {
        this.users = users;
        this.auth = auth;
    }

    //register(), login(), logout() go here
    public RegisterResult register(RegisterRequest registerRequest) throws DataAccessException {
        if (registerRequest == null ||
                registerRequest.username() == null || registerRequest.username().isBlank() ||
                registerRequest.password() == null || registerRequest.password().isBlank()) {
            throw new DataAccessException("bad request");
        }

        if (users.getUser(registerRequest.username()) != null) {
            throw new DataAccessException("already taken");
        }

        users.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
        var token = java.util.UUID.randomUUID().toString();
        auth.createAuth(new AuthData(token, registerRequest.username()));
        return new RegisterResult(registerRequest.username(), token);
    }


    public void clear() {
        try {
            users.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException("Internal error while clearing data", e);
        }
    }
}
