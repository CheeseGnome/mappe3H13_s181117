package hioa.android.chess;

/**
 * Represents a bishop
 * 
 * @author Lars Sætaberget
 * @version 2013-11-10
 */

public class Bishop extends Chesspiece {

	public Bishop(int color, int row, int column) {
		setColor(color);
		setRow(row);
		setColumn(column);
	}

	@Override
	public boolean move(int row, int column) {
		if (legalMoves()[row][column] == true) {
			chessboard.move(this, row, column);
			setRow(row);
			setColumn(column);
			return true;
		} else
			return false;
	}

	@Override
	public boolean[][] legalMoves() {
		boolean[][] board = new boolean[chessboard.getMaxRows()][chessboard.getMaxColumns()];
		int enemy;
		if (getColor() == WHITE)
			enemy = BLACK;
		else
			enemy = WHITE;
		getLegalMovesUpRight(board, enemy);
		getLegalMovesUpLeft(board, enemy);
		getLegalMovesDownLeft(board, enemy);
		getLegalMovesDownRight(board, enemy);
		return board;
	}

	@Override
	public boolean threatensPosition(int row, int column) {
		// False here means the king is not on a diagonal of this piece
		if (Math.abs(row - getRow()) != Math.abs(column - getColumn())) {
			return false;
		}

		if (row > getRow()) {
			if (column > getColumn()) {
				return threatensDownRight(row, column);
			} else {
				return threatensDownLeft(row, column);
			}
		} else {
			if (column > getColumn()) {
				return threatensUpRight(row, column);
			} else {
				return threatensUpLeft(row, column);
			}
		}
	}

	/**
	 * Checks if the piece is threatening the provided position diagonally
	 * up-right
	 * 
	 * @param kingRow
	 *            The row to check if this piece is threatening
	 * @param kingColumn
	 *            The column to check if this piece is threatening
	 * @return True if this piece is threatening the position
	 */
	private boolean threatensUpRight(int kingRow, int kingColumn) {
		int row = getRow() - 1;
		int column = getColumn() + 1;

		while (row >= 0 && column < chessboard.getMaxColumns()) {
			// non-empty tile
			if (chessboard.tileContains(row, column, false) != NO_PIECE) {
				if (row == kingRow && column == kingColumn)
					return true;
				else {
					break;
				}
			}
			row -= 1;
			column += 1;
		}
		return false;
	}

	/**
	 * Checks if the piece is threatening the provided position diagonally
	 * up-left
	 * 
	 * @param kingRow
	 *            The row to check if this piece is threatening
	 * @param kingColumn
	 *            The column to check if this piece is threatening
	 * @return True if this piece is threatening the position
	 */
	private boolean threatensUpLeft(int kingRow, int kingColumn) {
		int row = getRow() - 1;
		int column = getColumn() - 1;

		while (row >= 0 && column >= 0) {
			// non-empty tile
			if (chessboard.tileContains(row, column, false) != NO_PIECE) {
				if (row == kingRow && column == kingColumn)
					return true;
				else {
					break;
				}
			}
			row -= 1;
			column -= 1;
		}
		return false;
	}

	/**
	 * Checks if the piece is threatening the provided position diagonally
	 * down-right
	 * 
	 * @param kingRow
	 *            The row to check if this piece is threatening
	 * @param kingColumn
	 *            The column to check if this piece is threatening
	 * @return True if this piece is threatening the position
	 */
	private boolean threatensDownRight(int kingRow, int kingColumn) {
		int row = getRow() + 1;
		int column = getColumn() + 1;

		while (row < chessboard.getMaxRows() && column < chessboard.getMaxColumns()) {
			// non-empty tile
			if (chessboard.tileContains(row, column, false) != NO_PIECE) {
				if (row == kingRow && column == kingColumn)
					return true;
				else {
					break;
				}
			}
			row += 1;
			column += 1;
		}
		return false;
	}

	/**
	 * Checks if the piece is threatening the provided position diagonally
	 * down-left
	 * 
	 * @param kingRow
	 *            The row to check if this piece is threatening
	 * @param kingColumn
	 *            The column to check if this piece is threatening
	 * @return True if this piece is threatening the position
	 */
	private boolean threatensDownLeft(int kingRow, int kingColumn) {
		int row = getRow() + 1;
		int column = getColumn() - 1;

		while (row < chessboard.getMaxRows() && column >= 0) {
			// non-empty tile
			if (chessboard.tileContains(row, column, false) != NO_PIECE) {
				if (row == kingRow && column == kingColumn)
					return true;
				else {
					break;
				}
			}
			row += 1;
			column -= 1;
		}
		return false;
	}

	/**
	 * Modifies the provided boolean-array to reflect all possible moves
	 * diagonally up-right
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 * @param enemy
	 *            An int representing the enemy color as specified by the
	 *            constants in {@link Chesspiece}
	 */

	private void getLegalMovesUpRight(boolean[][] board, int enemy) {
		int row = getRow() - 1;
		int column = getColumn() + 1;

		while (row >= 0 && column < chessboard.getMaxColumns()) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, row, column)
					&& chessboard.tileContains(row, column, false) == NO_PIECE) {
				board[row][column] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, row, column)
					&& chessboard.tileContains(row, column, false) == enemy) {
				board[row][column] = true;
				break;
			}
			// Friendly piece blocking
			else {
				break;
			}
			row -= 1;
			column += 1;
		}
	}

	/**
	 * Modifies the provided boolean-array to reflect all possible moves
	 * diagonally up-left
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 * @param enemy
	 *            An int representing the enemy color as specified by the
	 *            constants in {@link Chesspiece}
	 */
	private void getLegalMovesUpLeft(boolean[][] board, int enemy) {
		int row = getRow() - 1;
		int column = getColumn() - 1;

		while (row >= 0 && column >= 0) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, row, column)
					&& chessboard.tileContains(row, column, false) == NO_PIECE) {
				board[row][column] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, row, column)
					&& chessboard.tileContains(row, column, false) == enemy) {
				board[row][column] = true;
				break;
			}
			// Friendly piece blocking
			else {
				break;
			}
			row -= 1;
			column -= 1;
		}
	}

	/**
	 * Modifies the provided boolean-array to reflect all possible moves
	 * diagonally down-left
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 * @param enemy
	 *            An int representing the enemy color as specified by the
	 *            constants in {@link Chesspiece}
	 */
	private void getLegalMovesDownLeft(boolean[][] board, int enemy) {
		int row = getRow() + 1;
		int column = getColumn() - 1;

		while (column >= 0 && row < chessboard.getMaxRows()) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, row, column)
					&& chessboard.tileContains(row, column, false) == NO_PIECE) {
				board[row][column] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, row, column)
					&& chessboard.tileContains(row, column, false) == enemy) {
				board[row][column] = true;
				break;
			}
			// Friendly piece blocking
			else {
				break;
			}
			row += 1;
			column -= 1;
		}

	}

	/**
	 * Modifies the provided boolean-array to reflect all possible moves
	 * diagonally down-right
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 * @param enemy
	 *            An int representing the enemy color as specified by the
	 *            constants in {@link Chesspiece}
	 */
	private void getLegalMovesDownRight(boolean[][] board, int enemy) {
		int row = getRow() + 1;
		int column = getColumn() + 1;

		while (row < chessboard.getMaxRows() && column < chessboard.getMaxColumns()) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, row, column)
					&& chessboard.tileContains(row, column, false) == NO_PIECE) {
				board[row][column] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, row, column)
					&& chessboard.tileContains(row, column, false) == enemy) {
				board[row][column] = true;
				break;
			}
			// Friendly piece blocking
			else {
				break;
			}
			row += 1;
			column += 1;
		}
	}

}
