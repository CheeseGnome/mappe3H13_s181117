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

	public int rebuildPosition(String moves) {
		String[] move = moves.split(SPLIT);
		int color = Chesspiece.WHITE;

		for (int i = 0; i < move.length; i++) {
			if (move[i] == null) {
				break;
			}
			performMove(move[i], color);
			if (color == Chesspiece.WHITE) {
				color = Chesspiece.BLACK;
			} else {
				color = Chesspiece.WHITE;
			}
		}
		if (move.length % 2 == 0) {
			return Chesspiece.WHITE;
		} else {
			return Chesspiece.BLACK;
		}
	}

	public int getCurrentMovesIndex() {
		return mCurrentMoveIndex;
	}

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
	 * Performs a move based on the chess annotation for the move and the color
	 * that moved
	 * 
	 * @param move
	 *            The chess annotation for the move to perform
	 * @param color
	 *            The color to move
	 */
	private void performMove(String move, int color) {
		move = move.replaceAll("x", "").replaceAll("\\+", "").replaceAll("#", "");
		char letter = move.charAt(0);
		Chesspiece piece;
		Chesspiece sameClass = null;

		if (letter == 'R') {
			sameClass = new Rook(color, -1, -1);
		} else if (letter == 'N') {
			sameClass = new Knight(color, -1, -1);
		} else if (letter == 'B') {
			sameClass = new Bishop(color, -1, -1);
		} else if (letter == 'Q') {
			sameClass = new Queen(color, -1, -1);
		}

		if (sameClass != null) {
			letter = move.charAt(2);
			if (Character.isDigit(letter)) {
				piece = mChessboard.otherPieceCanMoveTo(sameClass, translateRow(letter),
						translateColumn(move.charAt(1)));
				mChessboard.mView.setLastMoveHint(piece.getRow(), piece.getColumn(), translateRow(letter),
						translateColumn(move.charAt(1)));
				piece.move(translateRow(letter), translateColumn(move.charAt(1)));
			}
			else if (Character.isDigit(move.charAt(1))) {
				piece = mChessboard.getPieceOnRow(sameClass, translateRow(move.charAt(1)));
				mChessboard.mView.setLastMoveHint(piece.getRow(), piece.getColumn(), translateRow(letter),
						translateColumn(move.charAt(1)));
				piece.move(translateRow(move.charAt(3)), translateColumn(letter));
			} 
			else {
				piece = mChessboard.getPieceOnColumn(sameClass, translateColumn(move.charAt(1)));
				mChessboard.mView.setLastMoveHint(piece.getRow(), piece.getColumn(), translateRow(letter),
						translateColumn(move.charAt(1)));
				piece.move(translateRow(move.charAt(3)), translateColumn(letter));
			}
		}

		else if (letter == 'K') {
			piece = mChessboard.getKing(color);
			mChessboard.mView.setLastMoveHint(piece.getRow(), piece.getColumn(), translateRow(move.charAt(2)),
					translateColumn(move.charAt(1)));
			piece.move(translateRow(move.charAt(2)), translateColumn(move.charAt(1)));
		}
		// castle
		else if (letter == 'O') {
			piece = mChessboard.getKing(color);
			if (move.length() == 3) {
				// kingside
				mChessboard.mView.setLastMoveHint(piece.getRow(), piece.getColumn(), piece.getRow(),
						piece.getColumn() + 2);
				piece.move(piece.getRow(), piece.getColumn() + 2);
			} else {
				// queenside
				mChessboard.mView.setLastMoveHint(piece.getRow(), piece.getColumn(), piece.getRow(),
						piece.getColumn() - 2);
				piece.move(piece.getRow(), piece.getColumn() - 2);
			}
		}

		else {
			sameClass = new Pawn(color, -1, -1);
			if (move.lastIndexOf("Q") != -1) {
				mChessboard.setPromotionFlag(Chessboard.QUEEN);
			} else if (move.lastIndexOf("N") != -1) {
				mChessboard.setPromotionFlag(Chessboard.KNIGHT);
			} else if (move.lastIndexOf("R") != -1) {
				mChessboard.setPromotionFlag(Chessboard.ROOK);
			} else if (move.lastIndexOf("B") != -1) {
				mChessboard.setPromotionFlag(Chessboard.BISHOP);
			}
			int legalRow, legalColumn;
			if (Character.isDigit(move.charAt(1))) {
				legalRow = translateRow(move.charAt(1));
				legalColumn = translateColumn(move.charAt(0));
			} else {
				legalRow = translateRow(move.charAt(2));
				legalColumn = translateColumn(move.charAt(1));
			}
			piece = mChessboard.getPawnOnColumn(color, translateColumn(move.charAt(0)), legalRow, legalColumn);
			mChessboard.mView.setLastMoveHint(piece.getRow(), piece.getColumn(), legalRow, legalColumn);
			piece.move(legalRow, legalColumn);
		}
	}

	private int translateRow(char row) {
		int result;
		try {
			result = 8 - Integer.parseInt("" + row);
		} catch (NumberFormatException nfe) {
			result = -1;
		}
		return result;
	}

	private int translateColumn(char column) {
		int result = -1;
		if (column == 'a') {
			result = 0;
		} else if (column == 'b') {
			result = 1;
		} else if (column == 'c') {
			result = 2;
		} else if (column == 'd') {
			result = 3;
		} else if (column == 'e') {
			result = 4;
		} else if (column == 'f') {
			result = 5;
		} else if (column == 'g') {
			result = 6;
		} else if (column == 'h') {
			result = 7;
		}
		return result;
	}

	public void insertDrawByRepetition() {
		if (mMoves.length == mCurrentMoveIndex) {
			mMoves = expandArray(mMoves);
		}
		mMoves[mCurrentMoveIndex] = mMoves[--mCurrentMoveIndex];
		mRepetition = true;
	}

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
			builder.append(mMoves[i] + SPLIT);
		}
		// remove last split
		builder.deleteCharAt(builder.length() - 1);
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
