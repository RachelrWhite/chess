package ui;

import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import jakarta.websocket.Session;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final String authToken;
    private final int gameID;
    private final ServerMessageHandler messageHandler;
    private final Gson gson = new Gson();

    // Your UI / gameplay code implements this
    public interface ServerMessageHandler {
        void handle(ServerMessage message);
    }

    public WebSocketFacade(String baseUrl,
                           String authToken,
                           int gameID,
                           ServerMessageHandler messageHandler) throws IOException {
        this.authToken = authToken;
        this.gameID = gameID;
        this.messageHandler = messageHandler;

        try {
            // http://localhost:8080  ->  ws://localhost:8080/ws
            String wsUrl = baseUrl.replaceFirst("^http", "ws") + "/ws";
            URI socketURI = new URI(wsUrl);

            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, socketURI);

            // Incoming messages from the server
            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String json) {
                    ServerMessage msg = gson.fromJson(json, ServerMessage.class);
                    messageHandler.handle(msg);
                }
            });

            // Immediately send CONNECT once the socket is up
            UserGameCommand connect = new UserGameCommand(
                    UserGameCommand.CommandType.CONNECT,
                    this.authToken,
                    this.gameID
            );
            sendCommand(connect);

        } catch (DeploymentException | URISyntaxException ex) {
            // Wrap into a normal IOException for the caller
            throw new IOException("Failed to open WebSocket connection", ex);
        }
    }

    // Endpoint requires this, but we don't need to do anything here
    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        // no-op
    }

    // --- Public API for your gameplay UI ---

    public void makeMove(ChessMove move) throws IOException {
        UserGameCommand cmd = new UserGameCommand(
                UserGameCommand.CommandType.MAKE_MOVE,
                authToken,
                gameID
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

    // --- Helper ---

    private void sendCommand(UserGameCommand cmd) throws IOException {
        String json = gson.toJson(cmd);
        session.getBasicRemote().sendText(json);
    }
}
