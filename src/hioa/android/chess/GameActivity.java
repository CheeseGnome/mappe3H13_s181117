package hioa.android.chess;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewPropertyAnimator;
import android.view.Window;
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
			if (((ChessboardView) findViewById(R.id.chessboard)).getCurrentPlayer() == Chesspiece.WHITE) {
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
