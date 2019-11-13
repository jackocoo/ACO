import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;

public class Ant {

    private int[] tour;

    private double totalCost;

    private Environment world;

    private double length;

    private List<Integer> greedyTour = new ArrayList<Integer>();

    public Ant(Environment world) {
        this.world = world;
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
   
    public double getLength() {
        return length;
    }

    public Environment getEnvironment() {
        return this.world;
    }

}