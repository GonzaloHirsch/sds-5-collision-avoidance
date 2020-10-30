package pca;

import app.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictiveCollisionAvoidance {
    // Time variables
    private final double dt2;
    private final double dt;
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

    // Results
    private final List<ImmutablePair<Double, double[][]>> results = new ArrayList<>();

    // Constants
    // Limit to obstacle choosing, it takes the closest 3 particles in order to compute
    private static final int OBSTACLE_LIMIT = 3;
    private static final int K_STEEPNESS = 1;

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
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 SIMULATION RUNNING
    /////////////////////////////////////////////////////////////////////////////////////

    public List<ImmutablePair<Double, double[][]>> simulate() {
        int[] closestIndexes;
        double[] avoidanceForce;
        double[][] avoidanceManeuvers;
        int index = -1;

        while (!this.reachedGoal) {
            // Checking if results can be stored
            index = this.checkAndStoreResults(index);

            // Predict initial position
            this.computeInitialPositionPrediction();

            // Compute closest collisions
            closestIndexes = this.computeClosestParticles();

            // Compute avoidance maneuvers
            avoidanceManeuvers = this.computeAvoidanceManeuvers(closestIndexes);

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

    private int[] computeClosestParticles() {
        // TODO: GET THE CLOSEST N COLLISIONS IN ORDER TO CALCULATE
        return null;
    }

    private double[][] computeAvoidanceManeuvers(int[] particlesToCollide) {
        // TODO: COMPUTE FORCES TO AVOID EACH COLLISION
        return null;
    }

    private double[] computeTotalAvoidanceForce(double[][] maneuvers) {
        // TODO: COMPUTE TOTAL FORCE IN ORDER TO AVOID COLLISION
        return null;
    }

    private void updateMainParticle(double[] avoidanceForce) {
        // TODO: UPDATE THE MAIN PARTICLE VELOCITY
    }

    private void updateObstacleParticles() {
        // TODO: UPDATE OBSTACLE PARTICLES, IF NEAR A WALL, REVERSE THE VELOCITY IN Y
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
