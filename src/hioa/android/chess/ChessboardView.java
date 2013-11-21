package hioa.android.chess;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;

/**
 * A visual representation of a chessboard based on the chessboardlayout.xml
 * file
 * 
 * @author Lars Sætaberget
 * @version 2013-11-15
 */

public class ChessboardView extends TableLayout {

	private Chessboard mChessboard;
	private ImageButton[][] mButtons;
	private GameActivity mActivity;
	private boolean[][] mLegalMoves;
	private Resources mResources;
	private String mWhiteName, mBlackName;
	public static final int DRAWREPETITION = 0, DRAWCLAIMED = 1, DRAWAGREED = 2, WINCHECKMATE = 3, WINRESIGN = 4,
			DRAWSTALEMATE = 5, WINTIMEOUT = 6;
	/**
	 * The currently selected chesspiece.
	 * <p>
	 * This is the piece that will be moved if a legal move is clicked
	 */
	private Chesspiece mSelected;
	private Context mContext;
	private int mCurrentPlayer = Chesspiece.WHITE;

	/**
	 * Array of the icons for the various chesspieces.
	 * <p>
	 * This array is indexed through int constants named: BLACKPAWN, WHITEKING
	 * etc.
	 */
	private BitmapDrawable[] mIcons;
	private static final int BLACKPAWN = 0, BLACKROOK = 1, BLACKKNIGHT = 2, BLACKBISHOP = 3, BLACKQUEEN = 4,
			BLACKKING = 5, WHITEPAWN = 6, WHITEROOK = 7, WHITEKNIGHT = 8, WHITEBISHOP = 9, WHITEQUEEN = 10,
			WHITEKING = 11;

	public ChessboardView(Context context, AttributeSet attributes) {
		super(context, attributes);

		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.chessboardlayout, this);
		mResources = getResources();
		mContext = context;

		initializeDrawableArray();
		mChessboard = new Chessboard(context);
		mChessboard.setChessboardView(this);
		initializeButtonArray();
		placePieces();
	}

	public void timeOut(final int color) {
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				endTheGame(ChessboardView.WINTIMEOUT, color);
			}
		});
	}

	public void setTime(long startTime, long bonusTime) {
		mChessboard.setTime(startTime, bonusTime);
	}

	public void updateClock(final int color, final long time) {
		mActivity.updateClock(color, time);
	}

	public Chessboard getChessboard() {
		return mChessboard;
	}

	public String getWhiteName() {
		return mWhiteName;
	}

	public String getBlackName() {
		return mBlackName;
	}

	public void setActivity(GameActivity activity) {
		mActivity = activity;
		mChessboard.setActivity(activity);
	}

	public void setPlayerNames(String whiteName, String blackName) {
		mWhiteName = whiteName;
		mBlackName = blackName;
	}

	/**
	 * Loads the chesspiece icons
	 */
	private void initializeDrawableArray() {
		mIcons = new BitmapDrawable[12];
		mIcons[BLACKPAWN] = getDrawable(R.drawable.black_pawn);
		mIcons[BLACKROOK] = getDrawable(R.drawable.black_rook);
		mIcons[BLACKKNIGHT] = getDrawable(R.drawable.black_knight);
		mIcons[BLACKBISHOP] = getDrawable(R.drawable.black_bishop);
		mIcons[BLACKQUEEN] = getDrawable(R.drawable.black_queen);
		mIcons[BLACKKING] = getDrawable(R.drawable.black_king);

		mIcons[WHITEPAWN] = getDrawable(R.drawable.white_pawn);
		mIcons[WHITEROOK] = getDrawable(R.drawable.white_rook);
		mIcons[WHITEKNIGHT] = getDrawable(R.drawable.white_knight);
		mIcons[WHITEBISHOP] = getDrawable(R.drawable.white_bishop);
		mIcons[WHITEQUEEN] = getDrawable(R.drawable.white_queen);
		mIcons[WHITEKING] = getDrawable(R.drawable.white_king);
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
		Drawable dr = mResources.getDrawable(id);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		int size = mResources.getDimensionPixelSize(R.dimen.tile_size);
		return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
	}

	/**
	 * Ends the game due to the reason given in the flag.
	 * 
	 * @param flag
	 *            A constant explaining why the game ended
	 * @param color
	 *            The color that made the last move
	 */
	public void endTheGame(int flag, int color) {
		final Dialog dialog = new Dialog(mContext);
		TableLayout contentView = (TableLayout) View.inflate(mContext, R.layout.endgamedialog, null);
		dialog.setContentView(contentView);

		((Button) contentView.findViewById(R.id.btn_main_menu)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				mActivity.finish();
			}
		});
		final ChessboardView view = this;
		((Button) contentView.findViewById(R.id.btn_new_game)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mChessboard = new Chessboard(mContext);
				mChessboard.setTime(mActivity.getStartTime(), mActivity.getBonusTime());
				mChessboard.setChessboardView(view);
				mCurrentPlayer = Chesspiece.WHITE;
				mActivity.updateClock(Chesspiece.WHITE, mActivity.getStartTime());
				mActivity.updateClock(Chesspiece.BLACK, mActivity.getStartTime());
				mActivity.setChessboard(mChessboard);
				placePieces();
				dialog.dismiss();
			}
		});

		String title = "", body = "";
		String winner = (color == Chesspiece.WHITE ? mWhiteName : mBlackName);
		String loser = (winner.equals(mBlackName) ? mWhiteName : mBlackName);
		switch (flag) {
		case DRAWREPETITION:
			title = mResources.getString(R.string.title_draw);
			body = mResources.getString(R.string.txt_draw_repetition);
			break;
		case DRAWAGREED:
			title = mResources.getString(R.string.title_draw);
			body = mResources.getString(R.string.txt_draw_agreed);
			break;
		case DRAWCLAIMED:
			title = mResources.getString(R.string.title_draw);
			body = mResources.getString(R.string.txt_draw_claimed);
			break;
		case WINCHECKMATE:
			title = mResources.getString(R.string.title_win_checkmate);
			body = winner + " " + mResources.getString(R.string.txt_win_checkmate);
			break;
		case WINRESIGN:
			title = mResources.getString(R.string.title_win_resign);
			body = loser + " " + mResources.getString(R.string.txt_win_resign_1) + " " + winner + " "
					+ mResources.getString(R.string.txt_win_resign_2);
			break;
		case DRAWSTALEMATE:
			title = mResources.getString(R.string.title_draw);
			body = mResources.getString(R.string.txt_draw_stalemate);
			break;
		case WINTIMEOUT:
			title = mResources.getString(R.string.title_win_timeout);
			body = loser + " " + mResources.getString(R.string.txt_win_timeout_1) + " " + winner + " "
					+ mResources.getString(R.string.txt_win_timeout_2);

		}

		dialog.setTitle(title);
		((TextView) contentView.findViewById(R.id.txt_endgame)).setText(body);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	/**
	 * Calls a dialog enabling the player to select a piece to promote to.
	 * 
	 * @param pawn
	 * @param row
	 * @param column
	 */
	public void promote(Pawn pawn, final int row, final int column) {
		final Dialog dialog = new Dialog(mContext);
		TableLayout contentView = (TableLayout) View.inflate(mContext, R.layout.promotiondialog, null);
		dialog.setContentView(contentView);

		Queen queen = new Queen(pawn.getColor(), pawn.getRow(), pawn.getColumn());
		Rook rook = new Rook(pawn.getColor(), pawn.getRow(), pawn.getColumn());
		Bishop bishop = new Bishop(pawn.getColor(), pawn.getRow(), pawn.getColumn());
		Knight knight = new Knight(pawn.getColor(), pawn.getRow(), pawn.getColumn());

		ImageButton btn_queen = (ImageButton) contentView.findViewById(R.id.btn_queen);
		ImageButton btn_rook = (ImageButton) contentView.findViewById(R.id.btn_rook);
		ImageButton btn_bishop = (ImageButton) contentView.findViewById(R.id.btn_bishop);
		ImageButton btn_knight = (ImageButton) contentView.findViewById(R.id.btn_knight);

		btn_queen.setImageDrawable(getPieceIcon(queen));
		btn_rook.setImageDrawable(getPieceIcon(rook));
		btn_bishop.setImageDrawable(getPieceIcon(bishop));
		btn_knight.setImageDrawable(getPieceIcon(knight));

		btn_queen.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mChessboard.setPromotionFlag(Chessboard.QUEEN);
				performMove(row, column);
				dialog.dismiss();
			}
		});
		btn_rook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mChessboard.setPromotionFlag(Chessboard.ROOK);
				performMove(row, column);
				dialog.dismiss();
			}
		});
		btn_bishop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mChessboard.setPromotionFlag(Chessboard.BISHOP);
				performMove(row, column);
				dialog.dismiss();
			}
		});
		btn_knight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mChessboard.setPromotionFlag(Chessboard.KNIGHT);
				performMove(row, column);
				dialog.dismiss();
			}
		});
		dialog.setTitle(mResources.getString(R.string.promotion_title));
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
	}

	/**
	 * This method iterates through the {@link Chessboard} and places icons in
	 * the correct locations in this view
	 */
	private void placePieces() {
		Chesspiece piece;
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				piece = mChessboard.getPieceAt(i, j);
				if (piece == null || piece.getColor() == Chesspiece.EN_PASSANT) {
					mButtons[i][j].setImageResource(android.R.color.transparent);
				} else {
					mButtons[i][j].setImageDrawable(getPieceIcon(piece));
				}
			}
		}
	}

	/**
	 * Returns the appropriate icon for the provided piece
	 * 
	 * @param piece
	 *            The piece whose drawable to return
	 * @return A drawable representing the piece, scaled to fit inside a button
	 *         in this view
	 */
	private Drawable getPieceIcon(Chesspiece piece) {

		if (piece.getColor() == Chesspiece.WHITE) {
			if (piece instanceof Pawn) {
				return mIcons[WHITEPAWN];
			} else if (piece instanceof Rook) {
				return mIcons[WHITEROOK];
			} else if (piece instanceof Knight) {
				return mIcons[WHITEKNIGHT];
			} else if (piece instanceof Bishop) {
				return mIcons[WHITEBISHOP];
			} else if (piece instanceof Queen) {
				return mIcons[WHITEQUEEN];
			} else if (piece instanceof King) {
				return mIcons[WHITEKING];
			}
		} else {
			if (piece instanceof Pawn) {
				return mIcons[BLACKPAWN];
			} else if (piece instanceof Rook) {
				return mIcons[BLACKROOK];
			} else if (piece instanceof Knight) {
				return mIcons[BLACKKNIGHT];
			} else if (piece instanceof Bishop) {
				return mIcons[BLACKBISHOP];
			} else if (piece instanceof Queen) {
				return mIcons[BLACKQUEEN];
			} else if (piece instanceof King) {
				return mIcons[BLACKKING];
			}
		}
		return null;
	}

	/**
	 * Tie the classes array of buttons together with the matching buttons in
	 * the view and sets their onclick methods
	 */
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

	/**
	 * Sets the onclick method for the button in the provided location
	 * 
	 * @param button
	 *            The button whose onclick method you want to set
	 * @param row
	 *            The row position the button is representing
	 * @param column
	 *            The column position the button is representing
	 */
	private void setButtonListener(ImageButton button, final int row, final int column) {
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// We want this as early as possible
				mChessboard.setMoving(true);
				Chesspiece piece = mChessboard.getPieceAt(row, column);

				if (piece != null && piece.getColor() == mCurrentPlayer) {
					mChessboard.setMoving(false);
					mSelected = piece;
					mLegalMoves = mSelected.legalMoves();
					setLegalMovesHint();
				} else if (mLegalMoves != null && mLegalMoves[row][column]) {
					if (mSelected instanceof Pawn) {
						if (row == 0 || row == mChessboard.getMaxRows() - 1) {
							promote((Pawn) mSelected, row, column);
							return;
						}
					}
					performMove(row, column);
				} else {
					mChessboard.setMoving(false);
					mSelected = null;
					mLegalMoves = null;
					setLegalMovesHint();
				}
			}
		});
	}

	/**
	 * Move the currently selected piece to position row, column.
	 * <p>
	 * Also calls methods to update the view to it's new state
	 * 
	 * @param row
	 *            The row to move to
	 * @param column
	 *            The column to move to
	 */
	private void performMove(int row, int column) {
		mSelected.move(row, column);

		mSelected = null;
		mLegalMoves = null;
		if (mCurrentPlayer == Chesspiece.WHITE) {
			mCurrentPlayer = Chesspiece.BLACK;
		} else {
			mCurrentPlayer = Chesspiece.WHITE;
		}
		setLegalMovesHint();
		placePieces();
	}

	/**
	 * Marks the tiles that are considered legal moves for the currently
	 * selected piece
	 */
	private void setLegalMovesHint() {
		int id = -1;
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				if (mLegalMoves != null && mLegalMoves[i][j]) {
					switch (getTileColorId(i, j)) {
					case R.color.white_tile:
						id = R.color.white_tile_marked;
						break;
					case R.color.black_tile:
						id = R.color.black_tile_marked;
						break;
					}
					mButtons[i][j].setBackgroundColor(mResources.getColor(id));
				} else if (mSelected != null && mSelected.getRow() == i && mSelected.getColumn() == j) {
					mButtons[i][j].setBackgroundColor(mResources.getColor(R.color.selected_piece_tile));
				} else {
					mButtons[i][j].setBackgroundColor(mResources.getColor(getTileColorId(i, j)));
				}
			}
		}

	}

	/**
	 * Returns the ID for the background color of the provided position
	 * 
	 * @param row
	 *            The row position to get the color for
	 * @param column
	 *            The column position to get the color for
	 * @return The id of the color for this tile
	 */
	private int getTileColorId(int row, int column) {
		int id = -1;
		if (row % 2 == 0) {
			if (column % 2 == 0) {
				id = R.color.white_tile;
			} else {

				id = R.color.black_tile;
			}
		} else {
			if (column % 2 == 0) {
				id = R.color.black_tile;
			} else {
				id = R.color.white_tile;
			}
		}
		return id;
	}
}
