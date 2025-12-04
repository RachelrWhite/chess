package websocket;

import chess.ChessGame;

public class GameInfo {
    private final int gameID;
    private final String username;
    private final ChessGame.TeamColor playerColor;

    public GameInfo(int gameID, String username, ChessGame.TeamColor playerColor) {
        this.gameID = gameID;
        this.username = username;
        this.playerColor = playerColor;

    }

    public int gameID() { return gameID; }
    public String username() { return username; }
    public ChessGame.TeamColor playerColor() { return playerColor; }
}
