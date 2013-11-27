package hioa.android.chess;

import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * The activity that displays the game
 * 
 * @author Lars Sætaberget
 * @version 2013-11-23
 */

public class GameActivity extends Activity {

	private PlayerFrame mWhiteFrame, mBlackFrame;
	private String mWhiteName, mBlackName;
	private Chessboard mChessboard;
	private long mStartTime;
	private long mBonusTime;
	private float mCurrentRotation = 0;
	private float ROTATION = 180;
	private SharedPreferences mPreferences;
	private boolean mRotate;
	private AlertDialog mDialog;
	private boolean mAlwaysOn;

	/**
	 * Array of the icons for the various chesspieces.
	 * <p>
	 * This array is indexed through int constants named: BLACKPAWN, WHITEKING
	 * etc.
	 */
	private BitmapDrawable[] mIcons;

	public static final int CLAIMDRAW = 0, OFFERDRAW = 1;

	public static final int BLACKPAWN = 0, BLACKROOK = 1, BLACKKNIGHT = 2, BLACKBISHOP = 3, BLACKQUEEN = 4,
			BLACKKING = 5, WHITEPAWN = 6, WHITEROOK = 7, WHITEKNIGHT = 8, WHITEBISHOP = 9, WHITEQUEEN = 10,
			WHITEKING = 11;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		loadIcons();
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		ChessboardView board = (ChessboardView) findViewById(R.id.chessboard);
		board.setActivity(this);
		board.placePieces();
		mWhiteFrame = (PlayerFrame) findViewById(R.id.whiteFrame);
		mBlackFrame = (PlayerFrame) findViewById(R.id.blackFrame);

		mWhiteFrame.setActivity(this);
		mBlackFrame.setActivity(this);

		mWhiteFrame.setKingIcon(Chesspiece.WHITE);
		mBlackFrame.setKingIcon(Chesspiece.BLACK);

		mChessboard = board.getChessboard();
		setupBundleItems(board);
		mWhiteFrame.setName(mWhiteName);
		mBlackFrame.setName(mBlackName);

		setDrawButtonMode(OFFERDRAW);

		((Button) findViewById(R.id.btn_resign)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mChessboard.stopClock();
				int color;
				if (mChessboard.mView.getCurrentPlayer() == Chesspiece.WHITE) {
					color = Chesspiece.BLACK;
				} else {
					color = Chesspiece.WHITE;
				}
				mChessboard.mView.endTheGame(ChessboardView.WINRESIGN, color);
				setCheckText(mChessboard.mView.getCurrentPlayer(), PlayerFrame.RESIGNED);
				setCheckText(color, PlayerFrame.WINNER);
				DBAdapter database = new DBAdapter(GameActivity.this);
				database.open();
				String winner;
				if (color == Chesspiece.WHITE) {
					winner = DBAdapter.WHITE_WON;
				} else {
					winner = DBAdapter.BLACK_WON;
				}
				database.insertGameResult(mWhiteName, mBlackName, mChessboard.mPositionHashFactory.getMoves(), winner,
						new Date());
			}

		});

		((Button) findViewById(R.id.btn_quit)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DBAdapter database = new DBAdapter(GameActivity.this);
				database.open();
				database.clearGameState();
				finish();
			}

		});

	}

	/**
	 * Loads the chesspiece icons
	 */
	private void loadIcons() {
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
	 * Enables or disables the resign, draw and quit buttons
	 * 
	 * @param enabled
	 *            True to enable, false to disable
	 */
	public void setButtonsEnabled(boolean enabled) {
		((Button) findViewById(R.id.btn_resign)).setEnabled(enabled);
		((Button) findViewById(R.id.btn_draw)).setEnabled(enabled);
		((Button) findViewById(R.id.btn_quit)).setEnabled(enabled);
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
		Drawable dr = getResources().getDrawable(id);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		int size = getResources().getDimensionPixelSize(R.dimen.tile_size);
		return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
	}

	/**
	 * Returns the appropriate icon for the provided piece
	 * 
	 * @param piece
	 *            The piece whose drawable to return
	 * @return A drawable representing the piece, scaled to fit inside a button
	 *         in this view
	 */
	public Drawable getPieceIcon(int piece) {
		return mIcons[piece];
	}

	/**
	 * Returns the appropriate icon for the provided piece
	 * 
	 * @param piece
	 *            The piece whose drawable to return
	 * @return A drawable representing the piece, scaled to fit inside a button
	 *         in this view
	 */
	public Drawable getPieceIcon(Chesspiece piece) {

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
	 * Set the enabled state of the offer draw/claim draw button
	 * 
	 * @param enabled
	 *            True if the button should be enabled
	 */
	public void setDrawButtonEnabled(boolean enabled) {
		((Button) findViewById(R.id.btn_draw)).setEnabled(enabled);
	}

	/**
	 * Sets the draw button to either offer draw or claim draw by the 50-move
	 * rule
	 * 
	 * @param flag
	 *            OFFERDRAW or CLAIMDRAW
	 */

	public void setDrawButtonMode(int flag) {
		final Button button = (Button) findViewById(R.id.btn_draw);

		if (flag == OFFERDRAW) {
			button.setText(R.string.btn_offer_draw);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					button.setEnabled(false);
					mDialog = new AlertDialog.Builder(GameActivity.this).create();
					mDialog.setTitle(getResources().getString(R.string.title_draw_offer));
					String player;
					if (mChessboard.mView.getCurrentPlayer() == Chesspiece.WHITE) {
						player = mWhiteName;
					} else {
						player = mBlackName;
					}
					mDialog.setMessage(player + " " + getResources().getString(R.string.txt_draw_offer));
					mDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.btn_accept),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									mChessboard.mView.endTheGame(ChessboardView.DRAWAGREED,
											mChessboard.mView.getCurrentPlayer());
									setCheckText(Chesspiece.WHITE, PlayerFrame.DRAW);
									setCheckText(Chesspiece.BLACK, PlayerFrame.DRAW);
									DBAdapter database = new DBAdapter(GameActivity.this);
									database.open();
									database.insertGameResult(mWhiteName, mBlackName,
											mChessboard.mPositionHashFactory.getMoves(), DBAdapter.DRAW_AGREED,
											new Date());
									mDialog.dismiss();
									mDialog = null;
								}
							});
					mDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.btn_decline),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									mDialog.dismiss();
									mDialog = null;
								}
							});
					mDialog.show();
					if (mRotate && mCurrentRotation == 0) {
						ViewPropertyAnimator animator = mDialog.findViewById(android.R.id.content).animate();
						animator.rotation(ROTATION);
					}
				}
			});
		} else if (flag == CLAIMDRAW) {
			button.setText(R.string.btn_claim_draw);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mChessboard.mView.endTheGame(ChessboardView.DRAWCLAIMED, mChessboard.mView.getCurrentPlayer());
					setCheckText(Chesspiece.WHITE, PlayerFrame.DRAW);
					setCheckText(Chesspiece.BLACK, PlayerFrame.DRAW);
					DBAdapter database = new DBAdapter(GameActivity.this);
					database.open();
					database.insertGameResult(mWhiteName, mBlackName, mChessboard.mPositionHashFactory.getMoves(),
							DBAdapter.DRAW_CLAIMED, new Date());
				}
			});
		}
	}

	@Override
	protected void onResume() {
		loadPreferences();
		super.onResume();
	}

	@Override
	protected void onStop() {
		DBAdapter database = new DBAdapter(this);
		database.open();
		database.updateTimes("" + mChessboard.getTime(Chesspiece.WHITE), "" + mChessboard.getTime(Chesspiece.BLACK));
		super.onStop();
	}

	/**
	 * Reverts the view back to 0 degrees rotation
	 */
	public void unrotate() {
		if (mCurrentRotation != 0) {
			rotate();
		}
	}

	/**
	 * Sets the header text to indicate whose move it is
	 */
	public void switchPlayer() {
		TextView header = (TextView) findViewById(R.id.txt_move);
		if (header.getText().toString().equals(getResources().getString(R.string.txt_black_move))) {
			header.setText(R.string.txt_white_move);
		} else {
			header.setText(R.string.txt_black_move);
		}
	}

	/**
	 * Loads the preferences from xml and rotates the screen if it should be
	 * done as per the current state and loaded preferences
	 */
	@SuppressLint("Wakelock")
	private void loadPreferences() {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

		if (mPreferences.getBoolean("screenAlwaysOn", false)) {
			mAlwaysOn = true;
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		} else if (mAlwaysOn) {
			mAlwaysOn = false;
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		}

		mChessboard.mView.setMute(mPreferences.getBoolean("mute", false));

		mRotate = mPreferences.getBoolean("rotate", false);
		if (!mRotate && mCurrentRotation != 0) {
			mRotate = true;
			rotate();
			mRotate = false;
		} else if (mRotate) {
			if (mChessboard.mView.getCurrentPlayer() == Chesspiece.WHITE) {
				if (mCurrentRotation != 0) {
					rotate();
				}
			} else {
				if (mCurrentRotation == 0) {
					rotate();
				}
			}
		}
	}

	/**
	 * Rotates the screen if the preferences specified it. Otherwise does
	 * nothing
	 */
	public void rotate() {
		if (!mRotate) {
			return;
		}
		ViewPropertyAnimator animator = ((RelativeLayout) this.findViewById(R.id.view)).animate();
		if (mCurrentRotation == 0) {
			mCurrentRotation += ROTATION;
		} else {
			mCurrentRotation -= ROTATION;
		}
		animator.rotation(mCurrentRotation);
	}

	/**
	 * Adds the piece to the opposing player's frame
	 * 
	 * @param piece
	 *            The piece that was captured
	 */
	public void capturePiece(Chesspiece piece) {
		if (piece.getColor() == Chesspiece.WHITE) {
			mBlackFrame.addPiece(piece);
		} else {
			mWhiteFrame.addPiece(piece);
		}
	}

	/**
	 * Prepares the player frames for a new game
	 */
	public void resetPlayerFrames() {
		mWhiteFrame.resetPieces();
		mBlackFrame.resetPieces();
	}

	/**
	 * Gets the game settings from the {@link GameSettingsActivity}
	 * 
	 * @param view
	 *            The {@link ChessboardView} to set up with start time and
	 *            player names
	 */
	private void setupBundleItems(ChessboardView view) {
		Bundle bundle = getIntent().getExtras();
		String moves = bundle.getString(MainMenuActivity.MOVES);
		// Initialized from main menu
		if (moves != null) {
			mStartTime = Long.parseLong(bundle.getString(DBAdapter.TIME, "0"));
			mBonusTime = Long.parseLong(bundle.getString(DBAdapter.BONUS, "0"));
		}
		// Initialized from GameSettingsActivity
		else {
			mStartTime = bundle.getLong(GameSettingsActivity.TIME);
			mBonusTime = bundle.getLong(GameSettingsActivity.BONUS);
		}
		mChessboard.setTime(mStartTime, mBonusTime);
		mWhiteName = bundle.getString(GameSettingsActivity.WHITENAME);
		mBlackName = bundle.getString(GameSettingsActivity.BLACKNAME);
		view.setPlayerNames(mWhiteName, mBlackName);
		view.setActivity(this);
		// Initialized from main menu
		if (moves != null) {
			long whiteTime = Long.parseLong(bundle.getString(DBAdapter.WHITETIME, "0"));
			long blackTime = Long.parseLong(bundle.getString(DBAdapter.BLACKTIME, "0"));
			mChessboard.pauseClock(true);
			int toMove = mChessboard.mPositionHashFactory.rebuildPosition(moves);
			mChessboard.mView.setCurrentPlayer(toMove);
			mChessboard.setTime(Chesspiece.WHITE, whiteTime);
			mChessboard.setTime(Chesspiece.BLACK, blackTime);
			mChessboard.revalidateClock();
			mChessboard.pauseClock(false);
			mChessboard.mView.reDraw();
		}
		// Initialized from GameSettingsActivity
		else {
			mChessboard.revalidateClock();
			mChessboard.setTime(Chesspiece.WHITE, mStartTime);
			mChessboard.setTime(Chesspiece.BLACK, mStartTime);
		}
		updateClock(Chesspiece.WHITE, mChessboard.getTime(Chesspiece.WHITE));
		updateClock(Chesspiece.BLACK, mChessboard.getTime(Chesspiece.BLACK));
	}

	@Override
	protected void onDestroy() {
		// Kill the clock thread
		mChessboard.stopClock();
		// Wait for the clock thread to exit(up to 50 millis delay)
		while (mChessboard.clockRunning()) {
		}
		super.onDestroy();
	}

	public long getBonusTime() {
		return mBonusTime;
	}

	public long getStartTime() {
		return mStartTime;
	}

	public void setChessboard(Chessboard board) {
		mChessboard = board;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Intent settings = new Intent(this, Preferences.class);
			startActivity(settings);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Sets the clock for the provided color to a time-formatted string taken
	 * from the time parameter
	 * 
	 * @param color
	 *            The color who's clock should be updated
	 * @param time
	 *            The new time in millis
	 */
	public void updateClock(final int color, long time) {
		final StringBuilder clockBuilder = new StringBuilder();

		int seconds = (int) time / 1000 % 60;
		int minutes = (int) time / 1000 / 60 % 60;
		int hours = (int) time / 1000 / 60 / 60;

		addTimeString(clockBuilder, hours);
		addTimeString(clockBuilder, minutes);
		addTimeString(clockBuilder, seconds);

		// Delete last instance of :
		clockBuilder.deleteCharAt(clockBuilder.length() - 1);

		runOnUiThread(new Runnable() {
			public void run() {
				if (color == Chesspiece.WHITE) {
					mWhiteFrame.setTime(clockBuilder.toString());
				} else {
					mBlackFrame.setTime(clockBuilder.toString());
				}
			}
		});
	}

	/**
	 * Prepares the header for a new game
	 */
	private void resetMoveString() {
		TextView header = (TextView) findViewById(R.id.txt_move);
		if (!header.getText().equals(getResources().getString(R.string.txt_white_move))) {
			header.setText(R.string.txt_white_move);
		}
	}

	/**
	 * Sets all values to what they should be for the beginning of a game
	 * 
	 * @param board
	 */
	public void newGame(Chessboard board) {
		updateClock(Chesspiece.WHITE, mStartTime);
		updateClock(Chesspiece.BLACK, mStartTime);
		setChessboard(mChessboard);
		setCheckText(Chesspiece.WHITE, PlayerFrame.NO_CHECK);
		setCheckText(Chesspiece.BLACK, PlayerFrame.NO_CHECK);
		resetPlayerFrames();
		loadPreferences();
		resetMoveString();
		if (mDialog != null) {
			mDialog.dismiss();
			mDialog = null;
		}
		setDrawButtonMode(OFFERDRAW);
		setDrawButtonEnabled(true);
		setButtonsEnabled(true);
	}

	/**
	 * Sets the info text in the player frame based on the flag provided
	 * 
	 * @param color
	 *            The color who's frame should be updated
	 * @param flag
	 *            A constant from PlayerFrame
	 */
	public void setCheckText(int color, int flag) {
		if (flag != PlayerFrame.NO_CHECK && flag != PlayerFrame.CHECK) {
			((TextView) findViewById(R.id.txt_move)).setText(R.string.txt_game_over);
		}
		if (color == Chesspiece.WHITE) {
			mWhiteFrame.setCheckText(flag);
		} else {
			mBlackFrame.setCheckText(flag);
		}
	}

	/**
	 * Help method for the updateClock() method
	 * <p>
	 * Call this method in most significant to least significant order to get a
	 * properly formatted string.
	 * <p>
	 * For example:</br> addTimeString(builder, hours);</br>
	 * addTimeString(builder, minutes);</br> addTimeString(builder,
	 * seconds);</br>
	 * <p>
	 * Where time is the number you want to display.</br> (If you want to
	 * display 123 minutes as 2:03:00 you would need:</br>
	 * addTimeString(builder, 2);</br> addTimeString(builder, 3);</br>
	 * addTimeString(builder, 0);</br>
	 * 
	 * @param clockBuilder
	 *            The stringbuilder to write to
	 * @param time
	 *            The time to add
	 */
	private void addTimeString(StringBuilder clockBuilder, int time) {
		if (time == 0) {
			clockBuilder.append("00:");
		} else if (time > 0 && time < 10) {
			clockBuilder.append("0" + time + ":");
		} else {
			clockBuilder.append(time + ":");
		}
	}

}
