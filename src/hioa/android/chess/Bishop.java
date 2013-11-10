package hioa.android.chess;

public class Bishop extends Chesspiece {

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
		return null;
	}
	
	private void getLegalMovesUpRight(boolean[][] board, int enemy){
		
	}
	
	private void getLegalMovesUpLeft(boolean[][] board, int enemy){
		
	}
	
	private void getLegalMovesDownLeft(boolean[][] board, int enemy){
		
	}
	
	private void getLegalMovesDownRight(boolean[][] board, int enemy){
		
	}

	@Override
	public boolean threatensPosition(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}

}
