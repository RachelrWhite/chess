package server;

import dataaccess.*;
import service.AuthService;
import service.GameService;
import service.UserService;
import com.google.gson.Gson;
import io.javalin.Javalin;
import io.javalin.http.Context;
import model.*;

import java.util.Collections;
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
                .delete("/session", this::logout)
                .post("/game", this::createGame)
//                .get("/game/{authToken}", this::listGames)
                .put("/game", this::joinGame)
                .delete("/db", this::clear);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

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
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(result));
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

    private void logout(Context ctx) {
        Gson gson = new Gson();
        String token = ctx.header("Authorization");

        try {
            auth.logout(token);
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Collections.emptyMap()));
            return;

        } catch (DataAccessException e) {
            String msg = e.getMessage();
            int status = 500;
            String errorMessage = "Error: " + msg;

            if (msg != null && msg.equals("unauthorized")) {
                status = 401;
                errorMessage = "Error: unauthorized";
            }

            ctx.status(status);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(Collections.singletonMap("message", errorMessage)));

        }
    }

    private void createGame(Context ctx) throws DataAccessException {
        Gson gson = new Gson();

        String token = ctx.header("Authorization");
        CreateGameRequest request = gson.fromJson(ctx.body(), CreateGameRequest.class);

        try {
            int id = game.createGame(token, request.gameName());
            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(new CreateGameResult(id)));

        } catch (DataAccessException e){
            String m = e.getMessage();
            int status;
            String bodyMessage;

            if (m == null) {
                status = 500;
                bodyMessage = "Error: unknown";
            } else if (m.equals("unauthorized")) {
                status = 401;
                bodyMessage = "Error: unauthorized";
            } else if (m.equals("bad request")) {
                status = 400;
                bodyMessage = "Error: bad request";
            } else {
                status = 500;
                bodyMessage = "Error: " + m;
            }

            ctx.status(status);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(java.util.Collections.singletonMap("message", bodyMessage)));

        }
    }

    private void joinGame(Context ctx) throws DataAccessException {
        Gson gson = new Gson();
        String token = ctx.header("Authorization");
        JoinGameRequest request = gson.fromJson(ctx.body(), JoinGameRequest.class);

        try {
            game.joinGame(token, request.playerColor(), request.gameID());

            ctx.status(200);
            ctx.contentType("application/json");
            ctx.result(gson.toJson(java.util.Collections.emptyMap()));

        } catch (DataAccessException e) {
            String m = e.getMessage();
            int status;
            String bodyMessage;

            if (m == null) {
                status = 500;
                bodyMessage = "Error: unknown";
            } else if (m.equals("unauthorized")) {
                status = 401;
                bodyMessage = "Error: unauthorized";
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

    private void clear(Context ctx) throws DataAccessException {
        new Gson().fromJson(ctx.body(), ClearRequest.class);
        user.clear();
        game.clear();
        auth.clear();
        ctx.status(200);
    }

}




