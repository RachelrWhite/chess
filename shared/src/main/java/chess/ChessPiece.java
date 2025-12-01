package chess;

import chess.piecemoves.*;

import java.util.Collection;
import java.util.Objects;


/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }
    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;
    private MoveCalculator moveCalculator;

    //called anytime I create a new chess piece
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;

        this.moveCalculator = switch (this.type) {
            case PAWN ->  new PawnMovesCalculator();
            case ROOK -> new RookMovesCalculator();
            case BISHOP -> new BishopMovesCalculator();
            case KNIGHT -> new KnightMovesCalculator();
            case QUEEN -> new QueenMovesCalculator();
            case KING -> new KingMovesCalculator();
        };
    }

    /**
     * The various different chess piece options
     */

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        return moveCalculator.calculate(board, myPosition, this);
    }

    /**
     * write some private helpers that allow me to
     * index rows and cols using 0-7 instead of 0-8
     * tell me if something is in bounds
     * amd return a new chess position
     */


    public static boolean validPosition(int rIndex, int cIndex) {
        return rIndex >= 1 && rIndex <= 8 && cIndex >= 1 && cIndex <=8;
    }


    //hash and equals functions
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
