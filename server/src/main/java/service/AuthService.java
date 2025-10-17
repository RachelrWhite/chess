package service;

import dataaccess.*;
import model.*;

public class AuthService {
    private final AuthDAO auth;

    public AuthService(AuthDAO auth) {
        this.auth = auth;
    }
    // clear method goes here
    public void clear() {
        try {
            auth.clear();
        } catch (DataAccessException e) {
            throw new RuntimeException("Internal error while clearing data", e);
        }
    }
}
