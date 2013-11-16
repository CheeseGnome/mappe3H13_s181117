package hioa.android.chess;

/**
 * This class is used to occupy spaces where a pawn can be captured through en
 * passant
 * <p>
 * EnPassant cannot immediately be identified by color because EnPassants are to
 * be destroyed immediately if they were not captured the following turn. It is
 * therefore more meaningfull to simply give them their own identifier in place
 * of a color.
 * <p>
 * This means that calling getColor() on an EnPassant will result in EN_PASSANT
 * as defined in {@link Chesspiece} rather than it's actual color.
 * <p>
 * If the color needs to be identified for some reason. Use:
 * getPawn().getColor()
 * 
 * @author Lars Sætaberget
 * @version 2013-11-10
 */

public class EnPassant extends Chesspiece {

	private Pawn mPawn;

	public EnPassant(Pawn pawn) {
		setColor(EN_PASSANT);
		setColumn(pawn.getColumn());
		if (pawn.getColor() == WHITE) {
			setRow(pawn.getRow() + 1);
		} else {
			setRow(pawn.getRow() - 1);
		}
		mPawn = pawn;
	}

	/**
	 * Returns the {@link Pawn} that created this EnPassant opportunity
	 * 
	 * @return The Pawn that created this EnPassant
	 */
	public Pawn getPawn() {
		return mPawn;
	}

	@Override
	public boolean move(int row, int column) {
		// This piece cannot move
		return false;
	}

	@Override
	public boolean[][] legalMoves() {
		// This piece has no legal moves
		return null;
	}

	@Override
	public boolean threatensPosition(int row, int column) {
		// This piece cannot threaten any positions
		return false;
	}

}
