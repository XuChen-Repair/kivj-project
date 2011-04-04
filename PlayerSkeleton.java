import java.io.*;
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
	

	public static void main2(String[] args) {

	BufferedWriter bufferedWriter = null;
	BufferedReader bufferedReader = null;
	
//	double [] w1 = { 39.87226246992793, 0.2822313301577022, 0.0, 0.0, 0.0, 90.41644192059289, 0.0, 100.0, 29.025892632632264, 57.261460721109174, 0.0, 26.175997125047157, 100.0, 4.939800183261108, 0};

	double [] w2 = { 4.611666397824846, 0.0, 6.687078463473015, 0.0, 17.65034849478665, 100.0, 0.0, 99.63155660402734, 26.45383860283068, 1.7200906369830626, 22.454776188856126, 17.11024244496571, 44.18592529604314, 4.22881999848464, 5.29200858840683};

//	double [] w3 = { 21.68486505002145, 0.0, 9.023652352698946, 0.0, 0.0, 95.86317187656553, 0.0, 93.16080358412056, 5.803678433049965, 50.91238805305449, 9.799377808831037, 29.916726437473535, 100.0, 22.881255312018595, 48.07887629107038}; 

//	double [] w4 = { 0.0, 0.0, 0.0, 0.0, 1.5582433749262439, 91.8331292850514, 0.0, 98.1326665734521, 0.0, 61.78943974409394, 1.5444211711309312, 36.82438901150083, 100.0, 67.21811420862298, 0.11983310366898747 };
	double [] w5 = { 22.71084633664169, 0.0, 0.0, 0.0, 0.0, 100.0, 0.0, 0.0, 0.0, 24.379420943978545, 17.406699232557568, 29.124462600339385, 100.0, 63.5542726066547, 0.0 };

//	double [] w6 = {48.8552474545055, 0.0, 0.0, 0.0, 0.0, 86.82998862893662, 0.0, 81.59178548320465, 7.118188015109838, 72.82872916452304, 0.0, 16.887314224810783, 100.0, 91.71182159781006, 0.0 };
	double [] w7 = {11.708585390569464, 0.0, 1.8974325060818578, 0.0, 0.0, 100.0, 0.0, 83.40570775458364, 19.74020548619076, 49.12019352048655, 6.780747031944694, 35.93440126423564, 95.00893395888016, 6.4556750383596775, 19.070132593850886};

//	double [] w8 = {11.708585390569464, 0.0, 1.8974325060818578, 0.0, 0.0, 100.0, 0.0, 83.40570775458364, 19.74020548619076, 49.12019352048655, 6.780747031944694, 35.93440126423564, 95.00893395888016, 6.4556750383596775, 19.070132593850886};

//	double [] w9 = {0.0, 0.6619439531101865, 4.793286917178153, 0.0, 0.0, 100.0, 0.0, 100.0, 24.342880914974636, 21.06145429004378, 20.750565667664198, 33.55267648693068, 63.19066654766899, 56.73427792398737, 0.0};

//	double [] w10 = {61.88706839048098, 0.0, 0.0, 0.0, 2.830323893727921, 100.0, 0.0, 100.0, 0.0, 9.394831701193526, 32.371113940085, 17.889372940018198, 80.93552071247848, 8.288340692659537, 0.0};
	
	double [][] w = {w2,w5,w7};
	long seed = 0;
		for (int i = 0; i < w.length ; i++)			
		{
			int minscore =Integer.MAX_VALUE;
			int totalscore = 0;

			try {
				bufferedReader = new BufferedReader(new FileReader("seeds.txt"));
				String line;
				while ((line = (bufferedReader.readLine())) != null) 
				{
					seed = Long.parseLong(line);
					State s = new State();
					s.setSeed(seed);
					PlayerSkeleton p = new PlayerSkeleton(); 
					p.setWeights(w[i]);
					while(!s.hasLost()) {
					s.makeMove(p.pickMove(s,s.legalMoves()));
					}
					System.out.println("Weights " + i + " Rows Cleared: " + s.getRowsCleared());
					if (s.getRowsCleared() < minscore)
					{
						minscore = s.getRowsCleared();
					}	
					totalscore += s.getRowsCleared();
					try {
						//Construct the BufferedWriter object
						bufferedWriter = new BufferedWriter(new FileWriter("results.txt", true));

						//Start writing to the output stream
						bufferedWriter.append("Weights" + i +": min score :" + minscore + " total score :" + totalscore + '\n');
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
					} // End of bw try
				} // End of while
			} // End of br try
			catch (Exception e) {
			       System.err.println("Error: " + e);
		     	}
			finally {
				//Close the BufferedReader
				try {
				if (bufferedReader != null) {
					bufferedReader.close();
					}		
				} catch (IOException ex) {
					ex.printStackTrace();
				}	
			} // End Finally
			
		}
	}
	// Generating TestSeeds list, runs 20 iterations using a given weight 
	// And writes hardest seed sequence (lowest score) to textfile
	// The seed is then used in next run of particle swarm

	public static void main3(String[] args) {
		int min = Integer.MAX_VALUE;

	double [] w = {0.0, 0.6619439531101865, 4.793286917178153, 0.0, 0.0, 100.0, 0.0, 100.0, 24.342880914974636, 21.06145429004378, 20.750565667664198, 33.55267648693068, 63.19066654766899, 56.73427792398737, 0.0};
		long minSeed = 0;
		for (int i = 0; i < 20 ; i++)			
		{
			State s = new State();
			long seed = s.setRandomSeed();
		//	long seed = 1300715528447L;
		//	s.setSeed(seed);
			System.out.println("Seed " + i + ": "  + seed);
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
	
	// Default playing mode, plays one random game with modified state.java
	public static void main(String[] args) {
		double [] w = {  22.71084633664169, 0.0, 0.0, 0.0, 0.0, 100.0, 0.0, 0.0, 0.0, 24.379420943978545, 17.406699232557568, 29.124462600339385, 100.0, 63.5542726066547, 0.0 };
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
