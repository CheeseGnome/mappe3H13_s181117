package hioa.android.chess;

import java.util.Date;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A visual representation of a chessboard based on the chessboardlayout.xml
 * file
 * 
 * @author Lars Sætaberget
 * @version 2013-11-23
 */

public class ChessboardView extends TableLayout {

	private Chessboard mChessboard;
	private ImageButton[][] mButtons;
	private GameActivity mActivity;
	private boolean[][] mLegalMoves;
	private Resources mResources;
	private String mWhiteName, mBlackName;
	private int mOldRow = -1, mOldColumn = -1, mRow = -1, mColumn = -1;
	public static final int DRAWREPETITION = 0, DRAWCLAIMED = 1, DRAWAGREED = 2, WINCHECKMATE = 3, WINRESIGN = 4,
			DRAWSTALEMATE = 5, WINTIMEOUT = 6;

	private static final long VIEWBOARDTIME = 3 * 1000;
	/**
	 * The currently selected chesspiece.
	 * <p>
	 * This is the piece that will be moved if a legal move is clicked
	 */
	private Chesspiece mSelected;
	private Context mContext;
	private int mCurrentPlayer = Chesspiece.WHITE;
	private MediaPlayer mPlayer;
	private boolean mMute;

	public ChessboardView(Context context, AttributeSet attributes) {
		super(context, attributes);
		LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutInflater.inflate(R.layout.chessboardlayout, this);
		mResources = getResources();
		mContext = context;

		mChessboard = new Chessboard(context);
		mChessboard.setChessboardView(this);
		initializeButtonArray();
	}

	/**
	 * Mute the sounds coming from this view
	 * 
	 * @param mute
	 *            True to mute, false to unmute
	 */
	public void setMute(boolean mute) {
		mMute = mute;
	}

	/**
	 * Plays the move sound
	 */
	private void playMoveSound() {
		if (mMute) {
			return;
		}
		mPlayer = MediaPlayer.create(mContext, R.raw.move);
		mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mp.release();
			}

		});
		mPlayer.start();
	}

	/**
	 * Sets the hint indexes indicating what the last move was.
	 * <p>
	 * The actual coloring of the tiles happens in getLegalMovesHint()
	 * 
	 * @param oldRow
	 *            The row the piece was on
	 * @param oldColumn
	 *            The column the piece was on
	 * @param row
	 *            The row the piece is now on
	 * @param column
	 *            The column the piece is now on
	 */
	public void setLastMoveHint(int oldRow, int oldColumn, int row, int column) {
		mOldRow = oldRow;
		mOldColumn = oldColumn;
		mRow = row;
		mColumn = column;
	}

	/**
	 * Queues an endTheGame() method in the UI thread based on time out
	 * 
	 * @param color
	 *            The color who won by time out
	 */
	public void timeOut(final int color) {
		final int enemy;
		if (color == Chesspiece.WHITE) {
			enemy = Chesspiece.BLACK;
		} else {
			enemy = Chesspiece.WHITE;
		}
		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				endTheGame(ChessboardView.WINTIMEOUT, color);
				mActivity.setCheckText(enemy, PlayerFrame.TIMEOUT);
				mActivity.setCheckText(color, PlayerFrame.WINNER);
			}
		});
	}

	public void setCurrentPlayer(int color) {
		mCurrentPlayer = color;
	}

	/**
	 * Re-evaluate and refresh all the visual components in this view to reflect
	 * the state of the underlying {@link Chessboard}
	 */
	public void reDraw() {
		placePieces();
		setLegalMovesHint();
	}

	/**
	 * Returns an int Chesspiece.WHITE or Chesspiece.BLACK depending on who's
	 * turn it is
	 * 
	 * @return Chesspiece.WHITE or Chesspiece.BLACK
	 */
	public int getCurrentPlayer() {
		return mCurrentPlayer;
	}

	/**
	 * Gets the underlying {@link Chessboard} that is currently being used
	 * 
	 * @return A Chessboard object
	 */
	public Chessboard getChessboard() {
		return mChessboard;
	}

	public String getWhiteName() {
		return mWhiteName;
	}

	public String getBlackName() {
		return mBlackName;
	}

	/**
	 * Sets the activity for this view and the underlying {@link Chessboard}
	 * 
	 * @param activity
	 *            The activity to set
	 */
	public void setActivity(GameActivity activity) {
		mActivity = activity;
		mChessboard.setActivity(activity);
	}

	public void setPlayerNames(String whiteName, String blackName) {
		mWhiteName = whiteName;
		mBlackName = blackName;
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
		mSelected = null;
		mLegalMoves = null;
		mChessboard.stopClock();
		mRow = mColumn = mOldRow = mOldColumn = -1;
		mActivity.setButtonsEnabled(false);
		DBAdapter database = new DBAdapter(mContext);
		database.open();
		database.clearGameState();
		setLegalMovesHint();

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
		((Button) contentView.findViewById(R.id.btn_new_game)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonsEnabled(true);
				mChessboard = new Chessboard(mContext);
				mChessboard.setTime(mActivity.getStartTime(), mActivity.getBonusTime());
				mChessboard.setChessboardView(ChessboardView.this);
				mChessboard.setActivity(mActivity);
				mCurrentPlayer = Chesspiece.WHITE;
				mActivity.newGame(mChessboard);
				placePieces();
				dialog.dismiss();
			}
		});
		((Button) contentView.findViewById(R.id.btn_view_annotations)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final AlertDialog annotations = new AlertDialog.Builder(mActivity).create();
				annotations.setTitle(R.string.btn_annotations);
				final StringBuilder builder = new StringBuilder();
				String[] moves = mChessboard.mPositionHashFactory.getMovesArray();

				/*
				 * Don't waste your time trying to understand the string
				 * formatting that is done here. It sort of works.
				 */
				int moveNr = -1;
				String space1 = "  ";
				String space2 = " ";
				String space3 = "";
				String white = "";
				int spaces = 0;
				for (int i = 0; i < mChessboard.mPositionHashFactory.getCurrentMovesIndex(); i++) {
					if (i % 2 == 0) {
						moveNr = (i / 2) + 1;
						if (moveNr == 10) {
							space1 = "";
							space2 = "";
						}
						white = moves[i];
					} else {
						spaces = 8 - white.length();
						for (; spaces >= 0; spaces--) {
							space3 += " ";
						}
						builder.append(String.format("%-10s%-10s%s", space1 + moveNr + ".", space2 + white, space3
								+ moves[i] + "\n"));
						space3 = "";
					}
				}
				// White ends the game
				if (mChessboard.mPositionHashFactory.getCurrentMovesIndex() % 2 != 0) {
					builder.append(String.format("%-10s%s", space1 + moveNr + ".", space2 + white));
				}
				annotations.setMessage(builder.toString());
				annotations.setButton(AlertDialog.BUTTON_POSITIVE,
						(CharSequence) mResources.getString(R.string.btn_ok), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								annotations.dismiss();
							}

						});
				annotations.setButton(AlertDialog.BUTTON_NEUTRAL,
						(CharSequence) mResources.getString(R.string.btn_copy), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ClipboardManager clipboard = (ClipboardManager) mContext
										.getSystemService(Context.CLIPBOARD_SERVICE);
								ClipData clip = ClipData.newPlainText("Chess annotation", builder.toString());
								clipboard.setPrimaryClip(clip);
								Toast.makeText(mContext, mContext.getString(R.string.toast_annotations),
										Toast.LENGTH_LONG).show();
							}

						});
				annotations.show();
			}
		});
		((Button) contentView.findViewById(R.id.btn_view_board)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setButtonsEnabled(false);
				dialog.hide();
				new Thread(new Runnable() {
					public void run() {
						Date now = new Date();
						while (new Date().getTime() - now.getTime() < VIEWBOARDTIME) {
						}
						mActivity.runOnUiThread(new Runnable() {
							public void run() {
								dialog.show();
							}
						});
					}
				}).start();
			}
		});

		String title = "", body = "";
		String winner = (color == Chesspiece.WHITE ? mWhiteName : mBlackName);
		String loser = (winner.equals(mBlackName) ? mWhiteName : mBlackName);
		switch (flag) {
		case DRAWREPETITION:
			title = mResources.getString(R.string.title_draw);
			body = mResources.getString(R.string.txt_draw_repetition);
			mChessboard.mPositionHashFactory.insertGameResult(PositionHashFactory.DRAW);
			mChessboard.mPositionHashFactory.insertDrawByRepetitionOrStalemate();
			break;
		case DRAWAGREED:
			title = mResources.getString(R.string.title_draw);
			body = mResources.getString(R.string.txt_draw_agreed);
			mChessboard.mPositionHashFactory.insertGameResult(PositionHashFactory.DRAW);
			break;
		case DRAWCLAIMED:
			title = mResources.getString(R.string.title_draw);
			body = mResources.getString(R.string.txt_draw_claimed);
			mChessboard.mPositionHashFactory.insertGameResult(PositionHashFactory.DRAW);
			break;
		case WINCHECKMATE:
			title = mResources.getString(R.string.title_win_checkmate);
			body = winner + " " + mResources.getString(R.string.txt_win_checkmate);
			mChessboard.mPositionHashFactory.insertGameResult(color);
			break;
		case WINRESIGN:
			title = mResources.getString(R.string.title_win_resign);
			body = loser + " " + mResources.getString(R.string.txt_win_resign_1) + " " + winner + " "
					+ mResources.getString(R.string.txt_win_resign_2);
			mChessboard.mPositionHashFactory.insertGameResult(color);
			break;
		case DRAWSTALEMATE:
			title = mResources.getString(R.string.title_draw);
			body = mResources.getString(R.string.txt_draw_stalemate);
			mChessboard.mPositionHashFactory.insertGameResult(PositionHashFactory.DRAW);
			mChessboard.mPositionHashFactory.insertDrawByRepetitionOrStalemate();
			break;
		case WINTIMEOUT:
			title = mResources.getString(R.string.title_win_timeout);
			body = loser + " " + mResources.getString(R.string.txt_win_timeout_1) + " " + winner + " "
					+ mResources.getString(R.string.txt_win_timeout_2);
			mChessboard.mPositionHashFactory.insertGameResult(color);
		}

		dialog.setTitle(title);
		((TextView) contentView.findViewById(R.id.txt_endgame)).setText(body);
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		mActivity.unrotate();
	}

	/**
	 * Enable or disable the tiles
	 * 
	 * @param enabled
	 *            True to enable the tiles, false to disable
	 */
	protected void setButtonsEnabled(boolean enabled) {
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				mButtons[i][j].setEnabled(enabled);
			}
		}
	}

	/**
	 * Calls a dialog enabling the player to select a piece to promote to.
	 * 
	 * @param pawn
	 *            The pawn to be promoted
	 * @param row
	 *            The promotion row
	 * @param column
	 *            The promotion column
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

		btn_queen.setImageDrawable(mActivity.getPieceIcon(queen));
		btn_rook.setImageDrawable(mActivity.getPieceIcon(rook));
		btn_bishop.setImageDrawable(mActivity.getPieceIcon(bishop));
		btn_knight.setImageDrawable(mActivity.getPieceIcon(knight));

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
	protected void placePieces() {
		Chesspiece piece;
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				piece = mChessboard.getPieceAt(i, j);
				if (piece == null || piece.getColor() == Chesspiece.EN_PASSANT) {
					mButtons[i][j].setImageResource(android.R.color.transparent);
				} else {
					mButtons[i][j].setImageDrawable(mActivity.getPieceIcon(piece));
				}
			}
		}
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
		mOldRow = mSelected.getRow();
		mOldColumn = mSelected.getColumn();
		mRow = row;
		mColumn = column;
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
		playMoveSound();
	}

	/**
	 * Marks the tiles that are considered legal moves for the currently
	 * selected piece
	 */
	private void setLegalMovesHint() {
		int id = -1;
		// Performance
		boolean legal;
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				legal = false;
				if (mLegalMoves != null && mLegalMoves[i][j]) {
					legal = true;
					switch (getTileColorId(i, j)) {
					case R.color.white_tile:
						id = R.color.white_tile_marked;
						break;
					case R.color.black_tile:
						id = R.color.black_tile_marked;
						break;
					}
					mButtons[i][j].setBackgroundColor(mResources.getColor(id));
				}

				if (!legal && mSelected != null && mSelected.getRow() == i && mSelected.getColumn() == j) {
					mButtons[i][j].setBackgroundColor(mResources.getColor(R.color.selected_piece_tile));
				} else if (!legal) {
					mButtons[i][j].setBackgroundColor(mResources.getColor(getTileColorId(i, j)));
				}
				if (!legal && i == mOldRow && j == mOldColumn) {
					mButtons[i][j].setBackgroundColor(mResources.getColor(R.color.previous_move_tile));
				} else if (!legal && i == mRow && j == mColumn) {
					mButtons[i][j].setBackgroundColor(mResources.getColor(R.color.previous_move_tile));
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
