package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements UserDAO, GameDAO, AuthDAO {
    Map<String, UserData> users = new HashMap<>();
    Map<Integer, GameData> games = new HashMap<>();
    Map<String, AuthData> auths = new HashMap<>();
    int nextGameID = 1;


    @Override
    public void createAuth(AuthData auth) throws DataAccessException {

    }

    @Override
    public AuthData getAuth(String AuthToken) throws DataAccessException {
        return null;
    }

    @Override
    public void deleteAuth(String AuthToken) throws DataAccessException {

    }

    @Override
    public int createGame() throws DataAccessException {
        return 0;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public void updateGame(GameData game) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        auths.clear();
        nextGameID = 1;
    }

    @Override
    public void createUser(UserData user) throws DataAccessException {

    }

    @Override
    public void getUser(String username) throws DataAccessException {

    }
}
