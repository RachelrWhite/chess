package dataaccess;

import model.AuthData;

//all the functions that have to do with the auth token and also clear for some reason
public interface AuthDAO {
    void clear() throws DataAccessException;

    void createAuth(AuthData auth) throws DataAccessException;

    AuthData getAuth(String authToken) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

}
