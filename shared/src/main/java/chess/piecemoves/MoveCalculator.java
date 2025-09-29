package chess.piecemoves;


import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public interface MoveCalculator {

    static boolean validPosition(int rIndex, int cIndex) {
        return rIndex >= 1 && rIndex <= 8 && cIndex >= 1 && cIndex <= 8;
    }

    static boolean isEnemyPiece(int row, int col, ChessBoard board, ChessPiece piece) {
        ChessPiece tempPiece = board.getPiece(new ChessPosition(row, col));
        if (tempPiece == null) {
            return false;
        }
        if (!tempPiece.getTeamColor().equals(piece.getTeamColor())) {
            return true;
        } else {
            return false;
        }
    }

    Collection<ChessMove> calculate(ChessBoard board, ChessPosition from, ChessPiece piece);

    static Collection<ChessMove> continueInThisDirection(int rowDir, int colDir, int rowPos, int colPos, ChessPiece piece, ChessBoard board) {
        List<ChessMove> moves = new ArrayList<>();

        int r = rowPos + rowDir;
        int c = colPos + colDir;

        while (validPosition(r, c)) {

            ChessPiece target = board.getPiece(new ChessPosition(r, c));

            if (target == null) { // empty space
                moves.add(new ChessMove(new ChessPosition(rowPos, colPos), new ChessPosition(r, c), null));
            } else if (target.getTeamColor() != piece.getTeamColor()) { // opposite team
                moves.add(new ChessMove(new ChessPosition(rowPos, colPos), new ChessPosition(r, c), null));
                break;
            } else { // same team
                break;
            }
            r += rowDir;
            c += colDir;
        }
        return moves;
    }

}


