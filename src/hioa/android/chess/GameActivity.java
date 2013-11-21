package hioa.android.chess;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class GameActivity extends Activity {

	private TextView whiteClock, blackClock;
	private long startTime = /* 2 * 60 */10 * 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		// Show the Up button in the action bar.
		setupActionBar();

		whiteClock = (TextView) findViewById(R.id.txt_white_clock);
		blackClock = (TextView) findViewById(R.id.txt_black_clock);
		ChessboardView board = (ChessboardView) findViewById(R.id.chessboard);
		board.setPlayerNames("Player 1", "Player 2");
		board.setStartTime(startTime);
		board.setActivity(this);
		updateClock(Chesspiece.WHITE, startTime);
		updateClock(Chesspiece.BLACK, startTime);
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

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
					whiteClock.setText(clockBuilder.toString());
				} else {
					blackClock.setText(clockBuilder.toString());
				}
			}
		});
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
