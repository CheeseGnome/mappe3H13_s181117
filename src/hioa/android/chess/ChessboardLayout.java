package hioa.android.chess;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.TableLayout;

/**
 * A visual representation of a chessboard based on the chessboardlayout.xml file
 * @author Lars Sætaberget
 * @version 2013-11-15
 */

public class ChessboardLayout extends TableLayout {

	Chessboard mChessboard;
	
	public ChessboardLayout(Context context, AttributeSet attributes) {
		super(context, attributes);
		LayoutInflater layoutInflater = (LayoutInflater)context
	              .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    layoutInflater.inflate(R.layout.chessboardlayout, this);
	}
	
	public void setChessboard(Chessboard board){
		mChessboard = board;
	}

}
