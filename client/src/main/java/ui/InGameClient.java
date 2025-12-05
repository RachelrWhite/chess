package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.Scanner;

//import static ui.BoardDrawer.drawBoard;

public class InGameClient implements WebSocketFacade.ServerMessageHandler {
    private final WebSocketFacade webSocket;
    private ChessGame game;
    private final ChessGame.TeamColor playerColor;

    public InGameClient(String serverUrl, String authToken, int gameID, ChessGame.TeamColor playerColor) throws IOException {
        this.playerColor = playerColor;
        this.webSocket = new WebSocketFacade(serverUrl, authToken, gameID, this);
    }

    //REPL ________________________________REPL
    public void run() {
        System.out.println("You have Entered the Game! Type 'help' for commands.");
        Scanner scanner = new Scanner(System.in);
        var result = "";
        while (!result.equals("leave-game")) {
            System.out.print("[inGame] > ");
            String line = scanner.nextLine();

            try {
                result = eval(line);
                if (!result.isBlank() && !result.equals("leave-game")) {
                    System.out.println(result);
                }
            } catch (Throwable e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

        try {
            webSocket.close();
        } catch (IOException ignored) {}
    }


    public String eval(String input) {
        String[] raw = input.trim().split("\\s+");
        String cmd = (raw.length > 0 && !raw[0].isBlank()) ? raw[0].toLowerCase() : "help";
        String[] params = java.util.Arrays.copyOfRange(raw, 1, raw.length);

        return switch (cmd) {
            case "h", "help" -> inGameHelp();
            case "redraw", "print" -> redrawBoard();
            case "m", "move" -> moveCommand(params);
            case "leave" -> leaveGame();
            case "resign" -> resignGame();
            case "quit", "exit" -> "leave-game";
            default -> "Unknown command. Type 'help'.";
        };
    }


    @Override
    public void handle(ServerMessage message) {
        System.out.println("Got WS message: " + message.getServerMessageType());

        switch (message.getServerMessageType()) {
            case LOAD_GAME -> {
                System.out.println("LOAD_GAME got called");
                this.game = message.getGame();
                redrawBoard();
            }
            case NOTIFICATION -> System.out.println(message.getMessage());
            case ERROR -> System.out.println("Server error: " + message.getErrorMessage());
        }
    }


    private String redrawBoard() {
        System.out.println("this is calling the redrawBoard function game " + game);
        if (game == null) {
            return "Waiting for game state from server...";
        }

        boolean whitePerspective = (playerColor != ChessGame.TeamColor.BLACK);
        String boardString = BoardDrawer.drawGame(game, whitePerspective);
        System.out.print(boardString);

        return "";
    }


    private String moveCommand(String[] params) {
        if (params.length < 2) {
            return "usage: move <from> <to> [promotion]";
        }

        ChessPosition from = parsePosition(params[0]);
        ChessPosition to = parsePosition(params[1]);

        if (from == null || to == null) {
            return "Invalid squares. Example: move e2 e4";
        }

        ChessPiece.PieceType promo = null;
        if (params.length >= 3) {
            promo = parsePromotion(params[2]);
            if (promo == null) {
                return "Invalid promotion piece. Use q, r, b, or n.";
            }
        }

        ChessMove move = new ChessMove(from, to, promo);
        try {
            webSocket.makeMove(move);
        } catch (IOException e) {
            return "Error sending move: " + e.getMessage();
        }

        return "Sent move " + params[0] + " -> " + params[1]
                + (promo != null ? " (promote to " + promo + ")" : "");
    }

    private String leaveGame() {
        try {
            webSocket.leave();
        } catch (IOException e) {
            return "Error sending leave: " + e.getMessage();
        }
        return "leave-game";
    }

    private String resignGame() {
        try {
            webSocket.resign();
        } catch (IOException e) {
            return "Error sending resign: " + e.getMessage();
        }
        return "leave-game";
    }

    // all them helpers

    private ChessPosition parsePosition(String s) {
        if (s == null || s.length() != 2) {
            return null;
        }
        char fileChar = Character.toLowerCase(s.charAt(0)); // a-h
        char rankChar = s.charAt(1);                        // 1-8

        if (fileChar < 'a' || fileChar > 'h') return null;
        if (rankChar < '1' || rankChar > '8') return null;

        int col = fileChar - 'a' + 1;  // a->1
        int row = rankChar - '0';      // '1'->1

        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType parsePromotion(String s) {
        if (s == null || s.isEmpty()) return null;
        return switch (Character.toLowerCase(s.charAt(0))) {
            case 'q' -> ChessPiece.PieceType.QUEEN;
            case 'r' -> ChessPiece.PieceType.ROOK;
            case 'b' -> ChessPiece.PieceType.BISHOP;
            case 'n' -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }

    private String inGameHelp() {
        return """
                - In-game Commands:
                - help
                - redraw
                - move <from> <to> [promotionPiece(q|r|b|n)]
                - leave
                - resign
                """;
    }
}


