package pca;

import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PredictiveCollisionAvoidance {
    // Time variables
    private final double dt2;
    private final double dt;
    private double totalTime;

    // Status variables
    private boolean reachedGoal = false;

    // Particle data
    private final Map<Integer, Particle> particles;
    private final int particleCount;

    // Results
    private final List<ImmutablePair<Double, double[][]>> results = new ArrayList<>();

    // Constants
    // Limit to obstacle choosing, it takes the closest 3 particles in order to compute
    private static final int OBSTACLE_LIMIT = 3;

    public PredictiveCollisionAvoidance(double dt2, double dt, List<Particle> particleList) {
        this.dt2 = dt2;
        this.dt = dt;
        this.totalTime = 0;

        // Add all particles to map
        this.particles = new HashMap<>(particleList.size());
        particleList.forEach(p -> this.particles.put(p.getId(), p));
        this.particleCount = particleList.size();
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

            // Updating the time
            this.totalTime += this.dt;
        }

        return this.results;
    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 COMPUTATIONS
    /////////////////////////////////////////////////////////////////////////////////////

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
                particleData[j][0] = this.particles.get(j).getX();
                particleData[j][1] = this.particles.get(j).getY();
                particleData[j][2] = this.particles.get(j).getVx();
                particleData[j][3] = this.particles.get(j).getVy();
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
