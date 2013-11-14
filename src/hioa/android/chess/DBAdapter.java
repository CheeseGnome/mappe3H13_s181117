package hioa.android.birthdaycalendar;

import java.text.SimpleDateFormat;
import java.util.Date;

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
* @version 2013-10-22
*/
public class DBAdapter {
        Context context;

        static final String DB_NAME = "chess.db";
        static final String TABLE = "game", TABLE_POSITION = "position";
        static final String HASH = "hash";
        static final String ID = BaseColumns._ID;
        static final String DATE = "played_on";
        static final String WHITE_PLAYER = "white_player";
        static final String BLACK_PLAYER = "black_player";
        static final String RESULT = "result";
        static final String MOVES = "moves";
        static final int DB_VERSION = 1;
        
        public static final String WHITE_WON = "white_won", BLACK_WON = "black_won", DRAW = "draw";

        private DatabaseHelper dbHelper;
        private SQLiteDatabase database;

        /**
         * Create a new adapter for communicating with the database
         *
         * @param ctx
         * The current context
         */
        public DBAdapter(Context ctx) {
                this.context = ctx;
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

        /**
         * Insert a person into the database
         *
         * @param cv
         * The values to be put into the database
         */
        protected void insertGameResult(String white_name, String black_name, String moves, String result, Date date) {
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
         * Delete a person from the database
         *
         * @param where
         * The optional WHERE clause to apply when deleting. Passing null
         * will delete all rows.
         * @param whereArgs
         * You may include ?s in the where clause, which will be replaced
         * by the values from whereArgs. The values will be bound as
         * Strings.
         */
        public void delete(String where, String[] whereArgs) {
                database.delete(TABLE, where, whereArgs);
        }

        /**
         * Update a person in the database
         *
         * @param values
         * A map from column names to new column values. null is a valid
         * value that will be translated to NULL.
         * @param selection
         * The optional WHERE clause to apply when updating. Passing null
         * will update all rows.
         * @param selectionArgs
         * You may include ?s in the where clause, which will be replaced
         * by the values from whereArgs. The values will be bound as
         * Strings.
         */
        public void update(ContentValues values, String selection, String[] selectionArgs) {
                database.update(TABLE, values, selection, selectionArgs);
        }

        /**
         * Queries the database for the person entries matching the parameter
         *
         * @param where
         * A filter declaring which rows to return, formatted as an SQL
         * WHERE clause (excluding the WHERE itself). Passing null will
         * return all rows for the given table.
         * <p>
         * @param having
         * A filter declare which row groups to include in the cursor, if
         * row grouping is being used, formatted as an SQL HAVING clause
         * (excluding the HAVING itself). Passing null will cause all row
         * groups to be included, and is required when row grouping is
         * not being used.
         * @see SQLiteDatabase
         * @return A Cursor object positioned before the first entry
         */
        protected Cursor query(String where, String having) {
                String[] columns = { ID, WHITE_PLAYER, BLACK_PLAYER, DATE, RESULT, MOVES };
                return database.query(false, TABLE, columns, where, null, null, having, WHITE_PLAYER + " ASC", null, null);
        }

        /**
         * Queries the database for the person entries matching the parameters
         *
         * @param projection
         * A list of which columns to return. Passing null will return
         * all columns, which is discouraged to prevent reading data from
         * storage that isn't going to be used.
         * @param selection
         * A filter declaring which rows to return, formatted as an SQL
         * WHERE clause (excluding the WHERE itself). Passing null will
         * return all rows for the given table.
         * @param selectionArgs
         * You may include ?s in selection, which will be replaced by the
         * values from selectionArgs, in order that they appear in the
         * selection. The values will be bound as Strings.
         * @param sortOrder
         * How to order the rows, formatted as an SQL ORDER BY clause
         * (excluding the ORDER BY itself). Passing null will use the
         * default sort order, which may be unordered.
         * @return A Cursor object positioned before the first entry
         */
        public Cursor query(String[] projection, String selection, String[] selectionArgs, String sortOrder) {
                return database.query(TABLE, projection, selection, selectionArgs, null, null, sortOrder);
        }

        /**
         * Appropriate implementation of SQLiteOpenHelper
         *
         * @author Lars Sætaberget
         * @version 2013-10-05
         */
        private static class DatabaseHelper extends SQLiteOpenHelper {
                Context context;

                DatabaseHelper(Context context) {
                        super(context, DB_NAME, null, DB_VERSION);
                        this.context = context;
                }

                @Override
                public void onCreate(SQLiteDatabase db) {
                        String sql = "create table " + TABLE + " (" + ID + " integer primary key autoincrement, "
                                        + WHITE_PLAYER + " text, " + BLACK_PLAYER + " text, " + DATE + " date, " + RESULT + " text, "
                                        + MOVES + " text);";
                        db.execSQL(sql);
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                        // Will never be called
                }
        }// end of DatabaseHelper

}// end of DBAdapter