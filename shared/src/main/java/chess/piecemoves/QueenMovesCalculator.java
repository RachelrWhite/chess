package chess.piecemoves;

import chess.*;
import java.util.*;

public class QueenMovesCalculator implements MoveCalculator{


    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition from, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();
        //Rook moves
        moves.addAll(MoveCalculator.continueInThisDirection(0, +1, from.getRow(), from.getColumn(), piece, board));
        moves.addAll(MoveCalculator.continueInThisDirection(0, -1, from.getRow(), from.getColumn(), piece, board));
        moves.addAll(MoveCalculator.continueInThisDirection(-1, 0, from.getRow(), from.getColumn(), piece, board));
        moves.addAll(MoveCalculator.continueInThisDirection(+1, 0, from.getRow(), from.getColumn(), piece, board));
        //Bishop moves
        moves.addAll(MoveCalculator.continueInThisDirection(+1, +1, from.getRow(), from.getColumn(), piece, board));
        moves.addAll(MoveCalculator.continueInThisDirection(+1, -1, from.getRow(), from.getColumn(), piece, board));
        moves.addAll(MoveCalculator.continueInThisDirection(-1, +1, from.getRow(), from.getColumn(), piece, board));
        moves.addAll(MoveCalculator.continueInThisDirection(-1, -1, from.getRow(), from.getColumn(), piece, board));

        return moves;

    }
}
