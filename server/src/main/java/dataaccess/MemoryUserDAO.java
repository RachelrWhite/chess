package dataaccess;

import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO{
    private final Map<String, UserData> users = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        users.clear();
    }

    @Override
    public UserData createUser(UserData user) throws DataAccessException {
        if (user == null || user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("bad request");
        }

        String username = user.username();
        if (users.containsKey(username)) {
            throw new DataAccessException("already taken");
        }
        users.put(username, user);
        return user;
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }

}
