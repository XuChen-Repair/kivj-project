import java.util.Random;


public class GeneticAlgorithm 
{
	public static final int POPULATION = 100; //Population size to begin with
	public static final int MAX_GENERATIONS = 40;
	public static final double CROSSOVER = 0.7; //Chance for crossover to occur
	
	public static Chromosome [] geneticLibrary = new Chromosome[POPULATION];
	public static Chromosome [] nextGeneration = new Chromosome[POPULATION]; //Create storage for new generation
	
	public static double [] rowScore = new double[POPULATION];
	public static double rawTotalScore = 0;
	
	public static State s;
	public static PlayerSkeleton p;
	public static Random random = new Random();
	
	public static void main(String []args)
	{
		int maxScore = 0;
		
		
		
		//INITIALIZING STAGE
		for (int i = 0; i < POPULATION; i++)
		{
			s = new State();
			//new TFrame(s);
			p = new PlayerSkeleton();
			
			geneticLibrary[i] = new Chromosome(); //Creates a new Chromosome in the initial population
			oneRound(geneticLibrary[i]); //Play a single round for fitness score
			rowScore[i] = s.getRowsCleared();
			
			if (rowScore[i] > maxScore)
			{
				maxScore = (int) rowScore[i];
			}
			
			rawTotalScore += rowScore[i]; //Keeps track of total score for Roulette Wheel later
			
			//System.out.println("You have completed "+rowScore[i]+" rows." + "  CHECK: " + rawTotalScore);
		}
		
		int count = 0;
		
		while (count < MAX_GENERATIONS)
		{
			//SELECTION STAGE
			processScores();
			genesis();
			
			//Reset
			rowScore = new double[POPULATION];
			rawTotalScore = 0;
			for (int i = 0; i < POPULATION; i++)
		    {
				//System.out.println("Next Generation: " + nextGeneration[i].getChromosome());
				geneticLibrary[i] = nextGeneration[i];
		    }
			
			System.out.println("Generation: " + count);
			System.out.println("MAXSCORE: " + maxScore);
			System.out.println();
			
			count++;
			
			for (int i = 0; i < POPULATION; i++)
			{
				s = new State();
				//new TFrame(s);
				p = new PlayerSkeleton();
				
				oneRound(geneticLibrary[i]); //Play a single round for fitness score
				rowScore[i] = s.getRowsCleared();
				
				if (rowScore[i] > maxScore)
				{
					maxScore = (int) rowScore[i];
				}
				
				rawTotalScore += rowScore[i]; //Keeps track of total score for Roulette Wheel later
				
				//System.out.println("You have completed "+rowScore[i]+" rows." + "  CHECK: " + rawTotalScore);
			}
			
			
		}
		System.out.println("GAME ENDED - MAXSCORE: " + maxScore);
	}
	
	// Plays a single game and using a weights of the gene and returns score
	public static void oneRound(Chromosome current)
	{
		p.setWeights(current.getWeights());
		while(!s.hasLost()) 
		{
			s.makeMove(p.pickMove(s,s.legalMoves()));
			//s.draw();
			//s.drawNext(0,0);
		}
	}
	
	public static void processScores()
	{
		for (int i = 0; i < POPULATION; i++)
		{
			//System.out.println("Original score: " + rowScore[i] + " Raw Total: " + rawTotalScore);
			rowScore[i] = (rowScore[i] / rawTotalScore);
			//System.out.println("Converted percentage: " + rowScore[i]);
		}
	}
	
	public static void genesis()
	{
		int nextGenerationPop = 0;
		
		Chromosome temp1, temp2;
		
		while(nextGenerationPop != POPULATION)
		{
			temp1 = geneticLibrary[rouletteWheelSelection()]; //Store first selected child
			temp2 = geneticLibrary[rouletteWheelSelection()]; //Store second selected child
			
			//Crossover
			crossover(temp1, temp2);
			//System.out.println("change temp1: " + temp1.getChromosome());
			//System.out.println("change temp2: " + temp2.getChromosome());
			//System.out.println();
			
			//nextGeneration[nextGenerationPop++].setActualWeight(Integer.parseInt(temp1.getChromosome()));
			//nextGeneration[nextGenerationPop++].setActualWeight(Integer.parseInt(temp2.getChromosome()));
			
			nextGeneration[nextGenerationPop++] = temp1;
			nextGeneration[nextGenerationPop++] = temp2;
			
			//nextGeneration[nextGenerationPop++] = temp1.mutate();
			//nextGeneration[nextGenerationPop++] = temp2.mutate();
		}
		
	}
	
	public static int rouletteWheelSelection()
	{
		double spin = random.nextDouble();
		double totalScore = 0;
		
		Chromosome choice1, choice2;
		
		for (int i = 0; i < POPULATION; i++)
		{
			totalScore += rowScore[i];
			
			if (totalScore > spin)
			{
				//System.out.println("i chosen: " + i);
				return i; //This is the Chromosome selected
			}
		}
		
		return -1; //Failure
	}
	
	public static void crossover(Chromosome temp1, Chromosome temp2)
	{
		double chance = random.nextDouble();
		
		if (chance > CROSSOVER)
		{
			//Create random crossover point
			int position = random.nextInt(41); //40 or 41?
			
			/**
			System.out.println("temp1: " + temp1.getChromosome());
			System.out.println("temp2: " + temp2.getChromosome());
			System.out.println();
			**/
			
			String preventChange = temp1.getChromosome().substring(position, 40);
			
			temp1.setChromosome(temp1.getChromosome().substring(0, position) + temp2.getChromosome().substring(position, 40));
			temp2.setChromosome(temp2.getChromosome().substring(0, position) + preventChange);
			
			/**
			System.out.println("temp1: " + temp1.getChromosome());
			System.out.println("temp2: " + temp2.getChromosome());
			System.out.println();
			**/
		}
	}

}
