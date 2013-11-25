package hioa.android.chess;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * This class handles all queries towards the database
 * 
 * @author Lars Sætaberget
 * @version 2013-11-23
 */
public class DBAdapter {
	Context context;

	static final String DB_NAME = "chess.db";
	static final String TABLE = "game", TABLE_POSITION = "position";
	static final String ID = BaseColumns._ID;
	static final String DATE = "played_on";
	static final String WHITE_PLAYER = "white_player";
	static final String BLACK_PLAYER = "black_player";
	static final String RESULT = "result";
	static final String MOVES = "moves";
	static final String TIME = "time";
	static final String WHITETIME = "whitetime";
	static final String BLACKTIME = "blacktime";
	static final String BONUS = "bonus";
	static final int DB_VERSION = 1;

	public static final String WHITE_WON = "white_won",
			BLACK_WON = "black_won", DRAW_STALEMATE = "draw_stalemate",
			DRAW_REPETITION = "draw_repetition", DRAW_CLAIMED = "draw_claimed",
			DRAW_AGREED = "draw_agreed";

	private DatabaseHelper dbHelper;
	private SQLiteDatabase database;

	/**
	 * Create a new adapter for communicating with the database
	 * 
	 * @param context
	 *            The current context
	 */
	public DBAdapter(Context context) {
		this.context = context;
		this.dbHelper = new DatabaseHelper(this.context);
	}

	/**
	 * Open a writeable connection towards the database
	 * 
	 * @return A DBAdapter object with an open connection towards the database
	 * @throws SQLException
	 */
	public DBAdapter open() throws SQLException {
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void newPositionHashFactory() {
		database.delete(TABLE_POSITION, null, null);
	}

	public void insertMoves(String moves, String whiteName, String blackName,
			String whiteTime, String blackTime, String bonus, String time) {
		ContentValues values = new ContentValues();
		values.put(MOVES, moves);
		values.put(WHITE_PLAYER, whiteName);
		values.put(BLACK_PLAYER, blackName);
		values.put(WHITETIME, whiteTime);
		values.put(BLACKTIME, blackTime);
		values.put(BONUS, bonus);
		values.put(TIME, time);
		database.delete(TABLE_POSITION, null, null);
		database.insert(TABLE_POSITION, null, values);
	}

	public Cursor getMoves() {
		String[] columns = { MOVES, WHITE_PLAYER, BLACK_PLAYER, WHITETIME,
				BLACKTIME, BONUS, TIME };
		return database.query(TABLE_POSITION, columns, null, null, null, null,
				null, null);
	}

	/**
	 * Inserts a game result into the database
	 * 
	 * @param white_name
	 *            The white player's name
	 * @param black_name
	 *            The black player's name
	 * @param moves
	 *            A string representing the moves that were made
	 * @param result
	 *            The result(constants in this class)
	 * @param date
	 *            The date when the game was played
	 */
	@SuppressLint("SimpleDateFormat")
	protected void insertGameResult(String white_name, String black_name,
			String moves, String result, Date date) {
		ContentValues values = new ContentValues();
		values.put(WHITE_PLAYER, white_name);
		values.put(BLACK_PLAYER, black_name);
		values.put(MOVES, moves);
		values.put(RESULT, result);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		values.put(DATE, format.format(date));
		database.insert(TABLE, null, values);
	}

	/**
	 * Delete a game result from the database
	 * 
	 * @param where
	 *            The optional WHERE clause to apply when deleting. Passing null
	 *            will delete all rows.
	 * @param whereArgs
	 *            You may include ?s in the where clause, which will be replaced
	 *            by the values from whereArgs. The values will be bound as
	 *            Strings.
	 */
	public void delete(String where, String[] whereArgs) {
		database.delete(TABLE, where, whereArgs);
	}

	public void clearMoves() {
		database.delete(TABLE_POSITION, null, null);
	}

	/**
	 * Update an entry in the database
	 * 
	 * @param values
	 *            A map from column names to new column values. null is a valid
	 *            value that will be translated to NULL.
	 * @param selection
	 *            The optional WHERE clause to apply when updating. Passing null
	 *            will update all rows.
	 * @param selectionArgs
	 *            You may include ?s in the where clause, which will be replaced
	 *            by the values from whereArgs. The values will be bound as
	 *            Strings.
	 */
	public void update(ContentValues values, String selection,
			String[] selectionArgs) {
		database.update(TABLE, values, selection, selectionArgs);
	}

	/**
	 * Queries the database for the entries matching the parameter
	 * 
	 * @param where
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            WHERE clause (excluding the WHERE itself). Passing null will
	 *            return all rows for the given table.
	 *            <p>
	 * @param having
	 *            A filter declare which row groups to include in the cursor, if
	 *            row grouping is being used, formatted as an SQL HAVING clause
	 *            (excluding the HAVING itself). Passing null will cause all row
	 *            groups to be included, and is required when row grouping is
	 *            not being used.
	 * @see SQLiteDatabase
	 * @return A Cursor object positioned before the first entry
	 */
	protected Cursor query(String where, String having) {
		String[] columns = { ID, WHITE_PLAYER, BLACK_PLAYER, DATE, RESULT,
				MOVES };
		return database.query(false, TABLE, columns, where, null, null, having,
				WHITE_PLAYER + " ASC", null, null);
	}

	/**
	 * Queries the database for the entries matching the parameters
	 * 
	 * @param projection
	 *            A list of which columns to return. Passing null will return
	 *            all columns, which is discouraged to prevent reading data from
	 *            storage that isn't going to be used.
	 * @param selection
	 *            A filter declaring which rows to return, formatted as an SQL
	 *            WHERE clause (excluding the WHERE itself). Passing null will
	 *            return all rows for the given table.
	 * @param selectionArgs
	 *            You may include ?s in selection, which will be replaced by the
	 *            values from selectionArgs, in order that they appear in the
	 *            selection. The values will be bound as Strings.
	 * @param sortOrder
	 *            How to order the rows, formatted as an SQL ORDER BY clause
	 *            (excluding the ORDER BY itself). Passing null will use the
	 *            default sort order, which may be unordered.
	 * @return A Cursor object positioned before the first entry
	 */
	public Cursor query(String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return database.query(TABLE, projection, selection, selectionArgs,
				null, null, sortOrder);
	}

	/**
	 * Appropriate implementation of SQLiteOpenHelper
	 * 
	 * @author Lars Sætaberget
	 * @version 2013-10-05
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {

		DatabaseHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String sql = "create table " + TABLE + " (" + ID
					+ " integer primary key autoincrement, " + WHITE_PLAYER
					+ " text, " + BLACK_PLAYER + " text, " + DATE + " date, "
					+ RESULT + " text, " + MOVES + " text);";
			db.execSQL(sql);
			sql = "create table " + TABLE_POSITION + " (" + ID
					+ " integer primary key autoincrement, " + WHITE_PLAYER
					+ " text, " + BLACK_PLAYER + " text, " + WHITETIME
					+ " text, " + BLACKTIME + " text, " + BONUS + " text, " + TIME + " text, "
					+ MOVES + " text);";
			db.execSQL(sql);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// Will never be called
		}
	}// end of DatabaseHelper

}// end of DBAdapter