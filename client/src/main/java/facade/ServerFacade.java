package facade;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;

import java.net.*;
import java.net.http.*;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;


public class ServerFacade {
    private final HttpClient client = HttpClient.newHttpClient();
    private final String serverUrl;

    public ServerFacade(String url) {
        serverUrl = url;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    // end points
    public AuthData register(String username, String password, String email) {
        var req  = buildRequest("POST", "/user", new RegisterRequest(username, password, email), null);
        var resp = sendRequest(req);
        if (resp == null) {
            return null;
        }
        var out  = handleResponse(resp, AuthRes.class);
        return (out == null) ? null : new AuthData(out.authToken, out.username);
    }

    public AuthData login(String username, String password) {
        var req  = buildRequest("POST", "/session", new LoginRequest(username, password), null);
        var resp = sendRequest(req);
        if (resp == null) {
            return null;
        }
        var out  = handleResponse(resp, AuthRes.class);
        return (out == null) ? null : new AuthData(out.authToken, out.username);
    }

    public void logout(String authToken) {
        var req  = buildRequest("DELETE", "/session", null, authToken);
        var resp = sendRequest(req);
        if (resp == null) {
            return;        // TEMP
        }
        handleResponse(resp, null);
    }

    public java.util.List<GameData> listGames(String authToken) {
        var req  = buildRequest("GET", "/game", null, authToken);
        var resp = sendRequest(req);
        if (resp == null) {
            return java.util.List.of();
        }
        var out  = handleResponse(resp, GamesListWrapper.class);
        return (out == null || out.games == null) ? java.util.List.of() : out.games;
    }

    public int createGame(String authToken, String gameName) {
        var req  = buildRequest("POST", "/game", new CreateGameRequest(gameName), authToken);
        var resp = sendRequest(req);
        if (resp == null) {
            return 0;
        }
        var out  = handleResponse(resp, CreateGameResult.class);
        return (out == null) ? 0 : out.gameID;
    }

    public void joinGame(String authToken, String playerColor, int gameID) {
        var request  = buildRequest("PUT", "/game", new JoinGameRequest(playerColor, gameID), authToken);
        var resp = sendRequest(request);
        if (resp == null) {
            return;
        }
        handleResponse(resp, null);
    }

    //this is for testing - don't really need for the project but can take out if redundant
    public void clear() {
        var req  = buildRequest("DELETE", "/db", null, null);
        var resp = sendRequest(req);
        if (resp == null) {
            return;
        }
        handleResponse(resp, null);
    }

    //other functions - mostly from petShop
    private static class GamesListWrapper {

        List<GameData> games;
    }

    private static class ErrorMsg { String message; }

    private static class RegisterRequest {
        String username, password, email;
        RegisterRequest(String u, String p, String e) { username=u; password=p; email=e; }
    }
    private static class LoginRequest {
        String username, password;
        LoginRequest(String u, String p) { username=u; password=p; }
    }
    private static class AuthRes {
        String username, authToken;
    }
    private static class CreateGameRequest {
        String gameName;
        CreateGameRequest(String n) { gameName=n; }
    }
    private static class CreateGameResult { int gameID; }
    private static class JoinGameRequest {
        String playerColor; int gameID;
        JoinGameRequest(String c, int id) { playerColor=c; gameID=id; }
    }

    private HttpRequest buildRequest(String method, String path, Object body, String authToken) {
        var builder = HttpRequest.newBuilder()
                .uri(URI.create(serverUrl + path))
                .method(method, makeRequestBody(body));

        if (body != null) {
            builder.header("Content-Type", "application/json");
        }
        if (authToken != null && !authToken.isBlank()) {
            builder.header("Authorization", authToken);
        }

        return builder.build();

    }

    private BodyPublisher makeRequestBody(Object request) {
        if (request != null) {
            return BodyPublishers.ofString(new Gson().toJson(request));
        } else {
            return BodyPublishers.noBody();
        }
    }

    private HttpResponse<String> sendRequest(HttpRequest request) {
        try {
            return client.send(request, BodyHandlers.ofString());
        } catch (Exception ex) {
//            throw new ResponseException(ResponseException.Code.ServerError, ex.getMessage());
            return null;
        }
    }

    private <T> T handleResponse(HttpResponse<String> response, Class<T> responseClass)  {
        var status = response.statusCode();
        if (!isSuccessful(status)) {
            var body = response.body();
            if (body != null) {
//                throw ResponseException.fromJson(body);
                return null;
            }
            return null;
            //throw new ResponseException(ResponseException.fromHttpStatusCode(status), "other failure: " + status);
        }
        if (responseClass != null) {
            return new Gson().fromJson(response.body(), responseClass);
        }
        return null;
    }

    private boolean isSuccessful(int status) {
        return status / 100 == 2;
    }


}
