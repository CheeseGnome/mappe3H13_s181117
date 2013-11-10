package hioa.android.chess;

import android.content.Context;

public class Chessboard {

	private Chesspiece[][] mChessboard;
	private Context mContext;
	private EnPassant mEnPassant;

	public Chessboard(Context context) {
		mContext = context;
		Chesspiece.context = context;
		Chesspiece.chessboard = this;

	}

	/**
	 * This method checks if the provided move will result in the piece's
	 * color's king being in check
	 * 
	 * @param piece
	 *            The piece to move
	 * @param row
	 *            The row to move to
	 * @param column
	 *            The column to move to
	 * @return True if the king ends up in check
	 */
	public boolean kingInCheckAfter(Chesspiece piece, int row, int column) {
		return false;
	}

	/**
	 * Fetches the {@link Chesspiece} at the given position
	 * 
	 * @param row
	 *            The row to fetch the piece from
	 * @param column
	 *            The column to fetch the piece from
	 * @return The piece located at the provided position
	 */
	public Chesspiece getPieceAt(int row, int column) {
		return mChessboard[row][column];
	}

	/**
	 * Places the provided {@link EnPassant} onto the chessboard
	 * 
	 * @param enPassant
	 */
	public void placeEnPassant(EnPassant enPassant) {
		if (mEnPassant != null) {
			mChessboard[mEnPassant.getRow()][mEnPassant.getColumn()] = null;
		}
		mEnPassant = enPassant;
		mChessboard[enPassant.getRow()][enPassant.getColumn()] = enPassant;
	}

	/**
	 * Returns an int explaining what type of piece(if any) is in the given tile
	 * 
	 * @param row
	 *            The row to check
	 * @param column
	 *            The column to check
	 * @param showEnPassant
	 *            Use this flag to determine if you want EN_PASSANT to be a
	 *            returnable value. If this is false EN_PASSANT will be returned
	 *            as NO_PIECE
	 * @return NO_PIECE, EN_PASSANT, BLACK or WHITE as defined in
	 *         {@link Chesspiece}
	 */
	public int tileContains(int row, int column, boolean showEnPassant) {
		if (mChessboard[row][column] != null) {
			if (mChessboard[row][column].getColor() == Chesspiece.EN_PASSANT && !showEnPassant) {
				return Chesspiece.NO_PIECE;
			}
			return mChessboard[row][column].getColor();
		} else
			return Chesspiece.NO_PIECE;
	}

	/**
	 * Moves the piece to the provided row and column.
	 * <p>
	 * Note: This method does no error-checking and simply assumes that the move
	 * is legal
	 * 
	 * @param piece
	 *            The piece to move
	 * @param row
	 *            The piece's new row position
	 * @param column
	 *            The piece's new column position
	 */
	public void move(Chesspiece piece, int row, int column) {
		// TODO delete enpassant
		mChessboard[piece.getRow()][piece.getColumn()] = null;
		mChessboard[row][column] = piece;
	}

	public int getMaxRows() {
		return 1 + mContext.getResources().getInteger(R.integer.chesspiece_max_row_index);
	}

	public int getMaxColumns() {
		return 1 + mContext.getResources().getInteger(R.integer.chesspiece_max_column_index);
	}
}
