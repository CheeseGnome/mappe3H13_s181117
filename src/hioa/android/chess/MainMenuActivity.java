package hioa.android.chess;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainMenuActivity extends Activity {

	public static final String MOVES = "moves";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		Button button = (Button) findViewById(R.id.btn_new_game);
		final Intent gameSettingsIntent = new Intent(this, GameSettingsActivity.class);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DBAdapter database = new DBAdapter(getApplicationContext());
				database.open();
				Cursor cursor = database.getMoves();
				if (cursor.getCount() == 0) {
					startActivity(gameSettingsIntent);
				}else{
					Intent intent = new Intent(MainMenuActivity.this,GameActivity.class);
					cursor.moveToFirst();
					intent.putExtra(MOVES, cursor.getString(cursor.getColumnIndex(DBAdapter.MOVES)));
					intent.putExtra(GameSettingsActivity.WHITENAME, cursor.getString(cursor.getColumnIndex(DBAdapter.WHITE_PLAYER)));
					intent.putExtra(GameSettingsActivity.BLACKNAME, cursor.getString(cursor.getColumnIndex(DBAdapter.BLACK_PLAYER)));
					intent.putExtra(DBAdapter.WHITETIME, cursor.getString(cursor.getColumnIndex(DBAdapter.WHITETIME)));
					intent.putExtra(DBAdapter.BLACKTIME, cursor.getString(cursor.getColumnIndex(DBAdapter.BLACKTIME)));
					intent.putExtra(DBAdapter.TIME, cursor.getString(cursor.getColumnIndex(DBAdapter.TIME)));
					intent.putExtra(DBAdapter.BONUS, cursor.getString(cursor.getColumnIndex(DBAdapter.BONUS)));
					startActivity(intent);
				}
			}
		});

	}

	protected void onResume() {
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_menu, menu);
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
