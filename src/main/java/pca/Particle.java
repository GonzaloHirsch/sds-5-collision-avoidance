package pca;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

public class Particle implements Comparable<Particle> {
    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        PROPERTIES
    //////////////////////////////////////////////////////////////////////////////////////////

    /* Convention, ID starts at 0 */
    private final int id;
    private final double radius;
    private final double mass;
    private final double comfortRadius;
    private final double maxSpeed;
    private final double preferredSpeed;
    private final double pSpeedTime;

    /* Position + Velocity */
    private Vector2D position;
    private Vector2D nextPosition;
    private Vector2D velocity;
    private Vector2D desiredVelocity;

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////////

    public Particle(int id, double radius, double mass, double comfortRadius, double maxSpeed, double preferredSpeed, double pSpeedTime) {
        this.id = id;
        this.radius = radius;
        this.mass = mass;
        this.comfortRadius = comfortRadius;
        this.maxSpeed = maxSpeed;
        this.preferredSpeed = preferredSpeed;
        this.pSpeedTime = pSpeedTime;
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

    public Vector2D getPosition() {
        return position;
    }

    public Vector2D getNextPosition() {
        return nextPosition;
    }

    public Vector2D getVelocity() {
        return velocity;
    }

    public Vector2D getDesiredVelocity() {
        return desiredVelocity;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        SETTERS
    //////////////////////////////////////////////////////////////////////////////////////////

    public void setPosition(final double x, final double y) {
        this.position = new Vector2D(x, y);
    }

    public void setNextPosition(final double x, final double y) {
        this.nextPosition = new Vector2D(x, y);
    }

    public void setVelocity(final double vx, final double vy) {
        this.velocity = new Vector2D(vx, vy);
    }

    public void setDesiredVelocity(final double vx, final double vy) {
        this.desiredVelocity = new Vector2D(vx, vy);
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        METHODS
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return String.format("[Particle #%d] {x = %f, y = %f, radius = %f, mass = %f}\n",
                this.id,
                this.position.getX(),
                this.position.getY(),
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
        return this.position.distance(other.getPosition());
    }

    /**
     * Computes the distance between the instance of the particle and other particle,
     * using the next position for the instance of the particle
     *
     * @param other particle to calculate the distance to
     * @return distance between the particles
     */
    public double nextDistanceTo(Particle other) {
        return this.nextPosition.distance(other.getNextPosition());
    }

    /**
     * Computes the velocity norm of the current particle
     *
     * @return Norm of the velocity vector
     */
    public double getVelocityNorm() {
        return this.velocity.getNorm();
    }

    private Vector2D getGoalForce(Vector2D goal) {
        return this.getNVector(goal)
                .scalarMultiply(this.preferredSpeed)
                .subtract(this.velocity)
                .scalarMultiply(1/this.pSpeedTime);
    }

    private Vector2D getNVector(Vector2D goal) {
        Vector2D N = goal.subtract(this.position);
        N.normalize();
        return N;
    }
}
