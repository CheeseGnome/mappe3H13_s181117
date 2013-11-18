package hioa.android.chess;

import java.util.Arrays;

public class PositionHashFactory {

	private Chessboard mChessboard;
	private static final int ARRAY_INCREMENT = 100;
	String[] mHashedPositions = new String[ARRAY_INCREMENT];
	private int mCurrentIndex = 0;

	public PositionHashFactory(Chessboard board) {
		mChessboard = board;
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
			builder = new StringBuilder("a");
		} else {
			builder = new StringBuilder("b");
		}
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				builder.append(getHashValue(mChessboard.getPieceAt(i, j)));
			}
		}
		if (mCurrentIndex == mHashedPositions.length) {
			expandArray();
		}
		mHashedPositions[mCurrentIndex++] = builder.toString();
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
		String hash = mHashedPositions[mCurrentIndex - 1];
		int repetition_count = 1;
		for (int i = 0; i < mCurrentIndex - 1; i++) {
			if (mHashedPositions[i].equals(hash)) {
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
		if (piece == null || piece instanceof EnPassant) {
			return "c";
		}
		if (piece.getColor() == Chesspiece.WHITE) {
			if (piece instanceof Pawn) {
				return "d";
			}
			if (piece instanceof Rook) {
				return "e";
			}
			if (piece instanceof Bishop) {
				return "f";
			}
			if (piece instanceof Knight) {
				return "g";
			}
			if (piece instanceof King) {
				return "h";
			}
			if (piece instanceof Queen) {
				return "i";
			}
		} else {
			if (piece instanceof Pawn) {
				return "j";
			}
			if (piece instanceof Rook) {
				return "k";
			}
			if (piece instanceof Bishop) {
				return "l";
			}
			if (piece instanceof Knight) {
				return "m";
			}
			if (piece instanceof King) {
				return "n";
			}
			if (piece instanceof Queen) {
				return "o";
			}
		}
		throw new IllegalStateException("Unknown Chesspiece");
	}

	/**
	 * Expands the array by ARRAY_INCREMENT number of spaces
	 */
	private void expandArray() {
		mHashedPositions = Arrays.copyOf(mHashedPositions,
				mHashedPositions.length + ARRAY_INCREMENT);
	}
}
