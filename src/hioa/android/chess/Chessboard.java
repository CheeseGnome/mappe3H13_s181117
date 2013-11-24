package hioa.android.chess;

import java.util.Date;

import android.content.Context;

/**
 * The backend representation of the chessboard
 * 
 * @author Lars Sætaberget
 * @version 2013-11-23
 */

public class Chessboard {

	public static final int NO_PROMOTION = -1, QUEEN = 0, ROOK = 1, BISHOP = 2, KNIGHT = 3;

	private Chesspiece[][] mChessboard;
	private Context mContext;
	private EnPassant mEnPassant;
	private int mPromotionFlag = NO_PROMOTION;
	protected PositionHashFactory mPositionHashFactory;
	protected ChessboardView mView;
	private boolean firstMove = true;
	private long mStartTime, mBonusTime;
	private GameActivity mActivity;

	/**
	 * Used to count towards the 50-move rule
	 */
	private int mMoveCount = 0;

	/**
	 * This is used to confirm that the clock thread is not running
	 */
	private volatile boolean mClockRunning = false;

	/**
	 * Used by the clock-thread to determine whether or not the clock should
	 * switch to the other color.
	 * <p>
	 * The thread will unset this after changing color automatically
	 */
	private volatile boolean mChangeClockColor = false;

	/**
	 * Used to stop the clock from running without triggering a win by timeout.
	 * <p>
	 * This is used when the game should end by some other means.
	 */
	private volatile boolean mStopClock = false;
	/**
	 * The game will not end in a win by timeout while this is true.
	 * <p>
	 * Used to prevent players from losing by timeout while the program is
	 * performing a decisive move.
	 */
	private volatile boolean mMoving = false;

	public Chessboard(Context context) {
		mContext = context;
		Chesspiece.context = context;
		Chesspiece.chessboard = this;
		mChessboard = createChessboard();
		mPositionHashFactory = new PositionHashFactory(mContext, this);
	}

	/**
	 * Let the chessboard know that there will be a promotion during the next
	 * move.
	 * <p>
	 * The flags are given as constants in this class.</br> NO_PROMOTION
	 * probably shouldn't be used outside of this class.
	 * 
	 * @param flag
	 *            NO_PROMOTION, QUEEN, ROOK, BISHOP or KNIGHT
	 */
	public void setPromotionFlag(int flag) {
		mPromotionFlag = flag;
	}

	/**
	 * Set the time for the clock to use.
	 * 
	 * @param startTime
	 *            Starting time in millis
	 * @param bonusTime
	 *            Bonus time gained after each move in millis
	 */
	public void setTime(long startTime, long bonusTime) {
		mStartTime = startTime;
		mBonusTime = bonusTime;
	}

	/**
	 * Stops the clock and eventually(up to 50ms delay) kills the thread
	 * <p>
	 * Use clockRunning() to determine whether or not the clock is still
	 * running.
	 */
	public void stopClock() {
		mStopClock = true;
	}

	/**
	 * Starts the clock
	 * 
	 * @param startTime
	 *            The starting time for each player
	 */
	private void startClock(final long startTime) {
		new Thread(new Runnable() {
			public void run() {
				if (startTime == 0) {
					return;
				}
				mClockRunning = true;
				long diff1 = new Date().getTime();
				long diff2 = diff1;
				long difference;

				long whiteTime = startTime;
				long blackTime = startTime;
				int color = Chesspiece.BLACK;

				while (whiteTime > 0 && blackTime > 0) {
					while (mMoving) {
						if (mStopClock) {
							mStopClock = false;
							mClockRunning = false;
							return;
						}
					}
					if (mStopClock) {
						mStopClock = false;
						mClockRunning = false;
						// Return to avoid draw by timeout
						return;
					}
					if (mChangeClockColor) {
						mChangeClockColor = false;
						if (color == Chesspiece.WHITE) {
							whiteTime += mBonusTime;
							mActivity.updateClock(color, whiteTime);
							color = Chesspiece.BLACK;
						} else {
							blackTime += mBonusTime;
							mActivity.updateClock(color, blackTime);
							color = Chesspiece.WHITE;
						}
						diff1 = new Date().getTime();
					}
					diff2 = new Date().getTime();
					if (color == Chesspiece.WHITE) {
						difference = diff2 - diff1;
						whiteTime -= difference;
						diff1 = diff2;
						mActivity.updateClock(color, whiteTime);
					} else {
						difference = diff2 - diff1;
						blackTime -= difference;
						diff1 = diff2;
						mActivity.updateClock(color, blackTime);
					}
					try {
						// Performance
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
				// This prevents a player from losing by timeout while making a
				// winning move
				while (mMoving) {
					if (mStopClock) {
						mStopClock = false;
						mClockRunning = false;
						return;
					}
				}
				int enemy;
				if (color == Chesspiece.WHITE) {
					enemy = Chesspiece.BLACK;
				} else {
					enemy = Chesspiece.WHITE;
				}

				mView.timeOut(enemy);
				DBAdapter database = new DBAdapter(mContext);
				database.open();
				// TODO
				// database.insertGameResult(white_name, black_name, moves,
				// result, date);
				mClockRunning = false;
			}
		}).start();
	}

	/**
	 * True if the clock is still running.
	 * <p>
	 * (And thus has an active thread)
	 * 
	 * @return
	 */
	public boolean clockRunning() {
		return mClockRunning;
	}

	/**
	 * Initializes a new chessboard filled with {@link Chesspiece}s
	 * 
	 * @return A 2d-array of chesspieces representing a chessboard in it's
	 *         starting position
	 */
	private Chesspiece[][] createChessboard() {
		Chesspiece[][] board = new Chesspiece[getMaxRows()][getMaxColumns()];

		for (int i = 0; i < getMaxColumns(); i++) {
			board[1][i] = new Pawn(Chesspiece.BLACK, 1, i);
			board[getMaxRows() - 2][i] = new Pawn(Chesspiece.WHITE, getMaxRows() - 2, i);
		}
		board[0][0] = new Rook(Chesspiece.BLACK, 0, 0);
		board[0][1] = new Knight(Chesspiece.BLACK, 0, 1);
		board[0][2] = new Bishop(Chesspiece.BLACK, 0, 2);
		board[0][3] = new Queen(Chesspiece.BLACK, 0, 3);
		board[0][4] = new King(Chesspiece.BLACK, 0, 4);
		board[0][5] = new Bishop(Chesspiece.BLACK, 0, 5);
		board[0][6] = new Knight(Chesspiece.BLACK, 0, 6);
		board[0][7] = new Rook(Chesspiece.BLACK, 0, 7);

		board[getMaxRows() - 1][0] = new Rook(Chesspiece.WHITE, getMaxRows() - 1, 0);
		board[getMaxRows() - 1][1] = new Knight(Chesspiece.WHITE, getMaxRows() - 1, 1);
		board[getMaxRows() - 1][2] = new Bishop(Chesspiece.WHITE, getMaxRows() - 1, 2);
		board[getMaxRows() - 1][3] = new Queen(Chesspiece.WHITE, getMaxRows() - 1, 3);
		board[getMaxRows() - 1][4] = new King(Chesspiece.WHITE, getMaxRows() - 1, 4);
		board[getMaxRows() - 1][5] = new Bishop(Chesspiece.WHITE, getMaxRows() - 1, 5);
		board[getMaxRows() - 1][6] = new Knight(Chesspiece.WHITE, getMaxRows() - 1, 6);
		board[getMaxRows() - 1][7] = new Rook(Chesspiece.WHITE, getMaxRows() - 1, 7);

		return board;
	}

	/**
	 * This method checks if the provided move will result in the piece's
	 * color's king being in check
	 * 
	 * @param piece
	 *            The piece to move
	 * @param row
	 *            The row to move to
	 * @param column
	 *            The column to move to
	 * @return True if the king ends up in check
	 */
	public boolean kingInCheckAfter(Chesspiece piece, int row, int column) {
		int oldRow = piece.getRow();
		int oldColumn = piece.getColumn();

		// Temporary move
		Chesspiece oldPiece = getPieceAt(row, column);
		if (oldPiece instanceof King && oldPiece.getColor() == piece.getColor()) {
			return false;
		}
		mChessboard[oldRow][oldColumn] = null;
		mChessboard[row][column] = piece;
		piece.setRow(row);
		piece.setColumn(column);

		boolean result = isInCheck(piece.getColor());

		// revert the move
		mChessboard[oldRow][oldColumn] = piece;
		mChessboard[row][column] = oldPiece;
		piece.setRow(oldRow);
		piece.setColumn(oldColumn);

		return result;
	}

	/**
	 * Fetches the {@link Chesspiece} at the given position
	 * 
	 * @param row
	 *            The row to fetch the piece from
	 * @param column
	 *            The column to fetch the piece from
	 * @return The piece located at the provided position
	 */
	public Chesspiece getPieceAt(int row, int column) {
		return mChessboard[row][column];
	}

	/**
	 * Places the provided {@link EnPassant} onto the chessboard
	 * 
	 * @param enPassant
	 */
	public void placeEnPassant(EnPassant enPassant) {
		mEnPassant = enPassant;
		mChessboard[enPassant.getRow()][enPassant.getColumn()] = enPassant;
	}

	/**
	 * Returns an int explaining what type of piece(if any) is in the given tile
	 * 
	 * @param row
	 *            The row to check
	 * @param column
	 *            The column to check
	 * @param showEnPassant
	 *            Use this flag to determine if you want EN_PASSANT to be a
	 *            returnable value. If this is false EN_PASSANT will be returned
	 *            as NO_PIECE
	 * @return NO_PIECE, EN_PASSANT, BLACK or WHITE as defined in
	 *         {@link Chesspiece}
	 */
	public int tileContains(int row, int column, boolean showEnPassant) {
		if (mChessboard[row][column] != null) {
			if (mChessboard[row][column].getColor() == Chesspiece.EN_PASSANT && !showEnPassant) {
				return Chesspiece.NO_PIECE;
			}
			return mChessboard[row][column].getColor();
		} else
			return Chesspiece.NO_PIECE;
	}

	public void setChessboardView(ChessboardView view) {
		mView = view;
	}

	/**
	 * Set this to block the current player from losing from time out until the
	 * current next call to move() is completed
	 * 
	 * @param moving
	 *            Should probably only ever be set to true from outside this
	 *            class
	 */
	public void setMoving(boolean moving) {
		mMoving = moving;
	}

	public void setActivity(GameActivity activity) {
		mActivity = activity;
	}

	/**
	 * Moves the piece to the provided row and column.
	 * <p>
	 * Note: This method does no error-checking and simply assumes that the move
	 * is legal
	 * 
	 * @param piece
	 *            The piece to move
	 * @param row
	 *            The piece's new row position
	 * @param column
	 *            The piece's new column position
	 * @param oldRow
	 *            The piece's old row position
	 * @param oldColumn
	 *            The piece's old column position
	 */
	public void move(Chesspiece piece, int row, int column, int oldRow, int oldColumn) {
		move(piece, row, column, oldRow, oldColumn, false);
	}

	/**
	 * Moves the piece to the provided row and column.
	 * <p>
	 * Note: This method does no error-checking and simply assumes that the move
	 * is legal
	 * 
	 * @param piece
	 *            The piece to move
	 * @param row
	 *            The piece's new row position
	 * @param column
	 *            The piece's new column position
	 * @param oldRow
	 *            The piece's old row position
	 * @param oldColumn
	 *            The piece's old column position
	 * @param castle
	 *            True if this is a castle(clock should not switch colors)
	 */
	public void move(Chesspiece piece, int row, int column, int oldRow, int oldColumn, boolean castle) {
		if (!castle) {
			mActivity.rotate();
		}
		boolean incrementCount = !(piece instanceof Pawn);
		// Kill En-Passant
		if (mChessboard[row][column] != null && mChessboard[row][column].getColor() == Chesspiece.EN_PASSANT) {
			mChessboard[mEnPassant.getPawn().getRow()][mEnPassant.getPawn().getColumn()] = null;
			mActivity.capturePiece(mEnPassant.getPawn());
		}
		// Remove En-Passant opportunity (if there is one)
		if (mEnPassant != null) {
			mChessboard[mEnPassant.getRow()][mEnPassant.getColumn()] = null;
			mEnPassant = null;
		}

		mChessboard[oldRow][oldColumn] = null;

		Chesspiece captured = getPieceAt(row, column);
		if (captured != null) {
			incrementCount = true;
			mActivity.capturePiece(captured);
		}
		mChessboard[row][column] = piece;

		if (mPromotionFlag != NO_PROMOTION) {
			mChessboard[row][column] = getPieceByFlag(mPromotionFlag, piece.getColor(), row, column);
			mPromotionFlag = NO_PROMOTION;
		}
		getKing(piece.getColor()).setInCheck(false);
		mActivity.setCheckText(piece.getColor(), PlayerFrame.NO_CHECK);
		boolean gameOver = checkForGameEnd(piece.getColor());

		if (!firstMove && !castle) {
			mChangeClockColor = true;
		} else if (firstMove) {
			firstMove = false;
			startClock(mStartTime);
		}
		mMoving = false;
		if (!gameOver && !castle) {
			mActivity.switchPlayer();
			if (incrementCount) {
				mMoveCount++;
				if (mMoveCount >= 2 * 50) {
					mActivity.setDrawButtonMode(GameActivity.CLAIMDRAW);
				}
			} else {
				if (mMoveCount >= 2 * 50) {
					mActivity.setDrawButtonMode(GameActivity.OFFERDRAW);
				}
				mMoveCount = 0;
			}
		}
		mActivity.setDrawButtonEnabled(true);
	}

	/**
	 * Returns a Chesspiece of a certain type based on the flag provided.</br>
	 * The piece is initialized with the parameters color, row and column.
	 * 
	 * @param flag
	 *            The flag which decides what piece to return
	 * @param color
	 *            The color of the new piece
	 * @param row
	 *            The row position of the new piece
	 * @param column
	 *            The column position of the new piece
	 * @return A chesspiece matching the parameters
	 */
	private Chesspiece getPieceByFlag(int flag, int color, int row, int column) {
		switch (flag) {
		case QUEEN:
			return new Queen(color, row, column);
		case ROOK:
			return new Rook(color, row, column);
		case BISHOP:
			return new Bishop(color, row, column);
		case KNIGHT:
			return new Knight(color, row, column);
		}
		return null;
	}

	/**
	 * Finds the king of the provided color
	 * 
	 * @param color
	 *            The color whose king you want
	 * @return The king whose getColor() matches color
	 */
	private King getKing(int color) {
		for (int i = 0; i < getMaxRows(); i++) {
			for (int j = 0; j < getMaxColumns(); j++) {
				if (mChessboard[i][j] instanceof King && mChessboard[i][j].getColor() == color) {
					return (King) mChessboard[i][j];
				}
			}
		}
		return null;
	}

	/**
	 * Checks for various game over scenarios and ends the game if it should
	 * end.
	 * 
	 * @param color
	 *            The color who just moved
	 */
	private boolean checkForGameEnd(int color) {
		boolean gameOver = false;
		int enemy;
		if (color == Chesspiece.WHITE) {
			enemy = Chesspiece.BLACK;
		} else {
			enemy = Chesspiece.WHITE;
		}

		boolean inCheck = isInCheck(enemy);
		getKing(enemy).setInCheck(inCheck);
		if (inCheck) {
			mActivity.setCheckText(enemy, PlayerFrame.CHECK);
		} else {
			mActivity.setCheckText(enemy, PlayerFrame.NO_CHECK);
		}

		if (!hasLegalMoves(enemy)) {
			gameOver = true;
			mStopClock = true;
			DBAdapter database = new DBAdapter(mContext);
			database.open();
			if (inCheck) {
				mActivity.setCheckText(enemy, PlayerFrame.CHECKMATE);
				mActivity.setCheckText(color, PlayerFrame.WINNER);
				String won;
				if (color == Chesspiece.WHITE) {
					won = DBAdapter.WHITE_WON;
				} else {
					won = DBAdapter.BLACK_WON;
				}
				database.insertGameResult(mView.getWhiteName(), mView.getBlackName(), mPositionHashFactory.getMoves(),
						won, new Date());
				mView.endTheGame(ChessboardView.WINCHECKMATE, color);
			} else {
				database.insertGameResult(mView.getWhiteName(), mView.getBlackName(), mPositionHashFactory.getMoves(),
						DBAdapter.DRAW_STALEMATE, new Date());
				mView.endTheGame(ChessboardView.DRAWSTALEMATE, color);
				mActivity.setCheckText(color, PlayerFrame.DRAW);
				mActivity.setCheckText(enemy, PlayerFrame.DRAW);
			}

		}
		mPositionHashFactory.hashPosition(color);
		if (mPositionHashFactory.drawByRepetition()) {
			mStopClock = true;
			gameOver = true;
			DBAdapter database = new DBAdapter(mContext);
			database.open();
			database.insertGameResult(mView.getWhiteName(), mView.getBlackName(), mPositionHashFactory.getMoves(),
					DBAdapter.DRAW_REPETITION, new Date());
			mView.endTheGame(ChessboardView.DRAWREPETITION, color);
			mActivity.setCheckText(color, PlayerFrame.DRAW);
			mActivity.setCheckText(enemy, PlayerFrame.DRAW);
		}

		return gameOver;
		/*
		 * TODO lag denne Sjekk: 50 trekk uten sjakk/fanget brikke
		 */
	}

	/**
	 * Checks if the player controlling this color is in check
	 * 
	 * @param color
	 *            The color of pieces to check
	 * @return True if the player is in check
	 */
	private boolean isInCheck(int color) {
		King king = getKing(color);

		int enemy;
		if (color == Chesspiece.WHITE) {
			enemy = Chesspiece.BLACK;
		} else {
			enemy = Chesspiece.WHITE;
		}
		// search for threats
		for (int i = 0; i < getMaxRows(); i++) {
			for (int j = 0; j < getMaxColumns(); j++) {
				if (mChessboard[i][j] != null && mChessboard[i][j].getColor() == enemy
						&& mChessboard[i][j].threatensPosition(king.getRow(), king.getColumn())) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Checks if the player controlling this color has at least 1 legal move
	 * 
	 * @param color
	 *            The color of pieces to check
	 * @return True if the player has at least 1 legal move
	 */
	private boolean hasLegalMoves(int color) {
		for (int i = 0; i < getMaxRows(); i++) {
			for (int j = 0; j < getMaxColumns(); j++) {
				if (mChessboard[i][j] != null && mChessboard[i][j].getColor() == color) {
					if (containsTrue(mChessboard[i][j].legalMoves())) {
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Checks the 2d boolean array for true values
	 * 
	 * @param board
	 *            The array to check
	 * @return True if the array contains at least 1 field set to true
	 */
	private boolean containsTrue(boolean[][] board) {
		for (int i = 0; i < getMaxRows(); i++) {
			for (int j = 0; j < getMaxColumns(); j++) {
				if (board[i][j]) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Gets the number of rows on this chessboard
	 * 
	 * @return The number of rows on this chessboard
	 */
	public int getMaxRows() {
		return 1 + mContext.getResources().getInteger(R.integer.chesspiece_max_row_index);
	}

	/**
	 * Gets the number of columns on this chessboard
	 * 
	 * @return The number of columns on this chessboard
	 */
	public int getMaxColumns() {
		return 1 + mContext.getResources().getInteger(R.integer.chesspiece_max_column_index);
	}
}
