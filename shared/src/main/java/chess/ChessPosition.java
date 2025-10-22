package chess;

import java.util.Objects;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {

    private final int row; //1-8
    private final int col; //1-8

    public ChessPosition(int row, int col) {
        //make sure the position is in range
        if (row < 1 || row > 8 || col < 1 || col > 8) {
            throw new IllegalArgumentException("row or col isn't in range 1-8");
        }
        this.row = row;
        this.col = col;
    }
    //this returns the row
    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        return col;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) { return false; }
        ChessPosition that = (ChessPosition) o;
        return row == that.row && col == that.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return String.format("[%d,%d]", row, col);
    }
}
