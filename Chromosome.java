import java.util.*;


public class Chromosome 
{
	public static final int GENE_LENGTH = 8;
	public static final int CHROMOSOME_LENGTH = 5;
	
	public static final double MUTATION = 0.001; //Chance for a bit to mutate
	
	protected int [][] chromosome = new int[CHROMOSOME_LENGTH][GENE_LENGTH];
	
	private int actualWeight = 0;
	private String completeChromosome = "";
	
	Random random = new Random();
	
	public Chromosome()
	{	
		//Create Chromosome
		for (int i = 0; i < CHROMOSOME_LENGTH; i++)
		{
			seedGene(i);
		}
		//System.out.println();
		//System.out.println("Complete Chromosome: " + completeChromosome);
	}
	
	public void seedGene(int geneNum)
	{
		String completeGene = "";

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
		
		completeChromosome += completeGene;
		
		//System.out.print(completeGene + " ");
	}
	
	public Chromosome mutate()
	{
		//Check if any bit mutates
		for(int i = 0; i < 32; i++)
		{
			double check  = random.nextDouble();
			
			if (check < MUTATION)
			{
				System.out.println("Before: " + completeChromosome);
				int position = chromosome[(i)/8][(i)%8];
				if (position == 0)
				{
					chromosome[(i)/8][(i)%8] = 1;
				}
				else if (position == 1)
				{
					chromosome[(i)/8][(i)%8] = 0;
				}
				
				String completeGene = "";
				
				for (int j = 0; j < CHROMOSOME_LENGTH; j++)
				{
					for (int k = 0; k < GENE_LENGTH; k++)
					{
						completeGene += chromosome[j][k];
					}
					//weights[i] = Integer.parseInt(completeGene, 2);
					//System.out.println(weights[i]);
					
					completeChromosome += completeGene;
					completeGene = "";
				}
			}
		}
		
		return this;
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
	
	public String getChromosome()
	{
		return completeChromosome;
	}
	
	public void setChromosome(String newSequence)
	{
		completeChromosome = newSequence;
	}
	
	public void setActualWeight(int newWeight)
	{
		System.out.println("newWeight: " + newWeight);
		//actualWeight = newWeight;
		completeChromosome = Integer.toString(newWeight);
		System.out.println("completeChromosome: " + completeChromosome);
	}
}
