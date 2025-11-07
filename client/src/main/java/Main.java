import chess.*;
import ui.Repl;

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
            new Repl(serverUrl).run();

        } catch (Throwable ex) {
            System.out.printf("Unable to start server: %s%n", ex.getMessage());

        }
    }
}


////all this stuff is going to be in the help command - for now it is just in here
//            System.out.println("Options: ");
//            System.out.println("Login as an existing user: \"l\", \"login\" <USERNAME> <PASSWORD>");
//            System.out.println("Register a new user: \"r\", \"register\" <USERNAME> <PASSWORD> <EMAIL>");
//            System.out.println("Exit the program: \"q\", \"quit\"");
//            System.out.println("Print this message: \"h\", \"help\"");