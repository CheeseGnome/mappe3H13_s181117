package hioa.android.chess;

import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class PlayerFrame extends RelativeLayout {

	private BitmapDrawable[] mIcons;
	private static final int PAWN = 0, ROOK = 1, KNIGHT = 2, BISHOP = 3, QUEEN = 4;
	private static final float SHADOWRADIUS = 20;

	private LinkedList<Chesspiece> pieces = new LinkedList<Chesspiece>();
	private TextView mClock;

	private Context mContext;

	public PlayerFrame(Context context, AttributeSet attributes) {
		super(context, attributes);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.playerframe, this);
		mContext = context;
		mClock = (TextView) findViewById(R.id.txt_clock);
	}

	public void setName(String name) {
		((TextView) findViewById(R.id.txt_player_name)).setText(name);
	}

	public void resetPieces() {
		pieces.clear();
		drawPieces();
	}

	public void setTime(String time) {
		mClock.setText(time);
	}

	public void addPiece(Chesspiece piece) {
		if (piece instanceof Pawn) {
			pieces.addFirst(piece);
			drawPieces();
			return;
		} else if (piece instanceof Queen) {
			pieces.addLast(piece);
			drawPieces();
			return;
		}

		Iterator<Chesspiece> iterator = pieces.iterator();
		Chesspiece currentPiece;
		int index = -1;

		boolean added = false;

		while (iterator.hasNext()) {
			index++;
			currentPiece = iterator.next();
			if (addBefore(piece, currentPiece)) {
				pieces.add(index, piece);
				added = true;
				break;
			}
		}
		if (!added) {
			pieces.addLast(piece);
		}
		drawPieces();
	}

	public void setInCheck(boolean inCheck) {
		TextView check = (TextView) findViewById(R.id.txt_check);
		if (inCheck) {
			check.setText(getResources().getString(R.string.txt_check));
			check.setShadowLayer(SHADOWRADIUS, 0, 0, getResources().getColor(R.color.txt_check_shadow));
		} else {
			check.setText("");
			check.setShadowLayer(0, 0, 0, getResources().getColor(android.R.color.transparent));
		}
	}

	private boolean addBefore(Chesspiece piece, Chesspiece currentPiece) {
		if (piece instanceof Knight) {
			return (currentPiece instanceof Bishop || currentPiece instanceof Rook || currentPiece instanceof Queen);
		}
		if (piece instanceof Bishop) {
			return (currentPiece instanceof Rook || currentPiece instanceof Queen);
		}
		if (piece instanceof Rook) {
			return (currentPiece instanceof Queen);
		}
		throw new IllegalArgumentException("Illegal piece passed to addBefore()");
	}

	private void drawPieces() {
		Iterator<Chesspiece> iterator = pieces.iterator();
		int id;
		for (int i = 1; i < 16; i++) {
			id = mContext.getResources().getIdentifier("imageView" + i, "id", "hioa.android.chess");
			if (iterator.hasNext()) {
				((ImageView) findViewById(id)).setImageDrawable(getIcon(iterator.next()));
			} else {
				// Used when resetting the frame
				((ImageView) findViewById(id))
						.setImageDrawable(getResources().getDrawable(android.R.color.transparent));
			}
		}
		invalidate();
	}

	private Drawable getIcon(Chesspiece piece) {
		if (piece instanceof Pawn) {
			return mIcons[PAWN];
		}
		if (piece instanceof Knight) {
			return mIcons[KNIGHT];
		}
		if (piece instanceof Bishop) {
			return mIcons[BISHOP];
		}
		if (piece instanceof Rook) {
			return mIcons[ROOK];
		}
		if (piece instanceof Queen) {
			return mIcons[QUEEN];
		}
		return null;
	}

	public void loadIcons(int color) {
		mIcons = new BitmapDrawable[5];
		if (color == Chesspiece.BLACK) {
			mIcons[PAWN] = getDrawable(R.drawable.black_pawn);
			mIcons[ROOK] = getDrawable(R.drawable.black_rook);
			mIcons[KNIGHT] = getDrawable(R.drawable.black_knight);
			mIcons[BISHOP] = getDrawable(R.drawable.black_bishop);
			mIcons[QUEEN] = getDrawable(R.drawable.black_queen);
		} else {
			mIcons[PAWN] = getDrawable(R.drawable.white_pawn);
			mIcons[ROOK] = getDrawable(R.drawable.white_rook);
			mIcons[KNIGHT] = getDrawable(R.drawable.white_knight);
			mIcons[BISHOP] = getDrawable(R.drawable.white_bishop);
			mIcons[QUEEN] = getDrawable(R.drawable.white_queen);
		}
	}

	/**
	 * This method returns the drawable found at id, resized to fit inside a
	 * tile.
	 * 
	 * @param id
	 *            The image resource id
	 * @return The image found by the resource id resized to fit inside an
	 *         imagebutton in this view
	 */
	private BitmapDrawable getDrawable(int id) {
		Drawable dr = mContext.getResources().getDrawable(id);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		int size = mContext.getResources().getDimensionPixelSize(R.dimen.captured_size);
		return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
	}

}
