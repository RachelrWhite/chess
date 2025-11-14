package ui;

public class BoardDrawer {

    private BoardDrawer() {}

    public static String drawInitial(boolean whitePerspective) {
        // Board[row][col], row 0 = rank 8, row 7 = rank 1
        String[][] board = new String[8][8];

        // throw in that empty board amen

        for (int r = 0; r < 8; r++) {
            for (int c = 0; c < 8; c++) {
                board[r][c] = EscapeSequences.EMPTY;
            }
        }

        // black back row
        String[] blackBack = {
                EscapeSequences.BLACK_ROOK,
                EscapeSequences.BLACK_KNIGHT,
                EscapeSequences.BLACK_BISHOP,
                EscapeSequences.BLACK_QUEEN,
                EscapeSequences.BLACK_KING,
                EscapeSequences.BLACK_BISHOP,
                EscapeSequences.BLACK_KNIGHT,
                EscapeSequences.BLACK_ROOK
        };

        String[] whiteBack = {
                EscapeSequences.WHITE_ROOK,
                EscapeSequences.WHITE_KNIGHT,
                EscapeSequences.WHITE_BISHOP,
                EscapeSequences.WHITE_QUEEN,
                EscapeSequences.WHITE_KING,
                EscapeSequences.WHITE_BISHOP,
                EscapeSequences.WHITE_KNIGHT,
                EscapeSequences.WHITE_ROOK
        };

        for (int c = 0; c < 8; c++) {
            board[0][c] = blackBack[c];                         // throw in that back row for black
            board[1][c] = EscapeSequences.BLACK_PAWN;           // black pawns (rank 7)
            board[6][c] = EscapeSequences.WHITE_PAWN;           // white pawns (rank 2)
            board[7][c] = whiteBack[c];                         // hit them with the back row for white
        }

        return drawBoard(board, whitePerspective);
    }

    private static String drawBoard(String[][] board, boolean whitePerspective) {
        StringBuilder sb = new StringBuilder();

        String[] filesWhite = {"a","b","c","d","e","f","g","h"};
        String[] filesBlack = {"h","g","f","e","d","c","b","a"};
        String[] files = whitePerspective ? filesWhite : filesBlack;

        // Top file labels
        sb.append("    ");
        for (String f : files) {
            sb.append(' ').append(f).append(' ');
        }
        sb.append('\n');

        if (whitePerspective) {
            // White at bottom: show rank 8 down to 1, files a..h left→right
            for (int row = 7; row >= 0; row--) {
                int rank = row + 1;
                sb.append(' ').append(rank).append(' ');
                for (int col = 0; col < 8; col++) {
                    appendSquare(sb, board[row][col], row, col);
                }
                sb.append(' ').append(rank).append('\n');
            }
        } else {
            // Black at bottom: show rank 1 up to 8, files h..a left→right
            for (int row = 0; row < 8; row++) {
                int rank = row + 1;
                sb.append(' ').append(rank).append(' ');
                for (int col = 7; col >= 0; col--) {
                    appendSquare(sb, board[row][col], row, col);
                }
                sb.append(' ').append(rank).append('\n');
            }
        }

        // Bottom file labels
        sb.append("    ");
        for (String f : files) {
            sb.append(' ').append(f).append(' ');
        }
        sb.append('\n');

        // Make sure colors are reset when we’re done
        sb.append(EscapeSequences.RESET_TEXT_COLOR)
                .append(EscapeSequences.RESET_BG_COLOR);

        return sb.toString();
    }

    private static void appendSquare(StringBuilder sb, String piece, int row, int col) {
        // this one right here has to be the light color fr fr
        boolean light = (row + col) % 2 == 0;

        String bg = light
                //this used to be light grey - change if the TAs get mad
                //the other one was dark grey - spelled with an e
                ? EscapeSequences.SET_BG_COLOR_YELLOW
                : EscapeSequences.SET_BG_COLOR_MAGENTA;

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
            return EscapeSequences.SET_TEXT_COLOR_RED;
        }

        // Black pieces
        //these used to be blue - quite classy so change if the TAs get mad
        return EscapeSequences.SET_TEXT_COLOR_BLUE;
    }
}
