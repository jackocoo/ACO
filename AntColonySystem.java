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

        while (i < this.numIterations) {

            for (int j = 1; j < antList.size(); j++) {
                double tourCost = antList.get(j).makeACSProbTour();
                if (tourCost < bestScore) {
                    bestScore = tourCost;
                    bestAnt = antList.get(j);
                    bestAnt = antList.get(j).cloneAnt();
                }

                bestAntsList.add(bestAnt);
            }
            this.env.antColonySystemGlobalUpdate(bestAnt);

            System.out.println("************** COMPLETED the " + i + "iteration ... " + bestScore);
            i++;

            if(i % 10 == 0) {
                bests.add(bestScore);
            }
        }
        return bests;
    }

}