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
                case LEAVE -> leave(ctx, json);
                case RESIGN -> resign(ctx, json);
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
            System.out.println("GameData for gameID " + gameID + " is: " + gameData);
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
        System.out.println("MAKE_MOVE");
        Session session = ctx.session;

        try {
            MakeMoveCommand command = new Gson().fromJson(json, MakeMoveCommand.class);

            //get who the person is
            GameInfo gameInfo = connections.getInfo(session);
            if (gameInfo == null) {
                connections.send(session, ServerMessage.error("Error: not connected to a game"));
                return;
            }

            int gameID = gameInfo.gameID();
            String username = gameInfo.username();
            ChessGame.TeamColor color = gameInfo.playerColor(); //this is gonna be null if it just a mere observer

            //This will load the game from DB. amen
            GameData gameData = data.getGame(gameID);
            if (gameData == null) {
                connections.send(session, ServerMessage.error("Error: game does not exist"));
                return;
            }
            ChessGame game = gameData.game();
            if (color == null) {
                connections.send(session, ServerMessage.error("Error: observers cannot move pieces"));
                return;
            }

            if (game.getTeamTurn() != color) {
                connections.send(session, ServerMessage.error("Error: it is not your turn"));
                return;
            }

            // this is where they can give it a shot to try and move - riveting
            try {
                game.makeMove(command.getMove());
            } catch (Exception e) {
                connections.send(session, ServerMessage.error("Error: illegal move"));
                return;
            }

            //this line is intended to save the updated gameData to the database so fingers crossed on this one
            data.updateGame(gameData);

            connections.broadcastToGame(gameID, ServerMessage.loadGame(game), null);

            String moveSummary = username + " moved " + command.getMove();
            connections.broadcastToGame(gameID, ServerMessage.notification(moveSummary), session);

            // I dont really know if we need these but we will see (extra notificaitons)
            boolean whiteInCheck = game.isInCheck(ChessGame.TeamColor.WHITE);
            boolean blackInCheck = game.isInCheck(ChessGame.TeamColor.BLACK);

            if (game.isInCheckmate(ChessGame.TeamColor.WHITE)) {
                connections.broadcastToGame(gameID, ServerMessage.notification("White is in checkmate"), null);
            } else if (game.isInCheckmate(ChessGame.TeamColor.BLACK)) {
                connections.broadcastToGame(gameID, ServerMessage.notification("Black is in checkmate"), null);
            } else if (whiteInCheck || blackInCheck) {
                String who = whiteInCheck ? "White" : "Black";
                connections.broadcastToGame(gameID, ServerMessage.notification(who + " is in check"), null);
            }

        } catch (Exception ex) {
            System.out.println("WebSocketHandler makeMove error");
            ex.printStackTrace();
            try {
                connections.send(session, ServerMessage.error("Error: internal server error"));
            } catch (IOException ignored) {}
        }

    }

    private void leave(WsMessageContext ctx, String json) {
        System.out.println("LEAVE");
        Session session = ctx.session;
        try {
            UserGameCommand command = new Gson().fromJson(json, UserGameCommand.class);

            GameInfo gameInfo = connections.getInfo(session);
            if (gameInfo == null) {
                connections.send(session, ServerMessage.error("Error: not connected to a game"));
                return;
            }

            int gameID = gameInfo.gameID(); //this gameID is coming in CLUTCH!!!
            String username = gameInfo.username();

            GameData gameData = data.getGame(gameID);
            if (gameData == null) {
                connections.send(session, ServerMessage.error("Error: game does not exist"));
                return;
            }


            //this is where if i need to clear their spot in DB

            connections.remove(session);

            String msg = username + " left the game :(";
            connections.broadcastToGame(gameID, ServerMessage.notification(msg), session);

        } catch (Exception e) {
            e.printStackTrace();
            //lowkey i dont know if this is going to break everything but we must go forth
            try {
                connections.send(session, ServerMessage.error("Error: internal erro :("));
            } catch (Exception ignored) {
            }
        }

    }

    private void resign(WsMessageContext ctx, String json) {
        Session session = ctx.session;

        try {
            UserGameCommand command = new Gson().fromJson(json, UserGameCommand.class);

            GameInfo info = connections.getInfo(session);
            if (info == null) {
                connections.send(session, ServerMessage.error("Error: not connected to a game"));
                return;
            }

            int gameID = info.gameID();
            String username = info.username();

            GameData gameData = data.getGame(gameID);
            if (gameData == null) {
                connections.send(session, ServerMessage.error("Error: game does not exist"));
                return;
            }

            // Mark game over in DB somehow, like:
            // gameData = gameData.withGameOver(true).withWinner(...);
            // data.updateGame(gameData);

            String msg = username + " resigned the game";
            connections.broadcastToGame(gameID, ServerMessage.notification(msg), null);

        } catch (Exception ex) {
            ex.printStackTrace();
            try {
                connections.send(session, ServerMessage.error("Error: internal server error"));
            } catch (IOException ignored) {}
        }
    }
}
