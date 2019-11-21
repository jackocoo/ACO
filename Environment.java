import java.util.Arrays;
import java.util.Random;
import java.util.Set;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.math.RoundingMode;


/**
Environment represents the space that the ants function within, allowing ant actions
to affect pheromone levels in the environment, in turn impacting ants' decisions
Variable descriptions:
distances: stores the euclidean distances from one city to another using a 2-D array
pheromones: stores pheromone concentrations on each edge from one city to another using a 2-D array
iterationPheromones: intermediary pheromone matrix to allow for noting pheromone levels at time t and t+1
numCities: number of cities in the problem
numAnts: number of ants to use
alpha, beta, rho, elitistNum, epsilon, tau, q: parameter values to use for optimization
antList: used to store all ants in the environment
 */
public class Environment {

	private double[][] distances;
	private double[][] pheromones;
	private double[][] iterationPheromones;
	private int numCities;
	private int numAnts;

	public double alpha;
	public double beta;
	public double rho; // evaporation rate
	public double elitistNum;
	public double epsilon; // decaying coefficient
	public double tau; // 1 / length of greedy tour * num cities
	public double q; // prob for choosing to use greedy or prob

	public Random rand = new Random();

	public List<Ant> antList = new ArrayList<Ant>();

	/**
	Constructor for environment
	@param int numCities number of cities
	@param int numAnts number of ants
	@param double alpha parameter value for pheromone levels
	@param double beta parameter value for heuristic info
	@param double rho paramater value for pheromone evaporation 
	@param double elitistNum parameter value for elitist algorithm
	@param double epsilon paramter value for pheromone degradation
	@param double tau initial edge weight based on greedy tour
	@param double q probability of choosing best next node according to best path in ACS
	 */

	public Environment(int numCities, int numAnts, double alpha, double beta, double rho, double elitistNum,
			double epsilon, double tau, double q) {

		this.numAnts = numAnts;
		this.numCities = numCities;
		this.distances = new double[numCities][numCities];
		this.pheromones = new double[numCities][numCities];
		this.iterationPheromones = new double[1][1];
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
		this.elitistNum = elitistNum;
		this.epsilon = epsilon;
		this.tau = tau;
		this.q = q;

	}

	/**
	getter for the number of cities
	@return int numCities
	 */

	public int getEnvironmentSize() {
		return this.numCities;
	}

	/**
	getter for the parameter q
	@return double q
	 */
	public double getQ() {
		return this.q;
	}

	/**
	Used to calculate distances between cities and stores into distances matrix
	 */

	public void calculateDistances(List<City> cityList) {

		for (int i = 0; i < cityList.size(); i++) {
			City currentCity = cityList.get(i);
			double currentX = currentCity.getXCoord();
			double currentY = currentCity.getYCoord();

			for (int j = i; j < cityList.size(); j++) {
				City comparingCity = cityList.get(j);
				double comparingX = comparingCity.getXCoord();
				double comparingY = comparingCity.getYCoord();

				double distance = 0.0;
				if (j != i) {
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


	/**
	Used to update pheromones globally within the ACS algorithm
	Directly updates pheromone matrix
	 */
	public void antColonySystemGlobalUpdate(Ant bestAnt) {

		/*

		String tourString = "";
		int[] tour = bestAnt.getTour();

		for (int i = 0; i < tour.length; i++) {
			tourString += Integer.toString(tour[i]);
		}
		tourString += tour[0];

		double pheromoneBoost = 1.0 / bestAnt.getTotalCost();

		for (int i = 0; i < numCities; i++) {
			for (int j = i; j < numCities; j++) {

				String edge = Integer.toString(i);
				edge += Integer.toString(j);
				String edgeReverse = Integer.toString(j);
				edgeReverse += Integer.toString(i);

				double newPheromoneContent = this.pheromones[i][j] * (1.0 - this.rho);

				if (tourString.contains(edge) || tourString.contains(edgeReverse)) {
					newPheromoneContent += pheromoneBoost * (this.rho);
				}

				this.pheromones[i][j] = newPheromoneContent;
				this.pheromones[j][i] = newPheromoneContent;
			}
		}

		*/

		int[] tour = bestAnt.getTour();

		double pheromoneBoost = 1.0 / bestAnt.getTotalCost();

		for(int i = 0; i < tour.length - 1; i++) {
			int city1 = tour[i];
			int city2 = tour[i+1];

			double pheromonesCurrent = this.pheromones[city1][city2];
			double newPheromoneContent = (1.0 - this.rho) * pheromonesCurrent + pheromoneBoost * this.rho;
			this.pheromones[city1][city2] = newPheromoneContent;
			this.pheromones[city2][city1] = newPheromoneContent;
		}

		int city1 = tour[tour.length - 1];
		int city2 = tour[0];
		double pheromonesCurrent = this.pheromones[city1][city2];
		double newPheromoneContent = (1.0 - this.rho) * pheromonesCurrent + pheromoneBoost * this.rho;
		this.pheromones[city1][city2] = newPheromoneContent;
		this.pheromones[city2][city1] = newPheromoneContent;
	}

	/**
	Global pheromone update for elitist ant system
	Updates pheromone matrix
	 */

	public void elitistGlobalPheromoneUpdate(Ant bestAnt) {

		String tourString = "";
		int[] tour = bestAnt.getTour();

		for (int i = 0; i < tour.length; i++) {
			tourString += Integer.toString(tour[i]);
		}
		tourString += tour[0];

		double pheromoneBoost = 1.0 / bestAnt.getTotalCost();

		for (int i = 0; i < numCities; i++) {
			for (int j = i; j < numCities; j++) {

				String edge = Integer.toString(i);
				edge += Integer.toString(j);
				String edgeReverse = Integer.toString(j);
				edgeReverse += Integer.toString(i);

				double evaporatedPheromones = this.pheromones[i][j] * (1.0 - this.rho);
				double newPheromoneAdditions = this.iterationPheromones[i][j];
				this.iterationPheromones[i][j] = 0.0;
				this.iterationPheromones[j][i] = 0.0;

				if (tourString.contains(edge) || tourString.contains(edgeReverse)) {
					newPheromoneAdditions += pheromoneBoost * (this.elitistNum);
				}

				this.pheromones[i][j] = evaporatedPheromones + newPheromoneAdditions;
				this.pheromones[j][i] = evaporatedPheromones + newPheromoneAdditions;
			}
		}


	}



	/**
	Used to set elitism factor
 	*/

	public void setElitismFactor() {
		this.elitistNum = this.numAnts;
	}

	/**
	A local pheromone update for ACS
	 */
	public void antColonySystemLocalUpdate(int city1, int city2) {

		this.pheromones[city1][city2] = (1.0 - this.epsilon) * this.pheromones[city1][city2] + this.epsilon * this.tau;
		this.pheromones[city2][city1] = (1.0 - this.epsilon) * this.pheromones[city2][city1] + this.epsilon * this.tau;

	}

	/**
	@param int city1
	@param int city2
	calculate frequency of an edge appearing in our population of ants
	@return double total
	 */

	public double calculateTotal(int city1, int city2) {
		double total = 0;
		for (Ant ant : antList) {
			if (ant.containsCities(city1, city2)) {
				total += (1.0 / this.numCities);
			}
		}
		return total;
	}

	/**
	Updates the Iteration Pheromones matrix for EAS
	 */

	public void addIterationPheromonesElitist(int[] tour, double tourLength) {

		double pheromoneAddition = 1.0 / tourLength;
		for (int i = 0; i < tour.length - 1; i++) {
			int city1 = tour[i];
			int city2 = tour[i + 1];
			this.iterationPheromones[city1][city2] = pheromoneAddition;
			this.iterationPheromones[city2][city1] = pheromoneAddition;
		}
		int city1 = tour[tour.length - 1];
		int city2 = tour[0];
		this.iterationPheromones[city1][city2] = pheromoneAddition;
		this.iterationPheromones[city2][city1] = pheromoneAddition;
	}

	/**
	@param int city1, city2
	getter for edge distance
	@return double distance between city1, city2
	 */

	public double getDistance(int city1, int city2) {
		return this.distances[city1][city2];

	}

	/**
	@param int city1, city2
	getter for pheromone levels on an edge
	@return double distance between city1, city2
	 */

	public double getPheromones(int city1, int city2) {
		return this.pheromones[city1][city2];

	}

	/**
	@param int cityId
	@param Set visitedSet
	Make the greedy choice for the the next city
	@return int bestCity
	 */
	public int getNextCityGreedy(int cityId, Set<Integer> visitedSet) {

		double[] neighboringCities = this.distances[cityId];
		double[] neighoringPheromones = this.pheromones[cityId];

		double bestProductSoFar = 0.0;
		int bestCitySoFar = 0;

		for (int i = 0; i < neighboringCities.length; i++) {

			double product = 0.0;

			if (!visitedSet.contains(i)) {
				double cityDistance = 1.0 / neighboringCities[i];
				double pheromoneContent = neighoringPheromones[i];
				product = pheromoneContent * Math.pow(cityDistance, this.beta);

				if (product > bestProductSoFar) {
					bestProductSoFar = product;
					bestCitySoFar = i;
				}
			}
		}
		return bestCitySoFar;
	}

	/**
	@param int cityId
	@param Set visitedSet
	returns next city according to the calculated probabilities  
	@return int city
	 */

	public int getNextCityProb(int cityId, Set<Integer> visitedSet) {

		double[] probabilities = new double[this.distances.length];
		double[] neighboringCities = this.distances[cityId];
		double[] neighoringPheromones = this.pheromones[cityId];

		double runningProbSum = 0.0;

		for (int i = 0; i < neighboringCities.length; i++) {

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

	/**
	@param double proboabilities
	picks a city probabilistically according to the array input
	@return int city index
	 */

	public static int pickCityFromProbabilities(double[] probabilities) {

		Random randy = new Random();
		double randDouble = randy.nextDouble();

		int counter = 0;

		while (probabilities[counter] < randDouble) {
			counter++;
		}
		return counter;
	}

	/**
	populates antList with new ants
	 */

	public void setAntList() {
		int i = 0;
		Ant ant;
		while (i < this.numAnts) {
			ant = new Ant(i, this);
			antList.add(ant);
			i++;
		}
	}

	/**
	getter for the environment's list of ants
	@return List of ants
	 */
	public List<Ant> getAntList() {
		return this.antList;
	}

	/**
	Static method used to round accordingly
	 */
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	getter for the distances 2-D array
	@return 2-D array
	 */

	public double[][] getDistanceMatrix() {
		return this.distances;
	}

	/**
	getter for number of cities
	@return int number of cities
	 */

	public int getNumCities() {
		return this.numCities;
	}

	/**
	@param double pheromone content
	Used to populate the pheromone matrix initially
	 */

	public void setInitialPheromones(double pheromoneContent) {
		this.tau = pheromoneContent;
		for (int i = 0; i < this.numCities; i++) {
			for (int j = 0; j < this.numCities; j++) {
				this.pheromones[i][j] = 1.0 * pheromoneContent;
			}
		}
	}

	/**
	Used to print distances
	 */
	public void printDistances() {

		for (int i = 0; i < this.distances.length; i++) {
			String line = "";

			for (int j = 0; j < this.distances.length; j++) {
				line += " " + round(this.distances[i][j], 1);
			}

			System.out.println(line);
		}
	}
	/**
	Used to print pheromones
	 */
	public void printPheromones() {
		for (int i = 0; i < this.pheromones.length; i++) {
			String line = "";

			for (int j = 0; j < this.pheromones.length; j++) {
				line += " " + round(this.pheromones[i][j], 1);
			}

			System.out.println(line);
		}
	}

}