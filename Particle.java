import java.util.Random;


public class Particle {
	
	public static final int VELOCITYSTEP = 5;
	
	float[] weight;
	float[] velocity;
	int pBest; // personal best score so far
	float[] pBestWeight; // Weight values for best score
	public Particle(float[] w, float[] v) {
		weight = w;
		velocity = v;
		pBestWeight = new float[weight.length];
	}

	public float[] getWeights() {
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

	public void updateVelocity(float[] gBestWeight, Random rand) {
		float r = rand.nextFloat()*VELOCITYSTEP;
		for (int i = 0; i < velocity.length; i++)
		{
			velocity[i] += r*(gBestWeight[i]-velocity[i])*rand.nextFloat() + r*(pBestWeight[i]-velocity[i])*rand.nextFloat();
		}
	}
	
	public void updatePosition() // Updates Weights based on velocity
	{
		for (int i=0; i< velocity.length; i++)
		{
			weight[i] += velocity[i];
		}
	}

}
