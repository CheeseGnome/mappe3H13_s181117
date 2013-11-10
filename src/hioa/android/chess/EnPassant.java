package hioa.android.chess;

/**
 * This class is used to occupy spaces where a pawn can be captured through en
 * passant
 * 
 * @author Lars Sætaberget
 * @version 2013-11-10
 */

public class EnPassant extends Chesspiece {

	private Pawn mPawn;
	
	public EnPassant(Pawn pawn) {
		setColor(EN_PASSANT);
		setColumn(pawn.getColumn());
		if (getColor() == WHITE) {
			setRow(pawn.getRow() + 1);
		} else {
			setRow(pawn.getRow() - 1);
		}
		mPawn = pawn;
		chessboard.placeEnPassant(this);
	}
	
	public Pawn getPawn(){
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
