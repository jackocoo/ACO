public class City {

    /**
     * Integer identification instance of a City object.
     */
    private int id;

    /**
     * The xCoordinate of the City.
     */
    private double xCoord;

    /**
     * The yCoordinate of the City.
     */
    private double yCoord;

    /**
     * Constructor for the City object.
     */
    public City(int id, double xCoord, double yCoord) {
        this.id = id;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    /**
     * toString method.
     * @return String representation of the City.
     */
    public String toString() {
        String cityRep = "City: " + this.id + "Coordinates: " + "[ " + this.xCoord + "," + this.yCoord + "]";
        return cityRep;
    }

    /**
     * xCoord getter.
     * @return the xCoord of the City object.
     */
    public double getXCoord() {
        return this.xCoord;
    }

    /**
     * yCoord getter.
     * @return the xCoord of the City object.
     */
    public double getYCoord() {
        return this.yCoord;
    }

    /**
     * id getter.
     * @return the yCoord of the City object.
     */
    public int getCityId() {
        return this.id;
    }

}