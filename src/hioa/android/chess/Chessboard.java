package hioa.android.chess;

import hioa.android.logviewer.R;
import android.content.Context;
import android.util.Log;

public class Chessboard {

	private Chesspiece[][] mChessboard;
	private Context mContext;

	public Chessboard(Context context) {
		mContext = context;
		Chesspiece.context = context;
		Chesspiece.chessboard = this;
		
	}

	public int tileContains(int row, int column) {
		if (mChessboard[row][column] != null)
			return mChessboard[row][column].getColor();
		else
			return Chesspiece.NO_PIECE;
	}
	
	public int getMaxRows(){
		return 1 + mContext.getResources().getInteger(R.integer.chesspiece_max_row_index);
	}
	
	public int getMaxColumns(){
		return 1 + mContext.getResources().getInteger(R.integer.chesspiece_max_column_index);
	}
}
