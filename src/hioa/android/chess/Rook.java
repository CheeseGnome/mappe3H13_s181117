package hioa.android.chess;

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
		getLegalMoves(board);
		return board;
	}
	
	public boolean canCastle(){
		return !mHasMoved;
	}

	private void getLegalMoves(boolean[][] board) {
		int enemy;
		if (getColor() == WHITE)
			enemy = BLACK;
		else
			enemy = WHITE;
		getLegalMovesRight(board, enemy);
		getLegalMovesUp(board, enemy);
		getLegalMovesLeft(board, enemy);
		getLegalMovesDown(board, enemy);
	}

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
