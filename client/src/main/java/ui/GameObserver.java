package ui;
import chess.ChessGame;

public interface GameObserver {
    void onLoadGame(ChessGame game);
    void onNotification(String message);
    void onError(String message);
}
