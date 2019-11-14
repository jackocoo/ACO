import java.util.Arrays;
import java.util.Random;
import java.util.Set;



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
	private int numAnts;

	public double alpha;
	public double beta;
	public double rho; //evaporation rate 
	public double elitistNum;
	public double epsilon; //decaying coefficient
	public double tau; //1 / length of greedy tour * num cities
	public double q; //prob for choosing to use greedy or prob

	public Random rand = new Random();

	public List<Ant> antList = new ArrayList<Ant>();

	public Environment(int numCities, int numAnts, double alpha, double beta, double rho, double elitistNum, 
						double epsilon, double tau, double q ) {

		this.numAnts = numAnts;
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

	public int getEnvironmentSize() {
		return this.numCities;
	}

	public double getQ() {
		return this.q;
	}

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

	public void antColonySystemGlobalUpdate(Ant bestAnt) {
		int[] tour = bestAnt.getTour();
		for (int i = 0; i < tour.length - 1; i++) {
			if (bestAnt.containsCities(i, i+1)) {
				this.pheromones[i][i+1] = (1.0 - this.rho) * this.pheromones[i][i+1] + this.rho * (1.0 /this.numCities);
				this.pheromones[i+1][i] = (1.0 - this.rho) * this.pheromones[i+1][i] + this.rho * (1.0 /this.numCities);
			}
		}

	}

	public void antColonySystemLocalUpdate(int city1, int city2) {
		
		this.pheromones[city1][city2] = (1.0 - this.epsilon) * this.pheromones[city1][city2] + this.epsilon * this.tau;
		this.pheromones[city2][city1] = (1.0 - this.epsilon) * this.pheromones[city2][city1] + this.epsilon * this.tau;

	}

	public double calculateTotal(int city1, int city2) {
		double total = 0;
		for (Ant ant : antList) {
			if (ant.containsCities(city1, city2)) {
				total += (1.0/this.numCities);
			}
		}
		return total;
	}

	public void elitistPheromoneUpdate(int city1, int city2, int numAnts, Ant ant) {
		
		if (bestSoFar.containsCities(city1, city2)) {
			this.pheromones[city1][city2] = (1.0 - this.rho) * this.pheromones[city1][city2] + calculateTotal(city1, city2) + this.elitistNum * (1.0 /this.numCities);
			this.pheromones[city2][city1] = (1.0 - this.rho) * this.pheromones[city2][city1] + calculateTotal(city1, city2) + this.elitistNum * (1.0 /this.numCities);
		} else {
			this.pheromones[city1][city2] = (1.0 - this.rho) * this.pheromones[city1][city2] + calculateTotal(city1, city2);
			this.pheromones[city2][city1] = (1.0 - this.rho) * this.pheromones[city2][city1] + calculateTotal(city1, city2);

		}
	}

	public double getDistance(int city1, int city2) {
		return this.distances[city1][city2];

	}

	public double getPheromones(int city1, int city2) {
		return this.pheromones[city1][city2];

	}

	public int getNextCityGreedy(int cityId, Set<Integer> visitedSet) {

		double[] neighboringCities = this.distances[cityId];
		double[] neighoringPheromones = this.pheromones[cityId];

		double bestProductSoFar = 0.0;
		int bestCitySoFar = 0;

		for(int i = 0; i < neighboringCities.length; i++) {

			double product = 0.0;

			if (!visitedSet.contains(i)) {
				double cityDistance = 1.0 / neighboringCities[i];
				double pheromoneContent = neighoringPheromones[i];
				product = pheromoneContent * Math.pow(cityDistance, this.beta);

				if(product > bestProductSoFar) {
					bestProductSoFar = product;
					bestCitySoFar = i;
				}
			}
		}
		return bestCitySoFar;
	}


	public int getNextCityProb(int cityId, Set<Integer> visitedSet) {

		double[] probabilities = new double[this.distances.length];
		double[] neighboringCities = this.distances[cityId];
		double[] neighoringPheromones = this.pheromones[cityId];

		double runningProbSum = 0.0;

		for(int i = 0; i < neighboringCities.length; i++) {

			double product = 0.0;

			if (!visitedSet.contains(i)) {
				double cityDistance = 1.0 / neighboringCities[i];
				double pheromoneContent = neighoringPheromones[i];
				product = Math.pow(pheromoneContent, this.alpha) * Math.pow(cityDistance, this.beta);
			}
			probabilities[i] = product;
			runningProbSum += product;
		}

		double probCounter = 0.0;
		for (int i = 0; i < neighboringCities.length; i++) {
			double prob = probabilities[i] / runningProbSum;
			probCounter += prob;
			probabilities[i] = probCounter;
		}
		return pickCityFromProbabilities(probabilities);
	}


	public static int pickCityFromProbabilities(double[] probabilities) {

		Random randy = new Random();
		double randDouble = randy.nextDouble();

		int counter = 0;

		while(probabilities[counter] < randDouble) {
			counter++;
		}
		return counter;
	}

	public void setAntList() {
		int i = 0;
		Ant ant;
		while(i < this.numAnts) {
			ant = new Ant(i, this);
			antList.add(ant);
			i++;
		}
	}

	public List<Ant> getAntList() {
		return this.antList;
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

	public void setInitialPheromones(double pheromoneContent) {
		this.tau = pheromoneContent;
		for (int i = 0; i < this.numCities; i++) {
			for (int j = 0; j < this.numCities; j++) {
				this.pheromones[i][j] = pheromoneContent;
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

	public void printPheromones() {
		for(int i = 0; i < this.pheromones.length; i++) {
			String line = "";

			for(int j = 0; j < this.pheromones.length; j++) {
				line += " " + round(this.pheromones[i][j], 1);
			}

			System.out.println(line);
		}
	}

}