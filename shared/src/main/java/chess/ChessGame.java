package chess;

import java.util.*;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }
    //throwing in some fields
    private ChessBoard newBoard;
    private TeamColor turn;

    public ChessGame() {
        newBoard = new ChessBoard();
        newBoard.resetBoard();
        turn = TeamColor.WHITE;
    }



    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }


    private boolean tempMoveKing(ChessPosition from, ChessMove move, TeamColor color) {
        ChessPiece moving = newBoard.getPiece(from);
        ChessPosition to = move.getEndPosition();
        ChessPiece captured = newBoard.getPiece(to);

        newBoard.addPiece(to, moving);
        newBoard.addPiece(from, null);

        ChessPosition kingPosAfter;
        if(moving.getPieceType() == ChessPiece.PieceType.KING) {
            kingPosAfter = to;
        }
        else {
            kingPosAfter = findKing(color);
        }

        boolean safe = !isUnderAttack(kingPosAfter, color);

        newBoard.addPiece(from, moving);
        newBoard.addPiece(to, captured);

        return safe;
    }


    //going to throw in some silly goofy functions to help me do the stuff
    private ChessPosition findKing(TeamColor color) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition test = new ChessPosition(i, j);
                ChessPiece piece = newBoard.getPiece(test);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == color) {
                    return test;
                }
            }
        }
        throw new IllegalStateException("King not found for " + color);
    }


    //this one tells you if your piece (target) is under attack from the other team
    public boolean isUnderAttack(ChessPosition kingPos, TeamColor color) {
        for (int i = 1; i <= 8; i++ ) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition test = new ChessPosition(i, j);
                if (newBoard.getPiece(test) != null && !newBoard.getPiece(test).getTeamColor().equals(color)) {
                    List<ChessMove> moves = new ArrayList<>();
                    moves.addAll(newBoard.getPiece(test).pieceMoves(newBoard, test));
                    for (ChessMove m : moves) {
                        //get the end position of m and see if it is the same as the king position
                        if (m.getEndPosition().equals(kingPos)) {
                            //newBoard.addPiece(kingPos, saveForLater);
                            return true;
                        }
                    }
                    //newBoard.addPiece(kingPos, saveForLater);
                }
            }
        }
        return false;
    }


    /**
     * Gets a valid moves for a piece at the given location
     * Takes as input a position on the chessboard and returns all moves the piece there can legally make.
     * If there is no piece at that location, this method returns null. A move is valid if it is a "piece move"
     * for the piece at the input location and making that move would not leave the team’s king in danger of check.
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = newBoard.getPiece(startPosition);
        if (piece == null) return null;

        Collection<ChessMove> allMoves = piece.pieceMoves(newBoard, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();
        for (ChessMove m : allMoves) {
            if (tempMoveKing(startPosition, m, piece.getTeamColor())) {
                legalMoves.add(m);
            }
        }
        return legalMoves;
    }

    /**
     * Makes a move in a chess game
     *Receives a given move and executes it, provided it is a legal move. If the move is illegal,
     * it throws an InvalidMoveException. A move is illegal if it is not a "valid" move for the
     * piece at the starting location, or if it’s not the corresponding team's turn.
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        ChessPosition startPos = move.getStartPosition();
        ChessPosition endPos = move.getEndPosition();

        ChessPiece piece = newBoard.getPiece(startPos);
        if (piece == null) {
            throw new InvalidMoveException("No Piece at start");
        }
        if (piece.getTeamColor() != turn) {
            throw new InvalidMoveException("Not your turn");
        }


        Collection<ChessMove> legalMoves = validMoves(startPos);
        if (legalMoves == null || legalMoves.isEmpty()) {
            throw new InvalidMoveException("No legal moves for that piece");
        }


        boolean isLegal = false;
        for (ChessMove m : legalMoves) {
            if (m.equals(move)) {
                isLegal = true;
                break;
            }
        }
        if (!isLegal) {
            throw new InvalidMoveException("Move is Invalid");
        }

        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && (endPos.getRow() == 1 || endPos.getRow() == 8) && move.getPromotionPiece() != null) {
            newBoard.addPiece(endPos, new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            newBoard.addPiece(startPos, null);
        } else {
            newBoard.addPiece(endPos, piece);
            newBoard.addPiece(startPos, null);
        }
        if (turn == ChessGame.TeamColor.WHITE) {
            turn = ChessGame.TeamColor.BLACK;
        } else {
            turn = ChessGame.TeamColor.WHITE;
        }
    }

    /**
     * Determines if the given team is in check
     *  Returns true if the specified team’s King could be captured by an opposing piece.
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor);
        if (isUnderAttack(kingPos, teamColor)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Determines if the given team is in checkmate
     *  Returns true if the given team has no way to protect their king from being captured.
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) return false;
        ChessPosition kingPos0 = findKing(teamColor);

        for ( int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition from = new ChessPosition(i, j);
                ChessPiece p = newBoard.getPiece(from);
                if (p == null || p.getTeamColor() != teamColor) continue;

                Collection<ChessMove> moves = p.pieceMoves(newBoard, from);
                if (moves == null) continue;

                for (ChessMove m : moves) {
                    if (tempMoveKing(from, m, teamColor)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     * Returns true if the given team has no legal moves but their king is not in immediate danger.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition testPos = new ChessPosition(i, j);
                ChessPiece testPiece = newBoard.getPiece(testPos);

                if (testPiece == null || testPiece.getTeamColor() != teamColor) continue;

                List<ChessMove> candidateMoves = new ArrayList<>();
                candidateMoves.addAll(validMoves(testPos));

                for (ChessMove m : candidateMoves) {
                    if (tempMoveKing(testPos, m, teamColor) == true) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        newBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
            return newBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(newBoard, chessGame.newBoard) && turn == chessGame.turn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(newBoard, turn);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "newBoard=" + newBoard +
                ", turn=" + turn +
                '}';
    }
}
