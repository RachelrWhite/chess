package websocket;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
public class ConnectionManager {
    public final HashMap<Session, GameInfo> connections = new HashMap<>();

    public void add(Session session, GameInfo info) {
        connections.put(session, info);
    }

    public void remove(Session session) {
        connections.remove(session);
    }

    public GameInfo getInfo(Session session) {
        return connections.get(session);
    }

    // this one is going to broadcast to every player in the game
    public void broadcastToGame(int gameID, ServerMessage message, Session exclude) throws IOException {
        String json = new Gson().toJson(message);
        for (var entry : connections.entrySet()) {
            var session = entry.getKey();
            var info = entry.getValue();
            if (!session.isOpen()) continue;
            if (info.gameID() != gameID) continue;
            if (exclude != null && session.equals(exclude)) continue;
            session.getRemote().sendString(json);
        }
    }
}

