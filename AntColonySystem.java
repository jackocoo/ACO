import java.util.List;
import java.util.ArrayList;

public class AntColonySystem {

    private Environment env;
    private List<City> cityList = new ArrayList<City>();
    private int numIterations;

    /**
     * Constructor to create an instance of an AntColonySystem object, that will
     * then be optimized.
     * 
     * @param env           The particular environment the algorithm will be
     *                      operating within.
     * @param cityList      The list of City objects (read in when starting the
     *                      problem).
     * @param numIterations The number of iterations the problem will be run for.
     */
    public AntColonySystem(Environment env, List<City> cityList, int numIterations) {
        this.env = env;
        this.cityList = cityList;
        this.numIterations = numIterations;
    }

    /**
     * Performs the necessary steps of ACS optimization.
     * 
     * @return A list of the bestScores every 10 iterations (used to analyze our
     *         tests).
     */
    public List<Double> optimize() {
        // initialize the enviroment adjacency matrix
        this.env.calculateDistances(cityList);

        // calculate tauNot and initialize the pheromone adjacency matrix
        Ant testAnt = new Ant(-1, this.env);
        double tauNot = testAnt.calculateInitialPhermone();
        this.env.setInitialPheromones(tauNot);

        // set the list of ants in the environment
        this.env.setAntList();

        List<Ant> antList = this.env.getAntList();
        List<Ant> bestAntsList = new ArrayList<Ant>();
        List<Double> bests = new ArrayList<Double>();

        // initialize the bests
        int i = 0;
        Ant bestAnt = antList.get(0);
        double bestScore = Double.POSITIVE_INFINITY;

        while (i < this.numIterations) {
            // iterate through all the ants
            for (int j = 1; j < antList.size(); j++) {
                // have each ant make a tour
                double tourCost = antList.get(j).makeACSProbTour();

                // if the tourCost is better, then reset the tours
                if (tourCost < bestScore) {
                    bestScore = tourCost;
                    bestAnt = antList.get(j);
                    bestAnt = antList.get(j).cloneAnt();
                }

                bestAntsList.add(bestAnt);
            }

            // perfom the global update on the best ant found
            this.env.antColonySystemGlobalUpdate(bestAnt);

            System.out.println("************** COMPLETED the " + i + "iteration ... " + bestScore);
            i++;

            if (i % 10 == 0) {
                bests.add(bestScore);
            }
        }
        return bests;
    }

}