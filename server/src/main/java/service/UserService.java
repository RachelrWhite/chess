package service;

import dataaccess.*;
import model.*;

public class UserService {
    private final UserDAO users;
    private final AuthDAO auth;

    public UserService(UserDAO users, AuthDAO auth) {
        this.users = users;
        this.auth = auth;
    }
    //register(), login(), logout() go here
}
