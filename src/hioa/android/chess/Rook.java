package hioa.android.chess;

/**
 * Represents a rook
 * 
 * @author Lars Sætaberget
 * @version 2013-11-10
 */

public class Rook extends Chesspiece {

	/**
	 * Used to determine whether or not this piece can be used to castle
	 */
	private boolean mHasMoved = false;

	public Rook(int color, int row, int column) {
		setColor(color);
		setRow(row);
		setColumn(column);
	}

	@Override
	public boolean sameClass(Chesspiece piece) {
		return (piece instanceof Rook);
	}

	@Override
	public boolean threatensPosition(int row, int column) {
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
		return false;
	}

	@Override
	public boolean move(int row, int column) {
		return move(row, column, false);
	}

	public boolean move(int row, int column, boolean castle) {
		if (legalMoves()[row][column] == true) {
			int oldRow = getRow();
			int oldColumn = getColumn();
			setRow(row);
			setColumn(column);
			chessboard.move(this, row, column, oldRow, oldColumn, castle);
			mHasMoved = true;
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
		getLegalMovesRight(board, enemy);
		getLegalMovesUp(board, enemy);
		getLegalMovesLeft(board, enemy);
		getLegalMovesDown(board, enemy);
		return board;
	}

	public boolean canCastle() {
		return !mHasMoved;
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
			// Blocked
			else if (chessboard.tileContains(i, getColumn(), false) != NO_PIECE) {
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
			// Blocked
			else if (chessboard.tileContains(getRow(), i, false) != NO_PIECE) {
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
			// Blocked
			else if (chessboard.tileContains(i, getColumn(), false) != NO_PIECE) {
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
			// Blocked
			else if (chessboard.tileContains(getRow(), i, false) != NO_PIECE) {
				break;
			}

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

}
