package dataaccess;

import com.google.gson.Gson;
//import exception.DataAccessException;
import model.*;

import java.sql.*;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;
import static java.sql.Types.NULL;

public class MySqlDataAccess implements AuthDAO, GameDAO, UserDAO {
    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public void clear() throws DataAccessException {

    }


    public UserData createUser(UserData user) throws DataAccessException {
        var statement = "INSERT INTO user (username, password, email, json) VALUES (?, ?, ?, ?)";
        String json = new Gson().toJson(user);
        int id = executeUpdate(statement, user.username(), user.password(), user.email(), json);
        return new UserData(user.username(), user.password(), user.email());
    }


    public UserData getUser(String username) throws DataAccessException {
        return null;
    }


    public int createGame(String gameName) throws DataAccessException {
        return 0;
    }


    public GameData getGame(int gameID) throws DataAccessException {
        return null;
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

//    private final String[] createStatements = {
//            """
//            CREATE TABLE IF NOT EXISTS  user (
//              `username` VARCHAR(255) PRIMARY KEY,
//              `password` VARCHAR(255) NOT NULL,
//              `email` VARCHAR(255)
//            )
//            """
//    };


    private void configureDatabase() throws DataAccessException {
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
        } catch (SQLException e) {
            throw new DataAccessException("unable to configure database: " + e.getMessage());
        }
    }
}
