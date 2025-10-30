package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChessGameRoundTripTest {

    private MySqlDataAccess dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlDataAccess();
        dao.clear();
    }

    @Test
    void chessGame_roundTrip_mutationPersists_positive() throws Exception {
        int id = dao.createGame("RoundTrip");
        var g = dao.getGame(id);
        assertNotNull(g);

        var game = g.game();
        assertNotNull(game);

        // White pawn e2 -> e3 (use single step to avoid any edge cases)
        var from = new chess.ChessPosition(2, 5); // e2 if (row, col) with rows 1..8 from White
        var to   = new chess.ChessPosition(3, 5); // e3

        // If you really want to check "is this move legal?" without equals():
        boolean legal = game.validMoves(from).stream()
                .anyMatch(m -> m.getEndPosition().equals(to) && m.getPromotionPiece() == null);
        assertTrue(legal, "Expected e2->e3 to be legal for White at game start");

        // Make the move
        game.makeMove(new chess.ChessMove(from, to, null));

        // Save back
        var updated = new model.GameData(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(), game);
        dao.updateGame(updated);

        // Reload and verify
        var re = dao.getGame(id).game();
        assertNull(re.getBoard().getPiece(from), "e2 should now be empty");
        assertNotNull(re.getBoard().getPiece(to), "e3 should now have a piece");
        assertEquals(chess.ChessPiece.PieceType.PAWN, re.getBoard().getPiece(to).getPieceType());
        assertEquals(chess.ChessGame.TeamColor.WHITE, re.getBoard().getPiece(to).getTeamColor());
    }
}

