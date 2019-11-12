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

    }
}