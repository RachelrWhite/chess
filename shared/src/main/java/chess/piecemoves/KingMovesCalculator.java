package chess.piecemoves;

import chess.*;
import java.util.*;

public class KingMovesCalculator implements MoveCalculator{
    public static boolean isBlocked(int row, int col, ChessBoard board, ChessPiece piece) {
        ChessPiece testPiece = board.getPiece(new ChessPosition(row, col));
        if (testPiece == null) {
            return false;
        }
        else if (testPiece.getTeamColor().equals(piece.getTeamColor())) {
            return true;
        }
        return false;
    }
    private static final int[][] KING_OFFSETS = {
            {-1, +1}, {+1, +0}, {+1, +1}, {+0, -1},
            {+0, +1}, {-1, -1}, {-1, +0}, {+1, -1}
    };

    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition from, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        int r = from.getRow();
        int c = from.getColumn();
        for (int[] d : KING_OFFSETS) {
            int r2 = r + d[0];
            int c2 = c + d[1];
            if (ChessPiece.validPosition(r2, c2) && !isBlocked(r2, c2, board, piece)) {
                moves.add(new ChessMove(from, new ChessPosition(r2, c2), null));
            }
        }

        return moves;
    }
}