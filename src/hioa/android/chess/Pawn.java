package hioa.android.chess;

/**
 * Represents a pawn
 * 
 * @author Lars Sætaberget
 * @version 2013-11-09
 */

public class Pawn extends Chesspiece {

	// TODO En passant
	/*
	 * Can make en passant by placing an invisible en passant piece on double moves
	 */
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
		if (!chessboard.kingInCheckAfter(this, row + 1, column - 1)
				&& chessboard.tileContains(row + 1, column - 1) == WHITE) {
			board[row + 1][column - 1] = true;
		}
		// capture right
		if (!chessboard.kingInCheckAfter(this, row + 1, column + 1)
				&& chessboard.tileContains(row + 1, column + 1) == WHITE) {
			board[row + 1][column + 1] = true;
		}
		// move forward 1
		if (!chessboard.kingInCheckAfter(this, row + 1, column) && chessboard.tileContains(row + 1, column) == NO_PIECE) {
			board[row + 1][column] = true;
		}
		// move forward 2
		if (!chessboard.kingInCheckAfter(this, row + 2, column) && mHasMoved == false
				&& chessboard.tileContains(row + 2, column) == NO_PIECE
				&& chessboard.tileContains(row + 1, column) == NO_PIECE) {
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
		if (!chessboard.kingInCheckAfter(this, row - 1, column - 1)
				&& chessboard.tileContains(row - 1, column - 1) == BLACK) {
			board[row - 1][column - 1] = true;
		}
		// capture right
		if (!chessboard.kingInCheckAfter(this, row - 1, column + 1)
				&& chessboard.tileContains(row - 1, column + 1) == BLACK) {
			board[row - 1][column + 1] = true;
		}
		// move forward 1
		if (!chessboard.kingInCheckAfter(this, row - 1, column) && chessboard.tileContains(row - 1, column) == NO_PIECE) {
			board[row - 1][column] = true;
		}
		// move forward 2
		if (!chessboard.kingInCheckAfter(this, row - 2, column) && mHasMoved == false
				&& chessboard.tileContains(row - 2, column) == NO_PIECE
				&& chessboard.tileContains(row - 1, column) == NO_PIECE) {
			board[row - 2][column] = true;
		}
	}

}
