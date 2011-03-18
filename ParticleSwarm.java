import java.io.*;
import java.util.*;

public class ParticleSwarm {
	public static final int MAXBOUND = 100; // Max range of Weights to test
	public static final int MINBOUND = 0;
	public static final int RANGE = MAXBOUND - MINBOUND;
	public static final int VELOCITYINIT = RANGE;
	public static final int FEATURECOUNT = 13; // Number of features to assign weights to
	public static final int SWARMSIZE = 63;//203; // Number of swarm particles
	public static final int ITERATION = 50;
	public static final int REPETITION = 1;
	public static final String FILENAME = "particle.txt";

	private static Random rand = new Random(); // Random number generator
	private static Particle[] particles;
	private static int gBest= 0; // Best global score
	private static double [] gBestWeight=new double[FEATURECOUNT]; // Weights to get gBest score

	public static void main(String[] args) {
		initSwarm();
		for (int iter = 0; iter <ITERATION; iter++)
		{
			for (int i = 0; i < particles.length; i++)
			{
				int score = Integer.MAX_VALUE;
				for (int j= 0; j < REPETITION ; j++)
				{
					// Find min in several reps for pessimistic evaluation
					score = Math.min(evaluate(particles[i]), score);
				}
				particles[i].updateScore(score);
				if (score > gBest)
				{
					gBest =score;
					System.arraycopy(particles[i].getWeights(), 0, gBestWeight, 0, particles[i].getWeights().length);
				}
				particles[i].updateVelocity(gBestWeight, rand, VELOCITYINIT);
				particles[i].updatePosition(MAXBOUND, MINBOUND);
			}
			System.out.print("Best Score in iter" +iter + " " + gBest+ " ");
			for (int k = 0; k< gBestWeight.length; k++)
				System.out.print(gBestWeight[k] + " ");
			System.out.println();
		}
		System.out.print("Best Score " + gBest + " ");
		for (int i = 0; i< gBestWeight.length; i++)
			System.out.print(gBestWeight[i] + " ");
		System.out.println();
		/*	for (int i = 0; i < particles.length; i++)
		{
			System.out.print("Weights: ");
			for (int j = 0; j< particles[i].getWeights().length; j++)
			{
				System.out.print(particles[i].getWeights()[j] + " ");
			}
			System.out.println();
			for (int j = 0; j< particles[i].velocity.length; j++)
			{
				System.out.print(particles[i].velocity[j] + " ");
			}
			System.out.println();
		}*/

		BufferedWriter bufferedWriter = null;

		try {
			//Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter(FILENAME));

			//Start writing to the output stream
			bufferedWriter.append(gBest + " ");
			for (int i = 0; i< gBestWeight.length; i++)
				bufferedWriter.append(gBestWeight[i] + " ");
			bufferedWriter.append("\n");
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

	// Plays a single game and using a weights of the particle and returns score
	private static int evaluate(Particle particle) {
		State s = new State();
		//	new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();	
		p.setWeights(particle.getWeights());
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			//	s.draw();
			//	s.drawNext(0,0);
			//		try {
			//	Thread.sleep(300);
			//	} catch (InterruptedException e) {
			//		e.printStackTrace();	
			//	}
		}
		//System.out.println("You have completed "+s.getRowsCleared()+" rows.");
		return s.getRowsCleared();
	}

	private static void initSwarm() {
		particles = new Particle[SWARMSIZE];
		for (int i = 0; i < SWARMSIZE; i++)
		{
			double [] w = generateRandomW();
			double [] v = generateRandomV();
			particles[i] = new Particle (w,v);
		}
	}

	private static double[] generateRandomV() {
		double [] v = new double [FEATURECOUNT];
		for (int i = 0; i < v.length; i++)
		{
			v[i] = 	rand.nextDouble()*VELOCITYINIT;
			if (rand.nextDouble() < 0.5)
				v[i] *= -1; // Equal change to move in either direction
		}
		return v;
	}

	// Generates a int array with random values
	private static double[] generateRandomW() {
		double [] w = new double [FEATURECOUNT];
		for (int i = 0; i < w.length; i++)
		{
			w[i] = 	rand.nextDouble()*RANGE+ MINBOUND;
		}
		return w;
	}
}
