import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class Ant {

    private int[] tour;
    private double totalCost;
    private Environment world;
    private int numCities;
    private List<Integer> greedyTour = new ArrayList<Integer>();
    private Random rand = new Random();
    private int id;

    /**
     * Ant object. Operates within an Environment object.
     * 
     * @param id    The id of the given ant. Ranges from 0, 1, ... , m (where m
     *              represents the number of ants).
     * @param world The instance of an Environment object that an ant will be
     *              interacting with.
     */
    public Ant(int id, Environment world) {
        this.id = id;
        this.world = world;
        this.numCities = world.getNumCities();
        this.totalCost = Double.POSITIVE_INFINITY;
        this.tour = new int[this.numCities];
    }

    /**
     * Method to construct a greedy tour where an ant starts at 0. This is used to
     * calculate tauNot, which is what pheromones are initialized to when we begin
     * either our ACS or Elitist algorithm. A greedy tour is constructed when an ant
     * chooses the node closest the current node it is at and adds it to the tour if
     * it has not yet been visited.
     * 
     * @param startingCity Integer representing the starting location of the ant
     *                     when it begins constructing a greedy tour.
     * @return The total length of the greedy tour that the ant has constructed from
     *         the given starting city (which in our case we choose to be 0).
     */
    public double constructGreedyTour(int startingCity) {
        double[][] distances = this.world.getDistanceMatrix().clone();
        int numCities = this.world.getNumCities();

        double tourLength = 0.0;

        Set<Integer> visited = new HashSet<Integer>();
        visited.add(startingCity);

        int currentCity = startingCity;
        double[] row = distances[currentCity];
        while (visited.size() < numCities) {
            double currDistance = Double.POSITIVE_INFINITY;
            int bestCitySoFar = 0;
            row = distances[bestCitySoFar];
            for (int i = 0; i < row.length; i++) {
                if ((row[i] < currDistance) && !visited.contains(i)) {

                    currDistance = row[i];
                    bestCitySoFar = i;
                }
            }
            tourLength += currDistance;
            currentCity = bestCitySoFar;
            greedyTour.add(bestCitySoFar);
            visited.add(bestCitySoFar);
        }
        this.totalCost = tourLength;
        return tourLength;
    }

    /**
     * Method for calculating the value that we are going to the pheromones on all
     * edges equal to.
     * 
     * @return tauNot, which is the number that we want to set all pheromones to
     *         initially.
     */
    public double calculateInitialPhermone() {
        double answer = 1.0 / this.constructGreedyTour(0) * this.world.getNumCities();
        return answer;
    }


    /**
     * Method used to make a tour in the ACS algorithm using the psuedorandom
     * probability rule. Probabilistically picks next city to add to the tour with
     * the greedyTourConstruction method with probability q. Otherwise, the more
     * computationally exprensive method that chooses the next city
     * probabilistically (getNextCityProb). Updates the tour for the given ant and
     * returns the cost of the tours.
     * 
     * @return The cost of the tour after it has been created.
     */
    public double makeACSProbTour() {

        Set<Integer> visitedCities = new HashSet<Integer>();
        Arrays.fill(this.tour, 0);

        int worldSize = this.world.getNumCities();
        double q = this.world.getProbabilityPicker();
        int tourCounter = 0;
        double tourLength = 0;

        int startingCity = rand.nextInt(worldSize);

        visitedCities.add(startingCity);
        this.tour[tourCounter] = startingCity;
        tourCounter++;

        int currentCityId = startingCity;
        while (visitedCities.size() < worldSize) {

            double randDouble = rand.nextDouble();
            int nextCityId = startingCity;

            if (randDouble < q) {
                nextCityId = this.world.getNextCityGreedy(currentCityId, visitedCities);
            } else {
                nextCityId = this.world.getNextCityProb(currentCityId, visitedCities);
            }

            // perform the wearing away on the added edge while constructing the tour (local update)
            this.world.antColonySystemLocalUpdate(currentCityId, nextCityId);

            tour[tourCounter] = nextCityId;
            visitedCities.add(nextCityId);
            tourLength += this.world.getDistance(currentCityId, nextCityId);
            tourCounter++;
            currentCityId = nextCityId;

        }

        tourLength += this.world.getDistance(currentCityId, startingCity);

        this.totalCost = tourLength;
        return tourLength;
    }

    /**
     * Method used to make a tour in the Elitist Ant System algorithm. Unlike ACS,
     * which uses a different rule part of the time, ACS uses the getNextCityProb
     * all the time to choose which edges to include in the tour.
     * 
     * @return The cost of the tour after it has been created.
     */
    public double makeElitistProbTour() {

        Set<Integer> visitedCities = new HashSet<Integer>();
        Arrays.fill(this.tour, 0);

        int worldSize = this.world.getNumCities();
        double q = this.world.getProbabilityPicker();
        int tourCounter = 0;
        double tourLength = 0;

        int startingCity = rand.nextInt(worldSize);

        visitedCities.add(startingCity);
        this.tour[tourCounter] = startingCity;
        tourCounter++;

        int currentCityId = startingCity;
        while (visitedCities.size() < worldSize) {

            int nextCityId = startingCity;

            nextCityId = this.world.getNextCityProb(currentCityId, visitedCities);

            tour[tourCounter] = nextCityId;
            visitedCities.add(nextCityId);
            tourLength += this.world.getDistance(currentCityId, nextCityId);
            tourCounter++;
            currentCityId = nextCityId;
        }

        tourLength += this.world.getDistance(currentCityId, startingCity);

        this.totalCost = tourLength;
        return tourLength;
    }

    /**
     * String representation of an Ant, which is just its id number.
     * 
     * @return The string representation of an Ant.
     */
    public String toString() {
        String rep = "";
        rep += this.id;
        return rep;

    }

    /**
     * For a given ant, this method sets an ant's current tour instance to a new
     * tour instance. Used when we clone an ant. Also, returns the total cost of
     * this newTour that the ant's tour instance has been set to.
     * 
     * @param int[] The newTour that we want to set the ant's tour instance to.
     * @return The total cost of the given ant's new tour instance.
     */
    public double setTour(int[] newTour) {
        this.tour = newTour.clone();

        double totalLength = 0;
        for (int i = 0; i < this.tour.length; i++) {

            int next;
            // Handles connecting final node in list back to first node in list
            if (i == this.tour.length - 1) {
                next = 0;
            } else {
                next = i + 1;
            }
            double distance = this.world.getDistance(this.tour[i], this.tour[next]);
            totalLength += distance;
        }
        this.totalCost = totalLength;
        return totalLength;
    }

    /**
     * Copy the current ant object.
     * 
     * @return New Ant object that is the same at the current ant.
     */
    public Ant cloneAnt() {
        Ant newAnt = new Ant(this.id, this.world);
        newAnt.setTour(this.tour);
        return newAnt;
    }

    /**
     * Getter for the Ant's tour.
     * 
     * @return The Ant's tour.
     */
    public int[] getTour() {
        return this.tour;
    }

    /**
     * Getter to return the totalCost of an Ant's tour.
     * 
     * @return The total cost of the tour.
     */
    public double getTotalCost() {
        return this.totalCost;
    }

    /**
     * Prints out an Ant's tour.
     */
    public void printTour() {

        String output = "[";

        for (int i = 0; i < this.tour.length; i++) {
            output += " " + this.tour[i] + ",";
        }
        output += "] ! ";
        System.out.println(output);
    }

    /**
     * Getter to return the environment of a particular ant.
     * 
     * @return An ant's own Environment object.
     */
    public Environment getEnvironment() {
        return this.world;
    }

}