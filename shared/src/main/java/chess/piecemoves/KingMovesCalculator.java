package chess.piecemoves;

import chess.*;
import java.util.*;

public class KingMovesCalculator implements MoveCalculator{
    public static boolean isBlocked(int kingRow, int kingCol, ChessBoard board, ChessPiece piece) {
        ChessPiece testPiece = board.getPiece(new ChessPosition(kingRow, kingCol));
        if (testPiece == null) {
            return false;
        }
        else return testPiece.getTeamColor().equals(piece.getTeamColor());
    }
    private static final int[][] KING_OFFSETS = {
            {-1, +1}, {+1, +0}, {+1, +1}, {+0, -1},
            {+0, +1}, {-1, -1}, {-1, +0}, {+1, -1}
    };

    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition from, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        int colKing = from.getColumn();
        int rowKing = from.getRow();
        for (int[] d : KING_OFFSETS) {
            int c2King = colKing + d[1];
            int r2King = rowKing + d[0];
            if (ChessPiece.validPosition(r2King, c2King) && !isBlocked(r2King, c2King, board, piece)) {
                moves.add(new ChessMove(from, new ChessPosition(r2King, c2King), null));
            }
        }

        return moves;
    }
}