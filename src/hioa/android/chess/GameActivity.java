package hioa.android.chess;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

public class GameActivity extends Activity {

	private TextView mWhiteClock, mBlackClock;
	private PlayerFrame mWhiteFrame, mBlackFrame;
	private String mWhiteName, mBlackName;
	private Chessboard mChessboard;
	private long mStartTime;
	private long mBonusTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		mWhiteClock = (TextView) findViewById(R.id.txt_white_clock);
		mBlackClock = (TextView) findViewById(R.id.txt_black_clock);
		
		ChessboardView board = (ChessboardView) findViewById(R.id.chessboard);
		setupBundleItems(board);
		
		mWhiteFrame = (PlayerFrame) findViewById(R.id.whiteFrame);
		mBlackFrame = (PlayerFrame) findViewById(R.id.blackFrame);
		mWhiteFrame.setName(mWhiteName);
		mBlackFrame.setName(mBlackName);
		
		mWhiteFrame.loadIcons(Chesspiece.BLACK);
		mBlackFrame.loadIcons(Chesspiece.WHITE);
		
		mChessboard = board.getChessboard();
		updateClock(Chesspiece.WHITE, mStartTime);
		updateClock(Chesspiece.BLACK, mStartTime);
	}
	
	public void capturePiece(Chesspiece piece){
		if(piece.getColor() == Chesspiece.WHITE){
			mBlackFrame.addPiece(piece);
		}else{
			mWhiteFrame.addPiece(piece);
		}
	}
	
	public void resetPlayerFrames(){
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
	public void onBackPressed() {
		mChessboard.stopClock();
		finish();
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
		}
		return super.onOptionsItemSelected(item);
	}

	public void updateClock(final int color, long time) {
		final StringBuilder clockBuilder = new StringBuilder();

		int hundreds = (int) time / 10 % 100;
		int seconds = (int) time / 1000 % 60;
		int minutes = (int) time / 1000 / 60;

		addTimeString(clockBuilder, minutes);
		addTimeString(clockBuilder, seconds);
		addTimeString(clockBuilder, hundreds);
		// Delete last instance of :
		clockBuilder.deleteCharAt(clockBuilder.length() - 1);

		runOnUiThread(new Runnable() {
			public void run() {
				if (color == Chesspiece.WHITE) {
					mWhiteClock.setText(clockBuilder.toString());
				} else {
					mBlackClock.setText(clockBuilder.toString());
				}
			}
		});
	}
	
	public void setInCheck(int color, boolean inCheck){
		if(color == Chesspiece.WHITE){
			mWhiteFrame.setInCheck(inCheck);
		}else{
			mBlackFrame.setInCheck(inCheck);
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
