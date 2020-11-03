package pca;

import app.Constants;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

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
    private final Particle mainParticle;

    // Results
    private final List<ImmutablePair<Double, double[][]>> results = new ArrayList<>();

    // Constants
    // Limit to obstacle choosing, it takes the closest 3 particles in order to compute
    private static final int OBSTACLE_LIMIT = 3;
    private static final int K_STEEPNESS = 2;
    private static final int MAIN_PARTICLE_ID = 0;
    private static final Double[] BASE_WEIGHTS = new Double[]{0.8, 0.15, 0.05};
    private static final Vector2D[] NW = new Vector2D[]{
            new Vector2D(0, -1),
            new Vector2D(0, 1),
            new Vector2D(1, 0),
            new Vector2D(-1, 0)
    };
    private static final Supplier<List<MutablePair<Double, MutablePair<Double, Integer>>>> LIST_SUPPLIER = ArrayList::new;
    private static final double ANTICIPATION_TIME = 4;
    private static final int FORCE_MULTIPLIER = 3;
    private final double D_MIN;
    private final double D_MAX;
    private final double D_MID = 4;
    private static final double AS = 15;
    private static final Comparator<MutablePair<Double, MutablePair<Double, Integer>>> TREESET_COMPARATOR = (o1, o2) -> {
        int first = o1.left.compareTo(o2.left);
        if (first != 0){
            return first;
        }
        int second = o1.right.left.compareTo(o2.right.left);
        if (second != 0){
            return second;
        }
        return o1.right.right.compareTo(o2.right.right);
    };
    private static final int TIME_LIMIT = 150;

    public PredictiveCollisionAvoidance(double dt, double dt2, Collection<Particle> particleList, double areaHeight, double areaWidth, double safeWallDistance) {
        this.dt = dt;
        this.dt2 = dt2;
        this.totalTime = 0;

        // Add all particles to map
        this.particles = new HashMap<>(particleList.size());
        particleList.forEach(p -> this.particles.put(p.getId(), p));
        this.particleCount = particleList.size();
        this.mainParticle = this.particles.get(MAIN_PARTICLE_ID);

        // Setting constants
        D_MAX = this.particles.get(MAIN_PARTICLE_ID).getMaxSpeed() * this.particles.get(MAIN_PARTICLE_ID).getAnticipationTime();
        D_MIN = this.particles.get(MAIN_PARTICLE_ID).getComfortRadius();

        // Variables
        this.safeWallDistance = safeWallDistance;
        this.areaHeight = areaHeight;
        this.areaWidth = areaWidth;
        this.goal = new Vector2D(
                areaWidth - this.mainParticle.getComfortRadius() - this.mainParticle.getRadius() / 2,
                areaHeight / 2
        );

    }

    /////////////////////////////////////////////////////////////////////////////////////
    //                                 SIMULATION RUNNING
    /////////////////////////////////////////////////////////////////////////////////////

    public List<ImmutablePair<Double, double[][]>> simulate() {
        List<MutablePair<Double, MutablePair<Double, Integer>>> closestCollisions;
        Vector2D avoidanceForce, wallForce, goalForce;
        List<Vector2D> avoidanceManeuvers;
        int index = -1;

        while (!this.reachedGoal && this.totalTime < TIME_LIMIT) {
            // Checking if results can be stored
            index = this.checkAndStoreResults(index);

            // Computing forces
            wallForce = this.computeWallAvoidanceForce();
            goalForce = this.mainParticle.getGoalForce(this.goal);

            // Compute closest collisions
            closestCollisions = this.computeClosestParticles(wallForce, goalForce);

            // Compute avoidance maneuvers
            avoidanceManeuvers = this.computeAvoidanceManeuvers(closestCollisions);

            // Compute total avoidance force
            avoidanceForce = this.computeTotalAvoidanceForce(avoidanceManeuvers);

            // Updating the main particle
            this.updateMainParticle(avoidanceForce, wallForce, goalForce);

            // Updating the other particles
            this.updateObstacleParticles();

            // Checking if the main particle reached the goal
            this.reachedGoal = this.checkIfReachedGoal();

            // Updating the time
            this.totalTime += this.dt;
        }

        // In case the program is stuck, forcing it to stop
        if (this.totalTime < TIME_LIMIT){
            System.out.println("Forced stopped the program, time limit reached");
            System.exit(1);
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
     * @return vector with the resulting force applied to the particle
     */
    private Vector2D computeWallAvoidanceForce() {

        // Minimum distance to the walls
        final double[] dw = new double[]{
                this.areaHeight - this.mainParticle.getPosition().getY(),
                this.mainParticle.getPosition().getY(),
                this.mainParticle.getPosition().getX(),
                areaWidth - this.mainParticle.getPosition().getX()
        };

        Vector2D totalForce = Vector2D.ZERO;

        // Summing up the force each wall applies to the particle
        for (int i = 0; i < Constants.WALLS; i++) {
            Vector2D wallForce = (dw[i] - this.mainParticle.getRadius() >= this.safeWallDistance)
                    ? Vector2D.ZERO
                    : NW[i].scalarMultiply(this.getWallForceScalar(dw[i]));

            totalForce = totalForce.add(wallForce);
        }

        return totalForce;
    }

    /**
     * Calculating the wall avoidance force scalar
     * @param dw distance to the wall
     * @return double with the magnitude of the force
     */
    private double getWallForceScalar(double dw) {
        return (this.safeWallDistance + this.mainParticle.getRadius() - dw) / Math.pow(dw - this.mainParticle.getRadius(), K_STEEPNESS);
    }

    /**
     * Computes the closest particles to the main particle, computing also the desired velocity
     * @param wf Wall avoidance force
     * @param gf Goal driving force
     * @return List of pairs containing the time to collide and the particle involved
     */
    private List<MutablePair<Double, MutablePair<Double, Integer>>> computeClosestParticles(Vector2D wf, Vector2D gf) {
        // TreeSet for ordered results
        TreeSet<MutablePair<Double, MutablePair<Double, Integer>>> orderedCollisions = new TreeSet<>(TREESET_COMPARATOR);

        // Computing and storing the desired velocity
        Vector2D desiredVelocity = this.mainParticle.getVelocity().add(wf.add(gf).scalarMultiply(this.dt));
        this.mainParticle.setDesiredVelocity(desiredVelocity);

        Particle p;
        Optional<Double> col;
        double distance;
        for (int i = 1; i < this.particleCount; i++) {
            p = this.particles.get(i);

            // Calculating collisions
            col = this.mainParticle.collisionIsNear(p);

            // If the collision is present, add the pair
            if (col.isPresent()) {
                distance = p.getPosition().subtract(this.mainParticle.getPosition()).getNorm();
                orderedCollisions.add(new MutablePair<>(col.get(), new MutablePair<>(distance, i)));
            }
        }

        return orderedCollisions.stream().limit(OBSTACLE_LIMIT).collect(Collectors.toCollection(LIST_SUPPLIER));
    }

    /**
     * Computes the possible avoidance maneuvers given a list of sorted possible collisions
     *
     * @param particlesToCollide list of MutablePairs being the time in left and the particle index in right
     * @return a List of Vector2 with the force for each collision
     */
    private List<Vector2D> computeAvoidanceManeuvers(List<MutablePair<Double, MutablePair<Double, Integer>>> particlesToCollide) {
        // List of avoidance maneuver forces
        List<Vector2D> forces = new ArrayList<>(particlesToCollide.size());

        // Future positions of each particle
        Vector2D ci, cj;

        // Other particle to calculate collision
        Particle other;

        // D parameter calculated
        double d, fd;

        // Iterate each possible collision
        for (MutablePair<Double, MutablePair<Double, Integer>> col : particlesToCollide) {
            other = this.particles.get(col.right.right);

            // Calculating future positions
            ci = this.mainParticle.getDesiredVelocity().scalarMultiply(col.left).add(this.mainParticle.getPosition());
            cj = other.getVelocity().scalarMultiply(col.left).add(other.getPosition());

            // Calculating D parameter
            d = ci.subtract(this.mainParticle.getPosition()).getNorm() + (ci.subtract(cj).getNorm() - this.mainParticle.getRadius() - other.getRadius());

            // Calculating the force module
            fd = this.computeForceModule(d);
            
            // Adding the force to the list, using the direction
            forces.add(ci.subtract(cj).normalize().scalarMultiply(fd));
        }
        return forces;
    }

    /**
     * Computes the total avoidance force given a list of maneuver forces to be applied
     *
     * @param maneuvers List of forces to be applied
     * @return a total force calculated as the weighted sum
     */
    private Vector2D computeTotalAvoidanceForce(List<Vector2D> maneuvers) {
        // Computing the weights
        List<Double> weights = this.computeWeights(maneuvers.size());

        // Total force to be returned
        Vector2D totalForce = Vector2D.ZERO;

        // Compute the weighted sum
        for (int i = 0; i < maneuvers.size(); i++) {
            totalForce = totalForce.add(maneuvers.get(i).scalarMultiply(weights.get(i)));
        }

        return totalForce;
    }

    /**
     * Computes the total weights to be used, in case there are less than the limit of obstacles
     *
     * @param amountOfForces number of forces to average
     * @return List with ordered weights
     */
    private List<Double> computeWeights(int amountOfForces) {
        List<Double> values = new ArrayList<>(amountOfForces);

        // Checking if no calculations are needed
        if (amountOfForces == OBSTACLE_LIMIT) {
            values.addAll(Arrays.asList(BASE_WEIGHTS));
        } else {
            // Compute the total weight to be redistributed
            double valueToDistribute = 0;
            for (int i = amountOfForces; i < BASE_WEIGHTS.length; i++) {
                valueToDistribute += BASE_WEIGHTS[i];
            }

            // Equally redistribute among remaining
            valueToDistribute /= amountOfForces;

            // Insert new values to weights list
            for (int i = 0; i < amountOfForces; i++) {
                values.add(BASE_WEIGHTS[i] + valueToDistribute);
            }
        }
        return values;
    }

    /**
     * Updates the main particle velocity and position given the forces
     * @param af Avoidance force
     * @param wf Wall avoidance force
     * @param gf Goal driving force
     */
    private void updateMainParticle(Vector2D af, Vector2D wf, Vector2D gf) {
        // Updating the main particles velocity
        Vector2D forces = af.add(wf).add(gf);
        this.mainParticle.updateVelocity(forces, this.dt);

        // Check if we are exceeding the max velocity
        if (this.mainParticle.getVelocityNorm() > this.mainParticle.getMaxSpeed()){
            this.mainParticle.setVelocity(this.mainParticle.getVelocity().normalize().scalarMultiply(this.mainParticle.getMaxSpeed()));
        }

        // Updating main particles positions
        this.mainParticle.updatePosition(this.dt);
    }

    /**
     * Updates the position of the obstacle particles, and if necessary, it reverses the velocity on wall collision
     */
    private void updateObstacleParticles() {
        for (Particle p : this.particles.values()) {
            if (p.getId() > 0) {
                // Update the positions
                p.setPosition(p.getVelocity().scalarMultiply(this.dt).add(p.getPosition()));

                // Check top and bottom wall, if true, velocity should be reversed
                if (Math.abs(p.getPosition().getY() - this.areaHeight) < p.getRadius() || p.getPosition().getY() < p.getRadius()) {
                    p.setVelocity(p.getVelocity().scalarMultiply(-1));
                }
            }
        }
    }

    /**
     * Checks if the distance to the goal is within the main particles radius
     */
    private boolean checkIfReachedGoal() {
        double distanceToGoal = this.mainParticle.getPosition().subtract(goal).getNorm();
        return distanceToGoal <= this.mainParticle.getRadius();
    }

    /**
     * Computes the f(D) function, the parameters are fixed and can be explored with this link:
     * https://www.desmos.com/calculator/rdw4w5iizh
     * @param d D value to be used
     * @return magnitude of the force
     */
    private double computeForceModule(double d) {
        if (d < D_MIN) {
            return AS * Math.exp(FORCE_MULTIPLIER * (D_MIN - d));
        } else if (d < D_MID) {
            return AS;
        } else if (d < D_MAX) {
            return AS * (D_MAX - d)/(D_MAX - D_MID);
        } else {
            return 0;
        }
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
