package main;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import piece.Bishop;
import piece.King;
import piece.Knight;
import piece.Pawn;
import piece.Piece;
import piece.Queen;
import piece.Rook;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable {
	private StatusPanel sp;

	public static final int WIDTH = 800;
	public static final int HEIGHT = 800;
	final int FPS = 60;
	Sound sound = new Sound();
	Board board = new Board();
	Mouse mouse = new Mouse();
	Thread gameThread;
	
	// Piece Variables
	public static ArrayList<Piece> pieces = new ArrayList<>();
	public static ArrayList<Piece> simPieces = new ArrayList<>();
	ArrayList<Piece> promoPiece = new ArrayList<>();
	Piece activePiece, checkingPiece;
	public static Piece castlingPiece;
	
	// Piece Color
	public static final int WHITE = 0;
	public static final int BLACK = 1;
	int currentColor = WHITE;
	
	// Booleans
	boolean canMove, validSquare, promotion, gameOver, staleMate;
	private volatile boolean running = false;
	private boolean gameOverDialogShown = false;
	private boolean staleMateDialogShown = false;
	private boolean piecesInitialized = false;
	
	
	public GamePanel(StatusPanel sp) {
		this.sp = sp;
		
		setPreferredSize(new Dimension(WIDTH,HEIGHT));
		setBackground(Color.WHITE);
		addMouseMotionListener(mouse);
		addMouseListener(mouse);
		
		if(!piecesInitialized) {
		    setPieces();
		    copyPieces(pieces, simPieces);
		    piecesInitialized = true;
		}
	}
	@Override
	public void run() {
	    final double drawInterval = 1_000_000_000.0 / FPS;
	    double delta = 0;

	    long lastTime = System.nanoTime();

	    while (running) {
	        long currentTime = System.nanoTime();
	        delta += (currentTime - lastTime) / drawInterval;
	        lastTime = currentTime;

	        while (delta >= 1) {
	            update();
	            repaint();
	            delta--;
	        }

	        // Give CPU a small break
	        try {
	            Thread.sleep(1);
	        } catch (InterruptedException e) {
	            Thread.currentThread().interrupt();
	        }
	    }
	}
	
	public void launchGame() {
		running = true;
	    gameThread = new Thread(this);
	    gameThread.start();
	}
	
	public void stopGame() {
	    running = false;

	    try {
	        if (gameThread != null) {
	        	// Wait up to 100ms for the thread to finish
	        	gameThread.join(100); 
	        }
	    } catch (InterruptedException e) {
	        Thread.currentThread().interrupt();
	    }

	    gameThread = null;
	}
	
	public void resetGame() {
	    stopGame();

	    // Reset state
	    currentColor = WHITE;
	    promotion = false;
	    gameOver = false;
	    staleMate = false;
	    gameOverDialogShown = false;
	    staleMateDialogShown = false;

	    activePiece = null;
	    checkingPiece = null;
	    castlingPiece = null;

	    pieces.clear();
	    simPieces.clear();

	    launchGame(); // Clean restart
	}
	
	public void setPieces() {
		// White
		pieces.add(new Pawn(WHITE, 0, 6));
		pieces.add(new Pawn(WHITE, 1, 6));
		pieces.add(new Pawn(WHITE, 2, 6));
		pieces.add(new Pawn(WHITE, 3, 6));
		pieces.add(new Pawn(WHITE, 4, 6));
		pieces.add(new Pawn(WHITE, 5, 6));
		pieces.add(new Pawn(WHITE, 6, 6));
		pieces.add(new Pawn(WHITE, 7, 6));
		pieces.add(new Rook(WHITE, 0, 7));
		pieces.add(new Rook(WHITE, 7, 7));
		pieces.add(new Knight(WHITE, 1, 7));
		pieces.add(new Knight(WHITE, 6, 7));
		pieces.add(new Bishop(WHITE, 2, 7));
		pieces.add(new Bishop(WHITE, 5, 7));
		pieces.add(new Queen(WHITE, 3, 7));
		pieces.add(new King(WHITE, 4, 7));
		
		// Black
		pieces.add(new Pawn(BLACK, 0, 1));
		pieces.add(new Pawn(BLACK, 1, 1));
		pieces.add(new Pawn(BLACK, 2, 1));
		pieces.add(new Pawn(BLACK, 3, 1));
		pieces.add(new Pawn(BLACK, 4, 1));
		pieces.add(new Pawn(BLACK, 5, 1));
		pieces.add(new Pawn(BLACK, 6, 1));
		pieces.add(new Pawn(BLACK, 7, 1));
		pieces.add(new Rook(BLACK, 0, 0));
		pieces.add(new Rook(BLACK, 7, 0));
		pieces.add(new Knight(BLACK, 1, 0));
		pieces.add(new Knight(BLACK, 6, 0));
		pieces.add(new Bishop(BLACK, 2, 0));
		pieces.add(new Bishop(BLACK, 5, 0));
		pieces.add(new Queen(BLACK, 3, 0));
		pieces.add(new King(BLACK, 4, 0));
	}
	
	private void copyPieces(ArrayList<Piece> source, ArrayList<Piece> target) {
		target.clear();
        for (Piece p : source) {
            target.add(p);
        }
	}

	public void update() {
	    // 1. Handle promotion UI first
	    if (promotion) {
	        promotion();
	    }
	    else if(!gameOver && !staleMate) {
            if(mouse.pressed) {
                if(activePiece == null) {
                    for(Piece piece : simPieces) {
                        if(piece.color == currentColor &&
                            piece.col == mouse.x / Board.SQUARE_SIZE &&
                            piece.row == mouse.y / Board.SQUARE_SIZE) {
                                activePiece = piece;
                            }
                    }
                } else {
                    simulate();
                }
            }
	    }
	    if(!mouse.pressed) {
	    	if(activePiece != null) {
	    		if(validSquare) {
		            playSoundEffect(1);
		            // Commit move to real board
		            copyPieces(simPieces, pieces);
		            activePiece.updatePosition();

		            if(castlingPiece != null) {
		                castlingPiece.updatePosition();
		            }
//		            if(canPromote()) {
//		                promotion = true;
//		                return;
//		            }
		            int opponentColor = (currentColor == WHITE ? BLACK : WHITE);
		            Piece opponentKing = getKing(opponentColor);
		            
		            if(isKingInCheck(opponentColor) && isCheckMate(opponentColor) && !isKingCanMove(opponentKing)) {
		            	 System.out.println("Checkmate!");
		                 gameOver = true;
		                 return;
                    }
                    else if(isStaleMate(opponentColor) && !isKingCanMove(opponentKing)) {
                    	 System.out.println("Stalemate!");
 		                 staleMate = true;
 		                 return;
                    } else {
                        if(canPromote()) {
                            promotion = true;
                        } else {
                            changePlayer();
                        }
                    }
                } else {
                    copyPieces(pieces, simPieces);
                    activePiece.resetPosition();
                    activePiece = null;
                }
	    	}
	    }
	}


	
	private void simulate() {
		// Reset the piece list in every loop
		copyPieces(pieces, simPieces);	
		canMove = false;
		validSquare = false;
		
		// Reset castling
		if(castlingPiece != null) {
			castlingPiece.col = castlingPiece.preCol;
			castlingPiece.x = castlingPiece.getX(castlingPiece.col);
			castlingPiece = null;
		}	
		// If a piece is being held, update its position
		activePiece.x = mouse.x - Board.HALF_SQUARE_SIZE;
		activePiece.y = mouse.y - Board.HALF_SQUARE_SIZE;
		activePiece.col = activePiece.getCol(activePiece.x);
		activePiece.row = activePiece.getRow(activePiece.y);	
		// Check if the active piece is movable
		if(activePiece.canMove(activePiece.col,  activePiece.row)) {
			canMove = true;
			
			if(activePiece.hittingPiece != null) {
				simPieces.remove(activePiece.hittingPiece.getIndex());
			}
			
			checkCastling();
			validSquare = true;
			
			if(isIllegal(activePiece) == false) {
				validSquare = true;
			}
//			if(!isIllegal(activePiece) && !isKingInCapture(myColor)) {
//				validSquare = true;
//			}
//			
//			if(!isIllegal(activePiece)) {
//			    validSquare = true;
//			}
		}
	}
	
	private void changePlayer() {
		if(currentColor == WHITE) {
			currentColor = BLACK;
			// Reset two moved
			for(Piece piece : pieces) {
				if(piece.color == BLACK) {
					piece.twoMoved = false;
				}
			}
		}
		else {
			currentColor = WHITE;
			// Reset two moved
			for(Piece piece : pieces) {
				if(piece.color == WHITE) {
					piece.twoMoved = false;
				}
			}
		}
		activePiece = null;
	}
	
	private void checkCastling() {
		if(castlingPiece != null) {
			// Rook on left
			if(castlingPiece.col == 0) {
				castlingPiece.col += 3;				
			}
			// Rook on right
			else if(castlingPiece.col == 7) {
				castlingPiece.col -= 2;
			}
			castlingPiece.x = castlingPiece.getX(castlingPiece.col);
		}
	}
		
	private boolean canPromote() {
	    if (activePiece.type == Type.PAWN) {
	        if ((currentColor == WHITE && activePiece.row == 0) || 
	            (currentColor == BLACK && activePiece.row == 7)) {
	            return true;
	        }
	    }
	    return false;
	}
	
	private void promotion() {
	    if (activePiece == null || !canPromote()) return;
	    // Prepare promotion options
	    Piece[] options = {
	        new Queen(currentColor, activePiece.col, activePiece.row),
	        new Rook(currentColor, activePiece.col, activePiece.row),
	        new Bishop(currentColor, activePiece.col, activePiece.row),
	        new Knight(currentColor, activePiece.col, activePiece.row)
	    };
	    // Extract piece icons
	    Object[] icons = new Object[options.length];
	    for (int i = 0; i < options.length; i++) {
	    	 icons[i] = new javax.swing.ImageIcon(options[i].image);
	    }
	    // Show dialog
	    int choice = JOptionPane.showOptionDialog(
	        this,
	        "Choose a piece for promotion:",
	        "Pawn Promotion",
	        JOptionPane.DEFAULT_OPTION,
	        JOptionPane.PLAIN_MESSAGE,
	        null,
	        icons,
	        icons[0]
	    );
	    if (choice < 0 || choice >= options.length) return;

	    // Replace pawn with chosen piece
	    simPieces.remove(activePiece.getIndex());
	    simPieces.add(options[choice]);
	    copyPieces(simPieces, pieces);

	    activePiece = null;
	    promotion = false;
	    // 5. Check for check/checkmate immediately
	    int opponentColor = (currentColor == WHITE ? BLACK : WHITE);
	    if (isKingInCheck(opponentColor)) {
	        System.out.println("Check!");
	        if (isCheckMate(opponentColor)) {
	            System.out.println("Checkmate!");
	            gameOver = true;
	            return;
	        }
	    }
	    // 6. Switch player
	    changePlayer();
	}

	private boolean isIllegal(Piece king) {
		if(king.type == Type.KING) {
			for(Piece piece : simPieces) {
				if(piece != king && piece.color != king.color && piece.canMove(king.col, king.row)) {
					return true;
				}
			}
		}
	    return false;
	}
	
	private Piece getKing(int color) {
	    for (Piece piece : simPieces) {
	        if (piece.type == Type.KING && piece.color == color) {
	            return piece;
	        }
	    }
	    return null;
	}

	
	private boolean isKingInCheck(int kingColor) {
	    Piece king = getKing(kingColor);
	    if (king == null) return false;

	    for (Piece p : simPieces) {
	        if (p.color != kingColor && p.canMove(king.col, king.row)) {
	            checkingPiece = p;
	            return true;
	        }
	    }

	    checkingPiece = null;
	    return false;
	}

	
	private boolean isKingInCapture(int kingColor) {
	    Piece king = getKing(kingColor);
	    if (king == null) return false;

	    for (Piece piece : simPieces) {
	        if (piece.color != kingColor && piece.canMove(king.col, king.row)) {
	            return true;
	        }
	    }
	    return false;
	}

	
	private boolean isKingCanMove(Piece king) {
		// Check 8 moves
		int[][] moves = {
		        {-1, -1}, {0, -1}, {1, -1},
		        {-1,  0},          {1,  0},
		        {-1,  1}, {0,  1}, {1,  1}
		    };

		    for (int[] m : moves) {
		        if (isKingValidMove(king, m[0], m[1])) {
		            return true;
		        }
		    }
		    return false;
	}
	
	private boolean isKingValidMove(Piece king, int colPlus, int rowPlus) {
		boolean isValidMove = false;
		
		king.col += colPlus;
		king.row += rowPlus;
		
		if(king.canMove(king.col, king.row)) {
			if(king.hittingPiece != null) {
				simPieces.remove(king.hittingPiece.getIndex());
			}
			if(isIllegal(king) == false) {
				isValidMove = true;
			}
		}
		
		king.resetPosition();
		copyPieces(pieces, simPieces);
		
		return isValidMove;

	}
	
	private boolean isCheckMate(int kingColor) {
	    Piece king = getKing(kingColor);
	    if(king == null) return false;
	    // 1. If the king can move, it is NOT checkmate
	    if (isKingCanMove(king)) return false;
	    // 2. Identify all opposing pieces that put the king in check
	    ArrayList<Piece> threats = new ArrayList<>();
	    for (Piece p : simPieces) {
	        if(p.color != kingColor && p.canMove(king.col, king.row)) {
	            threats.add(p);
	        }
	    }
	    // 3. If no threats, not checkmate
	    if(threats.isEmpty()) return false;
	    // 4. Try to block or capture each threat
	    for (Piece threat : threats) {
	        int colDiff = threat.col - king.col;
	        int rowDiff = threat.row - king.row;
	        // Get all squares between king and threat (for sliding pieces)
	        ArrayList<int[]> path = new ArrayList<>();

	        if(colDiff == 0 || rowDiff == 0 || Math.abs(colDiff) == Math.abs(rowDiff)) {
	            // Vertical, horizontal, or diagonal
	            int stepCol = Integer.signum(colDiff);
	            int stepRow = Integer.signum(rowDiff);

	            int currCol = king.col + stepCol;
	            int currRow = king.row + stepRow;

	            while(currCol != threat.col || currRow != threat.row) {
	                path.add(new int[]{currCol, currRow});
	                currCol += stepCol;
	                currRow += stepRow;
	            }
	        }
	        // 5. Try to block or capture
	        for(Piece p : simPieces) {
	            if(p.color == kingColor && p != king) {
	                // Can capture threat directly?
	                if (p.canMove(threat.col, threat.row)) return false;

	                // Can block sliding piece?
	                for(int[] square : path) {
	                    if (p.canMove(square[0], square[1])) return false;
	                }
	            }
	        }
	    }
	    // 6. If king cannot move and no piece can block or capture threat → checkmate
	    return true;
	}

	private boolean isStaleMate(int kingColor) {
	    Piece king = getKing(kingColor);
	    if(king == null) return false;

	    // 1. King can move → not stalemate
	    if(isKingCanMove(king)) return false;

	    // 2. King is in check → not stalemate
	    if(isKingInCheck(kingColor)) return false;

	    // 3. Check all other pieces of the same color
	    for(Piece piece : simPieces) {
	        if(piece.color != kingColor) continue;

	        // Loop through all board positions
	        for(int col = 0; col < 8; col++) {
	            for(int row = 0; row < 8; row++) {
	                if(!piece.canMove(col, row)) continue;

	                // Simulate move using a temporary copy, not touching simPieces
	                int origCol = piece.col;
	                int origRow = piece.row;
	                Piece captured = piece.hittingPiece;

	                piece.col = col;
	                piece.row = row;

	                if(captured != null) simPieces.remove(captured);

	                // If king not in check after this move → legal move exists
	                if(!isKingInCapture(kingColor)) {
	                    // Restore piece
	                    piece.col = origCol;
	                    piece.row = origRow;
	                    if(captured != null) simPieces.add(captured);
	                    return false;
	                }

	                // Restore piece
	                piece.col = origCol;
	                piece.row = origRow;
	                if (captured != null) simPieces.add(captured);
	            }
	        }
	    }

	    // No legal moves for any piece → stalemate
	    return true;
	}

	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		board.draw(g2);
		
		for(Piece p : simPieces) {
			p.draw(g2);
		}
		
		if(activePiece != null) {
			if(canMove) {
				if(isIllegal(activePiece) || isKingInCapture(currentColor)) {
					g2.setColor(Color.RED);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activePiece.col * Board.SQUARE_SIZE, 
							activePiece.row * Board.SQUARE_SIZE, 
							Board.SQUARE_SIZE, 
							Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));	
				}
				else {
					g2.setColor(Color.WHITE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
					g2.fillRect(activePiece.col * Board.SQUARE_SIZE, 
							activePiece.row * Board.SQUARE_SIZE, 
							Board.SQUARE_SIZE, 
							Board.SQUARE_SIZE);
					g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));	
				}						
			}
			
			activePiece.draw(g2);
		}	
		if(promotion) {
			sp.setMessage("Promote to");
		}
		else {			
			if (currentColor == WHITE) {
			    if (checkingPiece != null && checkingPiece.color == BLACK) {
			        sp.setBackground(Color.RED);
			        sp.setTextColor(Color.WHITE);
			        sp.setMessage("White is in Check!");
			    } else {
			        sp.setBackground(Color.WHITE);
			        sp.setTextColor(Color.BLACK);
			        sp.setMessage("White's turn");
			    }
			} else {
			    if (checkingPiece != null && checkingPiece.color == WHITE) {
			        sp.setBackground(Color.RED);
			        sp.setTextColor(Color.WHITE);
			        sp.setMessage("Black is in Check!");
			    } else {
			        sp.setBackground(Color.WHITE);
			        sp.setTextColor(Color.BLACK);
			        sp.setMessage("Black's turn");
			    }
			}

		}		
		if(gameOver && !gameOverDialogShown) {
			playSoundEffect(2);
		    gameOverDialogShown = true;
		    sp.setMessage(currentColor == WHITE ? "White wins!" : "Black wins!");
		    SwingUtilities.invokeLater(() -> {
		        JOptionPane.showMessageDialog(this, 
		            currentColor == WHITE ? "White wins!" : "Black wins!", 
		            "Checkmate", 
		            JOptionPane.INFORMATION_MESSAGE);
		    });
		}


		if (staleMate && !staleMateDialogShown) {
			staleMateDialogShown = true; // mark as shown
		    sp.setMessage("Stalemate!");
		    SwingUtilities.invokeLater(() -> {
		        JOptionPane.showMessageDialog(this, 
		            currentColor == WHITE ? "White wins!" : "Black wins!", 
		            "Checkmate", 
		            JOptionPane.INFORMATION_MESSAGE);
		    });
		}

	}
	
	public void playMusic(int i) {
		sound.setFile(i);
		sound.play();
		sound.loop();
	}
	
	public void stopMusic() {
		sound.stop();
	}
	
	public void playSoundEffect(int i) {
		sound.setFile(i);
		sound.play();
	}
}
