package hioa.android.chess;

import java.util.Arrays;

import android.content.Context;
import android.database.Cursor;

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
	private int mCurrentHashIndex = 0, mCurrentMoveIndex = 0;
	DBAdapter database;

	private static final String WHITE = "a", BLACK = "b", ENPASSANT = "c", NOPIECE = "d", WPAWN = "e", WROOK = "f",
			WBISHOP = "g", WKNIGHT = "h", WKING = "i", WQUEEN = "j", BPAWN = "k", BROOK = "l", BBISHOP = "m",
			BKNIGHT = "n", BKING = "o", BQUEEN = "p";

	private static final int PIECE = 0, ROW = 1, COLUMN = 2, OLDROW = 3, OLDCOLUMN = 4, CASTLE = 5;

	public PositionHashFactory(Context context, Chessboard board) {
		mChessboard = board;
		database = new DBAdapter(context);
		database.open();
		database.newPositionHashFactory();
	}

	public PositionHashFactory(Context context, Chessboard board, Cursor oldHashes) {
		mChessboard = board;
		database = new DBAdapter(context);
		database.open();
		while (mHashedPositions.length < oldHashes.getCount()) {
			expandArray(mHashedPositions);
		}
		int i = 0;
		int columnIndex = oldHashes.getColumnIndex(DBAdapter.HASH);
		while (oldHashes.moveToNext()) {
			mHashedPositions[i] = oldHashes.getString(columnIndex);
			i++;
		}
	}

	public void insertMove(Chessboard board, Chesspiece piece, int row, int column, int oldRow, int oldColumn,
			Chesspiece captured, int flag) {
		StringBuilder builder = new StringBuilder();
		Chesspiece other;
		boolean otherPossible = false;
		if (!(piece instanceof King) && !(piece instanceof Pawn)) {
			loop: for (int i = 0; i < board.getMaxRows(); i++) {
				for (int j = 0; j < board.getMaxColumns(); j++) {
					other = board.getPieceAt(i, j);
					if (other != null && other.getColor() == piece.getColor() && other.sameClass(piece)
							&& other.legalMoves()[row][column]) {
						otherPossible = true;
						break loop;
					}
				}
			}
		}
		if (piece instanceof Pawn) {
			if (captured != null) {
				builder.append(translateColumn(oldColumn));
				builder.append("x");
			}
			builder.append(translateColumn(column));
			builder.append(translateColumn(row));
			if (flag != Chessboard.NO_PROMOTION) {
				switch (flag) {
				case Chessboard.QUEEN:
					builder.append("Q");
				case Chessboard.KNIGHT:
					builder.append("N");
				case Chessboard.ROOK:
					builder.append("R");
				case Chessboard.BISHOP:
					builder.append("B");
				}
			}
			mMoves[mCurrentMoveIndex++] = builder.toString();
			return;
		}
	}

	private String translateRow(int row) {
		int result = 8 - row;
		return "" + result;
	}

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
	 * Rebuilds the last hashed position on this {@link Chessboard}
	 * 
	 * @param board
	 *            The board where the rebuilding should occur
	 */
	public void rebuildPosition(Chessboard board) {
		// TODO klokke?
		Object[] parameters = new Object[6];
		String hash;
		// TODO promotion will not work here
		for (int k = 1; k <= mCurrentHashIndex; k++) {
			hash = mHashedPositions[k];
			for (int i = 0; i < board.getMaxRows(); i++) {
				for (int j = 0; j < board.getMaxColumns(); j++) {
					if (!("" + hash.charAt(i * 8 + j)).equals(getHashValue(board.getPieceAt(i, j)))) {
						if (board.getPieceAt(i, j) != null && !(board.getPieceAt(i, j) instanceof EnPassant)) {
							/*
							 * Special cases where we don't want to select rook
							 * if it's a castle move
							 */
							if (parameters[PIECE] == null || parameters[PIECE] instanceof Rook) {
								parameters[PIECE] = board.getPieceAt(i, j);
							}
							parameters[OLDROW] = i;
							parameters[OLDCOLUMN] = j;
						} else if (!(board.getPieceAt(i, j) instanceof EnPassant)) {
							parameters[ROW] = i;
							parameters[COLUMN] = j;
						}
					}
				}// columns
			}// rows
			move(board, parameters);
		}// hashes
	}

	private void move(Chessboard board, Object[] parameters) {
		boolean castle = (parameters[PIECE] instanceof King && Math.abs((Integer) parameters[OLDCOLUMN]
				- (Integer) parameters[COLUMN]) == 2);

		board.move((Chesspiece) parameters[PIECE], (Integer) parameters[ROW], (Integer) parameters[COLUMN],
				(Integer) parameters[OLDROW], (Integer) parameters[OLDCOLUMN], castle);

		for (int i = 0; i < parameters.length; i++) {
			parameters[i] = null;
		}
	}

	public String getMoves() {
		return "a";// TODO
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
			expandArray(mHashedPositions);
		}
		mHashedPositions[mCurrentHashIndex++] = builder.toString();
		database.insertHash(builder.toString());
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
	private void expandArray(String[] array) {
		array = Arrays.copyOf(array, array.length + ARRAY_INCREMENT);
	}
}
