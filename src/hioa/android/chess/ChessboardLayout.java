package hioa.android.chess;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TableLayout;

/**
 * A visual representation of a chessboard based on the chessboardlayout.xml
 * file
 * 
 * @author Lars Sætaberget
 * @version 2013-11-15
 */

public class ChessboardLayout extends TableLayout {

	Chessboard mChessboard;
	ImageButton[][] mButtons;
	Resources mResources;
	Chesspiece mSelected;

	public ChessboardLayout(Context context, AttributeSet attributes) {
		super(context, attributes);

		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.chessboardlayout, this);
		mResources = getResources();
		setChessboard(new Chessboard(context));
		initializeButtonArray();
		insertPieces();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// canvas.setDrawFilter(new PaintFlagsDrawFilter(1,
		// Paint.ANTI_ALIAS_FLAG));
		super.dispatchDraw(canvas);
	}

	public void setChessboard(Chessboard board) {
		mChessboard = board;
	}

	private void insertPieces() {
		Chesspiece piece;
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				piece = mChessboard.getPieceAt(i, j);

				if (piece == null) {
					mButtons[i][j].setImageResource(android.R.color.transparent);
				} else {
					mButtons[i][j].setImageDrawable(getPieceIcon(piece));
				}
			}
		}
	}

	private Drawable getPieceIcon(Chesspiece piece) {
		int id = -1;
		if (piece.getColor() == Chesspiece.WHITE) {
			if (piece instanceof Pawn) {
				id = R.drawable.white_pawn;
			} else if (piece instanceof Rook) {
				id = R.drawable.white_rook;
			} else if (piece instanceof Knight) {
				id = R.drawable.white_knight;
			} else if (piece instanceof Bishop) {
				id = R.drawable.white_bishop;
			} else if (piece instanceof Queen) {
				id = R.drawable.white_queen;
			} else if (piece instanceof King) {
				id = R.drawable.white_king;
			}
		} else {
			if (piece instanceof Pawn) {
				id = R.drawable.black_pawn;
			} else if (piece instanceof Rook) {
				id = R.drawable.black_rook;
			} else if (piece instanceof Knight) {
				id = R.drawable.black_knight;
			} else if (piece instanceof Bishop) {
				id = R.drawable.black_bishop;
			} else if (piece instanceof Queen) {
				id = R.drawable.black_queen;
			} else if (piece instanceof King) {
				id = R.drawable.black_king;
			}
		}
		Drawable dr = mResources.getDrawable(id);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		int size = mResources.getDimensionPixelSize(R.dimen.tile_size);
		return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
	}

	private void initializeButtonArray() {
		mButtons = new ImageButton[mChessboard.getMaxRows()][mChessboard.getMaxColumns()];
		int id;
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				id = mResources.getIdentifier("tile" + i + j, "id", "hioa.android.chess");
				mButtons[i][j] = (ImageButton) findViewById(id);
				setButtonListener(mButtons[i][j], i, j);
			}
		}
	}

	private void setButtonListener(ImageButton button, final int row, final int column) {
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// TODO gjør ferdig
//				if (mSelected == null) {
					mSelected = mChessboard.getPieceAt(row, column);
					if (mSelected != null)
						setLegalMovesHint(mSelected.legalMoves());
//				} else {
					mSelected = null;
//				}
			}
		});
	}

	private void setLegalMovesHint(boolean[][] legalMoves) {
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				if(legalMoves[i][j]){
					mButtons[i][j].setBackgroundColor(mResources.getColor(android.R.color.holo_green_light));
				}else{
					mButtons[i][j].setBackgroundColor(mResources.getColor(android.R.color.transparent));
				}
			}
		}
	}
}
