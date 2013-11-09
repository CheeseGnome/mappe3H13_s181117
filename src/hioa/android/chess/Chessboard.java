package hioa.android.chess;

import hioa.android.logviewer.R;
import android.content.Context;

public class Chessboard {

	private Chesspiece[][] mChessboard;
	private Context mContext;

	public Chessboard(Context context) {
		mContext = context;
		Chesspiece.context = context;
		Chesspiece.chessboard = this;
		
	}
	
	/**
	 * This method checks if the provided move will result in the piece's color's king being in check
	 * @param piece The piece to move
	 * @param row The row to move to
	 * @param column The column to move to
	 * @return True if the king ends up in check
	 */
	public boolean kingInCheckAfter(Chesspiece piece, int row, int column){
		return false;
	}

	/**
	 * Returns an int explaining what type of piece(if any) is in the given tile
	 * @param row The row to check
	 * @param column The column to check
	 * @return NO_PIECE, BLACK or WHITE as defined in {@link Chesspiece}
	 */
	public int tileContains(int row, int column) {
		if (mChessboard[row][column] != null)
			return mChessboard[row][column].getColor();
		else
			return Chesspiece.NO_PIECE;
	}
	/**
	 * Moves the piece to the provided row and column.
	 * <p>
	 * Note: This method does no error-checking and simply assumes that the move is legal
	 * @param piece The piece to move
	 * @param row The piece's new row position
	 * @param column The piece's new column position
	 */
	public void move(Chesspiece piece, int row, int column){
		mChessboard[piece.getRow()][piece.getColumn()] = null;
		mChessboard[row][column] = piece;
	}
	
	public int getMaxRows(){
		return 1 + mContext.getResources().getInteger(R.integer.chesspiece_max_row_index);
	}
	
	public int getMaxColumns(){
		return 1 + mContext.getResources().getInteger(R.integer.chesspiece_max_column_index);
	}
}
