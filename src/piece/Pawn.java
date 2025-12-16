package piece;

import main.GamePanel;
import main.Type;

public class Pawn extends Piece {

	public Pawn(int color, int col, int row) {
		super(color, col, row);
		type = Type.PAWN;
		
		if(color == GamePanel.WHITE) {
			image = getImage("/piece/w_pawn");
		}
		else {
			image = getImage("/piece/b_pawn");
		}
	}
	
	public boolean canMove(int targetCol, int targetRow) {
		if(isWithinBoard(targetCol, targetRow) && isSameSquare(targetCol, targetRow) == false) {
			int moveValue;
			// Define move value based on its color
			if(color == GamePanel.WHITE) {
				moveValue = -1;
			}
			else {
				moveValue = 1;
			}
			// Checking piece
			hittingPiece = getHittingPiece(targetCol, targetRow);		
			// 1 square move
			if(targetCol == preCol && targetRow == preRow + moveValue && hittingPiece == null) {
				return true;
			}
			// 2 square move
			if(targetCol == preCol && targetRow == preRow + moveValue*2 && hittingPiece == null && 
					moved == false && pieceIsOnStraightLine(targetCol, targetRow) == false	) {
				return true;
			}
			// Capture piece diagonally
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue && hittingPiece != null &&
					hittingPiece.color != color) {
				return true;
			}
			// En passant
			if(Math.abs(targetCol - preCol) == 1 && targetRow == preRow + moveValue ) {
				for(Piece piece : GamePanel.simPieces) {
					if(piece.col == targetCol && piece.row == preRow && piece.twoMoved == true) {
						hittingPiece = piece;
						return true;
					}
				}
			}
			// Promotion
			
			
		}
		return false;
	}
}
