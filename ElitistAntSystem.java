import java.util.List;
import java.util.ArrayList;

public class ElitistAntSystem {

    private Environment env;
    private List<City> cityList = new ArrayList<City>();
    private int numIterations;

    /**
     * Constructor for the instance of the ElitistAntSystem algorithm. Will run optimize to optimize the algorithm.
     * 
     * @param env The Environment instance we will be operating within.
     * @param cityList The list of City objects (i.e. problem information we read in).
     * @param numIterations The number of iterations we would like the algorithm to run for.
     */
    public ElitistAntSystem(Environment env, List<City> cityList, int numIterations) {
        this.env = env;
        this.cityList = cityList;
        this.numIterations = numIterations;
    }

    /**
     * Perfoms the necessary optimization steps for Elitist Ant System.
     * @return A list of the bestScores every 10 iterations (used to analyze our
     *         tests).
     */
    public List<Double> optimize() {
        // initialize the elitism factor
        this.env.setElitismFactor();

        // initialize the distances adjacency matrix
        this.env.calculateDistances(cityList);

        // calculate tauNot and initialize pheromones adjacency matrix
        Ant testAnt = new Ant(-1, this.env);
        double tauNot = testAnt.calculateInitialPhermone();
        this.env.setInitialPheromones(tauNot);

        // set the environment ant list
        this.env.setAntList();

        List<Ant> antList = this.env.getAntList();
        List<Ant> bestAntsList = new ArrayList<Ant>();
        List<Double> bests = new ArrayList<Double>();

        // initialize what we will compare everything to
        int i = 0;
        Ant bestAnt = antList.get(0);
        double bestSoFar = bestAnt.makeElitistProbTour();

        while (i < this.numIterations) {

            // iterate over all ants
            for (int j = 1; j < antList.size(); j++) {
                // for each ant, have an ant construct an elitist tour
                double tourCost = antList.get(j).makeElitistProbTour();

                // udpate iteration pheromones adjacency matrix
                this.env.addIterationPheromonesElitist(antList.get(j).getTour(), tourCost);

                // update the bestSoFar and bestAnt
                if (tourCost < bestSoFar) {
                    bestSoFar = tourCost;
                    bestAnt = antList.get(j).cloneAnt();
                }
            }
            // add the bestAnt
            bestAntsList.add(bestAnt);
            int listSize = bestAntsList.size();
            Ant lastBestAnt = bestAntsList.get(listSize - 1);

            // perform the elitistGlobalPheromone on the lastBestAnt found (even if it didn't change from the last time)
            this.env.elitistGlobalPheromoneUpdate(lastBestAnt);

            System.out.println("************** COMPLETED the " + i + "iteration ..." + bestSoFar);
            i++;

            if(i % 10 == 0) {
                bests.add(bestSoFar);
            }

        }
        return bests;
    }
}