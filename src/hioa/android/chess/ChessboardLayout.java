package hioa.android.chess;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
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
	Resources resources;

	public ChessboardLayout(Context context, AttributeSet attributes) {
		super(context, attributes);
		LayoutInflater layoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.chessboardlayout, this);
		resources = getResources();
		initializeButtonArray();
	}

	public void setChessboard(Chessboard board) {
		mChessboard = board;
		insertPieces();
	}

	private void insertPieces() {
		Chesspiece piece;
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				piece = mChessboard.getPieceAt(i, j);

				if (piece == null) {
					mButtons[i][j]
							.setImageResource(android.R.color.transparent);
				} else {
					mButtons[i][j].setImageDrawable(getPieceIcon(piece));
				}
			}
		}
	}

	private Drawable getPieceIcon(Chesspiece piece) {
		//TODO denne burde skrives om til å bare bruke id direkte
		String identifier;
		if (piece.getColor() == Chesspiece.WHITE) {
			identifier = "white_";
		} else {
			identifier = "black_";
		}
		if (piece instanceof Pawn) {
			identifier += "pawn";
		} else if (piece instanceof Rook) {
			identifier += "rook";
		} else if (piece instanceof Knight) {
			identifier += "knight";
		} else if (piece instanceof Bishop) {
			identifier += "bishop";
		} else if (piece instanceof Queen) {
			identifier += "queen";
		} else if (piece instanceof King) {
			identifier += "king";
		}
		
		int id = resources.getIdentifier(identifier, "id", "hioa.android.chess");
		return resources.getDrawable(id);
	}

	private void initializeButtonArray() {
		mButtons = new ImageButton[mChessboard.getMaxRows()][mChessboard
				.getMaxColumns()];
		int id;
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				id = resources.getIdentifier("tile" + i + j, "id",
						"hioa.android.chess");
				mButtons[i][j] = (ImageButton) findViewById(id);
			}
		}
	}

}
