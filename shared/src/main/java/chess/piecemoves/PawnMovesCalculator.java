package chess.piecemoves;

import chess.*;
import java.util.*;

public class PawnMovesCalculator implements MoveCalculator {

    public static boolean isBlocked(int row, int col, ChessBoard board) {
        return board.getPiece(new ChessPosition(row, col)) != null;
    }

    public static boolean isBlockedByEnemy(int row, int col,
                                           ChessBoard board,
                                           ChessPiece myPiece) {
        if (!validPosition(row, col)) {
            return false; // off-board can't have an enemy
        }
        ChessPiece target = board.getPiece(new ChessPosition(row, col));
        return target != null && target.getTeamColor() != myPiece.getTeamColor();
    }

    static boolean validPosition(int rIndex, int cIndex) {
        return rIndex >= 1 && rIndex <= 8 && cIndex >= 1 && cIndex <= 8;
    }

    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition from, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();

        // Direction and ranks depend on color: white goes +1 to row 8, black goes -1 to row 1
        final boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        final int dir = isWhite ? 1 : -1;
        final int startRank = isWhite ? 2 : 7;
        final int promoRank = isWhite ? 8 : 1;

        final int r = from.getRow();
        final int c = from.getColumn();

        // 1-step forward
        int r1 = r + dir;
        if (validPosition(r1, c) && !isBlocked(r1, c, board)) {
            addMoveOrPromotions(moves, from, r1, c, promoRank);

            if (r == startRank) {
                int r2 = r + 2 * dir;
                if (validPosition(r2, c) && !isBlocked(r2, c, board)) {
                    // must also ensure the square we "pass through" (r1,c) is clear — already checked
                    moves.add(new ChessMove(from, new ChessPosition(r2, c), null));
                }
            }
        }

        // Diagonal captures (left and right)
        for (int dc : new int[]{-1, 1}) {
            int tr = r + dir;
            int tc = c + dc;
            if (isBlockedByEnemy(tr, tc, board, piece)) {
                addMoveOrPromotions(moves, from, tr, tc, promoRank);
            }
        }

        return moves;
    }

    /** Adds a normal move, or (if on promotion rank) all 4 promotion choices for that destination. */
    private static void addMoveOrPromotions(List<ChessMove> moves, ChessPosition from, int toRow, int toCol, int promoRank) {
        if (!validPosition(toRow, toCol)) {
            return;
        }
        ChessPosition to = new ChessPosition(toRow, toCol);
        if (toRow == promoRank) {
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.BISHOP));
            moves.add(new ChessMove(from, to, ChessPiece.PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(from, to, null));
        }
    }
}
