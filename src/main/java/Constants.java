public final class Constants {
    // Mass in kg
    public static final double SUN_MASS = 1988500 * Math.pow(10, 24);
    public static final double EARTH_MASS = 5.97219 * Math.pow(10, 24);
    public static final double MARS_MASS = 6.4171 * Math.pow(10, 23);
    public static final double SHIP_MASS = 500000;
    public static final double STATION_ORBITAL_VELOCITY = 7.12;
    public static final double STATION_ORBITAL_DISTANCE = 1500;
    public static final double SHIP_INITIAL_VELOCITY = 8;
    public static final int SHIP_INDEX = 3;

    // Universal Gravitation Constant
    public static final double G = 6.693 * Math.pow(10, -20);      // km^3/(kg * s^2)

    private Constants() throws Exception {
        throw new Exception("Cannot create instance of class");
    }
}