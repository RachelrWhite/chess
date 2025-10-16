package dataaccess;

import chess.*;
import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.*;

public class MemoryDataAccess implements UserDAO, GameDAO, AuthDAO {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<Integer, GameData> games = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();
    private int nextGameID = 1;

    @Override
    public void clear() throws DataAccessException {
        users.clear();
        games.clear();
        auths.clear();
        nextGameID = 1;
    }
// users
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

    @Override
    public int createGame(GameData initial) throws DataAccessException {
        int id = nextGameID++;
        String name = initial.gameName();
        ChessGame chess = new ChessGame();
        GameData stored = new GameData(id, null, null, name, chess);

        games.put(id, stored);
        return id;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(games.values());
    }

    //this is probably wrong
    @Override
    public void updateGame(GameData updated) throws DataAccessException {

        if (!games.containsKey(updated.gameID())) {
            throw new DataAccessException("game not found");
        }
        games.put(updated.gameID(), updated);
    }
// Auths
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
//public record GameData(int gameID, String whiteUsername, String blackUsername, String gameName, ChessGame game) {

}

