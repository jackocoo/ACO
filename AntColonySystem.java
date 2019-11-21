import java.util.List;
import java.util.ArrayList;

public class AntColonySystem {

    private Environment env;

    private List<City> cityList = new ArrayList<City>();

    private int numIterations;

    public AntColonySystem(Environment env, List<City> cityList, int numIterations) {
        this.env = env;
        this.cityList = cityList;
        this.numIterations = numIterations;
    }

    public List<Double> optimize() {
        this.env.calculateDistances(cityList);
        Ant testAnt = new Ant(-1, this.env);
        double tauNot = testAnt.calculateInitialPhermone();
        this.env.setInitialPheromones(tauNot);

        this.env.setAntList();

        List<Ant> antList = this.env.getAntList();
        System.out.println(antList);
        System.out.println(antList.get(0).makeACSProbTour());
        List<Ant> bestAntsList = new ArrayList<Ant>();
        List<Double> bests = new ArrayList<Double>();

        int i = 0;
        Ant bestAnt = antList.get(0);
        double bestScore = Double.POSITIVE_INFINITY;
        double bestSoFar = bestAnt.makeACSProbTour();
        System.out.println("this is the best tour" + bestSoFar);
        while (i < this.numIterations) {
            Ant iterBest = antList.get(0);
            double iterBestScore = Double.POSITIVE_INFINITY;
            for (int j = 1; j < antList.size(); j++) {
<<<<<<< HEAD
                double tourCost = antList.get(j).makeACSProbTour();
                System.out.println("tour cost " + tourCost);
=======
                double tourCost = antList.get(j).makeProbTour();
                //System.out.println("tour cost " + tourCost);
>>>>>>> 96bc2f3fc314d51d1082166d76676f2db2e7cd04
                if (tourCost < bestSoFar) {
                    bestSoFar = tourCost;
                    // System.out.println("the bestsofar" + bestSoFar);
                    bestAnt = antList.get(j);
                    bestAnt = antList.get(j).cloneAnt();
                }
                if (tourCost < iterBestScore) {
                    iterBestScore = tourCost;
                    iterBest = antList.get(j);
                }
                // System.out.println("hit");
                bestAntsList.add(bestAnt);
                // System.out.println("best ant tour");
                // bestAnt.printTour();
                //System.out.println("            best has a score of " + bestSoFar + "\n");
            }
            // this.env.antColonySystemGlobalUpdate(bestAnt);
            this.env.antColonySystemGlobalUpdate(iterBest);

            if (iterBestScore < bestScore) {
                bestScore = iterBestScore;
                bestAnt = iterBest.cloneAnt();
            }

            System.out.println("************** COMPLETED the " + i + "iteration ... " + bestScore);
            i++;

            if(i % 10 == 0) {
                bests.add(bestScore);
            }
        }
        return bests;
    }

}