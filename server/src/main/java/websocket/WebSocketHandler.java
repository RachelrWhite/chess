package websocket;
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
            var command = new Gson().fromJson(ctx.message(), UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> connect(command.getAuthToken(), ctx.session);
                case MAKE_MOVE -> MakeMove(command.visitorName(), ctx.session);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void handleClose(WsCloseContext ctx) {
        System.out.println("Websocket closed");
    }

    private void connect(String authToken, Session session) {
        System.out.println("CONNECT");
        try {
            System.out.println("This is the authdata" + authToken);
            AuthData authData = data.getAuth(authToken);
            if (authData == null) {
                System.out.println("Invalid auth token (WebSocketHandler file CONNECT)");
                session.close();
                return;
            }
            connections.add(session);
            System.out.println("Connected sessions: " + connections.connections.size());
        } catch (Exception ex) {
            System.out.println("WebsocketHandlerConnect Function Error");
        }
    }

    private void makeMove()
}
