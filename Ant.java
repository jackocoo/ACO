import java.util.Arrays;

public class Ant {

    private int[] tour;

    private double totalCost;

    private Environment world;

    private double length;




    public Ant() {

    }

    public boolean containsCities(city1, city2) {
        boolean answer = false;
        if (Arrays.asList(tour).contains(city1) && Arrays.asList(tour).contains(city2)) {
            answer = true;
        }
        return answer;
    }
   
    public double getLength() {
        return length;
    }

    

}