import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Random;
import java.util.HashSet;
import java.util.Set;

public class Ant {

    public int[] tour;

    private double totalCost;

    private Environment world;

    private int numCities;

    private List<Integer> greedyTour = new ArrayList<Integer>();

    private Random rand = new Random();




    public Ant(Environment world) {

        this.world = world;
        this.numCities = world.getEnvironmentSize();
        this.totalCost = Double.POSITIVE_INFINITY;
        this.tour = new int[this.numCities];
    }

    public boolean containsCities(int city1, int city2) {
        boolean answer = false;
        if (Arrays.asList(tour).contains(city1) && Arrays.asList(tour).contains(city2)) {
            answer = true;
        }
        return answer;
    }

    public double constructGreedyTour(int startingCity) {
        double[][] distances = this.world.getDistanceMatrix().clone();
        int numCities = this.world.getNumCities();
        System.out.println(numCities);

        double tourLength = 0.0;
        
        Set<Integer> visited = new HashSet<Integer>();
        visited.add(startingCity);

        int currentCity = startingCity;
        double[] row = distances[currentCity];
        while (visited.size() < numCities) {
            double currDistance = Double.POSITIVE_INFINITY;
            row = distances[currentCity];
            int bestCitySoFar = 0;
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
            System.out.println(greedyTour);
        }
        return tourLength;
    }

    public double calculateInitialPhermone(int startingCity) {
        return 1.0 / this.constructGreedyTour(startingCity) * this.world.getNumCities();
    }
   
    public Environment getEnvironment() {
        return this.world;
    }

    public double makeProbTour() {

        Set<Integer> visitedCities = new HashSet<Integer>();
        Arrays.fill(this.tour, 0);

        int worldSize = this.world.getEnvironmentSize();
        double q = this.world.getQ();
        int tourCounter = 0;
        double tourLength = 0;

        int startingCity = rand.nextInt();

        visitedCities.add(startingCity);
        this.tour[tourCounter] = startingCity;
        tourCounter++;

        int currentCityId = startingCity;
        while(visitedCities.size() < worldSize) {

            double randDouble = rand.nextDouble();
            int nextCityId = startingCity;

            if(randDouble < q) {
                nextCityId = this.world.getNextCityGreedy(currentCityId, visitedCities);
            } else {
                nextCityId = this.world.getNextCityProb(currentCityId, visitedCities);
            }

            this.world.antColonySystemLocalUpdate(currentCityId, nextCityId);

            tour[tourCounter] = nextCityId;
            visitedCities.add(nextCityId);
            tourLength += this.world.getDistance(currentCityId, nextCityId);
            tourCounter++;
        }

        tourLength += this.world.getDistance(currentCityId, startingCity);

        return tourLength;
    }

}