import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

public class PlayerSkeleton {
	
	
	protected int [][] field = new int[State.ROWS][State.COLS];
	protected int [] top = new int[State.COLS];
	protected double[] weights;
	protected int nextPiece;
	protected int turn;
	// Following variables are features
	protected int height; // Landing Height
	protected int totalHeight;
	protected int diffHeight;
	protected int altitudeDiff;
	protected int maxHeight;
	protected int rowsCleared; // Rows cleared by a Single Move, not total rows cleared
	protected int weightedSum;
	protected int holeCount;
	protected int connectedHole;
	protected int maxWellDepth;
	protected int sumWells;
	protected int weightedWells;
	protected int hTransition;
	protected int vTransition;
	protected int erodedCells; // rowsCleared * number of cells cleared belonging to this turn's piece

	//possible orientations for a given piece type
	protected static int[] pOrients = {1,2,4,4,4,2,2};
	
	//the next several arrays define the piece vocabulary in detail
	//width of the pieces [piece ID][orientation]
	protected static int[][] pWidth = {
			{2},
			{1,4},
			{2,3,2,3},
			{2,3,2,3},
			{2,3,2,3},
			{3,2},
			{3,2}
	};
	//height of the pieces [piece ID][orientation]
	private static int[][] pHeight = {
			{2},
			{4,1},
			{3,2,3,2},
			{3,2,3,2},
			{3,2,3,2},
			{2,3},
			{2,3}
	};
	private static int[][][] pBottom = {
		{{0,0}},
		{{0},{0,0,0,0}},
		{{0,0},{0,1,1},{2,0},{0,0,0}},
		{{0,0},{0,0,0},{0,2},{1,1,0}},
		{{0,1},{1,0,1},{1,0},{0,0,0}},
		{{0,0,1},{1,0}},
		{{1,0,0},{0,1}}
	};
	private static int[][][] pTop = {
		{{2,2}},
		{{4},{1,1,1,1}},
		{{3,1},{2,2,2},{3,3},{1,1,2}},
		{{1,3},{2,1,1},{3,3},{2,2,2}},
		{{3,2},{2,2,2},{2,3},{1,2,1}},
		{{1,2,2},{3,2}},
		{{2,2,1},{2,3}}
	};
	
	//implement this function to have a working system
	public int pickMove(State s, int[][] legalMoves) {
		nextPiece = s.getNextPiece();
		turn = s.getTurnNumber();
		int bestMove = 0; // stores best move so far
		int bestHeuristics = Integer.MAX_VALUE;
		int currentHeuristics;
		for (int i = 0; i < legalMoves.length; i++)
		{
			for (int j=0; j <State.ROWS ;j++) {
				System.arraycopy(s.getField()[j],0, field[j],0, State.COLS);
			}
			System.arraycopy(s.getTop(), 0, top, 0, s.getTop().length);
			if (makeMove(legalMoves[i][0], legalMoves[i][1]))
			{
				currentHeuristics = getHeuristicsVal();
				if (currentHeuristics < bestHeuristics) // Smaller heuristics = better
				{
					bestMove = i;
					bestHeuristics = currentHeuristics;
				}
			}
		}
		return bestMove;
	}
	
	// Generating TestSeeds list, runs 20 iterations using a given weight 
	// And writes hardest seed sequence (lowest score) to textfile
	public static void main(String[] args) {
		int min = Integer.MAX_VALUE;
		 double [] w = { 39.87226246992793, 0.2822313301577022, 0.0, 0.0, 0.0, 90.41644192059289, 0.0, 100.0, 29.025892632632264, 57.261460721109174, 0.0, 26.175997125047157, 100.0, 4.939800183261108, 0};
		long minSeed = 0;
		for (int i = 0; i < 20 ; i++)			
		{
			State s = new State();
			long seed = s.setRandomSeed();
		//	long seed = 1300715528447L;
		//	s.setSeed(seed);
			System.out.println("Seed: "  + seed);
		//	new TFrame(s);
			PlayerSkeleton p = new PlayerSkeleton(); 
			p.setWeights(w);
			while(!s.hasLost()) {
				s.makeMove(p.pickMove(s,s.legalMoves()));
			//	s.draw();
			//	s.drawNext(0,0);
			}
			System.out.println("Rows Cleared: " + s.getRowsCleared());
			if (s.getRowsCleared() < min)
			{
				min = s.getRowsCleared();
				minSeed = seed;
			}
		//	System.out.println("You have completed "+s.getRowsCleared()+" rows.");
		}
		BufferedWriter bufferedWriter = null;

		try {
			//Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter("seeds.txt", true));

			//Start writing to the output stream
			bufferedWriter.append(minSeed + "\n");
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			//Close the BufferedWriter
			try {
				if (bufferedWriter != null) {
					bufferedWriter.flush();
					bufferedWriter.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	// Default playing mode, plays one random game
	public static void main2(String[] args) {
		double [] w = { 39.87226246992793, 0.2822313301577022, 0.0, 0.0, 0.0, 90.41644192059289, 0.0, 100.0, 29.025892632632264, 57.261460721109174, 0.0, 26.175997125047157, 100.0, 4.939800183261108, 0};
		State s = new State();
		s.setRandomSeed();
		//	long seed = 1300715528447L;
		//	s.setSeed(seed);
		//	new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton(); 
		p.setWeights(w);
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			//	s.draw();
			//	s.drawNext(0,0);
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
		
	// Copy of makeMove in State
	// Feature 13 - rows cleared in this move
	// Feature 14 - Eroded Cells in this move
	public boolean makeMove(int orient, int slot) {
		erodedCells = 0;
		rowsCleared = 0;
		//height if the first column makes contact
		height = top[slot]-pBottom[nextPiece][orient][0];
		//for each column beyond the first in the piece
		for(int c = 1; c < pWidth[nextPiece][orient];c++) {
			height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
		}
		
		//check if game ended
		if(height+pHeight[nextPiece][orient] >= State.ROWS) {
			return false;
		}
		
		//for each column in the piece - fill in the appropriate blocks
		for(int i = 0; i < pWidth[nextPiece][orient]; i++) {		
			//from bottom to top of brick
			for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
				field[h][i+slot] = turn;
			}
		}
		
		//adjust top
		for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
			top[slot+c]=height+pTop[nextPiece][orient][c];
		}
		
		//check for full rows - starting at the top
		for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
			//check all columns in the row
			boolean full = true;
			int erodedCandidate = 0;
			for(int c = 0; c < State.COLS; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
				else if (field[r][c]==turn)
				{
					erodedCandidate +=1;
				}
			}
			//if the row was full - remove it and slide above stuff down
			if(full) {
				rowsCleared++;
				erodedCells += erodedCandidate;
				//for each column
				for(int c = 0; c < State.COLS; c++) {

					//slide down all bricks
					for(int i = r; i < top[c]; i++) {
						field[i][c] = field[i+1][c];
					}
					//lower the top
					top[c]--;
					while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
				}
			}
		}
		
		erodedCells *= rowsCleared;
		return true;
	}
	
	// Sets weights for the heuristics
	public void setWeights(double[] newWeights)
	{
		this.weights = newWeights;
	}
	
	// Calculates and returns heuristics value
	public int getHeuristicsVal()
	{
		checkHeights();
		checkHoles();
		checkWells();
		checkHTransition();
		checkVTransition();
		int val = 0;
		// All Features are negative features(smaller = better)
		// Except rowsCleared and erodedCells which are negated
		val += weights[0]*height + weights[1]*totalHeight ;
		val += weights[2]*diffHeight + weights[3]*altitudeDiff;
		val += weights[4]*maxHeight + weights[5]*holeCount; 
		val += weights[6]*weightedSum + weights[7]*connectedHole;
		val += weights[8]*maxWellDepth + weights[9]*sumWells ;
		val += weights[10]*weightedWells + weights[11]*hTransition;
		val += weights[12]*vTransition - weights[13]*rowsCleared;
		val	-= weights[14]*erodedCells;
		return val;
	}
	
	// Feature 1 - Returns sum of heights of columns
	// Feature 2 - Returns sum of difference of height of adjacent columns
	// Feature 3 - Returns max - min column Height (altitude difference)
	// Feature 4 - Returns maxHeight
	public void checkHeights()
	{
		totalHeight = 0;
		diffHeight = 0;
		maxHeight = 0;
		int min = State.ROWS;
		for (int i=0; i< top.length-1; i++)
		{
			totalHeight += top[i];
			diffHeight += Math.abs(top[i] - top[i+1]);
			maxHeight = Math.max(maxHeight, top[i]);
			min = Math.min(min, top[i]);
		}
		totalHeight+= top[top.length-1];
		maxHeight = Math.max(maxHeight, top[top.length-1]);
		min = Math.min(min, top[top.length-1]);
		altitudeDiff = maxHeight -min;
	}
	
	
	// Feature 5 - Returns number of holes in field
	// Feature 6 - Sum of filled cells, weighted by the row
	// Feature 7 - Number of holes, vertically connected holes are counted as 1
	public void checkHoles()
	{
		holeCount = 0;
		weightedSum = 0;
		connectedHole = 0;
		for (int i = 0; i< State.COLS; i++)
		{
			for (int j = top[i]-1; j >= 0; j--)
			{
				if (field[j][i] == 0)
				{
					holeCount++;
					if (field[j+1][i] != 0)
						connectedHole++;
				}
				else
					weightedSum +=j;
			}
		}
	}
	
	// Feature 8 - depth of deepest well in field
	// Feature 9 - Sum of depths of wells
	// Feature 10 - Sum of weighted wells (Weight of depth n well = 1+2+..+n)
	public void checkWells()
	{
		maxWellDepth = 0;
		sumWells = 0;
		weightedWells = 0;
		int currDepth;
		if (top[0] < top[1])
		{
			currDepth = top[1] - top[0];
			maxWellDepth = currDepth;
			sumWells = currDepth;
			weightedWells = (currDepth +1)*currDepth/2; //Based on Arithmetic Progression
		}
		for (int i=1; i < top.length-1; i++)
		{
			if (top[i] < top[i-1] && top[i] < top[i+1])
			{
				currDepth = Math.min(top[i-1], top[i+1]) - top[i];
				maxWellDepth = Math.max(currDepth, maxWellDepth);
				sumWells += currDepth;
				weightedWells +=(currDepth +1)*currDepth/2;
			}
		}
		if (top[top.length-1] < top[top.length-2])
		{
			currDepth = top[top.length-2]- top[top.length-1];
			maxWellDepth = Math.max(maxWellDepth, currDepth);
			sumWells += currDepth;
			weightedWells +=(currDepth +1)*currDepth/2;
		}
	}
	
	// Feature 11 - Number of horizontal Transitions
	// Must be called after checkHeight to get correct values
	public void checkHTransition()
	{
		hTransition = 0;
		//Check first and last columns
		for (int i=0; i< maxHeight; i++)
		{
			if (field[i][0]== 0)
				hTransition++;
			if (field[i][State.COLS-1]==0)
				hTransition++;
		}
		for (int i =0; i<maxHeight; i++)
		{
			for (int j=0; j< State.COLS-1; j++)
			{
				if ( (field[i][j]==0 && field[i][j+1]!=0) || (field[i][j]!=0 && field[i][j+1]==0))
				{
					hTransition++;
				}
			}
		}
		
	}
	
	// Feature 12 - Number of vertical Transitions
	// Must be called after checkHeight to get correct values
	public void checkVTransition() 
	{
		vTransition = 0;
		for (int i=0; i<State.COLS; i++)
		{
			if (field[0][i]==0)
				vTransition++;
		}
		for (int i = 0; i<State.COLS; i++)
		{
			for (int j=0; j < maxHeight; j++)
			{
				if ((field[j][i]==0 && field[j+1][i]!=0) || (field[j][i]!=0 && field[j+1][i]==0))
				{
					vTransition++;
				}
			}
		}
	}
	
	
}
