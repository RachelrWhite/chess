package server;

import dataaccess.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.*;

import java.util.Map;


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
        auth = new AuthService(authDataAccess, userDataAccess);
        game = new GameService(gameDataAccess, authDataAccess);

        // Register your endpoints and exception handlers here
        javalin = Javalin.create(config -> config.staticFiles.add("web"))
                .post("/user", this::register)
                .post("/session", this::login)
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

//    private void register(Context ctx) {
//        UserData userData = new Gson().fromJson(ctx.body(), UserData.class);
//        RegisterResult registerResult = null;
//        try {
//            registerResult = user.register(new RegisterRequest(userData.username(), userData.password(), userData.email()));
//        } catch (DataAccessException e) {
//            ctx.status(403);
//            ctx.json("message: Error");
//        }
//        ctx.status(200);
//        ctx.json(new Gson().toJson(registerResult));
//    }

    private void register(Context ctx) {
        Gson gson = new Gson();
        UserData userData = gson.fromJson(ctx.body(), UserData.class);

        try {
            RegisterResult result = user.register(new RegisterRequest(userData.username(), userData.password(), userData.email()));
            ctx.status(200)
                    .contentType("application/json")
                    .result(gson.toJson(result));

        } catch (DataAccessException e) {
            String m = e.getMessage();
            int status;
            String bodyMessage;

            if (m == null) {
                status = 500;
                bodyMessage = "Error: unknown";
            } else if (m.equals("bad request")) {
                status = 400;
                bodyMessage = "Error: bad request";
            } else if (m.equals("already taken")) {
                status = 403;
                bodyMessage = "Error: already taken";
            } else {
                status = 500;
                bodyMessage = "Error: " + m;
            }

            ctx.status(status);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(java.util.Collections.singletonMap("message", bodyMessage)));
        }
    }

    private void login(Context ctx) {
        Gson gson = new Gson();
        LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);

        try {
            LoginResult result = auth.login(request);
            ctx.status(200)
                    .contentType("application/json")
                    .result(gson.toJson(result));
            return;
        } catch (DataAccessException e) {
            int status =
                    "bad request".equals(e.getMessage()) ? 400 :
                            "unauthorized".equals(e.getMessage()) ? 401 : 500;

            String msg =
                    status == 400 ? "Error: bad request" :
                            status == 401 ? "Error: unauthorized" :
                                    "Error: " + e.getMessage();

            ctx.status(status)
                    .contentType("application/json")
                    .result(gson.toJson(java.util.Collections.singletonMap("message", msg)));
            return;
        }
    }

    private void clear(Context ctx) throws DataAccessException {
        new Gson().fromJson(ctx.body(), ClearRequest.class);
        user.clear();
        game.clear();
        auth.clear();
        ctx.status(200);
    }

}




