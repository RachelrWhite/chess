package ui;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import jakarta.websocket.Session;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import websocket.commands.UserGameCommand;
import websocket.commands.MakeMoveCommand;
import websocket.messages.ServerMessage;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final String authToken;
    private final int gameID;
    private final ServerMessageHandler messageHandler;
    private final Gson gson = new Gson();

    public interface ServerMessageHandler {
        void handle(ServerMessage message);
    }

    public WebSocketFacade(String baseUrl, String authToken, int gameID,
                           ServerMessageHandler messageHandler) throws IOException {
        this.authToken = authToken;
        this.gameID = gameID;
        this.messageHandler = messageHandler;

        try {
            String wsUrl = baseUrl.replaceFirst("^http", "ws") + "/ws";
            URI socketURI = new URI(wsUrl);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String json) {
                    System.out.println("RAW WS message: " + json);
                    ServerMessage msg = gson.fromJson(json, ServerMessage.class);
                    messageHandler.handle(msg);
                }
            });


            // CONNECT command
            UserGameCommand connect = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT,
                    this.authToken,
                    this.gameID
            );
            sendCommand(connect);

        } catch (DeploymentException | URISyntaxException ex) {
            throw new IOException("Failed to open WebSocket connection", ex);
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // no-op
    }

    // --- Public API ---

    public void makeMove(ChessMove move) throws IOException {
        MakeMoveCommand cmd = new MakeMoveCommand(
                authToken,
                gameID,
                move
        );
        sendCommand(cmd);
    }

    public void leave() throws IOException {
        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.LEAVE,
                authToken,
                gameID
        );
        sendCommand(cmd);
    }

    public void resign() throws IOException {
        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.RESIGN,
                authToken,
                gameID
        );
        sendCommand(cmd);
    }

    public void close() throws IOException {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    private void sendCommand(Object cmd) throws IOException {
        String json = gson.toJson(cmd);
        session.getBasicRemote().sendText(json);
    }
}
