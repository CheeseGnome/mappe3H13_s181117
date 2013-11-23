package hioa.android.chess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

	public static final int CLAIMDRAW = 0, OFFERDRAW = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		ChessboardView board = (ChessboardView) findViewById(R.id.chessboard);
		setupBundleItems(board);

		mWhiteFrame = (PlayerFrame) findViewById(R.id.whiteFrame);
		mBlackFrame = (PlayerFrame) findViewById(R.id.blackFrame);
		mWhiteFrame.setName(mWhiteName);
		mBlackFrame.setName(mBlackName);
		mWhiteFrame.setKingIcon(Chesspiece.WHITE);
		mBlackFrame.setKingIcon(Chesspiece.BLACK);

		mWhiteFrame.loadIcons(Chesspiece.BLACK);
		mBlackFrame.loadIcons(Chesspiece.WHITE);

		mChessboard = board.getChessboard();
		updateClock(Chesspiece.WHITE, mStartTime);
		updateClock(Chesspiece.BLACK, mStartTime);

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
				// TODO DB
			}

		});

		((Button) findViewById(R.id.btn_quit)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Save
				finish();
			}

		});

	}

	public void setDrawButtonMode(int flag) {
		Button button = (Button) findViewById(R.id.btn_draw);

		if (flag == OFFERDRAW) {
			button.setText(R.string.btn_offer_draw);
			button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
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
				}
			});
		}
	}

	@Override
	protected void onResume() {
		loadPreferences();
		super.onResume();
	}

	public void unrotate() {
		if (mCurrentRotation != 0) {
			rotate();
		}
	}

	public void switchPlayer() {
		TextView header = (TextView) findViewById(R.id.txt_move);
		if (header.getText().toString().equals(getResources().getString(R.string.txt_black_move))) {
			header.setText(R.string.txt_white_move);
		} else {
			header.setText(R.string.txt_black_move);
		}
	}

	private void loadPreferences() {
		mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

	public void capturePiece(Chesspiece piece) {
		if (piece.getColor() == Chesspiece.WHITE) {
			mBlackFrame.addPiece(piece);
		} else {
			mWhiteFrame.addPiece(piece);
		}
	}

	public void resetPlayerFrames() {
		mWhiteFrame.resetPieces();
		mBlackFrame.resetPieces();
	}

	private void setupBundleItems(ChessboardView view) {
		Bundle bundle = getIntent().getExtras();
		mStartTime = bundle.getLong(GameSettingsActivity.TIME);
		mBonusTime = bundle.getLong(GameSettingsActivity.BONUS);
		view.setTime(mStartTime, mBonusTime);
		mWhiteName = bundle.getString(GameSettingsActivity.WHITENAME);
		mBlackName = bundle.getString(GameSettingsActivity.BLACKNAME);
		view.setPlayerNames(mWhiteName, mBlackName);
		view.setActivity(this);
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
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			// NavUtils.navigateUpFromSameTask(this);
			return true;
		case R.id.action_settings:
			Intent settings = new Intent(this, Preferences.class);
			startActivity(settings);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

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

	private void resetMoveString() {
		TextView header = (TextView) findViewById(R.id.txt_move);
		if (!header.getText().equals(getResources().getString(R.string.txt_white_move))) {
			header.setText(R.string.txt_white_move);
		}
	}

	public void newGame(Chessboard board) {
		updateClock(Chesspiece.WHITE, mStartTime);
		updateClock(Chesspiece.BLACK, mStartTime);
		setChessboard(mChessboard);
		setCheckText(Chesspiece.WHITE, PlayerFrame.NO_CHECK);
		setCheckText(Chesspiece.BLACK, PlayerFrame.NO_CHECK);
		resetPlayerFrames();
		loadPreferences();
		resetMoveString();
		if(mDialog != null){
			mDialog.dismiss();
			mDialog = null;
		}
		setDrawButtonMode(OFFERDRAW);
	}

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

	public void addTimeString(StringBuilder clockBuilder, int time) {
		if (time == 0) {
			clockBuilder.append("00:");
		} else if (time > 0 && time < 10) {
			clockBuilder.append("0" + time + ":");
		} else {
			clockBuilder.append(time + ":");
		}
	}

}
