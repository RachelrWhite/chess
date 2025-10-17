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
        String u = registerRequest.username();
        if (users.getUser(u) == null) {
            String token = UUID.randomUUID().toString();
            users.createUser(new UserData(registerRequest.username(), registerRequest.password(), registerRequest.email()));
            auth.createAuth(new AuthData(token, registerRequest.username()));
            return new RegisterResult(u, token);
        } else {
            throw new DataAccessException("AlreadyTakenException");
        }
    }


    public void clear() {
        try {
            users.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException("Internal error while clearing data", e);
        }
    }
}
