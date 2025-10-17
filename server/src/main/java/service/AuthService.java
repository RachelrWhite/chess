package service;

import dataaccess.*;
import model.*;

import java.util.UUID;

public class AuthService {
    private final AuthDAO auth;
    private final UserDAO users;

    public AuthService(AuthDAO auth, UserDAO users) {
        this.auth = auth;
        this.users = users;
    }
    // clear method goes here
    public LoginResult login(LoginRequest loginRequest) throws DataAccessException {
        if (loginRequest == null ||
                loginRequest.username() == null || loginRequest.username().isBlank() ||
                loginRequest.password() == null || loginRequest.password().isBlank()) {
            throw new DataAccessException("bad request");
        }

        UserData user = users.getUser(loginRequest.username());
        if (user == null || !user.password().equals(loginRequest.password())) {
            throw new DataAccessException("unauthorized");
        }

        String token = UUID.randomUUID().toString();
        auth.createAuth(new AuthData(token, loginRequest.username()));
        return new LoginResult(loginRequest.username(), token);
    }

    public void clear() {
        try {
            auth.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException("Internal error while clearing data", e);
        }
    }
}
