import java.util.*;


public class Chromosome 
{
	public static final int GENE_LENGTH = 8;
	public static final int CHROMOSOME_LENGTH = 4;
	
	public static final double MUTATION = 0.001; //Chance for a bit to mutate
	
	protected int [][] chromosome = new int[CHROMOSOME_LENGTH][GENE_LENGTH];
	
	public Chromosome()
	{	
		//Create Chromosome
		for (int i = 0; i < CHROMOSOME_LENGTH; i++)
		{
			seedGene(i);
		}
		System.out.println();
	}
	
	public void seedGene(int geneNum)
	{
		String completeGene = "";
		Random random = new Random();
		//int gene = random.nextInt(101 - 1) + 1; //Upper limit is exclusive, lower limit is inclusive
		
		for (int j = 0; j < GENE_LENGTH; j++)
		{
			boolean geneCell = random.nextBoolean();
			
			if (geneCell)
			{
				chromosome[geneNum][j] = 1;
			}
			else
			{
				chromosome[geneNum][j] = 0;
			}
			
			completeGene += chromosome[geneNum][j];
		}
		
		//System.out.print(completeGene + " ");
	}
	
	public void mutate()
	{
		//Check if any bit mutates
	}
	
	public int[] getWeights()
	{
		int [] weights = new int[CHROMOSOME_LENGTH];
		
		String completeGene = "";
		
		for (int i = 0; i < CHROMOSOME_LENGTH; i++)
		{
			for (int j = 0; j < GENE_LENGTH; j++)
			{
				completeGene += chromosome[i][j];
			}
			weights[i] = Integer.parseInt(completeGene, 2);
			//System.out.println(weights[i]);
			
			completeGene = "";
		}
		
		return weights;
	}
}
