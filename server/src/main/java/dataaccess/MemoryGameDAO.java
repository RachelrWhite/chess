package dataaccess;

import model.AuthData;
import model.GameData;
import chess.*;

import java.util.*;

public class MemoryGameDAO implements GameDAO{
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextId = 1;

    @Override
    public void clear() throws DataAccessException {
        games.clear();
    }

    @Override
    public int createGame(String gameName) throws DataAccessException {
        int id = nextId++;
        GameData gd = new GameData(id, null, null, gameName, new ChessGame());
        games.put(id, gd);
        return id;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return games.get(gameID);
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return games.values().stream().toList();
    }

    @Override
    public void updateGame(GameData updated) throws DataAccessException {
        if (updated == null || !games.containsKey(updated.gameID())) {
            throw new DataAccessException("bad request");
        }
        games.put(updated.gameID(), updated);
    }
}
