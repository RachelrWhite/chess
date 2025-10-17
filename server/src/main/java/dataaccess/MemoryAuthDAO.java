package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;

public class MemoryAuthDAO implements AuthDAO{
    private final Map<String, AuthData> auths = new HashMap<>();

    @Override
    public void clear() throws DataAccessException {
        auths.clear();
    }

    @Override
    public void createAuth(AuthData auth) throws DataAccessException {
        auths.put(auth.AuthToken(), auth);
    }

    @Override
    public AuthData getAuth(String AuthToken) throws DataAccessException {
        return auths.get(AuthToken);
    }

    @Override

    public void deleteAuth(String AuthToken) throws DataAccessException {
        auths.remove(AuthToken);
    }
}
