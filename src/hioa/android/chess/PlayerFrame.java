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

/**
 * A view that contains a player's name, clock, captured pieces and provides
 * info on the game state(in check etc)
 * 
 * @author Lars Sætaberget
 * @version 2013-11-23
 */

public class PlayerFrame extends RelativeLayout {

	private static final float SHADOWRADIUS = 20;

	public static final int NO_CHECK = 0, CHECK = 1, CHECKMATE = 2, WINNER = 3, DRAW = 4, RESIGNED = 5, TIMEOUT = 6;

	private LinkedList<Chesspiece> mCapturedPieces = new LinkedList<Chesspiece>();
	private TextView mClock;
	private GameActivity mActivity;
	private int mColor;
	private Context mContext;

	public PlayerFrame(Context context, AttributeSet attributes) {
		super(context, attributes);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.playerframe, this);
		mContext = context;
		mClock = (TextView) findViewById(R.id.txt_clock);
	}

	public void setActivity(GameActivity activity) {
		mActivity = activity;
	}

	public void setName(String name) {
		((TextView) findViewById(R.id.txt_player_name)).setText(name);
	}

	/**
	 * Sets the King icon for this view to the king image of the selected color
	 * 
	 * @param color
	 */
	public void setKingIcon(int color) {
		mColor = color;
		ImageView image = (ImageView) findViewById(R.id.img_king);
		if (color == Chesspiece.WHITE) {
			image.setImageDrawable(getDrawable(R.drawable.white_king, R.dimen.img_king_size));
		} else {

			image.setImageDrawable(getDrawable(R.drawable.black_king, R.dimen.img_king_size));
		}
	}

	/**
	 * Clears all the captured pieces from the view
	 */
	public void resetPieces() {
		mCapturedPieces.clear();
		drawPieces();
	}

	/**
	 * Sets the time string
	 * 
	 * @param time
	 *            The formatted time string to display
	 */
	public void setTime(String time) {
		mClock.setText(time);
	}

	/**
	 * Adds a captured piece to the list and displays it in order:</br> Pawn -
	 * Knight - Bishop - Rook - Queen
	 * 
	 * @param piece
	 */
	public void addPiece(Chesspiece piece) {
		if (piece instanceof Pawn) {
			mCapturedPieces.addFirst(piece);
			drawPieces();
			return;
		} else if (piece instanceof Queen) {
			mCapturedPieces.addLast(piece);
			drawPieces();
			return;
		}

		Iterator<Chesspiece> iterator = mCapturedPieces.iterator();
		Chesspiece currentPiece;
		int index = -1;

		boolean added = false;

		while (iterator.hasNext()) {
			index++;
			currentPiece = iterator.next();
			if (addBefore(piece, currentPiece)) {
				mCapturedPieces.add(index, piece);
				added = true;
				break;
			}
		}
		if (!added) {
			mCapturedPieces.addLast(piece);
		}
		drawPieces();
	}

	/**
	 * Sets the large info text for this view based on the provided flag
	 * 
	 * @param flag
	 *            NO_CHECK to hide this TextView from the frame, or another flag
	 *            provided in this class
	 */
	public void setCheckText(int flag) {
		TextView check = (TextView) findViewById(R.id.txt_check);
		switch (flag) {
		case NO_CHECK:
			check.setText("");
			check.setShadowLayer(0, 0, 0, getResources().getColor(android.R.color.transparent));
			break;
		case CHECK:
			check.setTextColor(getResources().getColor(R.color.txt_check_color));
			check.setText(getResources().getString(R.string.txt_check));
			check.setShadowLayer(SHADOWRADIUS, 0, 0, getResources().getColor(R.color.txt_check_shadow));
			break;
		case CHECKMATE:
			check.setTextColor(getResources().getColor(R.color.txt_check_color));
			check.setText(getResources().getString(R.string.txt_checkmate));
			check.setShadowLayer(SHADOWRADIUS, 0, 0, getResources().getColor(R.color.txt_check_shadow));
			break;
		case WINNER:
			check.setTextColor(getResources().getColor(R.color.txt_winner_color));
			check.setText(getResources().getString(R.string.txt_winner));
			check.setShadowLayer(SHADOWRADIUS, 0, 0, getResources().getColor(R.color.txt_check_shadow));
			break;
		case DRAW:
			check.setTextColor(getResources().getColor(R.color.txt_draw_color));
			check.setText(getResources().getString(R.string.txt_draw));
			check.setShadowLayer(SHADOWRADIUS, 0, 0, getResources().getColor(R.color.txt_check_shadow));
			break;
		case RESIGNED:
			check.setTextColor(getResources().getColor(R.color.txt_check_color));
			check.setText(getResources().getString(R.string.txt_resign));
			check.setShadowLayer(SHADOWRADIUS, 0, 0, getResources().getColor(R.color.txt_check_shadow));
			break;
		case TIMEOUT:
			check.setTextColor(getResources().getColor(R.color.txt_check_color));
			check.setText(getResources().getString(R.string.txt_timeout));
			check.setShadowLayer(SHADOWRADIUS, 0, 0, getResources().getColor(R.color.txt_check_shadow));
			break;
		}
	}

	/**
	 * Help method to determine if the piece should be added before currentPiece
	 * in the list
	 * 
	 * @param piece
	 *            The piece to add
	 * @param currentPiece
	 *            The piece that was jsut iterated
	 * @return True if the piece should be added here
	 */
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

	/**
	 * Sets the imageviews to display the contents of the list
	 */
	private void drawPieces() {
		Iterator<Chesspiece> iterator = mCapturedPieces.iterator();
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

	/**
	 * Gets the icon for this piece
	 * 
	 * @param piece
	 *            The piece who's icon to retrieve
	 * @return A drawable scaled to fit inside the imageViews
	 */
	private Drawable getIcon(Chesspiece piece) {
		if (mColor == Chesspiece.WHITE) {
			if (piece instanceof Pawn) {
				return mActivity.getPieceIcon(GameActivity.WHITEPAWN);
			}
			if (piece instanceof Knight) {
				return mActivity.getPieceIcon(GameActivity.WHITEKNIGHT);
			}
			if (piece instanceof Bishop) {
				return mActivity.getPieceIcon(GameActivity.WHITEBISHOP);
			}
			if (piece instanceof Rook) {
				return mActivity.getPieceIcon(GameActivity.WHITEROOK);
			}
			if (piece instanceof Queen) {
				return mActivity.getPieceIcon(GameActivity.WHITEQUEEN);
			}
		} else {
			if (piece instanceof Pawn) {
				return mActivity.getPieceIcon(GameActivity.BLACKPAWN);
			}
			if (piece instanceof Knight) {
				return mActivity.getPieceIcon(GameActivity.BLACKKNIGHT);
			}
			if (piece instanceof Bishop) {
				return mActivity.getPieceIcon(GameActivity.BLACKBISHOP);
			}
			if (piece instanceof Rook) {
				return mActivity.getPieceIcon(GameActivity.BLACKROOK);
			}
			if (piece instanceof Queen) {
				return mActivity.getPieceIcon(GameActivity.BLACKQUEEN);
			}
		}
		return null;
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
	private BitmapDrawable getDrawable(int id, int sizeId) {
		Drawable dr = mContext.getResources().getDrawable(id);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		int size = mContext.getResources().getDimensionPixelSize(sizeId);
		return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
	}

}
