import java.util.Random;

// Used by ParticleSwarm, the Particle class describes the status of each particle in the swarm
public class Particle {
	
	// Using some recommended values from http://www.hvass-labs.org/people/magnus/publications/pedersen10good-pso.pdf
	// Affects how much the velocity is affected by each parameter
	public static final double V_PARAM1 = 0.6571; //-0.3488;//0.5069;
	public static final double V_PARAM2 = 1.6319; //-0.2746;//2.5524;
	public static final double V_PARAM3 = 0.6239; //4.8976;//1.0056;
	
	double[] weight; // Corresponds to the particle location in the swarm space
	public double[] velocity; // current velocity of particle
	int pBest; // personal best score so far
	double[] pBestWeight; // Weight values for best score
	
	// Constructor
	public Particle(double[] w, double[] v) {
		weight = w;
		velocity = v;
		pBestWeight = new double[weight.length];
	}

	public double[] getWeights() {
		return weight;
	}

	// Compare current score against historical best
	public void updateScore(int score) {
		if (score > pBest) // Update to best score so far
		{
			pBest = score;
			System.arraycopy(weight, 0, pBestWeight, 0, weight.length);
		}
	}

	// Updates the particle's velocity at every iteration
	public void updateVelocity(double[] gBestWeight, Random rand, int bound) {
		for (int i = 0; i < velocity.length; i++)
		{
			velocity[i] = V_PARAM1*velocity[i] + V_PARAM2*(pBestWeight[i]-weight[i])*rand.nextDouble() + V_PARAM3*(gBestWeight[i]-weight[i])*rand.nextDouble();
			if (velocity[i] > bound) // Bound velocity so it does not speed up infinitely
				velocity[i] = bound;
			else if (velocity[i] < -bound)
				velocity[i] = -bound;
		}
	}
	
	 // Updates Weights based on velocity
	public void updatePosition(int maxBound, int minBound)
	{
		for (int i=0; i< velocity.length; i++)
		{
			weight[i] += velocity[i];
			if (weight[i] > maxBound) // Bounds range of weights so it stays within the swarm space
				weight[i] = maxBound;
			else if (weight[i] < minBound)
				weight[i] = minBound;
		}
	}

}
