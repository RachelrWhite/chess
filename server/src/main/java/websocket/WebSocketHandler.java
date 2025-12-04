package websocket;
import chess.ChessGame;
import com.google.gson.Gson;
//import exception.ResponseException;
import dataaccess.MySqlDataAccess;
import io.javalin.websocket.WsCloseContext;
import io.javalin.websocket.WsCloseHandler;
import io.javalin.websocket.WsConnectContext;
import io.javalin.websocket.WsConnectHandler;
import io.javalin.websocket.WsMessageContext;
import io.javalin.websocket.WsMessageHandler;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import websocket.commands.*;
import websocket.messages.ServerMessage;
//import messages.
//import webSocketMessages.Notification;
import java.io.IOException;


public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    MySqlDataAccess data;
    private final ConnectionManager connections = new ConnectionManager();

    public WebSocketHandler(MySqlDataAccess data) {
        this.data = data;
    }

    @Override
    public void handleConnect(WsConnectContext ctx) {
        System.out.println("Websocket connected");
        ctx.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctx) {
        System.out.println("HandleMessage got called");
        try {
            //var command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            var json = ctx.message();
            var command = new Gson().fromJson(json, UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), command.getGameID(), ctx.session);
                case MAKE_MOVE -> makeMove(ctx, json);
                //case LEAVE -> leave(ctx, json);
                //case RESIGN -> resign(ctx, json);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String authToken, int gameID, Session session) {
        System.out.println("CONNECT");
        try {
            System.out.println("This is the authdata" + authToken);
            //get that auth validated fr fr
            AuthData authData = data.getAuth(authToken);
            if (authData == null) {
                System.out.println("Invalid auth token (WebSocketHandler file CONNECT)");
                connections.send(session, ServerMessage.error("Error: invalid auth token"));
                session.close();
                return;
            }
            String username = authData.username();

            //validate the game
            GameData gameData = data.getGame(gameID);
            if (gameData == null) {
                connections.send(session, ServerMessage.error("Error: game does not exist"));
                session.close();
                return;
            }
            //this is where we see what the role is (if they are WHITE,BLACK,observer)
            //I made this so the observer stays null - could need to change that so they see the white prespective
            ChessGame.TeamColor color = null;
            if (username.equals(gameData.whiteUsername())) {
                color = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                color = ChessGame.TeamColor.BLACK;
            }

            //throw that game info into the connections hashmap - this is lowkey genius
            GameInfo info = new GameInfo(gameID, username, color);
            connections.add(session, info);

            //this is where we send LOAD_GAME back to this client
            ChessGame game = gameData.game();
            connections.send(session, ServerMessage.loadGame(game));

            //now we send the NOTIFICATION to others in the game(all the commonfolk)
            String role; //this is how i'm going to keep track of the people who are observing or not in a game
            if (color == null) {
                role = "as an observer";
            } else {
                role = "as " + color.toString().toLowerCase();
            }

            String joinSummary = username + " joined the game " + role;
            connections.broadcastToGame(gameID, ServerMessage.notification(joinSummary), session);

            System.out.println("Connected sessions: " + connections.connections.size());
        } catch (Exception ex) {
            System.out.println("WebsocketHandlerConnect Function Error");
            ex.printStackTrace(); //this one builds character
        }
    }

    private void makeMove(WsMessageContext ctx, String json) {

    }
}
