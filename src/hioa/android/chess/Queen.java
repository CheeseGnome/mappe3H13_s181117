package hioa.android.chess;

/**
 * Represents a queen
 * 
 * @author Lars Sætaberget
 * @version 2013-11-10
 */

public class Queen extends Chesspiece {

	public Queen(int color, int row, int column) {
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
		boolean[][] board = new boolean[chessboard.getMaxRows()][chessboard
				.getMaxColumns()];
		int enemy;
		if (getColor() == WHITE)
			enemy = BLACK;
		else
			enemy = WHITE;
		getLegalMovesRight(board, enemy);
		getLegalMovesUp(board, enemy);
		getLegalMovesLeft(board, enemy);
		getLegalMovesDown(board, enemy);
		getLegalMovesUpRight(board, enemy);
		getLegalMovesUpLeft(board, enemy);
		getLegalMovesDownLeft(board, enemy);
		getLegalMovesDownRight(board, enemy);
		return board;
	}

	@Override
	public boolean threatensPosition(int row, int column) {
		// Not on the same row, not on the same column and not on a diagonal
		if (getRow() != row && getColumn() != column
				&& Math.abs(row - getRow()) != Math.abs(column - getColumn())) {
			return false;
		}
		// Straight movement
		if (getRow() == row) {
			if (column > getColumn()) {
				return threatensRight(column);
			} else {
				return threatensLeft(column);
			}
		} else if (getColumn() == column) {
			if (row > getRow()) {
				return threatensDown(row);
			} else {
				return threatensUp(row);
			}
		}
		// Diagonal movement
		else if (row > getRow()) {
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
	 * Modifies the provided boolean-array to reflect all possible moves towards
	 * the right direction
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 * @param enemy
	 *            An int representing the enemy color as specified by the
	 *            constants in {@link Chesspiece}
	 */

	private void getLegalMovesUp(boolean[][] board, int enemy) {
		for (int i = getRow() - 1; i >= 0; i--) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, i, getColumn())
					&& chessboard.tileContains(i, getColumn(), false) == NO_PIECE) {
				board[i][getColumn()] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, i, getColumn())
					&& chessboard.tileContains(i, getColumn(), false) == enemy) {
				board[i][getColumn()] = true;
				break;
			}
			// Friendly piece blocking
			else {
				break;
			}

		}
	}

	/**
	 * Modifies the provided boolean-array to reflect all possible moves in an
	 * upwards direction
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 * @param enemy
	 *            An int representing the enemy color as specified by the
	 *            constants in {@link Chesspiece}
	 */

	private void getLegalMovesRight(boolean[][] board, int enemy) {

		for (int i = getColumn() + 1; i < chessboard.getMaxColumns(); i++) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, getRow(), i)
					&& chessboard.tileContains(getRow(), i, false) == NO_PIECE) {
				board[getRow()][i] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, getRow(), i)
					&& chessboard.tileContains(getRow(), i, false) == enemy) {
				board[getRow()][i] = true;
				break;
			}
			// Friendly piece blocking
			else {
				break;
			}

		}

	}

	/**
	 * Modifies the provided boolean-array to reflect all possible moves towards
	 * the left direction
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 * @param enemy
	 *            An int representing the enemy color as specified by the
	 *            constants in {@link Chesspiece}
	 */
	private void getLegalMovesDown(boolean[][] board, int enemy) {

		for (int i = getRow() + 1; i < chessboard.getMaxRows(); i++) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, i, getColumn())
					&& chessboard.tileContains(i, getColumn(), false) == NO_PIECE) {
				board[i][getColumn()] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, i, getColumn())
					&& chessboard.tileContains(i, getColumn(), false) == enemy) {
				board[i][getColumn()] = true;
				break;
			}
			// Friendly piece blocking
			else {
				break;
			}

		}

	}

	/**
	 * Modifies the provided boolean-array to reflect all possible moves in a
	 * downwards direction
	 * 
	 * @param board
	 *            The array which will represent the possible moves
	 * @param enemy
	 *            An int representing the enemy color as specified by the
	 *            constants in {@link Chesspiece}
	 */
	private void getLegalMovesLeft(boolean[][] board, int enemy) {
		for (int i = getColumn() - 1; i >= 0; i--) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, getRow(), i)
					&& chessboard.tileContains(getRow(), i, false) == NO_PIECE) {
				board[getRow()][i] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, getRow(), i)
					&& chessboard.tileContains(getRow(), i, false) == enemy) {
				board[getRow()][i] = true;
				break;
			}
			// Friendly piece blocking
			else {
				break;
			}

		}
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

		while (row < chessboard.getMaxRows()
				&& column < chessboard.getMaxColumns()) {
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

	/**
	 * Checks if the piece is threatening the provided position in a downwards
	 * direction
	 * <p>
	 * Note: This method assumes that the column is the same
	 * 
	 * @param row
	 *            The row to check if this piece is threatening
	 * @return True if this piece is threatening the position
	 */
	private boolean threatensDown(int row) {
		for (int i = getRow() + 1; i < chessboard.getMaxRows(); i++) {
			// Piece found
			if (chessboard.tileContains(i, getColumn(), false) != NO_PIECE) {
				// King
				if (i == row) {
					return true;
				}
				// Blocking piece
				else {
					return false;
				}

			}
		}
		return false;
	}

	/**
	 * Checks if the piece is threatening the provided position in an upwards
	 * direction
	 * <p>
	 * Note: This method assumes that the column is the same
	 * 
	 * @param row
	 *            The row to check if this piece is threatening
	 * @return True if this piece is threatening the position
	 */
	private boolean threatensUp(int row) {
		for (int i = getRow() - 1; i >= 0; i--) {
			// Piece found
			if (chessboard.tileContains(i, getColumn(), false) != NO_PIECE) {
				// King
				if (i == row) {
					return true;
				}
				// Blocking piece
				else {
					return false;
				}

			}
		}
		return false;
	}

	/**
	 * Checks if the piece is threatening the provided position toward the left
	 * <p>
	 * Note: This method assumes that the row is the same
	 * 
	 * @param column
	 *            The column to check if this piece is threatening
	 * @return True if this piece is threatening the position
	 */
	private boolean threatensLeft(int column) {
		for (int i = getColumn() - 1; i >= 0; i--) {
			// Piece found
			if (chessboard.tileContains(getRow(), i, false) != NO_PIECE) {
				// King
				if (i == column) {
					return true;
				}
				// Blocking piece
				else {
					return false;
				}

			}
		}
		return false;
	}

	/**
	 * Checks if the piece is threatening the provided position toward the
	 * right.
	 * <p>
	 * Note: This method assumes that the row is the same
	 * 
	 * @param column
	 *            The column to check if this piece is threatening
	 * @return True if this piece is threatening the position
	 */
	private boolean threatensRight(int column) {
		for (int i = getColumn() + 1; i < chessboard.getMaxColumns(); i++) {
			// Piece found
			if (chessboard.tileContains(getRow(), i, false) != NO_PIECE) {
				// King
				if (i == column) {
					return true;
				}
				// Blocking piece
				else {
					return false;
				}

			}
		}
		return false;
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

		while (row < chessboard.getMaxRows()
				&& column < chessboard.getMaxColumns()) {
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

}
