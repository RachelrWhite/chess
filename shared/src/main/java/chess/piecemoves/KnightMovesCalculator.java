package chess.piecemoves;


import chess.*;
import java.util.*;
public class KnightMovesCalculator implements MoveCalculator {
    public static boolean isBlocked(int row, int col, ChessBoard board, ChessPiece piece) {
        ChessPiece tempPiece = board.getPiece(new ChessPosition(row, col));
        if (tempPiece == null) {
            return false;
        }
        else if (tempPiece.getTeamColor().equals(piece.getTeamColor())) {
            return true;
        }
        return false;
    }


    private static final int[][] KNIGHT_OFFSETS = {
            {+1, +2}, {-1, +2}, {+1, -2}, {-1, -2},
            {+2, +1}, {+2, -1}, {-2, +1}, {-2, -1}
    };

    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition from, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();

        int r = from.getRow();
        int c = from.getColumn();

        for (int[] d : KNIGHT_OFFSETS) {
            int r2 = r + d[0];
            int c2 = c + d[1];
            if (ChessPiece.validPosition(r2, c2) && !isBlocked(r2, c2, board, piece)) {
                moves.add(new ChessMove(from, new ChessPosition(r2, c2), null));
            }
        }
        return moves;
    }
}