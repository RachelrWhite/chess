package chess.piecemoves;

import chess.*;
import java.util.*;


public class RookMovesCalculator implements MoveCalculator{

    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition from, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        moves.addAll(MoveCalculator.continueInThisDirection(0, +1, from.getRow(), from.getColumn(), piece, board));
        moves.addAll(MoveCalculator.continueInThisDirection(0, -1, from.getRow(), from.getColumn(), piece, board));
        moves.addAll(MoveCalculator.continueInThisDirection(-1, 0, from.getRow(), from.getColumn(), piece, board));
        moves.addAll(MoveCalculator.continueInThisDirection(+1, 0, from.getRow(), from.getColumn(), piece, board));

        return moves;
    }
}


//    public static boolean isBlocked(int row, int col, ChessBoard board, ChessPiece piece) {
//        ChessPiece testPiece = board.getPiece(new ChessPosition(row, col));
//        if (testPiece == null) {
//            return false;
//        }
//        else if (testPiece.getTeamColor().equals(piece.getTeamColor())) {
//            return true;
//        }
//        return false;
//    }
