public class City {
    
    private int id;

    private double xCoord;

    private double yCoord;

    public City(int id, double xCoord, double yCoord) {
        this.id = id;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public String toString() {
        String cityRep = "City: " + this.id + "Coordinates: " + "[ " + this.xCoord + "," + this.yCoord + "]";
        return cityRep;
    }

    public double getXCoord(){
        return this.xCoord;
    }

    public double getYCoord(){
        return this.yCoord;
    }

}