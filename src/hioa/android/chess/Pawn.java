package hioa.android.chess;

/**
 * Represents a pawn
 * 
 * @author Lars Sætaberget
 * @version 2013-11-09
 */

public class Pawn extends Chesspiece {

	// TODO En passant
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
	public boolean move(int row, int column) {
		if (legalMoves()[row][column] == true) {
			setRow(row);
			setColumn(column);
			mHasMoved = true;
			return true;
		} else
			return false;
	}

	@Override
	public boolean[][] legalMoves() {
		int row = getRow();
		int column = getColumn();
		boolean[][] board = new boolean[chessboard.getMaxRows()][chessboard.getMaxColumns()];

		if (getColor() == WHITE) {
			getLegalWhiteMoves(board, row, column);
		} else {
			getLegalBlackMoves(board, row, column);
		}
		return board;
	}

	private void getLegalBlackMoves(boolean[][] board, int row, int column) {
		if (chessboard.tileContains(row + 1, column - 1) == WHITE) {
			board[row + 1][column - 1] = true;
		}
		if (chessboard.tileContains(row + 1, column + 1) == WHITE) {
			board[row + 1][column + 1] = true;
		}
		if (chessboard.tileContains(row + 1, column) == NO_PIECE) {
			board[row + 1][column] = true;
		}
		if (mHasMoved == false && chessboard.tileContains(row + 2, column) == NO_PIECE && chessboard.tileContains(row + 1, column) == NO_PIECE) {
			board[row + 2][column] = true;
		} 
	}

	private void getLegalWhiteMoves(boolean[][] board, int row, int column) {
		if (chessboard.tileContains(row - 1, column - 1) == BLACK) {
			board[row - 1][column - 1] = true;
		}
		if (chessboard.tileContains(row - 1, column + 1) == BLACK) {
			board[row - 1][column + 1] = true;
		}
		if (chessboard.tileContains(row - 1, column) == NO_PIECE) {
			board[row - 1][column] = true;
		}
		if (mHasMoved == false && chessboard.tileContains(row - 2, column) == NO_PIECE && chessboard.tileContains(row - 1, column) == NO_PIECE) {
			board[row - 2][column] = true;
		} 
	}

}
