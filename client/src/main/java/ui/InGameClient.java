package ui;

import chess.ChessGame;

public class InGameClient {
    private final String authToken;
    private final int gameID;
    private ChessGame game;
    private GameplaySocket socket;

    public InGameClient(String authToken, int gameID) {
        this.authToken = authToken;
        this.gameID = gameID;
    }
    public void run() {
        this.socket = new GameplaySocket(authToken, gameID, this);

        // wait for LOAD_GAME (socket sets this.game)
        // then draw the board

        gameplayLoop();

        socket = new GameplayWebSocket(authToken, gameID, this);

    }
}
