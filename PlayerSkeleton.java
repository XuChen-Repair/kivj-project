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
	protected int hTransition;
	protected int vTransition;

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
	//	int sum = 0;
	//	int min = Integer.MAX_VALUE;
	//	for (int i = 0; i < 5 ; i++)
	//	{
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		// Best ave weights achieved so far 
		double [] w = { 39.87226246992793, 0.2822313301577022, 0.0, 0.0, 0.0, 90.41644192059289, 0.0, 100.0, 29.025892632632264, 57.261460721109174, 26.175997125047157, 100.0, 4.939800183261108};      
	//	double [] w = { 58.912809904494324, 0.0, 0.31403775562943975, 1.3580772272231452, 14.987648344779851, 97.40010722018096, 0.2238928871419224, 72.2956798023515, 43.969592318687155, 44.01371135773606, 25.64396612944828, 86.53687675629048, 88.44109531383216 };
		p.setWeights(w);
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	//	sum +=s.getRowsCleared();
	//	min = Math.min(min, s.getRowsCleared());
	//	}
	//	System.out.println(sum/5 + " " + min);
		
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
		val += weights[0]*height + weights[1]*totalHeight ;
		val += weights[2]*diffHeight + weights[3]*altitudeDiff;
		val += weights[4]*maxHeight + weights[5]*holeCount; 
		val += weights[6]*weightedSum + weights[7]*connectedHole;
		val += weights[8]*maxWellDepth + weights[9]*sumWells ;
		val += weights[10]*hTransition + weights[11]*vTransition;
		val += weights[12]*rowsCleared;
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
	
	// Feature 10 - Number of horizontal Transitions
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
	
	// Feature 11 - Number of vertical Transitions
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
