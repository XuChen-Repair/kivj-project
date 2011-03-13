
public class PlayerSkeleton {

	int simulationField[][] = new int[State.ROWS][State.COLS];
	int simulationTop[] = new int[State.COLS];
	
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		// evaluate all possible moves to see which one is best
		int bestMove = -1;
		int orient = 0;
		int slot = 0;
		double highestEvaluation = Integer.MIN_VALUE;
		
		for (int i = 0; i < legalMoves.length; i++) {
			orient = legalMoves[i][0];
			slot = legalMoves[i][1];
			
			// Simulate playing the move
			simulatePlayingMove(s, orient, slot, simulationField, simulationTop);
			
			// Evaluate the move
			double evaluation = getLandingHeight(s, orient, slot) * -1
				+ getNumberOfHoles(simulationField, simulationTop) * -4
				+ getErodedPieceCells(s, orient, slot)
				+ getRowTransitions(simulationField) * -1
				+ getColumnTransitions(simulationField) * -1
				+ getWellSums(simulationField) * -1;
			
			if (evaluation > highestEvaluation) {
				highestEvaluation = evaluation;
				bestMove = i;
			}
		}
		
		return bestMove;
	}
	
	// Get landing height of a piece
	private double getLandingHeight(State s, int orient, int slot) {
		// get the height of the highest column under the piece.
		int top[] = s.getTop();
		int pBottom[][][] = State.getpBottom();
		int nextPiece = s.getNextPiece();
		int pWidth[][] = State.getpWidth();
		
		int height = top[slot]-pBottom[nextPiece][orient][0];
		
		//for each column beyond the first in the piece
		for(int c = 1; c < pWidth[nextPiece][orient];c++) {
			height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
		}
		
		int pieceHeight = State.getpHeight()[nextPiece][orient];
		
		return 0.5 * (height + (height + pieceHeight));
	}
	
	/**
	 * Get the total number of holes. A hole is an empty cell that has at least
	 * one filled cell above it in the column.
	 */
	private int getNumberOfHoles(int field[][], int top[]) {
		int numHoles = 0;
		
		for (int columnIndex = 0; columnIndex < field[0].length; columnIndex++) {
			for (int rowIndex = Math.min(top[columnIndex] - 1, 19); rowIndex >= 0; rowIndex--) {
				if (field[rowIndex][columnIndex] == 0)
					numHoles++;
			}
		}
		
		return numHoles;
	}
	
	
	private int getErodedPieceCells(State s, int orient, int slot) {
		int field[][] = new int[s.getField().length][s.getField()[0].length];
		multiArrayCopy(s.getField().clone(), field);
		int nextPiece = s.getNextPiece();
		int pieceWidth = State.getpWidth()[nextPiece][orient];
		int pieceHeight = State.getpHeight()[nextPiece][orient];
		int pieceBottom[] = State.getpBottom()[nextPiece][orient];
		int pieceTop[] = State.getpTop()[nextPiece][orient];
		int top[] = s.getTop().clone();
		
		int height = top[slot] - pieceBottom[0];
		//for each column beyond the first in the piece
		for(int c = 1; c < pieceWidth;c++) {
			height = Math.max(height,top[slot+c] - pieceBottom[c]);
		}
		
		// fill in the appropriate blocks as if the piece has been played
		for(int i = 0; i < pieceWidth; i++) {
			//from bottom to top of brick
			for(int h = height+pieceBottom[i]; h < height+pieceTop[i] && h < 20; h++) {
				field[h][i+slot] = -1; // to indicate that this is a simulated piece (not any more).
			}
		}
		
		//adjust top
		for(int c = 0; c < pieceWidth; c++) {
			top[slot+c]=height+pieceTop[c];
		}
		
		// we now have the piece simulated.
		int rowsCleared = 0;
		int totalErodedCells = 0;
		
		//check for full rows - starting at the top
		for(int r = Math.min(height + pieceHeight - 1, 19); r >= height; r--) {
			//check all columns in the row
			boolean full = true;
			int erodedCellsInRow = 0;
			for(int c = 0; c < 10; c++) {
				if(field[r][c] == 0) {
					full = false;
					erodedCellsInRow = 0;
					break;
				} else if(field[r][c] == -1) {
					erodedCellsInRow++;
				}
			}
			
			if(full) {
				rowsCleared++;
				totalErodedCells += erodedCellsInRow;
			}
		}
		
		return rowsCleared * totalErodedCells;
	}
	
	int getRowTransitions(int field[][] /* must be const */) {
		int rowTransitions = 0;
		for (int rowIndex = 0; rowIndex < field.length; rowIndex++) {
			if (field[rowIndex][0] == 0) {
				rowTransitions++;
			} else if (field[rowIndex][field[0].length -1] == 0) {
				rowTransitions++;
			}
			
			for (int columnIndex = 1; columnIndex < field[0].length; columnIndex++) {
				if ((field[rowIndex][columnIndex] == 0) ^ (field[rowIndex][columnIndex - 1] == 0)) {
					rowTransitions++;
				}
			}
		}

		return rowTransitions;
	}
	
	int getColumnTransitions(int field[][]) {
		int columnTransitions = 0;
		for (int columnIndex = 0; columnIndex < field[0].length; columnIndex++) {
			if (field[0][columnIndex] == 0) {
				columnTransitions++;
			}
			
			for (int rowIndex = 1; rowIndex < field.length; rowIndex++) {
				if ((field[rowIndex - 1][columnIndex] == 0) ^ (field[rowIndex][columnIndex] == 0)) {
					columnTransitions++;
				}
			}
		}
		
		return columnTransitions;
	}
	
	public void multiArrayCopy(int[][] source,int[][] destination) {
		for (int a=0;a<source.length;a++) {
			System.arraycopy(source[a],0,destination[a],0,source[a].length);
		}
	}
	
	public void arrayCopy(int source[], int destination[]) {
		for (int i = 0; i < source.length; i++) {
			destination[i] = source[i];
		}
	}
	
	// Simulate the effect of playing the next piece.
	// Returns the field of the game as if that piece has been played.
	// The piece's cells are have value -1 in the field grid.
	private void simulatePlayingMove(State s, int orient, int slot, int field[][], int top[]) {
		multiArrayCopy(s.getField().clone(), field);
		int nextPiece = s.getNextPiece();
		int pieceWidth = State.getpWidth()[nextPiece][orient];
		int pieceBottom[] = State.getpBottom()[nextPiece][orient];
		int pieceTop[] = State.getpTop()[nextPiece][orient];
		arrayCopy(s.getTop(), top);
		
		int height = top[slot] - pieceBottom[0];
		//for each column beyond the first in the piece
		for(int c = 1; c < pieceWidth;c++) {
			height = Math.max(height,top[slot+c] - pieceBottom[c]);
		}
		
		// fill in the appropriate blocks as if the piece has been played
		for(int i = 0; i < pieceWidth; i++) {
			//from bottom to top of brick
			for(int h = height+pieceBottom[i]; h < height+pieceTop[i] && h < 20; h++) {
				field[h][i+slot] = -1; // to indicate that this is a simulated piece (not any more).
			}
		}
		
		//adjust top
		for(int c = 0; c < pieceWidth; c++) {
			top[slot+c]=height+pieceTop[c];
		}
	}
	
	public int getWellSums(int field[][]) {
		int wellCells = 0;
		int wellWeights = 0;
		
		for (int rowIndex = 0; rowIndex < field.length; rowIndex++) {
			if (field[rowIndex][0] == 0 &&
					(field[rowIndex][1] != 0)) {
				wellCells++;
				// found well cell. Count how many rows beneath the cell and add it to the well weight
				for (int i = rowIndex - 1; i >= 0 && field[i][0] == 0; i--) {
					wellWeights++;
				}
			}
		}
		
		for (int rowIndex = 0; rowIndex < field.length; rowIndex++) {
			if (field[rowIndex][field[0].length - 1] == 0 &&
					(field[rowIndex][field[0].length - 2] != 0)) {
				wellCells++;
				// found well cell. Count how many rows beneath the cell and add it to the well weight
				for (int i = rowIndex - 1; i >= 0 && field[i][field[0].length - 1] == 0; i--) {
					wellWeights++;
				}
			}
		}
		
		for (int columnIndex = 1; columnIndex < field[0].length - 1; columnIndex++) {
			for (int rowIndex = 0; rowIndex < field.length; rowIndex++) {
				if (field[rowIndex][columnIndex] == 0 &&
						(field[rowIndex][columnIndex - 1] != 0) && (field[rowIndex][columnIndex + 1] != 0)) {
					// found well cell. Count how many rows beneath the cell and add it to the well weight
					for (int i = rowIndex - 1; i >= 0 && field[i][columnIndex] == 0; i--) {
						wellWeights++;
					}
					wellCells++;
				}
			}
		}
		
		return wellCells + wellWeights;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			
			//System.err.println("Current score: " + s.getRowsCleared());
			//System.err.println("Well Cells: " + p.getWellSums(s.getField()));
			s.draw();
			s.drawNext(0,0);
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
}
