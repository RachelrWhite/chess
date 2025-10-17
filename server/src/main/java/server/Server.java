package server;

import dataaccess.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.*;


public class Server {

    private final Javalin javalin;
    private final UserService user;
    private final GameService game;
    private final AuthService auth;
    UserDAO userDataAccess = new MemoryUserDAO();
    AuthDAO authDataAccess = new MemoryAuthDAO();
    GameDAO gameDataAccess = new MemoryGameDAO();


    public Server() {
        userDataAccess = new MemoryUserDAO();
        authDataAccess = new MemoryAuthDAO();
        gameDataAccess = new MemoryGameDAO();
        user = new UserService(userDataAccess, authDataAccess);
        auth = new AuthService(authDataAccess);
        game = new GameService(gameDataAccess, authDataAccess);

        // Register your endpoints and exception handlers here
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
//                .post("/session", this::login)
//                .post("/game/{authToken, gameName}", this::createGame)
//                .delete("/session/{authToken}", this::logout)
//                .get("/game/{authToken}", this::listGames)
//                .put("/game/{authToken, playerColor, gameID}", this::joinGame)
                 .delete("/db", this::clear);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void register(Context ctx)  {
        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
        RegisterResult registerResult = null;
        try {
            registerResult = user.register(new RegisterRequest(userData.username(), userData.password(), userData.email()));
        } catch (DataAccessException e) {
            ctx.status(403);
            ctx.json("message: Error");
        }
        ctx.status(200);
        ctx.json(new Gson().toJson(registerResult));
    }

    private void clear(Context ctx) throws DataAccessException {
        user.clear();
        game.clear();
        auth.clear();
        ctx.status(204);
    }

}




