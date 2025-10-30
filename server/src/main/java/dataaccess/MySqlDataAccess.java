package dataaccess;

import com.google.gson.Gson;
//import exception.DataAccessException;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements AuthDAO, GameDAO, UserDAO {

    public MySqlDataAccess() {
        configureDatabase();
    }

    public void clear() throws DataAccessException {
        executeUpdate("DELETE FROM auth");
        executeUpdate("DELETE FROM game");
        executeUpdate("DELETE FROM user");
        //reset game IDs
        //executeUpdate("ALTER TABLE game AUTO_INCREMENT = 1");
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
        var json = new Gson().toJson(game);
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
                if (!rs.next()) return null;
                var gson = new Gson();
                var gameJson = rs.getString("json");
                var game = gson.fromJson(gameJson, chess.ChessGame.class);
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
        return List.of();
    }


    public void updateGame(GameData updated) throws DataAccessException {

    }


    public void createAuth(AuthData auth) throws DataAccessException {

    }


    public AuthData getAuth(String authToken) throws DataAccessException {
        return null;
    }


    public void deleteAuth(String authToken) throws DataAccessException {

    }

    private int executeUpdate(String statement, Object... params) throws DataAccessException {
        try (Connection conn = DatabaseManager.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                for (int i = 0; i < params.length; i++) {
                    Object param = params[i];
                    if (param instanceof String p) ps.setString(i + 1, p);
                    else if (param instanceof Integer p) ps.setInt(i + 1, p);
                    else if (param instanceof UserData p) ps.setString(i + 1, p.toString());
                    else if (param == null) ps.setNull(i + 1, NULL);
                }
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                return 0;
            }
        } catch (SQLException e) {
            throw new DataAccessException("unable to configure database: " + e.getMessage());
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
        }
    }
}
