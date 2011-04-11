import java.io.*;
import java.util.*;

// Performs Particle Swarm Optimisation for finding best weights for heuristic of Tetris player

public class ParticleSwarm {
	public static final int MAXBOUND = 100; // Max value of Weights to test
	public static final int MINBOUND = 0; // Min value of weight
	public static final int RANGE = MAXBOUND - MINBOUND; 
	public static final int VELOCITYINIT = RANGE; // Max initial velocity of particles
	public static final int FEATURECOUNT = 15; // Number of features to assign weights to, correspond to dimension of space to search
	public static final int SWARMSIZE = 63; // Number of swarm particles
	public static final int ITERATION = 100; // Number of iteration to loop 
	public static final String FILENAME = "particle.txt";
	public static final long testSeed = 1301478912376L; // Use a fixed seed for deterministic playing
	
	private static Random rand = new Random(); // Random number generator
	private static Particle[] particles;
	private static int gBest= 0; // Best global score
	private static double [] gBestWeight=new double[FEATURECOUNT]; // Weights of features to get gBest score

	public static void main(String[] args) {
		initSwarm(); // Initial particles randomly in the space
		BufferedWriter bufferedWriter = null;

		for (int iter = 0; iter <ITERATION; iter++)
		{
			for (int i = 0; i < particles.length; i++) // Check through every particle at each iteration
			{
				int score  = evaluate(particles[i]); // Evaluate the current weights of the particle 
				particles[i].updateScore(score);
				if (score > gBest) // Update the Swarm's best score and weights
				{
					gBest =score;
					System.arraycopy(particles[i].getWeights(), 0, gBestWeight, 0, particles[i].getWeights().length);
				}
				// Upate the particle position and velocity
				particles[i].updateVelocity(gBestWeight, rand, VELOCITYINIT);
				particles[i].updatePosition(MAXBOUND, MINBOUND);
			}
			
			// Print current iteration result
			System.out.print("Best Score in iter" +iter + " " + gBest+ " ");
			for (int k = 0; k< gBestWeight.length; k++)
				System.out.print(gBestWeight[k] + " ");
			System.out.println();
			
			// Write to file the curent status
			try {
				//Construct the BufferedWriter object
				bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));
				
				//Start writing to the output stream
				bufferedWriter.append("Iter " + iter + " Score: " + gBest + "\n");
				for (int i = 0; i< gBestWeight.length; i++)
					bufferedWriter.append(gBestWeight[i] + ", ");
				bufferedWriter.append("\n");
			} catch (Exception ex) {
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
		
		// Show End results at end required number of iterations
		System.out.print("Best Score " + gBest + " ");
		for (int i = 0; i< gBestWeight.length; i++)
			System.out.print(gBestWeight[i] + " ");
		System.out.println();

		try {
			//Construct the BufferedWriter object
			bufferedWriter = new BufferedWriter(new FileWriter(FILENAME, true));

			//Start writing to the output stream
			bufferedWriter.append("Best Results: " + gBest + " ");
			for (int i = 0; i< gBestWeight.length; i++)
				bufferedWriter.append(gBestWeight[i] + ", ");
			bufferedWriter.append("\n");
		} catch (Exception ex) {
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

	// Evaluate the position of a particle in the swarm space
	// by playing a single game and using the position as weights and returns score
	private static int evaluate(Particle particle) {
		State s = new State();
		s.setSeed(testSeed); // Requires modified state which takes a seed value
		PlayerSkeleton p = new PlayerSkeleton();	
		p.setWeights(particle.getWeights());
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
		}
		return s.getRowsCleared();
	}

	// Initialise swarm by randomising every particle 
	private static void initSwarm() {
		particles = new Particle[SWARMSIZE];
		for (int i = 0; i < SWARMSIZE; i++)
		{
			double [] w = generateRandomW();
			double [] v = generateRandomV();
			particles[i] = new Particle (w,v);
		}
	}

	// Generates random initial velocity for a particle
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

	// Generates an array with random values for initial location (weight) of particle
	private static double[] generateRandomW() {
		double [] w = new double [FEATURECOUNT];
		for (int i = 0; i < w.length; i++)
		{
			w[i] = 	rand.nextDouble()*RANGE+ MINBOUND;
		}
		return w;
	}
}
