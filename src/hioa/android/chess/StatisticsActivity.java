package hioa.android.chess;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class StatisticsActivity extends Activity {

	private TextView mWhiteWins, mBlackWins, mDraws;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_statistics);
		
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		mWhiteWins = (TextView) findViewById(R.id.txt_white_win_count);
		mBlackWins = (TextView) findViewById(R.id.txt_black_win_count);
		mDraws = (TextView) findViewById(R.id.txt_draw_count);
		
		((ImageView) findViewById(R.id.img_stat_king_white)).setImageDrawable(getKingIcon(Chesspiece.WHITE));
		((ImageView) findViewById(R.id.img_stat_king_black)).setImageDrawable(getKingIcon(Chesspiece.BLACK));
	}
	
	private Drawable getKingIcon(int color){
		int id;
		if(color == Chesspiece.WHITE){
			id = R.drawable.white_king;
		}else{
			id = R.drawable.black_king;
		}
		Drawable dr = getResources().getDrawable(id);
		Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
		int size = getResources().getDimensionPixelSize(R.dimen.img_stat_king_size);
		return new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, size, size, true));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.statistics, menu);
		return true;
	}
	
	protected void onResume(){
		DBAdapter database = new DBAdapter(this);
		database.open();
		mWhiteWins.setText(getString(R.string.txt_white_win_count) + " " + database.query(DBAdapter.RESULT + " = '" + DBAdapter.WHITE_WON + "'", null).getCount());
		mBlackWins.setText(getString(R.string.txt_black_win_count) + " " + database.query(DBAdapter.RESULT + " = '" + DBAdapter.BLACK_WON + "'", null).getCount());
		String where = DBAdapter.RESULT + " = '" + DBAdapter.DRAW_AGREED + "' OR " + DBAdapter.RESULT + " = '" + DBAdapter.DRAW_CLAIMED + "' OR " + DBAdapter.RESULT + " = '" + DBAdapter.DRAW_REPETITION + "' OR " + DBAdapter.RESULT + " = '" + DBAdapter.DRAW_STALEMATE + "'";
		mDraws.setText(getString(R.string.txt_draw_count) + " " + database.query(where, null).getCount());
		super.onResume();
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
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
