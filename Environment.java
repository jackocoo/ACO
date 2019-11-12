


public class Environment {

	private double[][] distances;
	private double[][] pheromones;

	double alpha;
	double beta;
	double rho;
	double elitistNum;
	double epsilon;
	double tau;
	double q;






	public Environment(int numCities, double alpha, double beta, double rho, double elitistNum, 
						double epsilon, double tau, double q ) {

		this.distances = new double[numCities][numCities]
		this.pheromones = new double[numCities][numCities]
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
		this.elitistNum = elitistNum;
		this.epsilon = epsilon;
		this.tau = tau;
		this.q = q;

	}


	public void calculateDistances(List<City> cityList) {

	}


	public void updatePheromones(int city1, int city2) {

	}

	public double getDistance(int city1, int city2) {

	}

	public double getPheromones(int city1, int city2) {

	}


}