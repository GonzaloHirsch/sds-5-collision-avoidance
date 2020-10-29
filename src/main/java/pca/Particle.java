package pca;

public class Particle implements Comparable<Particle> {
    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        PROPERTIES
    //////////////////////////////////////////////////////////////////////////////////////////

    /* Convention, ID starts at 0 */
    private final int id;
    private final double radius;
    private final double mass;
    private final double comfortRadius;

    /* Positions */
    public double x = 0.0;
    public double y = 0.0;
    public double nextX = 0.0;
    public double nextY = 0.0;

    /* Velocities */
    public double vx = 0.0;
    public double vy = 0.0;
    public double nextVx = 0.0;
    public double nextVy = 0.0;


    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////////

    public Particle(int id, double radius, double mass, double comfortRadius) {
        this.id = id;
        this.radius = radius;
        this.mass = mass;
        this.comfortRadius = comfortRadius;
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        GETTERS
    //////////////////////////////////////////////////////////////////////////////////////////

    public int getId() {
        return id;
    }

    public double getRadius() {
        return radius;
    }

    public double getMass() {
        return mass;
    }

    public double getComfortRadius() {
        return comfortRadius;
    }


    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        SETTERS
    //////////////////////////////////////////////////////////////////////////////////////////


    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        METHODS
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return String.format("[Particle #%d] {x = %f, y = %f, radius = %f, mass = %f}\n",
                this.id,
                this.x,
                this.y,
                this.radius,
                this.mass
        );
    }

    public int compareTo(Particle particle) {
        return Integer.compare(id, particle.getId());
    }

    /**
     * Computes the distance between the instance of the particle and other particle
     *
     * @param other particle to calculate the distance to
     * @return distance between the particles
     */
    public double distanceTo(Particle other) {
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    /**
     * Computes the distance between the instance of the particle and other particle,
     * using the next position for the instance of the particle
     *
     * @param other particle to calculate the distance to
     * @return distance between the particles
     */
    public double nextDistanceTo(Particle other) {
        return Math.sqrt(Math.pow(this.nextX - other.x, 2) + Math.pow(this.nextY - other.y, 2));
    }
}
