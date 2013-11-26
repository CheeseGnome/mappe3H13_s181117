package hioa.android.chess;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainMenuActivity extends Activity {

	public static final String MOVES = "moves";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		((ImageView) findViewById(R.id.img_menu_king_white)).setImageDrawable(getKingIcon(Chesspiece.WHITE));
		((ImageView) findViewById(R.id.img_menu_king_black)).setImageDrawable(getKingIcon(Chesspiece.BLACK));

		Button button = (Button) findViewById(R.id.btn_new_game);
		final Intent gameSettingsIntent = new Intent(this, GameSettingsActivity.class);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				DBAdapter database = new DBAdapter(getApplicationContext());
				database.open();
				Cursor cursor = database.getGameState();
				if (cursor.getCount() == 0) {
					startActivity(gameSettingsIntent);
				} else {

					new Thread(new Runnable() {
						public void run() {
							Looper.prepare();
							Toast.makeText(MainMenuActivity.this, R.string.toast_loading, Toast.LENGTH_LONG).show();
							Looper.loop();
							Looper.myLooper().quit();
						}
					}).start();
					Intent intent = new Intent(MainMenuActivity.this, GameActivity.class);
					cursor.moveToFirst();
					intent.putExtra(MOVES, cursor.getString(cursor.getColumnIndex(DBAdapter.MOVES)));
					intent.putExtra(GameSettingsActivity.WHITENAME,
							cursor.getString(cursor.getColumnIndex(DBAdapter.WHITE_PLAYER)));
					intent.putExtra(GameSettingsActivity.BLACKNAME,
							cursor.getString(cursor.getColumnIndex(DBAdapter.BLACK_PLAYER)));
					intent.putExtra(DBAdapter.WHITETIME, cursor.getString(cursor.getColumnIndex(DBAdapter.WHITETIME)));
					intent.putExtra(DBAdapter.BLACKTIME, cursor.getString(cursor.getColumnIndex(DBAdapter.BLACKTIME)));
					intent.putExtra(DBAdapter.TIME, cursor.getString(cursor.getColumnIndex(DBAdapter.TIME)));
					intent.putExtra(DBAdapter.BONUS, cursor.getString(cursor.getColumnIndex(DBAdapter.BONUS)));
					startActivity(intent);
				}
			}
		});

		((Button) findViewById(R.id.btn_statistics)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent statistics = new Intent(MainMenuActivity.this, StatisticsActivity.class);
				startActivity(statistics);
			}
		});

		((Button) findViewById(R.id.btn_settings)).setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent settings = new Intent(MainMenuActivity.this, Preferences.class);
				startActivity(settings);
			}
		});
	}
	
	private Drawable getKingIcon(int color) {
		int id;
		if (color == Chesspiece.WHITE) {
			id = R.drawable.white_king;
		} else {
			id = R.drawable.black_king;
		}
		Drawable dr = getResources().getDrawable(id);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		int size = getResources().getDimensionPixelSize(R.dimen.img_menu_king_size);
		return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
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
		case R.id.action_settings:
			Intent settings = new Intent(this, Preferences.class);
			startActivity(settings);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
