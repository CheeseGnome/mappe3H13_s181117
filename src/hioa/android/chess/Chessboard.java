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
		mChessboard = createChessboard();
	}

	private Chesspiece[][] createChessboard() {
		Chesspiece[][] board = new Chesspiece[getMaxRows()][getMaxColumns()];

		for (int i = 0; i < getMaxColumns(); i++) {
			board[1][i] = new Pawn(Chesspiece.BLACK, 1, i);
			board[getMaxRows() - 2][i] = new Pawn(Chesspiece.WHITE, 1, i);
		}
		board[0][0] = new Rook(Chesspiece.BLACK, 0, 0);
		board[0][1] = new Knight(Chesspiece.BLACK, 0, 1);
		board[0][2] = new Bishop(Chesspiece.BLACK, 0, 2);
		board[0][3] = new Queen(Chesspiece.BLACK, 0, 3);
		board[0][4] = new King(Chesspiece.BLACK, 0, 4);
		board[0][5] = new Bishop(Chesspiece.BLACK, 0, 5);
		board[0][6] = new Knight(Chesspiece.BLACK, 0, 6);
		board[0][7] = new Rook(Chesspiece.BLACK, 0, 7);

		board[getMaxRows() - 1][0] = new Rook(Chesspiece.WHITE, getMaxRows() - 1, 0);
		board[getMaxRows() - 1][1] = new Knight(Chesspiece.WHITE, getMaxRows() - 1, 1);
		board[getMaxRows() - 1][2] = new Bishop(Chesspiece.WHITE, getMaxRows() - 1, 2);
		board[getMaxRows() - 1][3] = new Queen(Chesspiece.WHITE, getMaxRows() - 1, 3);
		board[getMaxRows() - 1][4] = new King(Chesspiece.WHITE, getMaxRows() - 1, 4);
		board[getMaxRows() - 1][5] = new Bishop(Chesspiece.WHITE, getMaxRows() - 1, 5);
		board[getMaxRows() - 1][6] = new Knight(Chesspiece.WHITE, getMaxRows() - 1, 6);
		board[getMaxRows() - 1][7] = new Rook(Chesspiece.WHITE, getMaxRows() - 1, 7);

		return board;
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
