package pca;

import app.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;

public class PredictiveCollisionAvoidance {
    // Time variables
    private final double dt2;
    private final double dt;
    private final double anticipationTime;
    private double totalTime;

    // Goal variables
    private boolean reachedGoal = false;
    private final Vector2D goal;
    private final double safeWallDistance;

    // Area variables
    private final double areaHeight;
    private final double areaWidth;

    // Particle data
    private final Map<Integer, Particle> particles;
    private final int particleCount;
    private final Particle mainParticle;

    // Results
    private final List<ImmutablePair<Double, double[][]>> results = new ArrayList<>();

    // Constants
    // Limit to obstacle choosing, it takes the closest 3 particles in order to compute
    private static final int OBSTACLE_LIMIT = 3;
    private static final int K_STEEPNESS = 1;
    private static final int MAIN_PARTICLE_ID = 0;
    private static final Double[] BASE_WEIGHTS = new Double[]{0.8, 0.15, 0.05};
    private static final Vector2D[] NW = new Vector2D[]{
            new Vector2D(0, -1),
            new Vector2D(0, 1),
            new Vector2D(1, 0),
            new Vector2D(-1, 0)
    };

    public PredictiveCollisionAvoidance(double dt2, double dt, List<Particle> particleList, double areaHeight, double areaWidth, double safeWallDistance) {
        this.dt2 = dt2;
        this.dt = dt;
        this.totalTime = 0;

        // Add all particles to map
        this.particles = new HashMap<>(particleList.size());
        particleList.forEach(p -> this.particles.put(p.getId(), p));
        this.particleCount = particleList.size();

        // Variables
        this.safeWallDistance = safeWallDistance;
        this.areaHeight = areaHeight;
        this.areaWidth = areaWidth;
        this.goal = new Vector2D(
                areaWidth - this.particles.get(0).getComfortRadius(),
                areaHeight - this.particles.get(0).getComfortRadius()
        );
        this.mainParticle = this.particles.get(0);
        this.anticipationTime = 1.0; // FIXME! Pass as parameter
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 SIMULATION RUNNING
    /////////////////////////////////////////////////////////////////////////////////////

    public List<ImmutablePair<Double, double[][]>> simulate() {
        List<MutablePair<Double, Integer>> closestCollisions;
        Vector2D avoidanceForce;
        List<Vector2D> avoidanceManeuvers;
        int index = -1;
        Particle mainParticle = this.particles.get(MAIN_PARTICLE_ID);

        while (!this.reachedGoal) {
            // Checking if results can be stored
            index = this.checkAndStoreResults(index);

            // Predict initial position
            this.computeInitialPositionPrediction();

            // Compute closest collisions
            closestCollisions = this.computeClosestParticles();

            // Compute avoidance maneuvers
            avoidanceManeuvers = this.computeAvoidanceManeuvers(mainParticle, closestCollisions);

            // Compute total avoidance force
            avoidanceForce = this.computeTotalAvoidanceForce(avoidanceManeuvers);

            // Updating the main particle
            this.updateMainParticle(avoidanceForce);

            // Updating the other particles
            this.updateObstacleParticles();

            this.checkIfReachedGoal();

            // Updating the time
            this.totalTime += this.dt;
        }

        return this.results;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 COMPUTATIONS
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Computes the force applied to the particles by the walls in order
     * to avoid collisions with them and remain at a safe distance.
     *
     * @param particle to analyze
     * @return vector with the resulting force applied to the particle
     */
    private Vector2D computeWallAvoidanceForce(Particle particle) {

        // Minimum distance to the walls
        final double[] dw = new double[]{
                this.areaHeight - particle.getPosition().getY(),
                particle.getPosition().getY(),
                particle.getPosition().getX(),
                areaWidth - particle.getPosition().getX()
        };

        Vector2D totalForce = Vector2D.ZERO;

        // Summing up the force each wall applies to the particle
        for (int i = 0; i < Constants.WALLS; i++) {
            Vector2D wallForce = (dw[i] - particle.getRadius() >= this.safeWallDistance)
                    ? Vector2D.ZERO
                    : NW[i].scalarMultiply(this.getWallForceScalar(particle, dw[i]));

            totalForce.add(wallForce);
        }

        return totalForce;
    }

    private double getWallForceScalar(Particle particle, double dw) {
        return (this.safeWallDistance + particle.getRadius() - dw) / Math.pow(dw - particle.getRadius(), K_STEEPNESS);
    }

    private void computeInitialPositionPrediction() {
        // TODO: CALCULATE THE INITIAL PREDICTION FOR THE MAIN PARTICLE
    }

    private List<MutablePair<Double, Integer>> computeClosestParticles() {
        // TODO: GET THE CLOSEST N COLLISIONS IN ORDER TO CALCULATE
        return null;
    }

    /**
     * Tells us if there will be a collision and if it is within the anticipated time
     * @param p particle we will use to analyze future collisions with the main particle
     * @return boolean meaning the collision will happen soon
     */
    private boolean collisionWithMainParticleIsNear(Particle p) {
        Vector2D desiredVelocity = this.computeDesiredVelocityOfMainParticle();
        return this.mainParticle.collisionIsNear(p, desiredVelocity, this.anticipationTime);
    }

    /**
     * Infer the desired velocity of the main particle as the sum of its actual velocity and
     * the velocity derived by virtually applying the goal force and the wall repulsive forces.
     * @return Vector2D of the desired velocity
     */
    private Vector2D computeDesiredVelocityOfMainParticle() {
        // Forces are the sum of the wall forces and the goal force
        Vector2D forces = this.computeWallAvoidanceForce(this.mainParticle).add(this.mainParticle.getGoalForce(this.goal));
        return this.mainParticle.getVelocity().add(forces.scalarMultiply(this.dt));
    }

    /**
     * Computes the possible avoidance maneuvers given a list of sorted possible collisions
     * @param mainParticle is the main particle that is being studied
     * @param particlesToCollide list of MutablePairs being the time in left and the particle index in right
     * @return a List of Vector2 with the force for each collision
     */
    private List<Vector2D> computeAvoidanceManeuvers(Particle mainParticle, List<MutablePair<Double, Integer>> particlesToCollide) {
        // List of avoidance maneuver forces
        List<Vector2D> forces = new ArrayList<>(particlesToCollide.size());

        // Future positions of each particle
        Vector2D ci, cj;

        // Other particle to calculate collision
        Particle other;

        // D parameter calculated
        double d;

        // Iterate each possible collision
        for (MutablePair<Double, Integer> col : particlesToCollide){
            other = this.particles.get(col.right);
            ci = mainParticle.getDesiredVelocity().scalarMultiply(col.left).add(mainParticle.getPosition());
            cj = mainParticle.getVelocity().scalarMultiply(col.left).add(other.getPosition());
            d = ci.subtract(mainParticle.getPosition()).getNorm() + (ci.subtract(cj).getNorm() - mainParticle.getRadius() - other.getRadius());
            // TODO: COMPUTE FORCE GIVEN D
        }
        return forces;
    }

    /**
     * Computes the total avoidance force given a list of maneuver forces to be applied
     * @param maneuvers List of forces to be applied
     * @return a total force calculated as the weighted sum
     */
    private Vector2D computeTotalAvoidanceForce(List<Vector2D> maneuvers) {
        // Computing the weights
        List<Double> weights = this.computeWeights(maneuvers.size());

        // Total force to be returned
        Vector2D totalForce = Vector2D.ZERO;

        // Compute the weighted sum
        for (int i = 0; i < maneuvers.size(); i++){
            totalForce.add(maneuvers.get(i).scalarMultiply(weights.get(i)));
        }

        return totalForce;
    }

    /**
     * Computes the total weights to be used, in case there are less than the limit of obstacles
     * @param amountOfForces number of forces to average
     * @return List with ordered weights
     */
    private List<Double> computeWeights(int amountOfForces){
        List<Double> values = new ArrayList<>(amountOfForces);

        // Checking if no calculations are needed
        if (amountOfForces == OBSTACLE_LIMIT){
            values.addAll(Arrays.asList(BASE_WEIGHTS));
        } else {
            // Compute the total weight to be redistributed
            double valueToDistribute = 0;
            for (int i = amountOfForces; i < BASE_WEIGHTS.length; i++){
                valueToDistribute += BASE_WEIGHTS[i];
            }

            // Equally redistribute among remaining
            valueToDistribute /= amountOfForces;

            // Insert new values to weights list
            for (int i = 0; i < amountOfForces; i++){
                values.add(BASE_WEIGHTS[i] + valueToDistribute);
            }
        }
        return values;
    }

    private void updateMainParticle(Vector2D avoidanceForce) {
        // TODO: UPDATE THE MAIN PARTICLE VELOCITY
    }

    /**
     * Updates the position of the obstacle particles, and if necessary, it reverses the velocity on wall collision
     */
    private void updateObstacleParticles() {
        Vector2D newPosition, newVelocity;
        for (Particle p :this.particles.values()){
            if (p.getId() > 0){
                // Update the positions
                p.setPosition(p.getVelocity().scalarMultiply(this.dt).add(p.getPosition()));

                // Check top and bottom wall, if true, velocity should be reversed
                if (Math.abs(p.getPosition().getY() - this.areaHeight) < p.getRadius() || p.getPosition().getY() < p.getRadius()){
                    p.setVelocity(p.getVelocity().scalarMultiply(-1));
                }
            }
        }
    }

    private void checkIfReachedGoal() {
        // TODO: CHECK IF THE MAIN PARTICLE REACHED THE GOAL
    }


    /////////////////////////////////////////////////////////////////////////////////////
    //                                 RESULT STORING
    /////////////////////////////////////////////////////////////////////////////////////

    /**
     * Checks if given the total time and the delta, results can be stored
     *
     * @param i current index of results
     * @return new current index of results
     */
    private int checkAndStoreResults(int i) {
        // Calculate the possible index to use
        int target_index = (int) Math.floor(this.totalTime / (this.dt2));
        if (target_index > i) {
            // Creating the data structure for the particles
            double[][] particleData = new double[this.particleCount][4];
            for (int j = 0; j < this.particleCount; j++) {
                particleData[j][0] = this.particles.get(j).getPosition().getX();
                particleData[j][1] = this.particles.get(j).getPosition().getY();
                particleData[j][2] = this.particles.get(j).getVelocity().getX();
                particleData[j][3] = this.particles.get(j).getVelocity().getY();
            }

            // Creating the pair for the output list
            ImmutablePair<Double, double[][]> data = new ImmutablePair<>(this.totalTime, particleData);

            // Adding our data points to the results
            this.results.add(data);

            return target_index;
        }
        return i;
    }
}
