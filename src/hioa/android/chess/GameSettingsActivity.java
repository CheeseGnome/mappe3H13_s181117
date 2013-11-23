package hioa.android.chess;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class GameSettingsActivity extends Activity {

	private static final int INVALIDTIME = 0, INVALIDNAME = 1;

	public static final String TIME = "time", BONUS = "bonus", WHITENAME = "white_name", BLACKNAME = "black_name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_settings);

		Button start = (Button) findViewById(R.id.btn_start);
		final Intent gameIntent = new Intent(this, GameActivity.class);
		start.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				long time = getTime();
				long bonus = getBonus();
				if (time == -1 || bonus == -1) {
					invalidInput(INVALIDTIME);
					return;
				}
				String white = getWhiteName();
				String black = getBlackName();
				if (white.equals("") || black.equals("")) {
					invalidInput(INVALIDNAME);
					return;
				}
				gameIntent.putExtra(WHITENAME, white);
				gameIntent.putExtra(BLACKNAME, black);
				gameIntent.putExtra(TIME, time);
				gameIntent.putExtra(BONUS, bonus);
				startActivity(gameIntent);
				finish();
			}
		});

		Button cancel = (Button) findViewById(R.id.btn_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	/**
	 * Creates a warning dialog based on the provided flag
	 * 
	 * @param flag
	 *            A constant that decides the dialog texts
	 */

	private void invalidInput(int flag) {
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle(getString(R.string.title_invalid_input));
		if (flag == INVALIDTIME) {
			dialog.setMessage(getString(R.string.txt_invalid_time));
		} else {
			dialog.setMessage(getString(R.string.txt_invalid_name));
		}
		dialog.setIconAttribute(android.R.attr.alertDialogIcon);
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.btn_ok),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		dialog.show();
	}

	private String getWhiteName() {
		return ((EditText) findViewById(R.id.etxt_white_name)).getText().toString();
	}

	private String getBlackName() {
		return ((EditText) findViewById(R.id.etxt_black_name)).getText().toString();
	}

	private long getTime() {
		String minutes = ((EditText) findViewById(R.id.etxt_minutes)).getText().toString();
		String seconds = ((EditText) findViewById(R.id.etxt_seconds)).getText().toString();

		long time;
		try {
			time = Long.parseLong(minutes) * 60 + Long.parseLong(seconds);
		} catch (NumberFormatException nfe) {
			return -1;
		}
		return time * 1000;
	}

	private long getBonus() {
		String minutes = ((EditText) findViewById(R.id.etxt_bonus_minutes)).getText().toString();
		String seconds = ((EditText) findViewById(R.id.etxt_bonus_seconds)).getText().toString();

		long time;
		try {
			time = Long.parseLong(minutes) * 60 + Long.parseLong(seconds);
		} catch (NumberFormatException nfe) {
			return -1;
		}
		return time * 1000;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game_settings, menu);
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

}
