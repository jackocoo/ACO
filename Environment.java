import java.util.Arrays;

import java.util.List;
import java.util.ArrayList;

public class Environment {

	private double[][] distances;
	private double[][] pheromones;
	private Ant bestSoFar;

	public double alpha;
	public double beta;
	public double rho;
	public double elitistNum;
	public double epsilon;
	public double tau;
	public double q;

	public List<Ant> ants = new ArrayList<Ant>();

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


	public void updatePheromones(int city1, int city2, String acoType) {

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
			this.pheromones[city1][city2] = (1.0 - this.rho) * this.pheromones[city1][city2] + calcuLateTotal(city1, city2) + this.elitistNum * (1.0 /bestSoFar.getLength());
		} else {
			this.pheromones[city1][city2] = (1.0 - this.rho) * this.pheromones[city1][city2] + calcuLateTotal(city1, city2);

		}
	}

	public double getDistance(int city1, int city2) {

	}

	public double getPheromones(int city1, int city2) {

	}


}