package dataaccess;

import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FullThingTest {

    private MySqlDataAccess dao;

    @BeforeEach
    void setup() throws Exception {
        dao = new MySqlDataAccess();
        dao.clear();
    }

    @Test
    void chessGameRoundTripMutationPersistsPositive() throws Exception {
        int id = dao.createGame("RoundTrip");
        GameData g = dao.getGame(id);
        assertNotNull(g);
        var game = g.game();
        assertNotNull(game);

        // Example: white pawn e2 -> e4 (adjust if your coords differ)
        var from = new chess.ChessPosition(2, 5);
        var to   = new chess.ChessPosition(4, 5);
        var move = new chess.ChessMove(from, to, null);

        // If your validMoves is strict, keep this; otherwise you can omit.
        assertTrue(game.validMoves(from).contains(move));
        game.makeMove(move);

        // Save updated game
        var updated = new GameData(g.gameID(), g.whiteUsername(), g.blackUsername(), g.gameName(), game);
        dao.updateGame(updated);

        // Reload and verify board changed
        var after = dao.getGame(id);
        var reloaded = after.game();
        assertNotNull(reloaded.getBoard().getPiece(to));
        assertNull(reloaded.getBoard().getPiece(from));
        assertEquals(chess.ChessPiece.PieceType.PAWN, reloaded.getBoard().getPiece(to).getPieceType());
        assertEquals(chess.ChessGame.TeamColor.WHITE, reloaded.getBoard().getPiece(to).getTeamColor());
    }
}

