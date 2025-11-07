package dataaccess;

//import exception.DataAccessException;

import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import chess.piecemoves.MoveCalculator;
import chess.piecemoves.KingMovesCalculator;


public class MySqlDataAccess implements AuthDAO, GameDAO, UserDAO {
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(
                    MoveCalculator.class,
                    (InstanceCreator<MoveCalculator>) type -> new KingMovesCalculator()
            )
            .create();


    public MySqlDataAccess() {
        try {
            DatabaseManager.createDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        executeUpdate("DELETE FROM auth");
        executeUpdate("DELETE FROM game");
        executeUpdate("DELETE FROM user");
        //reset game IDs
        executeUpdate("ALTER TABLE game AUTO_INCREMENT = 1");
    }


    public UserData createUser(UserData user) throws DataAccessException {
        var hashed = BCrypt.hashpw(user.password(), BCrypt.gensalt());
        var sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?)";
        executeUpdate(sql, user.username(), hashed, user.email());
        return new UserData(user.username(), hashed, user.email());
    }


    public UserData getUser(String username) throws DataAccessException {
        var sql = "SELECT username, password, email FROM user WHERE username = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new UserData(rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("getUser failed: " + e.getMessage());
        }
    }


    public int createGame(String gameName) throws DataAccessException {
        var game = new chess.ChessGame();
        var json = GSON.toJson(game);
        var sql = "INSERT INTO game (gameName, json) VALUES (?, ?)";
        return executeUpdate(sql, gameName, json);
    }


    public GameData getGame(int gameID) throws DataAccessException {
        var sql = """
                    SELECT gameID, whiteUsername, blackUsername, gameName, json
                    FROM game WHERE gameID = ?
                """;
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, gameID);
            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                var gameJson = rs.getString("json");
                var game = GSON.fromJson(gameJson, chess.ChessGame.class);
                return new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        game
                );
            }
        } catch (SQLException e) {
            throw new DataAccessException("getGame failed: " + e.getMessage());
        }
    }


    public Collection<GameData> listGames() throws DataAccessException {
        var out = new ArrayList<GameData>();
        var sql = "SELECT gameID, whiteUsername, blackUsername, gameName, json FROM game";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            while (rs.next()) {
                var game = GSON.fromJson(rs.getString("json"), chess.ChessGame.class);
                out.add(new GameData(
                        rs.getInt("gameID"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        game
                ));
            }
            return out;
        } catch (SQLException e) {
            throw new DataAccessException("listGames failed: " + e.getMessage());
        }
    }


    public void updateGame(GameData updated) throws DataAccessException {
        var json = GSON.toJson(updated.game());
        var sql = """
                    UPDATE game
                    SET whiteUsername = ?, blackUsername = ?, gameName = ?, json = ?
                    WHERE gameID = ?
                """;
        int rows = executeUpdate(sql,
                updated.whiteUsername(),
                updated.blackUsername(),
                updated.gameName(),
                json,
                updated.gameID());
        if (rows == 0) {
            throw new DataAccessException("updateGame failed: game not found");
        }
    }


    public void createAuth(AuthData auth) throws DataAccessException {
        var sql = "INSERT INTO auth (authToken, username) VALUES (?, ?)";
        executeUpdate(sql, auth.authToken(), auth.username());
    }


    public AuthData getAuth(String authToken) throws DataAccessException {
        var sql = "SELECT authToken, username FROM auth WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, authToken);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(rs.getString("authToken"), rs.getString("username"));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new DataAccessException("getAuth failed: " + e.getMessage());
        }
    }


    public void deleteAuth(String authToken) throws DataAccessException {
        int rows = executeUpdate("DELETE FROM auth WHERE authToken = ?", authToken);
        if (rows == 0) {
            throw new DataAccessException("deleteAuth failed: token not found");
        }
        executeUpdate("DELETE FROM auth WHERE authToken = ?", authToken);
    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(statement, RETURN_GENERATED_KEYS)) {

            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof String p) {
                    preparedStatement.setString(i + 1, p);
                } else if (param instanceof Integer p) {
                    preparedStatement.setInt(i + 1, p);
                } else if (param == null) {
                    preparedStatement.setNull(i + 1, NULL);
                } else {
                    preparedStatement.setObject(i + 1, param);
                }
            }

            int updated = preparedStatement.executeUpdate();

            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            return updated;
        } catch (SQLException e) {
            throw new DataAccessException("unable to execute update: " + e.getMessage(), e);
        }
    }


    private void configureDatabase() {
        System.out.println("This was run");
        try (var conn = DatabaseManager.getConnection()) {
            try (var st = conn.createStatement()) {
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS user (
                              username    VARCHAR(255) PRIMARY KEY,
                              password    VARCHAR(255) NOT NULL,
                              email       VARCHAR(255)
                            )
                        """);
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS auth (
                              authToken   VARCHAR(255) PRIMARY KEY,
                              username    VARCHAR(255) NOT NULL,
                              FOREIGN KEY (username) REFERENCES user(username)
                                ON DELETE CASCADE
                            )
                        """);
                st.executeUpdate("""
                            CREATE TABLE IF NOT EXISTS game (
                              gameID        INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                              whiteUsername VARCHAR(255),
                              blackUsername VARCHAR(255),
                              gameName      VARCHAR(255) NOT NULL,
                              json          MEDIUMTEXT
                            )
                        """);
            }
        } catch (Exception e) {
            System.out.print("try catch failed under the configure database function");
            throw new RuntimeException("configureDatabase failed", e);
        }
    }
}
