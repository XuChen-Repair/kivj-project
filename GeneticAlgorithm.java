
public class GeneticAlgorithm 
{
	public static final int POPULATION = 10; //Population size to begin with
	public static final int MAX_GENERATIONS = 400;
	public static final double CROSSOVER = 0.7; //Chance for crossover to occur
	
	public static Chromosome [] geneticLibrary = new Chromosome[POPULATION];
	public static int [] rowScore = new int[POPULATION];
	public static int rawTotalScore = 0;
	
	public static State s;
	public static PlayerSkeleton p;
	
	// Plays a single game and using a weights of the particle and returns score	
	public static void main(String []args)
	{
		//Initialize population
		for (int i = 0; i < POPULATION; i++)
		{
			s = new State();
			new TFrame(s);
			p = new PlayerSkeleton();
			
			geneticLibrary[i] = new Chromosome();
			oneRound(geneticLibrary[i]);
			rowScore[i] = s.getRowsCleared();
			
			rawTotalScore += rowScore[i];
			
			System.out.println("You have completed "+rowScore[i]+" rows." + "  CHECK: " + rawTotalScore);
		}	
	}
	
	// Plays a single game and using a weights of the gene and returns score
	public static void oneRound(Chromosome current)
	{
		p.setWeights(current.getWeights());
		while(!s.hasLost()) 
		{
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
		}
	}

}
