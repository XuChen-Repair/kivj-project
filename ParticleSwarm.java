import java.util.*;
public class ParticleSwarm {
	public static final int RANGE = 100; // Max range of Weights to test
	public static final int VELOCITYINIT = 5;
	public static final int FEATURECOUNT = 4; // Number of features to assign weights to
	public static final int SWARMSIZE = 10; // Number of swarm particles
	public static final int ITERATION = 10;
	
	private static Random rand = new Random(); // Random number generator
	private static Particle[] particles;
	private static int gBest= 0; // Best global score
	private static float [] gBestWeight=new float[FEATURECOUNT]; // Weights to get gBest score
	
	public static void main(String[] args) {
		initSwarm();
		int score = 0;
		for (int iter = 0; iter <ITERATION; iter++)
		{
			for (int i = 0; i < particles.length; i++)
			{
				score = evaluate(particles[i]);
				particles[i].updateScore(score);
				if (score > gBest)
				{
					gBest =score;
					System.arraycopy(particles[i].getWeights(), 0, gBestWeight, 0, particles[i].getWeights().length);
				}
				particles[i].updateVelocity(gBestWeight, rand);
				particles[i].updatePosition();
			}
		}
		System.out.println("Best Score " + gBest);
		for (int i = 0; i< gBestWeight.length; i++)
			System.out.print(gBestWeight[i] + " ");
	}

	// Plays a single game and using a weights of the particle and returns score
	private static int evaluate(Particle particle) {
		State s = new State();
		new TFrame(s);
		PlayerSkeleton p = new PlayerSkeleton();	
		p.setWeights(particle.getWeights());
		while(!s.hasLost()) {
			s.makeMove(p.pickMove(s,s.legalMoves()));
			s.draw();
			s.drawNext(0,0);
	//		try {
			//	Thread.sleep(300);
		//	} catch (InterruptedException e) {
		//		e.printStackTrace();	
		//	}
		}
		System.out.println("You have completed "+s.getRowsCleared()+" rows.");
		return s.getRowsCleared();
	}

	private static void initSwarm() {
		particles = new Particle[SWARMSIZE];
		for (int i = 0; i < SWARMSIZE; i++)
		{
			float [] w = generateRandomW();
			float [] v = generateRandomV();
			particles[i] = new Particle (w,v);
		}
	}

	private static float[] generateRandomV() {
		float [] v = new float [FEATURECOUNT];
		for (int i = 0; i < v.length; i++)
		{
			v[i] = 	(rand.nextFloat()-0.5f)*VELOCITYINIT;
		}
		return v;
	}

	// Generates a int array with random values
	private static float[] generateRandomW() {
		float [] w = new float [FEATURECOUNT];
		for (int i = 0; i < w.length; i++)
		{
			w[i] = 	rand.nextFloat()*RANGE;
		}
		return w;
	}
}
