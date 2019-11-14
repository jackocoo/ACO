import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;
import java.util.List;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        String data = null;
        int dimension = 0;
        String edgeWeightType = "";
        List<City> cities = new ArrayList<City>();
        String filename = "ulysses16.tsp";

        try {
            File initialFile = new File(filename);
            initialFile.createNewFile();

            Reader reader = new FileReader(initialFile);

            BufferedReader buffReader = new BufferedReader(reader);

            boolean first = true;
            int counter = 1;

            while ((data = buffReader.readLine()) != null) {
                City city;
                if (first) {
                    if (data.contains("EDGE_WEIGHT_TYPE")) {
                        edgeWeightType = data.split(" ")[1];
                    } else if (data.contains("DIMENSION")) {
                        dimension = Integer.parseInt(data.split(" ")[1]);
                    } else if (data.contains("NODE_COORD_SECTION")) {
                        first = false;
                    }
                } else {
                    if (counter <= dimension) {
                        String[] temp = data.split(" ");
                        double xCoord = Double.parseDouble(temp[1]);
                        double yCoord = Double.parseDouble(temp[2]);
                        city = new City(counter, xCoord, yCoord);
                        cities.add(city);
                        counter++;
                    }
                }
            }

            System.out.println(cities);
            buffReader.close();
        } catch (IOException e) {
            System.out.println("IO Exception");
        }

        //Environment env = new Environment(cities.size(), 100, 0.1, 0.1, 0.1, 0.1, 0.1, 0, 0.1);
        // System.out.println("before:" );
        // env.calculateDistances(cities);
        // env.setInitialPheromones(0);

        // System.out.println("after: ");
        // env.printDistances();

        // env.printPheromones();

        // System.out.println("test greedy tour construction");
        // Ant testAnt = new Ant(env);
        // testAnt.constructGreedyTour(0);

        //AntColonySystem acs = new AntColonySystem(env, cities, 100);
        //acs.optimize();

        Environment env = new Environment(cities.size(), 5, 1, 3, 0.1, 0.1, 0.1, 0.1, 0.9);
        System.out.println("before:" );
        env.printDistances();
        env.calculateDistances(cities);

        System.out.println("after: ");
        env.printDistances();

        System.out.println("test greedy tour construction");
        Ant testAnt = new Ant(1, env);

        double tauNot = testAnt.calculateInitialPhermone();
        //double greedyTourLength = testAnt.constructGreedyTour(0);

        //double tauNot = 1.0 / (greedyTourLength * cities.size());
        env.setInitialPheromones(tauNot);

        System.out.println("test ant making a tour");
        Ant test2 = new Ant(2, env);
        test2.makeProbTour();
    }
}