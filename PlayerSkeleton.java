public class PlayerSkeleton {
	
	public static final int COLS = 10;
	public static final int ROWS = 21;
	public static final int N_PIECES = 7;
	
	protected int [][] field;
	protected int [] top = new int[COLS];
	protected double[] weights;
	protected int rowsCleared; // Rows cleared by a Single Move, not total rows cleared
	protected int nextPiece;
	protected int turn;
	protected int height;
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
			field = s.getField();
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
			undoMove(legalMoves[i][0], legalMoves[i][1]);
		}
		return bestMove;
	}
	
	public static void main(String[] args) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();
		double [] w = {7.439441181924077, 17.2275084057153, 0.9602701415538313 ,100.0 };
		p.setWeights(w);
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
	}
	
	// Copy of makeMove in State, but without row clearing 
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
		if(height+pHeight[nextPiece][orient] >= ROWS) {
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
			for(int c = 0; c < COLS; c++) {
				if(field[r][c] == 0) {
					full = false;
					break;
				}
			}
			//if the row was full - remove it and slide above stuff down
			if(full) {
				rowsCleared++;
	// Commenting out rows clearing code to simplify undoing
	/*			//for each column
				for(int c = 0; c < COLS; c++) {

					//slide down all bricks
					for(int i = r; i < top[c]; i++) {
						field[i][c] = field[i+1][c];
					}
					//lower the top
					top[c]--;
					while(top[c]>=1 && field[top[c]-1][c]==0)	top[c]--;
				}*/
			}
		}
		
		return true;
	}
	
	
	public void undoMove(int orient, int slot){
		// Undo the move
		int i = 0;
		int h = 0;
		try
		{
		//for each column in the piece - fill in the appropriate blocks
		for( i = 0; i < pWidth[nextPiece][orient]; i++) {		
			//from bottom to top of brick
			for( h = height+pBottom[nextPiece][orient][i]; h < ROWS; h++) {
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
	}
	// Sets weights for the heuristics
	public void setWeights(double[] newWeights)
	{
		this.weights = newWeights;
	}
	// Calculates and returns heuristics value
	public int getHeuristicsVal()
	{
		int val = 0;
		val += weights[0]*totalHeight()  + weights[1]*diffHeight();
		val += weights[2]*maxHeight() + weights[3]*holesCount();
		return val;
	}
	
	// Feature 1 - Returns sum of heights of columns
	public int totalHeight()
	{
		int tHeight = 0;
		for (int i=0; i< top.length; i++)
		{
			tHeight += top[i];
		}
		return tHeight - rowsCleared*top.length;
	}
	
	// Feature 2 - Returns sum of difference of height of adjacent columns
	public int diffHeight()
	{
		int dHeight = 0;
		for (int i=0; i < top.length-1; i++)
		{
			dHeight += Math.abs(top[i] - top[i+1]);
		}
		return dHeight;
	}
	
	// Feature 3 - Returns max column Height
	public  int maxHeight()
	{
		int mHeight = 0;
		for (int i = 0; i< top.length; i++)
		{
			mHeight = Math.max(mHeight, top[i]);
		}
		return mHeight - rowsCleared;
	}
	
	// Feature 4 - Returns number of holes in field
	public int holesCount()
	{
		int count = 0;
		for (int i = 0; i< COLS; i++)
		{
			for (int j = top[i]-1; j >= 0; j--)
			{
				if (field[j][i] == 0)
					//System.out.print(i);
					count++;
			}
		}
		return count;
	}

	
}
