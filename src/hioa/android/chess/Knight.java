package hioa.android.chess;

/**
 * Represents a knight
 * @author Lars Sætaberget
 * @version 2013-11-10
 */

public class Knight extends Chesspiece {

	public Knight(int color, int row, int column) {
		setColor(color);
		setRow(row);
		setColumn(column);
	}
	
	@Override
	public boolean sameClass(Chesspiece piece){
		return (piece instanceof Knight);
	}
	
	@Override
	public boolean move(int row, int column) {
		if (legalMoves()[row][column] == true) {
			int oldRow = getRow();
			int oldColumn = getColumn();
			setRow(row);
			setColumn(column);
			chessboard.move(this, row, column, oldRow, oldColumn);
			return true;
		} else
			return false;
	}
	
	@Override
	public boolean threatensPosition(int row, int column) {
		if(getRow() == row - 2 || getRow() == row + 2){
			if(getColumn() == column - 1 || getColumn() == column + 1)
				return true;
		}
		if(getRow() == row - 1 || getRow() == row + 1){
			if(getColumn() == column - 2 || getColumn() == column + 2)
				return true;
		}
		return false;
	}

	@Override
	public boolean[][] legalMoves() {
		boolean[][] board = new boolean[chessboard.getMaxRows()][chessboard.getMaxColumns()];
		int row = getRow();
		int column = getColumn();

		if (legalIndexes(row + 2, column + 1))
			board[row + 2][column + 1] = legalMove(row + 2, column + 1);

		if (legalIndexes(row + 2, column - 1))
			board[row + 2][column - 1] = legalMove(row + 2, column - 1);

		if (legalIndexes(row - 2, column + 1))
			board[row - 2][column + 1] = legalMove(row - 2, column + 1);

		if (legalIndexes(row - 2, column - 1))
			board[row - 2][column - 1] = legalMove(row - 2, column - 1);

		if (legalIndexes(row + 1, column + 2))
			board[row + 1][column + 2] = legalMove(row + 1, column + 2);

		if (legalIndexes(row + 1, column - 2))
			board[row + 1][column - 2] = legalMove(row + 1, column - 2);

		if (legalIndexes(row - 1, column + 2))
			board[row - 1][column + 2] = legalMove(row - 1, column + 2);

		if (legalIndexes(row - 1, column - 2))
			board[row - 1][column - 2] = legalMove(row - 1, column - 2);

		return board;
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
		return (row >= 0 && row < chessboard.getMaxRows() && column >= 0 && column < chessboard.getMaxColumns());
	}

}
