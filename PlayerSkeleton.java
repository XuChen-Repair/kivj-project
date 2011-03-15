public class PlayerSkeleton {
	
	
	protected int [][] field = new int[State.ROWS][State.COLS];
	protected int [] top = new int[State.COLS];
	protected double[] weights;
	protected int nextPiece;
	protected int turn;
	protected int height;
	// Following variables are features
	protected int totalHeight;
	protected int diffHeight;
	protected int maxHeight;
	protected int rowsCleared; // Rows cleared by a Single Move, not total rows cleared
	protected int weightedSum;
	protected int holeCount;
	protected int connectedHole;
	protected int maxWellDepth;
	protected int sumWells;

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
				if (currentHeuristics < bestHeuristics)
				{
					bestMove = i;
					bestHeuristics = currentHeuristics;
				}
			}
//			undoMove(legalMoves[i][0], legalMoves[i][1]);
		}
		return bestMove;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		double [] w = {0.0 ,0.0 ,31.00038279871916, 0.0, 100.0, 0.19552791317599985, 0.0 ,0.0 ,30.268532359992115};
		p.setWeights(w);
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
	// Copy of makeMove in State
	public boolean makeMove(int orient, int slot) {

		rowsCleared = 0;
		//System.out.println(orient + " " + slot + " " + nextPiece);
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
			for(int c = 0; c < State.COLS; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
			}
			//if the row was full - remove it and slide above stuff down
			if(full) {
				rowsCleared++;
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
		
		return true;
	}
	
	/*
	public void undoMove(int orient, int slot){
		// Undo the move
		int i = 0;
		int h = 0;
		try
		{
		//for each column in the piece - fill in the appropriate blocks
		for( i = 0; i < pWidth[nextPiece][orient]; i++) {		
			//from bottom to top of brick
			for( h = height+pBottom[nextPiece][orient][i]; h < State.ROWS; h++) {
				if (field[h][i+slot]== turn)
				{
					field[h][i+slot] = 0;
				}
			}
		}
		}
		catch (Exception e)
		{
			System.out.println(i + " "+ h + " "+ slot);
		}
	}*/
	
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
		int val = 0;
		val += weights[0]*totalHeight  + weights[1]*diffHeight;
		val += weights[2]*maxHeight + weights[3]*rowsCleared;
		val +=weights[4]*holeCount + weights[5]*weightedSum;
		val += weights[6]*connectedHole + weights[7]*maxWellDepth;
		val += weights[8]*sumWells;
		return val;
	}
	
	// Feature 1 - Returns sum of heights of columns
	// Feature 2 - Returns sum of difference of height of adjacent columns
	// Feature 3 - Returns max column Height
	public void checkHeights()
	{
		totalHeight = 0;
		diffHeight = 0;
		maxHeight = 0;
		for (int i=0; i< top.length-1; i++)
		{
			totalHeight += top[i];
			diffHeight += Math.abs(top[i] - top[i+1]);
			maxHeight = Math.max(maxHeight, top[i]);
		}
		totalHeight+= top[top.length-1];
		maxHeight = Math.max(maxHeight, top[top.length-1]);
	}
	
	
	// Feature 4 - Returns number of holes in field
	// Feature 5 - Sum of filled cells, weighted by the row
	// Feature 6 - Number of holes, vertically connected holes are counted as 1
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
	
	// Feature 7 - depth of deepest well in field
	// Feature 8 - Sum of depths of wells
	public void checkWells()
	{
		maxWellDepth = 0;
		sumWells = 0;
		if (top[0] < top[1])
		{
			maxWellDepth = top[1] - top[0];
			sumWells = top[1] - top[0];
		}
		for (int i=1; i < top.length-1; i++)
		{
			if (top[i] < top[i-1] && top[i] < top[i+1])
			{
				int currDepth = Math.min(top[i-1], top[i+1]) - top[i];
				maxWellDepth = Math.max(currDepth, maxWellDepth);
				sumWells += currDepth;
			}
		}
		if (top[top.length-1] < top[top.length-2])
		{
			maxWellDepth = Math.max(maxWellDepth, top[top.length-2]- top[top.length-1]);
			sumWells += top[top.length-2]- top[top.length-1];
		}
	}
	
	
}
