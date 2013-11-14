package hioa.android.birthdaycalendar;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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
* @version 2013-10-22
*/
public class DBAdapter {
        Context context;

        static final String DB_NAME = "chess.db";
        static final String TABLE = "game";
        static final String ID = BaseColumns._ID;
        static final String DATE = "played_on";
        static final String WHITE_PLAYER = "white_player";
        static final String BLACK_PLAYER = "black_player";
        static final String WHITE_WON = "white_won";
        static final String MOVES = "moves";
        static final int DB_VERSION = 1;

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
        protected void insertPerson(ContentValues cv) {
                database.insert(TABLE, null, cv);
        }

        /**
         * Insert an SMS text into the database
         *
         * @param cv
         * The values to be put into the database
         */
        protected void insertSMS(ContentValues cv) {
                database.delete(TABLE_SMS, null, null);
                database.insert(TABLE_SMS, null, cv);
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
                String[] columns = { ID, WHITE_PLAYER, LASTNAME, DATE, PHONE };
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
         * Get the currently saved default SMS from the database. This is an
         * untreated direct copy of the String entered in EditSMSActivity. It will
         * still contain all special tags.
         *
         * @return A string representing the default SMS
         */
        protected String getDefaultSMS() {
                Cursor cursor = database.query(false, TABLE_SMS, new String[] { SMS }, IS_DEFAULT + "=1", null, null, null,
                                null, null, null);

                if (cursor.getCount() != 1)
                        throw new IllegalStateException("The number of default SMS in database is not exactly 1");

                cursor.moveToFirst();
                return cursor.getString(cursor.getColumnIndex(SMS));
        }

        /**
         * Returns all people who's birthdays are today and who have not received an
         * SMS today If a person was born on february 29th they will be returned on
         * March 1st if the current year is not a leap year
         *
         * @return A cursor positioned before the first entry
         */
        @SuppressLint("SimpleDateFormat")
        protected Cursor getTodaysBirthdays() {
                Calendar today = Calendar.getInstance();
                // month is indexed from 0-11 in Calendar, but from 1-12 in DB
                int m = today.get(Calendar.MONTH) + 1;
                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");

                String month = "" + m;
                if (m < 10)
                        month = "0" + m;

                String sql = "strftime('%m', " + DATE + ")='" + month + "' AND strftime('%d', " + DATE + ")='"
                                + today.get(Calendar.DAY_OF_MONTH) + "'";

                if (leapYearShouldBeIncluded(today)) {
                        sql += " OR strftime('%m', " + DATE + ")='02' AND strftime('%d', " + DATE + ")='29'";
                }
                sql += " AND " + LAST_SMS_SENT + "!='" + date_format.format(today.getTime()) + "'";

                String[] columns = { ID, WHITE_PLAYER, LASTNAME, DATE, PHONE };
                return database.query(false, TABLE, columns, sql, null, null, null, null, null, null);
        }

        /**
         * Help method for getTodaysBirthdays(). Returns true if feb. 29th should be
         * included in the results.
         *
         * @param date
         * A calendar object set to today
         * @return True if today is march 1st and it is not a leap year
         */
        private boolean leapYearShouldBeIncluded(Calendar date) {
                // No special case if this year has feb 29th
                if (isLeapYear(date))
                        return false;
                else if (date.get(Calendar.MONTH) == Calendar.MARCH && date.get(Calendar.DAY_OF_MONTH) == 1)
                        return true;
                return false;
        }

        /**
         * Help-method for leapYearShouldBeIncluded.
         *
         * @param date
         * A calendar object set to today
         * @return True if it is currently a leap year
         */
        private boolean isLeapYear(Calendar date) {
                if (date.get(Calendar.YEAR) % 400 == 0)
                        return true;
                else if (date.get(Calendar.YEAR) % 100 == 0)
                        return false;
                else if (date.get(Calendar.YEAR) % 4 == 0)
                        return true;
                return false;
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
                                        + WHITE_PLAYER + " text, " + BLACK_PLAYER + " text, " + DATE + " date, " + WHITE_WON + " boolean, "
                                        + MOVES + " text);";
                        db.execSQL(sql);
                }

                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                        // Will never be called
                }
        }// end of DatabaseHelper

}// end of DBAdapter