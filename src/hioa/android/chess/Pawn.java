package hioa.android.chess;

/**
 * Represents a pawn
 * 
 * @author Lars Sætaberget
 * @version 2013-11-09
 */

public class Pawn extends Chesspiece {

	/**
	 * Used to determine whether or not this piece can move 2 spaces
	 */
	private boolean mHasMoved = false;

	public Pawn(int color, int row, int column) {
		setColor(color);
		setRow(row);
		setColumn(column);
	}

	@Override
	public boolean sameClass(Chesspiece piece) {
		return (piece instanceof Pawn);
	}

	@Override
	public boolean move(int row, int column) {
		if (legalMoves()[row][column] == true) {
			boolean placeEnPassant = Math.abs(row - getRow()) == 2;
			int oldRow = getRow();
			int oldColumn = getColumn();
			setRow(row);
			setColumn(column);
			chessboard.move(this, row, column, oldRow, oldColumn);
			if (placeEnPassant) {
				chessboard.placeEnPassant(new EnPassant(this));
			}
			mHasMoved = true;
			return true;
		} else
			return false;
	}

	@Override
	public boolean threatensPosition(int row, int column) {
		if ((getRow() == row + 1 && getColor() == WHITE) || (getRow() == row - 1 && getColor() == BLACK)) {
			if (getColumn() == column - 1 || getColumn() == column + 1)
				return true;
		}
		return false;
	}

	@Override
	public boolean[][] legalMoves() {
		boolean[][] board = new boolean[chessboard.getMaxRows()][chessboard.getMaxColumns()];

		if (getColor() == WHITE) {
			getLegalWhiteMoves(board);
		} else {
			getLegalBlackMoves(board);
		}
		return board;
	}

	/**
	 * Modifies the provided boolean-array to reflect all possible moves if this
	 * pawn moves as a black pawn
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 */
	private void getLegalBlackMoves(boolean[][] board) {
		int row = getRow();
		int column = getColumn();
		// capture left
		if (row + 1 < chessboard.getMaxRows() && column - 1 >= 0
				&& !chessboard.kingInCheckAfter(this, row + 1, column - 1)) {
			if (chessboard.tileContains(row + 1, column - 1, true) == WHITE
					|| chessboard.tileContains(row + 1, column - 1, true) == EN_PASSANT) {
				board[row + 1][column - 1] = true;
			}
		}
		// capture right
		if (row + 1 < chessboard.getMaxRows() && column + 1 < chessboard.getMaxColumns()
				&& !chessboard.kingInCheckAfter(this, row + 1, column + 1)) {
			if (chessboard.tileContains(row + 1, column + 1, true) == WHITE
					|| chessboard.tileContains(row + 1, column + 1, true) == EN_PASSANT) {
				board[row + 1][column + 1] = true;
			}
		}
		// move forward 1
		if (row + 1 < chessboard.getMaxRows() && !chessboard.kingInCheckAfter(this, row + 1, column)
				&& chessboard.tileContains(row + 1, column, true) == NO_PIECE) {
			board[row + 1][column] = true;
		}
		// move forward 2
		if (mHasMoved == false && !chessboard.kingInCheckAfter(this, row + 2, column)
				&& chessboard.tileContains(row + 2, column, true) == NO_PIECE
				&& chessboard.tileContains(row + 1, column, true) == NO_PIECE) {
			board[row + 2][column] = true;
		}
	}

	/**
	 * Modifies the provided boolean-array to reflect all possible moves if this
	 * pawn moves as a white pawn
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 */
	private void getLegalWhiteMoves(boolean[][] board) {
		int row = getRow();
		int column = getColumn();
		// capture left
		if (row - 1 >= 0 && column - 1 >= 0 && !chessboard.kingInCheckAfter(this, row - 1, column - 1)) {
			if (chessboard.tileContains(row - 1, column - 1, true) == BLACK
					|| chessboard.tileContains(row - 1, column - 1, true) == EN_PASSANT) {
				board[row - 1][column - 1] = true;
			}
		}
		// capture right
		if (column + 1 < chessboard.getMaxColumns() && row - 1 >= 0
				&& !chessboard.kingInCheckAfter(this, row - 1, column + 1)) {
			if (chessboard.tileContains(row - 1, column + 1, true) == BLACK
					|| chessboard.tileContains(row - 1, column + 1, true) == EN_PASSANT) {
				board[row - 1][column + 1] = true;
			}
		}
		// move forward 1
		if (row - 1 >= 0 && !chessboard.kingInCheckAfter(this, row - 1, column)
				&& chessboard.tileContains(row - 1, column, true) == NO_PIECE) {
			board[row - 1][column] = true;
		}
		// move forward 2
		if (mHasMoved == false && !chessboard.kingInCheckAfter(this, row - 2, column)
				&& chessboard.tileContains(row - 2, column, true) == NO_PIECE
				&& chessboard.tileContains(row - 1, column, true) == NO_PIECE) {
			board[row - 2][column] = true;
		}
	}

}
