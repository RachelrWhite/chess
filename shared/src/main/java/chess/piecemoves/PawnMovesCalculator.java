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
        if (!validPosition(row, col)) return false; // off-board can't have an enemy
        ChessPiece target = board.getPiece(new ChessPosition(row, col));
        return target != null && target.getTeamColor() != myPiece.getTeamColor();
    }

    static boolean validPosition(int rIndex, int cIndex) {
        return rIndex >= 1 && rIndex <= 8 && cIndex >= 1 && cIndex <= 8;
    }



    @Override
    public Collection<ChessMove> calculate(ChessBoard board, ChessPosition from, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<>();


        //this is the chunk of code for if the piece is white, and we want to see if the piece can move froward 1 and 2 pieces from teh start line (row2)
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {

            int r = from.getRow();
            int c = from.getColumn();

            //check to see if the first step forward is good for the white pawns
            int r1 = r + 1;
            if (validPosition(r1, c) && !isBlocked(r1, c, board)) {
                //this is for the promo
                if (r1 == 8) {
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), null));

                    if (r == 2) {
                        int r2 = r1 + 1;
                        if (ChessPiece.validPosition(r2, c) && !isBlocked(r2, c, board)) {
                            moves.add(new ChessMove(from, new ChessPosition(r2, c), null));
                        }
                    }
                }
            }


            //this is going to check if there is an enemy piece that is diagonal form the white piece
            int rightCornerRow = r + 1;
            int rightCornerCol = c + 1;
            int leftCornerRow = r + 1;
            int leftCornerCol = c - 1;

            if (isBlockedByEnemy(rightCornerRow, rightCornerCol, board, piece )) {
                if (rightCornerRow == 8) {
                    // capture + promotion
                    moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), null));
                }
            }
            if (isBlockedByEnemy(leftCornerRow, leftCornerCol, board, piece )) {
                if (leftCornerRow == 8) {
                    // capture + promotion
                    moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), null));
                }
            }

        }


        //this is where we check to see if the pawn is black and if it is then check to see if it can move past 1 and 2 spots forward from its start position (row 7)
        if (piece.getTeamColor() == ChessGame.TeamColor.BLACK) {

            int r = from.getRow();
            int c = from.getColumn();

            //check to see if the next step forward is good for the black pawns
            int r1 = r - 1;
            if (validPosition(r1, c) && !isBlocked(r1, c, board)) {
                //this is for the promo
                if (r1 == 1) {
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), ChessPiece.PieceType.QUEEN));
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), ChessPiece.PieceType.ROOK));
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), ChessPiece.PieceType.BISHOP));
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), ChessPiece.PieceType.KNIGHT));
                } else {
                    moves.add(new ChessMove(from, new ChessPosition(r1, c), null));

                    if (r == 7) {
                        int r2 = r1 - 1;
                        if (ChessPiece.validPosition(r2, c) && !isBlocked(r2, c, board)) {
                            moves.add(new ChessMove(from, new ChessPosition(r2, c), null));
                        }
                    }
                }
            }
                //this is going to check if there is an enemy piece that is diagonal from the black piece
                int rightCornerRow = r - 1;
                int rightCornerCol = c + 1;
                int leftCornerRow = r - 1;
                int leftCornerCol = c - 1;

                if (isBlockedByEnemy(rightCornerRow, rightCornerCol, board, piece)) {
                    if (rightCornerRow == 1) {
                        // capture + promotion
                        moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), ChessPiece.PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(from, new ChessPosition(rightCornerRow, rightCornerCol), null));
                    }
                }
                if (isBlockedByEnemy(leftCornerRow, leftCornerCol, board, piece)) {
                    if (leftCornerRow == 1) {
                        // capture + promotion
                        moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), ChessPiece.PieceType.QUEEN));
                        moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), ChessPiece.PieceType.ROOK));
                        moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), ChessPiece.PieceType.BISHOP));
                        moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), ChessPiece.PieceType.KNIGHT));
                    } else {
                        moves.add(new ChessMove(from, new ChessPosition(leftCornerRow, leftCornerCol), null));
                    }
                }

        }
        return moves;
    }
}
