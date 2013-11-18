package hioa.android.chess;

import java.util.Arrays;

public class Positions {

	private Chessboard mChessboard;
	private static final int ARRAY_INCREMENT = 100;
	String[] hashed_positions = new String[ARRAY_INCREMENT];
	private int current_index = 0;

	public Positions(Chessboard board) {
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
		if (current_index == hashed_positions.length) {
			expandArray();
		}
		hashed_positions[current_index++] = builder.toString();
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
		String hash = hashed_positions[current_index - 1];
		int repetition_count = 1;
		for (int i = 0; i < current_index - 1; i++) {
			if (hashed_positions[i].equals(hash)) {
				repetition_count++;
				if (repetition_count >= 3) {
					return true;
				}
			}
		}
		return false;
	}

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
		throw new IllegalStateException("Unknown Chesspiece instance");
	}

	private void expandArray() {
		hashed_positions = Arrays.copyOf(hashed_positions, hashed_positions.length + ARRAY_INCREMENT);
	}
}
