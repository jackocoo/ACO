import java.util.Arrays;



import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal; 
import java.math.RoundingMode;

public class Environment {

	private double[][] distances;
	private double[][] pheromones;
	private Ant bestSoFar;
	private int numCities;

	public double alpha;
	public double beta;
	public double rho;
	public double elitistNum;
	public double epsilon;
	public double tau;
	public double q;

	public List<Ant> antList = new ArrayList<Ant>();

	public Environment(int numCities, double alpha, double beta, double rho, double elitistNum, 
						double epsilon, double tau, double q ) {
		this.numCities = numCities;
		this.distances = new double[numCities][numCities];
		this.pheromones = new double[numCities][numCities];
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
		this.elitistNum = elitistNum;
		this.epsilon = epsilon;
		this.tau = tau;
		this.q = q;

	}

/*


	[1, 2, 3, 4]


	[0, 2, 3, 4]
	[2, 0, 0, 0]
	[3, 0, 0, 0]
	[4, 0, 0, 0]


*/

	public void calculateDistances(List<City> cityList) {

		for(int i = 0; i < cityList.size(); i++) {
			City currentCity = cityList.get(i);
			double currentX = currentCity.getXCoord();
			double currentY = currentCity.getYCoord();

			for (int j = i; j < cityList.size(); j++) {
				City comparingCity = cityList.get(j);
				double comparingX = comparingCity.getXCoord();
				double comparingY = comparingCity.getYCoord();

				double distance = 0.0;
				if(j != i) {
					double xDiffSquared = Math.pow(currentX - comparingX, 2);
					double yDiffSquared = Math.pow(currentY - comparingY, 2);
					double sum = xDiffSquared + yDiffSquared;
					distance = Math.sqrt(sum);
				}

				this.distances[i][j] = distance;
				this.distances[j][i] = distance;
			}
		}
	}

	private void antColonySystemGlobalUpdate(int city1, int city2) {

		if (bestSoFar.containsCities(city1, city2)) {
			this.pheromones[city1][city2] = (1.0 - this.rho) * this.pheromones[city1][city2] + this.rho * (1.0 / bestSoFar.getLength());
		} else {
			this.pheromones[city1][city2] = (1.0 - this.rho) * this.pheromones[city1][city2];
		}

	}

	private void antColonySystemLocalUpdate(int city1, int city2) {

		if (bestSoFar.containsCities(city1, city2)) {
			this.pheromones[city1][city2] = (1.0 - this.epsilon) * this.pheromones[city1][city2] + this.epsilon * (1.0 /bestSoFar.getLength());
		} else {
			this.pheromones[city1][city2] = (1.0 - this.epsilon) * this.pheromones[city1][city2];

		}

	}

	private double calculateTotal(int city1, int city2) {
		double total = 0;
		for (Ant ant : antList) {
			if (ant.containsCities(city1, city2)) {
				total += (1.0/ant.getLength());
			}
		}
		return total;
	}

	private void elitistPheromoneUpdate(int city1, int city2, int numAnts, Ant ant) {
		
		if (bestSoFar.containsCities(city1, city2)) {
			this.pheromones[city1][city2] = (1.0 - this.rho) * this.pheromones[city1][city2] + calculateTotal(city1, city2) + this.elitistNum * (1.0 /bestSoFar.getLength());
		} else {
			this.pheromones[city1][city2] = (1.0 - this.rho) * this.pheromones[city1][city2] + calculateTotal(city1, city2);

		}
	}

	public double getDistance(int city1, int city2) {
		return 0.0;

	}

	public double getPheromones(int city1, int city2) {
		return 0.0;

	}

	public static double round(double value, int places) {
    	if (places < 0) throw new IllegalArgumentException();

    	BigDecimal bd = BigDecimal.valueOf(value);
    	bd = bd.setScale(places, RoundingMode.HALF_UP);
    	return bd.doubleValue();
	}

	public double[][] getDistanceMatrix() {
		return this.distances;
	}

	public int getNumCities() {
		return this.numCities;
	}

	public void setInitialPheromones(double val) {
		for (int i = 0; i < this.numCities; i++) {
			for (int j = 0; j < this.numCities; j++) {
				this.pheromones[i][j] = val;
			}
		}
	}

	public void printDistances() {

		for(int i = 0; i < this.distances.length; i++) {
			String line = "";

			for(int j = 0; j < this.distances.length; j++) {
				line += " " + round(this.distances[i][j], 1);
			}

			System.out.println(line);
		}
	}

}