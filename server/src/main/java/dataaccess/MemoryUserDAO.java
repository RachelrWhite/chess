package dataaccess;

import model.AuthData;
import model.GameData;
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
    public void createUser(UserData user) throws DataAccessException {
        var username = user.username();
        if (users.containsKey(username)) {
            throw new DataAccessException("duplicate user");
        }
    }

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }



}
