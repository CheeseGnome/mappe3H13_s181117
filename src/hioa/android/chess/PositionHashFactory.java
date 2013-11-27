package hioa.android.chess;

import java.util.Arrays;

/**
 * This class hashes positions to check for draw by repetition
 * 
 * @author Lars Sætaberget
 * @version 2013-11-20
 */

public class PositionHashFactory {

	private Chessboard mChessboard;
	private static final int ARRAY_INCREMENT = 100;
	String[] mHashedPositions = new String[ARRAY_INCREMENT];
	String[] mMoves = new String[ARRAY_INCREMENT];
	String[] mIntMoves = new String[ARRAY_INCREMENT];
	private int mCurrentHashIndex = 0, mCurrentMoveIndex = 0, mCurrentIntMoveIndex = 0;

	private static final int ROW = 0, COLUMN = 1, OLDROW = 2, OLDCOLUMN = 3;

	private static final String WHITE = "a", BLACK = "b", ENPASSANT = "c", NOPIECE = "d", WPAWN = "e", WROOK = "f",
			WBISHOP = "g", WKNIGHT = "h", WKING = "i", WQUEEN = "j", BPAWN = "k", BROOK = "l", BBISHOP = "m",
			BKNIGHT = "n", BKING = "o", BQUEEN = "p";
	private static final String SPLIT = " ";
	private boolean mRepetition = false;

	/*
	 * It is extremely important that this does not equal Chesspiece.WHITE or
	 * Chesspiece.BLACK
	 */
	public static final int DRAW = Integer.MAX_VALUE;

	public PositionHashFactory(Chessboard board) {
		mChessboard = board;
	}

	/**
	 * Rebuilds the last position in this string representing an array of moves
	 * seperated by SPLIT
	 * 
	 * @param moves
	 *            The string to be converted into an array of chess annotations
	 * @return The color who's turn it is
	 */
	public int rebuildPosition(String moves) {
		String[] move = moves.split(SPLIT);
		int row = -1, column = -1, oldRow = -1, oldColumn = -1;

		for (int i = 0; i < move.length; i++) {
			if (move[i] == null) {
				break;
			}
			row = Integer.parseInt("" + move[i].charAt(ROW));
			column = Integer.parseInt("" + move[i].charAt(COLUMN));
			oldRow = Integer.parseInt("" + move[i].charAt(OLDROW));
			oldColumn = Integer.parseInt("" + move[i].charAt(OLDCOLUMN));
			mChessboard.getPieceAt(oldRow, oldColumn).move(row, column);
		}
		mChessboard.mView.setLastMoveHint(oldRow, oldColumn, row, column);
		if (move.length % 2 == 0) {
			return Chesspiece.WHITE;
		} else {
			return Chesspiece.BLACK;
		}
	}

	public int getCurrentMovesIndex() {
		return mCurrentMoveIndex;
	}

	/**
	 * Inserts an annotation representing the end of the game
	 * 
	 * @param winningColor
	 *            The color that won or DRAW (in this class)
	 */
	public void insertGameResult(int winningColor) {
		if (mCurrentMoveIndex == mMoves.length) {
			mMoves = expandArray(mMoves);
		}
		switch (winningColor) {
		case Chesspiece.WHITE:
			mMoves[mCurrentMoveIndex++] = "1-0";
			return;
		case Chesspiece.BLACK:
			mMoves[mCurrentMoveIndex++] = "0-1";
			return;
		case DRAW:
			mMoves[mCurrentMoveIndex++] = "½–½";
			return;
		}
	}

	/**
	 * Shifts the draw annotation to it's correct place. This is a bugfix for
	 * draws by repetitions
	 */
	public void insertDrawByRepetition() {
		if (mMoves.length == mCurrentMoveIndex) {
			mMoves = expandArray(mMoves);
		}
		mMoves[mCurrentMoveIndex] = mMoves[--mCurrentMoveIndex];
		mRepetition = true;
	}

	/**
	 * Returns the array used to rebuild the state of the game
	 * 
	 * @return A string that should be stored in the database
	 */
	public String getIntMoves() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < mCurrentIntMoveIndex; i++) {
			builder.append(mIntMoves[i] + SPLIT);
		}
		// remove last split
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	/**
	 * Inserts a move into the array responsible for saving the game state
	 * 
	 * @param row
	 *            the row that was moved to
	 * @param column
	 *            the column that was moved to
	 * @param oldRow
	 *            the row that was moved from
	 * @param oldColumn
	 *            the column that was moved from
	 */
	private void insertIntMove(int row, int column, int oldRow, int oldColumn) {
		if (mIntMoves.length == mCurrentIntMoveIndex) {
			mIntMoves = expandArray(mIntMoves);
		}
		String move = "" + row + "" + column + "" + oldRow + "" + oldColumn;
		mIntMoves[mCurrentIntMoveIndex++] = move;
	}

	/**
	 * Creates and stores the chess annotation for this move
	 * 
	 * @param board
	 *            The {@link Chessboard} where the move took place
	 * @param piece
	 *            The {@link Chesspiece} that moved
	 * @param row
	 *            The row that was moved to
	 * @param column
	 *            The column that was moved to
	 * @param oldRow
	 *            The row that was moved from
	 * @param oldColumn
	 *            The column that was moved from
	 * @param captured
	 *            The {@link Chesspiece} that was captured, or null if no
	 *            capture took place
	 * @param flag
	 *            The promotion flag for this move
	 * @param check
	 *            True if the move ended in check(but not in checkmate)
	 * @param checkmate
	 *            True if the move ended in checkmate
	 * @param other
	 *            The other piece of the same type and color that could have
	 *            moved to the same location
	 */
	public void insertMove(Chessboard board, Chesspiece piece, int row, int column, int oldRow, int oldColumn,
			Chesspiece captured, int flag, boolean check, boolean checkmate, Chesspiece other) {

		if (mCurrentMoveIndex == mMoves.length) {
			mMoves = expandArray(mMoves);
		}
		int addToIndex = 1;
		if (checkmate) {
			// Game result is added before the final move if the game ends by
			// checkmate
			String result = mMoves[mCurrentMoveIndex - 1];
			mMoves[mCurrentMoveIndex--] = result;
			addToIndex++;
		} else if (mRepetition) {
			addToIndex++;
		}
		StringBuilder builder = new StringBuilder();
		insertIntMove(row, column, oldRow, oldColumn);

		if (piece instanceof Pawn) {
			if (captured != null) {
				builder.append(translateColumn(oldColumn));
				builder.append("x");
			}
			builder.append(translateColumn(column));
			builder.append(translateRow(row));
			if (flag != Chessboard.NO_PROMOTION) {
				switch (flag) {
				case Chessboard.QUEEN:
					builder.append("Q");
					break;
				case Chessboard.KNIGHT:
					builder.append("N");
					break;
				case Chessboard.ROOK:
					builder.append("R");
					break;
				case Chessboard.BISHOP:
					builder.append("B");
					break;
				}
			}
			if (check) {
				builder.append("+");
			} else if (checkmate) {
				builder.append("#");
			}
			mMoves[mCurrentMoveIndex] = builder.toString();
			mCurrentMoveIndex += addToIndex;
			return;
		}

		else if (piece instanceof Rook || piece instanceof Knight || piece instanceof Bishop || piece instanceof Queen) {
			if (piece instanceof Rook) {
				builder.append("R");
			} else if (piece instanceof Knight) {
				builder.append("N");
			} else if (piece instanceof Bishop) {
				builder.append("B");
			} else if (piece instanceof Queen) {
				builder.append("Q");
			}
			if (other != null) {
				if (oldRow == other.getRow()) {
					builder.append(translateColumn(oldColumn));
				} else {
					builder.append(translateRow(oldRow));
				}
			}
			if (captured != null) {
				builder.append("x");
			}
			builder.append(translateColumn(column));
			builder.append(translateRow(row));
			if (check) {
				builder.append("+");
			} else if (checkmate) {
				builder.append("#");
			}
			mMoves[mCurrentMoveIndex] = builder.toString();
			mCurrentMoveIndex += addToIndex;
			return;
		}

		else if (piece instanceof King) {
			if (column - oldColumn == 2) {
				builder.append("O-O");
				mMoves[mCurrentMoveIndex] = builder.toString();
				mCurrentMoveIndex += addToIndex;
				return;
			} else if (column - oldColumn == -2) {
				builder.append("O-O-O");
				mMoves[mCurrentMoveIndex] = builder.toString();
				mCurrentMoveIndex += addToIndex;
				return;
			}
			builder.append("K");
			if (captured != null) {
				builder.append("x");
			}
			builder.append(translateColumn(column));
			builder.append(translateRow(row));
			if (check) {
				builder.append("+");
			} else if (checkmate) {
				builder.append("#");
			}
			mMoves[mCurrentMoveIndex] = builder.toString();
			mCurrentMoveIndex += addToIndex;
			return;
		}
	}

	/**
	 * Translates this row index to the chess annotation for rows
	 * 
	 * @param row
	 *            The row index to translate
	 * @return A string representing the chess annotation for this index
	 */
	private String translateRow(int row) {
		int result = 8 - row;
		return "" + result;
	}

	/**
	 * Translates this column index to the chess annotation for columns
	 * 
	 * @param row
	 *            The column index to translate
	 * @return A string representing the chess annotation for this index
	 */
	private String translateColumn(int column) {

		switch (column) {
		case 0:
			return "a";
		case 1:
			return "b";
		case 2:
			return "c";
		case 3:
			return "d";
		case 4:
			return "e";
		case 5:
			return "f";
		case 6:
			return "g";
		case 7:
			return "h";
		}
		return "";
	}

	/**
	 * Gets a String representation of the move annotations for all the moves in
	 * this game seperated by SPLI
	 * 
	 * @return A string of all the stored moves
	 */
	public String getMoves() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < mCurrentMoveIndex; i++) {
			builder.append(mMoves[i] + SPLIT);
		}
		// remove last split
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	/**
	 * Gets an array of all the chess annotations that are stored in this object
	 * 
	 * @return An array of all the moves that have been made so far
	 */
	public String[] getMovesArray() {
		return mMoves;
	}

	/**
	 * Creates a hash of the current boardstate and stores it in the database.
	 * <p>
	 * These hashes are used to draw the game if the exact same boardstate
	 * appears 3 times in 1 game
	 */
	public void hashPosition(int color) {
		StringBuilder builder;
		if (color == Chesspiece.WHITE) {
			builder = new StringBuilder(WHITE);
		} else {
			builder = new StringBuilder(BLACK);
		}
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				builder.append(getHashValue(mChessboard.getPieceAt(i, j)));
			}
		}
		if (mCurrentHashIndex == mHashedPositions.length) {
			mHashedPositions = expandArray(mHashedPositions);
		}
		mHashedPositions[mCurrentHashIndex++] = builder.toString();
	}

	/**
	 * Checks all the hashed positions to see if this game should end in a draw
	 * by repetition
	 * 
	 * @return True if the last entered position has occured 3 times during this
	 *         game
	 */
	public boolean drawByRepetition() {
		// Last entered hash
		String hash = mHashedPositions[mCurrentHashIndex - 1].replaceAll(ENPASSANT, NOPIECE);
		int repetition_count = 1;
		for (int i = 0; i < mCurrentHashIndex - 1; i++) {
			if (mHashedPositions[i].replaceAll(ENPASSANT, NOPIECE).equals(hash)) {
				repetition_count++;
				if (repetition_count >= 3) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Returns the hash value used to represent a piece of this color and class
	 * 
	 * @param piece
	 *            The piece to hash
	 * @return A hash value representing a piece of this type and color
	 */
	private String getHashValue(Chesspiece piece) {
		if (piece == null) {
			return NOPIECE;
		}
		if (piece instanceof EnPassant) {
			return ENPASSANT;
		}
		if (piece.getColor() == Chesspiece.WHITE) {
			if (piece instanceof Pawn) {
				return WPAWN;
			}
			if (piece instanceof Rook) {
				return WROOK;
			}
			if (piece instanceof Bishop) {
				return WBISHOP;
			}
			if (piece instanceof Knight) {
				return WKNIGHT;
			}
			if (piece instanceof King) {
				return WKING;
			}
			if (piece instanceof Queen) {
				return WQUEEN;
			}
		} else {
			if (piece instanceof Pawn) {
				return BPAWN;
			}
			if (piece instanceof Rook) {
				return BROOK;
			}
			if (piece instanceof Bishop) {
				return BBISHOP;
			}
			if (piece instanceof Knight) {
				return BKNIGHT;
			}
			if (piece instanceof King) {
				return BKING;
			}
			if (piece instanceof Queen) {
				return BQUEEN;
			}
		}
		throw new IllegalStateException("Unknown Chesspiece");
	}

	/**
	 * Expands the array by ARRAY_INCREMENT number of spaces
	 */
	private String[] expandArray(String[] array) {
		return Arrays.copyOf(array, array.length + ARRAY_INCREMENT);
	}
}
