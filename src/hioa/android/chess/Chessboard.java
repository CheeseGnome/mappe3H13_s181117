package hioa.android.chess;

import hioa.android.logviewer.R;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class Chessboard extends View {

	private ArrayList<Paint> mTiles = new ArrayList<Paint>(R.integer.chessboard_tiles_count);
	
	public Chessboard(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Chessboard(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public Chessboard(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}
	
	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas){
	}
	
	private void createPaintObjects(Color whiteTileColor, Color blackTileColor){
		for(int i = 0; i < mTiles.size(); i++){
			if(i % 2 == 0){
				mTiles.add(new Paint());
			}
			else{
				
			}
		}
	}

}
