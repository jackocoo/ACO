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
	private double[][] iterationPheromones;
	private int numCities;
	private int numAnts;

	private double alpha;
	private double beta;
	private double rho; // evaporation rate
	private double elitistNum; // the elitism factor
	private double epsilon; // decaying coefficient
	private double tau; // 1 / length of greedy tour * num cities
	private double q; // prob for choosing to use greedy or prob

	private List<Ant> antList = new ArrayList<Ant>();

	/**
	 * Constructor for an Environment object. For ACO, the Environment store
	 * relevant problem information. The Ants operate within the Environment and
	 * respond to changes. The Environmnet holds onto distances between cities,
	 * pheromone concentrations, and bestTours. These are adjusted as Ants move
	 * about the Enviroment, altering the pheromone concentration on the edges
	 * between cities.
	 * 
	 * @param numCities  Number of cities in the problem.
	 * @param numAnts    The number of ants in the Environment.
	 * @param alpha      Real number value that is used for creating probabilistic
	 *                   tours.
	 * @param beta       Real number value that is used for creating probabilistic
	 *                   tours and greedy tour in ACS tour construction.
	 * @param rho        The evaporation rate.
	 * @param elitistNum The elitism factor - in our implementation, this is set to
	 *                   the number of ants.
	 * @param epsilon    The pheromone degredation, which is used for ACS local
	 *                   pheromone update.
	 * @param tau        The initial value we set the pheromones to.
	 * @param q          The probability of choosing greedy tour construction over
	 *                   probabilistic tour construction in ACS.
	 */

	public Environment(int numCities, int numAnts, double alpha, double beta, double rho, double elitistNum,
			double epsilon, double tau, double q) {

		this.numAnts = numAnts;
		this.numCities = numCities;
		this.distances = new double[numCities][numCities];
		this.pheromones = new double[numCities][numCities];
		this.iterationPheromones = new double[numCities][numCities];
		this.alpha = alpha;
		this.beta = beta;
		this.rho = rho;
		this.elitistNum = elitistNum;
		this.epsilon = epsilon;
		this.tau = tau;
		this.q = q;

	}

	/****************
	 * General Ant Colony Optimization Methods - used in both ACS and EAS
	 **********************/

	/**
	 * Given a list of City objects, this method calculates the Euclidean distance
	 * between two nodes and populates our adjacency matrix at (i, j) and (j, i)
	 * because our implementation of TSP is symmetric.
	 * 
	 * @param cityList A list object holding all the City objects. Created when we
	 *                 read in a particular problem.
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
	 * Populates the pheromone matrix at the beginning of the problem.
	 * 
	 * @param pheromoneContent The pheromone amount we are depositing on each edge
	 *                         of the graph initially. Calculated in
	 *                         calculateInitialPheromone method (which is found
	 *                         within the Ant class).
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
	 * Method used to initialize a list of Ants for both Ant Colony System and
	 * Elitist Ant System. This creates a list of Ant objects that are to operate
	 * within an instance of an Environment object.
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
	 * 
	 * @param cityId
	 * @param visitedSet
	 * @return
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
	 * @param double proboabilities picks a city probabilistically according to the
	 *               array input
	 * @return int city index
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

	/***********************************
	 * PERTINENT ANT COLONY SYSTEM METHODS
	 ******************************/

	/**
	 * Algorithm used only in Ant Colony System. Relies on heuristic information.
	 * Rather than using the getNextCityProb all the (because it is computationally
	 * expensive), ACS uses this method to add cities to a tour with probability q
	 * (which is typically 0.9).
	 * 
	 * Eta and beta are heuristic information for this portion of the algorithm: -
	 * eta(i,j) = 1 / d(i,j), where d(i,j) is the distance between node i and node
	 * j. - beta = some real constant
	 * 
	 * Using this information, a city is added to the tour if it maximizes the
	 * tau(i,j) * eta(i,j)^beta if and only if the ant has not yet visited the next
	 * city.
	 * 
	 * So given a city id, which can be interpretted to be i in this case, we are
	 * looking to pick some city j that minimizes the above statement if an ant has
	 * not yet visited that city.
	 * 
	 * @param cityId     The id of the city we are looking to connect another node
	 *                   to.
	 * @param visitedSet Set of the cities that the ant has already visited.
	 * @return The next city to add to the tour.
	 */
	public int getNextCityGreedy(int cityId, Set<Integer> visitedSet) {

		double[] neighboringCities = this.distances[cityId];
		double[] neighoringPheromones = this.pheromones[cityId];

		double bestProductSoFar = 0.0;
		int bestCitySoFar = 0;

		for (int i = 0; i < neighboringCities.length; i++) {

			double product = 0.0;

			// if the ant has not yet visited the city
			if (!visitedSet.contains(i)) {

				// calculate the product
				double eta = 1.0 / neighboringCities[i];
				double pheromoneContent = neighoringPheromones[i];
				product = pheromoneContent * Math.pow(eta, this.beta);

				// if we find a greater product, then reset the bestProductSoFar and
				// bestCitySoFar
				if (product > bestProductSoFar) {
					bestProductSoFar = product;
					bestCitySoFar = i;
				}
			}
		}
		return bestCitySoFar;
	}

	/**
	 * Global update for Ant Colony System
	 * 
	 * Using the bestAnt so far, which holds onto the best tour the ACS algorithm
	 * has encountered so far, this method performs the ACS global pheromone update
	 * on the edges of the graph.
	 * 
	 * The global update is as follows: (1 - rho) * pheromones[i][j] + (the sum from
	 * k = 1 to m of 1 / cost of ant k's tour)
	 * 
	 * NOTE: We attempted to do this one way, and left it commented out (because it
	 * worked better), but was in fact not the best way to perform the task.
	 * 
	 * @param bestAnt Among the m (numAnts) so far, the ant that holds the best
	 *                tour.
	 */
	public void antColonySystemGlobalUpdate(Ant bestAnt) {

		/*
		 * 
		 * String tourString = ""; int[] tour = bestAnt.getTour();
		 * 
		 * for (int i = 0; i < tour.length; i++) { tourString +=
		 * Integer.toString(tour[i]); } tourString += tour[0];
		 * 
		 * double pheromoneBoost = 1.0 / bestAnt.getTotalCost();
		 * 
		 * for (int i = 0; i < numCities; i++) { for (int j = i; j < numCities; j++) {
		 * 
		 * String edge = Integer.toString(i); edge += Integer.toString(j); String
		 * edgeReverse = Integer.toString(j); edgeReverse += Integer.toString(i);
		 * 
		 * double newPheromoneContent = this.pheromones[i][j] * (1.0 - this.rho);
		 * 
		 * if (tourString.contains(edge) || tourString.contains(edgeReverse)) {
		 * newPheromoneContent += pheromoneBoost * (this.rho); }
		 * 
		 * this.pheromones[i][j] = newPheromoneContent; this.pheromones[j][i] =
		 * newPheromoneContent; } }
		 * 
		 */

		int[] tour = bestAnt.getTour();

		double pheromoneBoost = 1.0 / bestAnt.getTotalCost();

		for (int i = 0; i < tour.length - 1; i++) {
			int city1 = tour[i];
			int city2 = tour[i + 1];

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
	 * In ACS, while an Ant constructs a tour, the ant will gradually wear away the
	 * pheromones on the legs of their tour to deter other ants from choosing the
	 * same path initially. This allows for more exploration.
	 * 
	 * The algorithm for this is as follows: t(i,j) = (1 - epsilon) * t(i,j) + tau *
	 * epsilon - t(i,j) -- pheromones on edge (i,j) - epsilon -- pheromone decay
	 * coefficient - tau -- tau0 (i.e. the value the pheromones are first
	 * initialized to)
	 * 
	 * @param city1 The id of city1.
	 * @param city2 The id of city2.
	 */
	public void antColonySystemLocalUpdate(int city1, int city2) {

		this.pheromones[city1][city2] = (1.0 - this.epsilon) * this.pheromones[city1][city2] + this.epsilon * this.tau;
		this.pheromones[city2][city1] = (1.0 - this.epsilon) * this.pheromones[city2][city1] + this.epsilon * this.tau;

	}

	/********************************
	 * PERTINENT ELITIST ANT SYSTEM METHODS
	 *******************************/

	/**
	 * In Elitist Ant System, the global update occurs at the end of each iteration
	 * of the algorithm. We want to update our pheromones adjacency matrix at the
	 * end of the iteration to reflect these global updates.
	 * 
	 * The global update is as follows:
	 * 
	 * t(i, j) [note: at timestep t+1] = (1 - rho)t(i,j) [note at timestep t] + (1 /
	 * cost(bestTourSofar))*rho
	 * 
	 * @param bestAnt The best ant, which holds the bestTourSoFar.
	 */
	public void elitistGlobalPheromoneUpdate(Ant bestAnt) {

		// convert the tour to a string (so that we can only update legs in the best so
		// far)
		String bestTourSoFarString = "";
		int[] bestTourSoFar = bestAnt.getTour();

		for (int i = 0; i < bestTourSoFar.length; i++) {
			bestTourSoFarString += Integer.toString(bestTourSoFar[i]);
			bestTourSoFarString += ".";
		}
		bestTourSoFarString += bestTourSoFar[0];

		double pheromoneBoost = 1.0 / bestAnt.getTotalCost();

		// iterate over the pheromones adjacency matrix
		for (int i = 0; i < numCities; i++) {
			for (int j = i; j < numCities; j++) {

				// create stringified edges - so that we can compare to our stringified tour
				String edge = Integer.toString(i);
				edge += ".";
				edge += Integer.toString(j);
				String edgeReverse = Integer.toString(j);
				edgeReverse += ".";
				edgeReverse += Integer.toString(i);

				// grab and compute (1 - rho)t(i,j)
				double evaporatedPheromones = this.pheromones[i][j] * (1.0 - this.rho);

				// get what we are going to add based on the pheromones deposited on the
				// iteration
				double newPheromoneAdditions = this.iterationPheromones[i][j];

				// reset
				this.iterationPheromones[i][j] = 0.0;
				this.iterationPheromones[j][i] = 0.0;

				// check to see if our tourString contains the edges we are looking at
				// if they do then compute the newPheromoneAdditions
				if (bestTourSoFarString.contains(edge) || bestTourSoFarString.contains(edgeReverse)) {
					newPheromoneAdditions += pheromoneBoost * (this.elitistNum);
				}

				// update pheromones instance var
				this.pheromones[i][j] = evaporatedPheromones + newPheromoneAdditions;
				this.pheromones[j][i] = evaporatedPheromones + newPheromoneAdditions;
			}
		}

	}

	/**
	 * For each iteration, we want to keep track of the pheromones deposited. So,
	 * while optimizing this algorithm, we are going to update the iteration
	 * pheromones in order to compute the globalElitistPheromone update.
	 * 
	 * See the Ant Colony System optimize function to see where this is used.
	 * 
	 * @param tour       The tour an ant created.
	 * @param tourLength The length of the tour.
	 */
	public void addIterationPheromonesElitist(int[] tour, double tourLength) {

		double pheromoneAddition = 1.0 / tourLength;

		// for each city in the tour, update the pheromones to the 1 / tour length
		for (int i = 0; i < tour.length - 1; i++) {
			int city1 = tour[i];
			int city2 = tour[i + 1];
			this.iterationPheromones[city1][city2] = pheromoneAddition;
			this.iterationPheromones[city2][city1] = pheromoneAddition;
		}

		// mannually dealing with first and last node connection in list
		int city1 = tour[tour.length - 1];
		int city2 = tour[0];
		this.iterationPheromones[city1][city2] = pheromoneAddition;
		this.iterationPheromones[city2][city1] = pheromoneAddition;
	}

	/********************************
	 * SETTERS AND GETTERS
	 **********************************************/

	/**
	 * Since we want the elitismNum to be the same as the number of ants, we will
	 * just set these to be the same here.
	 */
	public void setElitismFactor() {
		this.elitistNum = this.numAnts;
	}

	/**
	 * Get a distance between two nodes (city1 and city2) from our distance
	 * adjacency matrix.
	 * 
	 * @param city1 The id of city1.
	 * @param city2 The id of city2.
	 * @return Distance between two cities
	 */
	public double getDistance(int city1, int city2) {
		return this.distances[city1][city2];

	}

	/**
	 * Get the pheromones on the edge between two cities from our pheromones
	 * adjacency matrix.
	 * 
	 * @param city1 The first city id.
	 * @param city2 The second city id.
	 * @return The pheromones on the edge between city1 and city2.
	 */
	public double getPheromones(int city1, int city2) {
		return this.pheromones[city1][city2];

	}

	/**
	 * Returns the list of ants operationing within the Environment instance.
	 * 
	 * @return The list of ants that operate within the Environment.
	 */
	public List<Ant> getAntList() {
		return this.antList;
	}

	/**
	 * Getter to retrieve our distances adjacency matrix.
	 * 
	 * @return The distance adjacency matrix.
	 */
	public double[][] getDistanceMatrix() {
		return this.distances;
	}

	/**
	 * Getter for the numCities.
	 * 
	 * @return The number of cities, i.e. problem size.
	 */
	public int getNumCities() {
		return this.numCities;
	}

	/**
	 * Getter that returns the parameter q. Used to determine how to build tours in
	 * ACS.
	 * 
	 * @return The parameter q, which determines in ACS whether to perform greedy
	 *         tour construction, or probabilistic tour construction.
	 */
	public double getProbabilityPicker() {
		return this.q;
	}

	/*******************************
	 * METHODS USED TO PRINT/DEBUG
	 *******************************************/

	/**
	 * Static helper method to round values in both our distance and adjacency
	 * matrices. This method is used for debugging purposes only.
	 * 
	 * @param value  The particular value we would like to round.
	 * @param places The number of places we would like to round to.
	 */
	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(places, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	/**
	 * Helper method used to print our distances adjacency matrix. This method is
	 * used for debugging purposes.
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
	 * Helper method used to print our pheromones adjacency matrix. This method is
	 * used for debugging purposes.
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