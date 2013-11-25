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
	private int mCurrentHashIndex = 0, mCurrentMoveIndex = 0;

	private static final String WHITE = "a", BLACK = "b", ENPASSANT = "c", NOPIECE = "d", WPAWN = "e", WROOK = "f",
			WBISHOP = "g", WKNIGHT = "h", WKING = "i", WQUEEN = "j", BPAWN = "k", BROOK = "l", BBISHOP = "m",
			BKNIGHT = "n", BKING = "o", BQUEEN = "p";

	// Just making sure that DRAW doees not equal the color for white or black
	public static final int DRAW = Chesspiece.WHITE + Chesspiece.BLACK + 1;

	public PositionHashFactory(Chessboard board) {
		mChessboard = board;
	}

	public int getCurrentMovesIndex() {
		return mCurrentMoveIndex;
	}

	public void insertGameResult(int winningColor) {
		if (mCurrentMoveIndex == mMoves.length) {
			expandArray(mMoves);
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

	public void insertMove(Chessboard board, Chesspiece piece, int row, int column, int oldRow, int oldColumn,
			Chesspiece captured, int flag, boolean check, boolean checkmate, Chesspiece other) {

		if (mCurrentMoveIndex == mMoves.length) {
			expandArray(mMoves);
		}
		int addToIndex = 1;
		if (checkmate) {
			// Game result is added before the final move if the game ends by
			// checkmate
			String result = mMoves[mCurrentMoveIndex - 1];
			mMoves[mCurrentMoveIndex--] = result;
			addToIndex++;
		}
		StringBuilder builder = new StringBuilder();

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
		} else if (piece instanceof Rook) {
			builder.append("R");
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
		} else if (piece instanceof Knight) {
			builder.append("N");
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
		} else if (piece instanceof Bishop) {
			builder.append("B");
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
		} else if (piece instanceof Queen) {
			builder.append("Q");
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
		} else if (piece instanceof King) {
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

	public String getMoves() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < mCurrentMoveIndex; i++) {
			builder.append(mMoves[i] + " ");
		}
		return builder.toString();
	}

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
			expandArray(mHashedPositions);
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
	private void expandArray(String[] array) {
		array = Arrays.copyOf(array, array.length + ARRAY_INCREMENT);
	}
}
