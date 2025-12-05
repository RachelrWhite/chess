package ui;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;


public class BoardDrawer {

    private BoardDrawer() {}

    //this is a functions I am making for when the board is full
    public static String drawGame(ChessGame game, boolean whitePerspective, java.util.Set<ChessPosition> highlights) {
        // Board[row][col], row 0 = rank 8, row 7 = rank 1
        String[][] board = new String[8][8];
        boolean[][] highlightGrid = new boolean[8][8];

        // Start with all empty squares
        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = EscapeSequences.EMPTY;
                highlightGrid[r][c] = false;
            }
        }

        var chessBoard = game.getBoard();
        for (int rank = 1; rank <= 8; rank++) {
            for (int file = 1; file <= 8; file++) {
                ChessPosition pos = new ChessPosition(rank, file);
                ChessPiece piece = chessBoard.getPiece(pos);
                if (piece != null) {
                    int rowIndex = 8 - rank;
                    int colIndex = file - 1;
                    board[rowIndex][colIndex] = pieceToEscape(piece);
                }
            }
        }

        if (highlights != null) {
            for (ChessPosition pos : highlights) {
                int rank = pos.getRow();
                int file = pos.getColumn();
                int rowIndex = 8 - rank;
                int colIndex = file - 1;
                if (rowIndex >= 0 && rowIndex < 8 && colIndex >= 0 && colIndex < 8) {
                    highlightGrid[rowIndex][colIndex] = true;
                }
            }
        }

        return drawBoard(board, whitePerspective, highlightGrid);
    }

    public static String drawGame(ChessGame game, boolean whitePerspective) {
        return drawGame(game, whitePerspective, null);
    }


    //this function is also for phase 6 - it allwos the
    private static String pieceToEscape(ChessPiece piece) {
        boolean w = piece.getTeamColor() == ChessGame.TeamColor.WHITE;

        return switch (piece.getPieceType()) {
            case KING   -> w ? EscapeSequences.WHITE_KING   : EscapeSequences.BLACK_KING;
            case QUEEN  -> w ? EscapeSequences.WHITE_QUEEN  : EscapeSequences.BLACK_QUEEN;
            case ROOK   -> w ? EscapeSequences.WHITE_ROOK   : EscapeSequences.BLACK_ROOK;
            case BISHOP -> w ? EscapeSequences.WHITE_BISHOP : EscapeSequences.BLACK_BISHOP;
            case KNIGHT -> w ? EscapeSequences.WHITE_KNIGHT : EscapeSequences.BLACK_KNIGHT;
            case PAWN   -> w ? EscapeSequences.WHITE_PAWN   : EscapeSequences.BLACK_PAWN;
        };
    }



    private static String drawBoard(String[][] board, boolean whitePerspective,
                                    boolean[][] highlightGrid) {
        StringBuilder sb = new StringBuilder();

        String[] filesWhite = {"a ","b ","c","d ","e ","f","g ","h"};
        String[] filesBlack = {"h ","g ","f","e ","d ","c","b ","a"};
        String[] files = whitePerspective ? filesWhite : filesBlack;

        // Top file labels
        sb.append("   ");
        for (String f : files) {
            sb.append(' ').append(f).append(' ');
        }
        sb.append('\n');

        if (whitePerspective) {
            // White at bottom: show rank 8 (boardRow 0) down to rank 1 (boardRow 7)
            for (int boardRow = 0; boardRow < 8; boardRow++) {
                int rank = 8 - boardRow;          // 8,7,...,1
                sb.append(' ').append(rank).append(' ');
                for (int boardCol = 0; boardCol < 8; boardCol++) {
                    boolean hl = (highlightGrid != null && highlightGrid[boardRow][boardCol]);
                    appendSquare(sb, board[boardRow][boardCol], boardRow, boardCol, hl);
                }
                sb.append(' ').append(rank).append('\n');
            }
        } else {
            // Black at bottom: from black's POV, top is rank 1 (boardRow 7), bottom is rank 8 (boardRow 0)
            for (int visualRow = 0; visualRow < 8; visualRow++) {
                int boardRow = 7 - visualRow;     // 7,6,...,0
                int rank = visualRow + 1;         // 1,2,...,8
                sb.append(' ').append(rank).append(' ');

                // Files h..a left→right: map visualCol 0..7 to boardCol 7..0
                for (int visualCol = 0; visualCol < 8; visualCol++) {
                    int boardCol = 7 - visualCol;
                    boolean hl = (highlightGrid != null && highlightGrid[boardRow][boardCol]);
                    appendSquare(sb, board[boardRow][boardCol], boardRow, boardCol, hl);
                }
                sb.append(' ').append(rank).append('\n');
            }
        }

        // Bottom file labels
        sb.append("   ");
        for (String f : files) {
            sb.append(' ').append(f).append(' ');
        }
        sb.append('\n');

        sb.append(EscapeSequences.RESET_TEXT_COLOR)
                .append(EscapeSequences.RESET_BG_COLOR);

        return sb.toString();
    }


    // Backward-compatible old signature:
    private static String drawBoard(String[][] board, boolean whitePerspective) {
        return drawBoard(board, whitePerspective, null);
    }


    private static void appendSquare(StringBuilder sb, String piece, int row, int col, boolean highlight) {
        boolean light = (row + col) % 2 == 0;

        String bg;
        if (highlight) {
            // pick a nice highlight color your EscapeSequences supports
            bg = EscapeSequences.SET_BG_COLOR_YELLOW;  // or GREEN, etc.
        } else {
            bg = light
                    ? EscapeSequences.SET_BG_COLOR_LIGHT_GREY
                    : EscapeSequences.SET_BG_COLOR_DARK_GREY;
        }

        String textColor = colorForPiece(piece);

        sb.append(bg)
                .append(textColor)
                .append(piece)
                .append(EscapeSequences.RESET_TEXT_COLOR)
                .append(EscapeSequences.RESET_BG_COLOR);
    }


    private static String colorForPiece(String piece) {
        if (piece == null || piece.equals(EscapeSequences.EMPTY)) {
            // this one was for the empty squares
            return EscapeSequences.SET_TEXT_COLOR_BLACK;
        }

        // White pieces
        if (piece.equals(EscapeSequences.WHITE_KING)   ||
                piece.equals(EscapeSequences.WHITE_QUEEN)  ||
                piece.equals(EscapeSequences.WHITE_ROOK)   ||
                piece.equals(EscapeSequences.WHITE_BISHOP) ||
                piece.equals(EscapeSequences.WHITE_KNIGHT) ||
                piece.equals(EscapeSequences.WHITE_PAWN)) {
            // if TAs get mad change this to WHITE
            //return EscapeSequences.SET_TEXT_COLOR_RED;
            return EscapeSequences.SET_TEXT_COLOR_WHITE;
        }

        // Black pieces
        //these used to be blue - quite classy so change if the TAs get mad
        return EscapeSequences.SET_TEXT_COLOR_BLUE;
    }
}
