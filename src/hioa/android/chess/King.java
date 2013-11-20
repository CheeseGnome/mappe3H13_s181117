package hioa.android.chess;

public class King extends Chesspiece {

	private boolean mHasMoved = false;
	private boolean mInCheck = false;

	public King(int color, int row, int column) {
		setColor(color);
		setRow(row);
		setColumn(column);
	}

	public boolean isInCheck() {
		return mInCheck;
	}

	public void setInCheck(boolean inCheck) {
		mInCheck = inCheck;
	}

	@Override
	public boolean move(int row, int column) {
		if (legalMoves()[row][column] == true) {
			// Castle
			if (Math.abs(getColumn() - column) == 2) {
				if (column > getColumn()) {
					((Rook) chessboard.getPieceAt(row, column + 1)).move(row,
							column - 1, true);
				} else {
					((Rook) chessboard.getPieceAt(row, column - 2)).move(row,
							column + 1, true);
				}
			}
			int oldRow = getRow();
			int oldColumn = getColumn();
			setRow(row);
			setColumn(column);
			chessboard.move(this, row, column, oldRow, oldColumn);
			mHasMoved = true;
			return true;
		} else
			return false;
	}

	@Override
	public boolean[][] legalMoves() {
		boolean[][] board = new boolean[chessboard.getMaxRows()][chessboard
				.getMaxColumns()];
		getLegalCastles(board);

		int row = getRow();
		int column = getColumn();

		if (legalIndexes(row + 1, column + 1))
			board[row + 1][column + 1] = legalMove(row + 1, column + 1);

		if (legalIndexes(row + 1, column))
			board[row + 1][column] = legalMove(row + 1, column);

		if (legalIndexes(row + 1, column - 1))
			board[row + 1][column - 1] = legalMove(row + 1, column - 1);

		if (legalIndexes(row, column + 1))
			board[row][column + 1] = legalMove(row, column + 1);

		if (legalIndexes(row, column - 1))
			board[row][column - 1] = legalMove(row, column - 1);

		if (legalIndexes(row - 1, column + 1))
			board[row - 1][column + 1] = legalMove(row - 1, column + 1);

		if (legalIndexes(row - 1, column))
			board[row - 1][column] = legalMove(row - 1, column);

		if (legalIndexes(row - 1, column - 1))
			board[row - 1][column - 1] = legalMove(row - 1, column - 1);

		return board;
	}

	/**
	 * Modifies the provided boolean-array to reflect possible castles
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 */
	private void getLegalCastles(boolean[][] board) {
		int row = getRow();
		int column = getColumn();
		// Kingside Castle
		if (!mHasMoved && !mInCheck
				&& chessboard.tileContains(row, column + 1, false) == NO_PIECE
				&& chessboard.tileContains(row, column + 2, false) == NO_PIECE
				&& !chessboard.kingInCheckAfter(this, row, column + 1)
				&& !chessboard.kingInCheckAfter(this, row, column + 2)) {

			Chesspiece rook = chessboard.getPieceAt(row, column + 3);
			if (rook instanceof Rook && ((Rook) rook).canCastle()) {
				board[row][column + 2] = true;
			}
		}
		// Queenside Castle
		if (!mHasMoved && !mInCheck
				&& chessboard.tileContains(row, column - 1, false) == NO_PIECE
				&& chessboard.tileContains(row, column - 2, false) == NO_PIECE
				&& chessboard.tileContains(row, column - 3, false) == NO_PIECE
				&& !chessboard.kingInCheckAfter(this, row, column - 1)
				&& !chessboard.kingInCheckAfter(this, row, column - 2)) {

			Chesspiece rook = chessboard.getPieceAt(row, column - 4);
			if (rook instanceof Rook && ((Rook) rook).canCastle()) {
				board[row][column - 2] = true;
			}
		}
	}

	/**
	 * Checks if the move is legal
	 * 
	 * @param row
	 *            The row to move to
	 * @param column
	 *            The column to move to
	 * @return True if the move is legal
	 */
	private boolean legalMove(int row, int column) {
		if (!chessboard.kingInCheckAfter(this, row, column)
				&& chessboard.tileContains(row, column, false) != getColor()) {
			return true;
		}
		return false;
	}

	/**
	 * Checks the row and column indexes to make sure they are within range
	 * 
	 * @param row
	 *            The row to check
	 * @param column
	 *            The column to check
	 * @return True if both indexes are within the allowed range
	 */
	private boolean legalIndexes(int row, int column) {
		return (row >= 0 && row < chessboard.getMaxRows() && column >= 0 && column < chessboard
				.getMaxColumns());
	}

	@Override
	public boolean threatensPosition(int row, int column) {
		/*
		 * We only need to make sure that the king is within reach of the
		 * position. If the tile contains a piece it doesn't matter, because if
		 * it's the same color then the king is guarding it. And if it's the
		 * opposite color then no piece can move there anyway.
		 */
		if (row == getRow()) {
			return (Math.abs(column - getColumn()) == 1);
		} else if (column == getColumn()) {
			return (Math.abs(row - getRow()) == 1);
		} else {
			return (Math.abs(row - getRow()) == 1 && Math.abs(column
					- getColumn()) == 1);
		}
	}

}
