package hioa.android.chess;

/**
 * Represents a rook
 * 
 * @author Lars Sætaberget
 * @version 2013-11-09
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
	public boolean move(int row, int column) {
		if (legalMoves()[row][column] == true) {
			chessboard.move(this, row, column);
			setRow(row);
			setColumn(column);
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

	private void getLegalMovesRight(boolean[][] board, int enemy) {
		for (int i = getRow() + 1; i < chessboard.getMaxRows(); i++) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, i, getColumn())
					&& chessboard.tileContains(i, getColumn()) == NO_PIECE) {
				board[i][getColumn()] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, i, getColumn())
					&& chessboard.tileContains(i, getColumn()) == enemy) {
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

	private void getLegalMovesUp(boolean[][] board, int enemy) {

		for (int i = getColumn() + 1; i < chessboard.getMaxRows(); i++) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, getRow(), i) && chessboard.tileContains(i, getColumn()) == NO_PIECE) {
				board[getRow()][i] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, getRow(), i)
					&& chessboard.tileContains(i, getColumn()) == enemy) {
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
	private void getLegalMovesLeft(boolean[][] board, int enemy) {

		for (int i = getRow() - 1; i < chessboard.getMaxRows(); i--) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, i, getColumn())
					&& chessboard.tileContains(i, getColumn()) == NO_PIECE) {
				board[i][getColumn()] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, i, getColumn())
					&& chessboard.tileContains(i, getColumn()) == enemy) {
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
	private void getLegalMovesDown(boolean[][] board, int enemy) {
		for (int i = getColumn() - 1; i < chessboard.getMaxRows(); i--) {
			// empty tile
			if (!chessboard.kingInCheckAfter(this, getRow(), i) && chessboard.tileContains(i, getColumn()) == NO_PIECE) {
				board[getRow()][i] = true;
			}
			// Piece which can be captured
			else if (!chessboard.kingInCheckAfter(this, getRow(), i)
					&& chessboard.tileContains(i, getColumn()) == enemy) {
				board[getRow()][i] = true;
				break;
			}
			// Friendly piece blocking
			else {
				break;
			}

		}
	}

}
