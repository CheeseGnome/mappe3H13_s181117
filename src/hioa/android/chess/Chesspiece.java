package hioa.android.chess;

import hioa.android.chess.R;
import android.content.Context;

/**
 * Superclass for the various Chesspieces
 * <p>
 * Note: This is an abstract class
 * 
 * @author Lars Sætaberget
 * @version 2013-11-10
 */

public abstract class Chesspiece {
	public static final int NO_PIECE = -1, BLACK = 0, WHITE = 1, EN_PASSANT = 2;
	
	/**
	 * This variable is only used for allowing the pieces to easily orient themselves on the board.
	 * It is used to determine legal moves and which positions they threaten.
	 * <p>
	 * The values for all other pieces in the same methods are derived from the current board state in {@link Chessboard}
	 */
	private int mCurrentRow, mCurrentColumn;
	private int mColor;
	public static Context context;
	public static Chessboard chessboard;

	/**
	 * Moves the piece to the provided row and column
	 * 
	 * @param row
	 *            The row to move to
	 * @param column
	 *            The column to move to
	 * @return False if the move is illegal
	 */
	public abstract boolean move(int row, int column);

	/**
	 * Provides an array of all legal moves for this piece
	 * @return A 2d boolean array representing the chessboard where all legal moves are true and all illegal moves are false
	 */
	public abstract boolean[][] legalMoves();
	
	/**
	 * Used to determine whether or not this piece is currently checking the opposing king.
	 * @param row The row that the opposing king is on
	 * @param column The column that the opposing king is on
	 * @return True if this piece is threatening the given position
	 */
	public abstract boolean threatensPosition(int row, int column);

	public abstract boolean sameClass(Chesspiece piece);
	
	public int getColor() {
		return mColor;
	}

	protected void setColor(int color) {
		mColor = color;
	}
	
	/**
	 * Get the row that this piece is currently positioned at
	 * @return The row position for this piece
	 */
	protected int getRow(){
		return mCurrentRow;
	}
	/**
	 * Get the column that this piece is currently positioned at
	 * @return The column position for this piece
	 */
	protected int getColumn(){
		return mCurrentColumn;
	}

	/**
	 * Sets the current row for this chesspiece
	 * 
	 * @param row
	 *            The new row index
	 * @return False if the index is illegal
	 */
	protected boolean setRow(int row) {
		int maxIndex = context.getResources().getInteger(R.integer.chesspiece_max_row_index);
		if (row >= 0 && row <= maxIndex) {
			mCurrentRow = row;
			return true;
		} else
			return false;
	}

	/**
	 * Sets the current column for this chesspiece
	 * 
	 * @param column
	 *            The new column index
	 * @return False if the index is illegal
	 */
	protected boolean setColumn(int column) {
		int maxIndex = context.getResources().getInteger(R.integer.chesspiece_max_column_index);
		if (column >= 0 && column <= maxIndex) {
			mCurrentColumn = column;
			return true;
		} else
			return false;
	}
}
