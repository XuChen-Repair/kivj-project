import java.util.Random;


public class Particle {
	
	// Using some recommended values from http://www.hvass-labs.org/people/magnus/publications/pedersen10good-pso.pdf
	public static final double V_PARAM1 = -0.3488;//0.5069;
	public static final double V_PARAM2 = -0.2746;//2.5524;
	public static final double V_PARAM3 = 4.8976;//1.0056;
	
	double[] weight;
	public double[] velocity;
	int pBest; // personal best score so far
	double[] pBestWeight; // Weight values for best score
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

	public void updateVelocity(double[] gBestWeight, Random rand, int bound) {
		for (int i = 0; i < velocity.length; i++)
		{
			//System.out.println( r + " " + r*(gBestWeight[i]-weight[i]) + " " + (r*pBestWeight[i]-velocity[i]));
			//System.out.println("Velocites:" + velocity[i] + " " + (velocity[i] + r*(gBestWeight[i]-weight[i])) + " " + (velocity[i] + r*(gBestWeight[i]-weight[i]) + r*(pBestWeight[i]-weight[i])));
			velocity[i] = V_PARAM1*velocity[i] + V_PARAM2*(pBestWeight[i]-weight[i])*rand.nextDouble() + V_PARAM3*(gBestWeight[i]-weight[i])*rand.nextDouble();
			if (velocity[i] > bound) // Bound velocity
				velocity[i] = bound;
			else if (velocity[i] < -bound)
				velocity[i] = -bound;
	//		System.out.println("New:" + velocity[i]);
		}
	}
	
	public void updatePosition(int maxBound, int minBound) // Updates Weights based on velocity
	{
		for (int i=0; i< velocity.length; i++)
		{
			weight[i] += velocity[i];
			if (weight[i] > maxBound) // Bounds range of weights
				weight[i] = maxBound;
			else if (weight[i] < minBound)
				weight[i] = minBound;
		}
	}

}
