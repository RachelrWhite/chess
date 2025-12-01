import chess.*;
import ui.PreloginClient;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        //System.out.println("♕ Welcome to Chess. Sign in to start. ♕ " + piece);
        System.out.println("♕ Welcome to Chess. Sign in to start. ♕ ");
        String serverUrl = "http://localhost:8080";
        if (args.length == 1) {
            serverUrl = args[0];
        }

        try {
            new PreloginClient(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());

        }
    }
}
