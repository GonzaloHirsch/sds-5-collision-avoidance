package pca;

import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.Optional;

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
    private final double anticipationTime;

    /* Position + Velocity */
    private Vector2D position;
    private Vector2D nextPosition;
    private Vector2D velocity;
    private Vector2D desiredVelocity;

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////////

    public Particle(int id, double radius, double mass, double comfortRadius, double maxSpeed, double preferredSpeed, double pSpeedTime, double anticipationTime) {
        this.id = id;
        this.radius = radius;
        this.mass = mass;
        this.comfortRadius = comfortRadius;
        this.maxSpeed = maxSpeed;
        this.preferredSpeed = preferredSpeed;
        this.pSpeedTime = pSpeedTime;
        this.anticipationTime = anticipationTime;
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

    public double getMaxSpeed() {
        return maxSpeed;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        SETTERS
    //////////////////////////////////////////////////////////////////////////////////////////

    public void setPosition(final double x, final double y) {
        this.position = new Vector2D(x, y);
    }

    public void setPosition(Vector2D v) {
        this.position = v;
    }

    public void setVelocity(final double vx, final double vy) {
        this.velocity = new Vector2D(vx, vy);
    }

    public void setVelocity(Vector2D v) {
        this.velocity = v;
    }

    public void setDesiredVelocity(Vector2D v) {
        this.desiredVelocity = v;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        METHODS
    //////////////////////////////////////////////////////////////////////////////////////////

    public int compareTo(Particle particle) {
        return Integer.compare(id, particle.getId());
    }

    /**
     * Computes the velocity norm of the current particle
     *
     * @return Norm of the velocity vector
     */
    public double getVelocityNorm() {
        return this.velocity.getNorm();
    }

    public Vector2D getGoalForce(Vector2D goal) {
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

    /**
     * Given a time and a force, will compute the new velocity
     *
     * @param forces being applied to the particle
     * @param time how much time will pass for our prediction
     */
    public void updateVelocity(Vector2D forces, double time) {
        this.setVelocity(this.getVelocity().add(forces.scalarMultiply(time)));
    }

    /**
     * Given a time and a force, will compute the new position
     *
     * @param time how much time will pass for our prediction
     */
    public void updatePosition(double time) {
        this.setPosition(this.getPosition().add(this.getVelocity().scalarMultiply(time)));
    }


    /**
     * Tells us if there will be a collision and if it is within the anticipated time
     * @param particle to analyze with current
     * @return boolean
     */
    public Optional<Double> collisionIsNear(Particle particle) {
        // Current variables
        double xi  = this.getPosition().getX();
        double yi  = this.getPosition().getY();

        // Particle variables
        double xj  = particle.getPosition().getX();
        double yj  = particle.getPosition().getY();

        // Velocity variables
        Vector2D v = this.desiredVelocity.subtract(particle.getVelocity());
        double vx = v.getX();
        double vy = v.getY();

        double xd = xj - xi;
        double yd = yj - yi;

        // Quadratic variables
        double c = xd*xd + yd*yd - Math.pow(this.getComfortRadius() + particle.getRadius(), 2);
        double b = -1 * (2*vx*xd + 2*vy*yd);
        double a = vx*vx + vy*vy;
        double value = (b * b) - 4.0 * a * c;

        // No solution means no collision
        if (value < 0) {
            return Optional.empty();
        }

        // Solutions
        double t1 = (-b + Math.sqrt(value)) / (2.0 * a);
        double t2 = (-b - Math.sqrt(value)) / (2.0 * a);

        // One solution or both negative means no collision
        if (t1 == t2 || (t1 < 0.0 && t2 < 0.0)) {
            return Optional.empty();
        } else if (t1 * t2 < 0) {
            // If one is negative and the other one positive,
            // then a collision is imminent
            return Optional.of(0.0);
        } else {
            double min = Math.min(t1,t2);
            if (min <= this.anticipationTime){
                return Optional.of(min);
            }
            return Optional.empty();
        }
    }
}
