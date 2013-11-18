package hioa.android.chess;

import java.util.Arrays;

public class Positions {

	private Chessboard mChessboard;
	private static final int ARRAY_INCREMENT = 100;
	int[] hashed_positions = new int[ARRAY_INCREMENT];
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
			builder = new StringBuilder("1");
		} else {
			builder = new StringBuilder("2");
		}
		for (int i = 0; i < mChessboard.getMaxRows(); i++) {
			for (int j = 0; j < mChessboard.getMaxColumns(); j++) {
				builder.append(getHashValue(mChessboard.getPieceAt(i, j)));
			}
		}
		int hash = Integer.parseInt(builder.toString(), 16);
		if (current_index == hashed_positions.length) {
			expandArray();
		}
		hashed_positions[current_index++] = hash;
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
		int hash = hashed_positions[current_index - 1];
		int repetition_count = 1;
		for (int i = 0; i < current_index; i++) {
			if (hashed_positions[i] == hash) {
				repetition_count++;
				if (repetition_count >= 3) {
					return true;
				}
			}
		}
		return false;
	}

	private int getHashValue(Chesspiece piece) {
		if (piece == null) {
			return 0;
		}
		if (piece.getColor() == Chesspiece.WHITE) {
			if (piece instanceof Pawn) {
				return 3;
			}
			if (piece instanceof Rook) {
				return 4;
			}
			if (piece instanceof Bishop) {
				return 5;
			}
			if (piece instanceof Knight) {
				return 6;
			}
			if (piece instanceof King) {
				return 7;
			}
			if (piece instanceof Queen) {
				return 8;
			}
		} else {
			if (piece instanceof Pawn) {
				return 9;
			}
			if (piece instanceof Rook) {
				return 0xA;
			}
			if (piece instanceof Bishop) {
				return 0xB;
			}
			if (piece instanceof Knight) {
				return 0xC;
			}
			if (piece instanceof King) {
				return 0xD;
			}
			if (piece instanceof Queen) {
				return 0xE;
			}
		}
		return -1;
	}

	private void expandArray() {
		hashed_positions = Arrays.copyOf(hashed_positions, hashed_positions.length + ARRAY_INCREMENT);
	}
}
