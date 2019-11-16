import java.util.List;
import java.util.ArrayList;

public class ElitistAntSystem {

    private Environment env;

    private List<City> cityList = new ArrayList<City>();
    
    private int numIterations;

    public ElitistAntSystem(Environment env, List<City> cityList, int numIterations) {
        this.env = env;
        this.cityList = cityList;
        this.numIterations = numIterations;
    }

    public void optimize() {
        this.env.setElitismFactor();
        this.env.calculateDistances(cityList);
        Ant testAnt = new Ant(-1, this.env);
        double tauNot = testAnt.calculateInitialPhermone();
        this.env.setInitialPheromones(tauNot);

        this.env.setAntList();

        List<Ant> antList = this.env.getAntList();
        System.out.println(antList);
        System.out.println(antList.get(0).makeProbTour());
        List<Ant> bestAntsList = new ArrayList<Ant>();
        
        int i = 0;
        Ant bestAnt = antList.get(0);
        double bestSoFar = bestAnt.makeProbTour();
        System.out.println("this is the best tour" + bestSoFar);
        while (i < this.numIterations) {
            for (int j = 1; j < antList.size(); j++) {
                double tourCost = antList.get(j).makeProbTour();
                System.out.println("tour cost " + tourCost);
                if (tourCost < bestSoFar) {
                    bestSoFar = tourCost;
                    // System.out.println("the bestsofar" + bestSoFar);
                    bestAnt = antList.get(j);
                    bestAnt = antList.get(j).cloneAnt();
                }
                System.out.println("            best has a score of " + bestSoFar + "\n");              
            }
            bestAntsList.add(bestAnt);
            int listSize = bestAntsList.size();
            Ant lastBestAnt = bestAntsList.get(listSize - 1);

            this.env.elitistPheromoneUpdate(lastBestAnt);

            System.out.println("************** COMPLETED the " + i + "iteration");
            i++;

        }
    }
}