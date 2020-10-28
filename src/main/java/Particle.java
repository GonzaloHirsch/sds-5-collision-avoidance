public class Particle implements Comparable<Particle> {
    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        PROPERTIES
    //////////////////////////////////////////////////////////////////////////////////////////

    /* Convention, ID starts at 0 */
    private int id;
    private double radius;
    private double mass;

    /* Positions */
    private double x = 0.0;
    private double y = 0.0;
    private double prevX = 0.0;
    private double prevY = 0.0;
    private Double futureX = null;
    private Double futureY = null;

    /* Velocities */
    private double vx = 0.0;
    private double vy = 0.0;
    private double prevVx = 0.0;
    private double prevVy = 0.0;
    private Double futureVx = null;
    private Double futureVy = null;

    /* Acceleration */
    private double ax = 0.0;
    private double ay = 0.0;
    private double prevAx = 0.0;
    private double prevAy = 0.0;
    private Double futureAx = null;
    private Double futureAy = null;

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////////

    public Particle(int id, double radius, double mass) {
        this.id     = id;
        this.radius = radius;
        this.mass   = mass;
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

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getPrevX() {
        return prevX;
    }

    public double getPrevY() {
        return prevY;
    }

    public double getFutureX() {
        return futureX;
    }

    public double getFutureY() {
        return futureY;
    }

    public double getVx() {
        return vx;
    }

    public double getVy() {
        return vy;
    }

    public double getAx() {
        return ax;
    }

    public double getAy() {
        return ay;
    }

    public double getPrevAx() {
        return prevAx;
    }

    public double getPrevAy() {
        return prevAy;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        SETTERS
    //////////////////////////////////////////////////////////////////////////////////////////

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setFutureX(double x) {
        this.futureX = x;
    }

    public void setFutureY(double y) {
        this.futureY = y;
    }

    public void setVx(double vx) {
        this.vx = vx;
    }

    public void setVy(double vy) {
        this.vy = vy;
    }

    public void setFutureVx(double vx) {
        this.futureVx = vx;
    }

    public void setFutureVy(double vy) {
        this.futureVy = vy;
    }

    public void setAx(double ax) {
        this.ax = ax;
    }

    public void setAy(double ay) {
        this.ay = ay;
    }

    public void setFutureAx(double ax) {
        this.futureAx = ax;
    }

    public void setFutureAy(double ay) {
        this.futureAy = ay;
    }

    public void setPrevX(double prevX) {
        this.prevX = prevX;
    }

    public void setPrevY(double prevY) {
        this.prevY = prevY;
    }

    //////////////////////////////////////////////////////////////////////////////////////////
    //                                        METHODS
    //////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || this.getClass() != o.getClass()) return false;
        Particle particle = (Particle) o;
        return this.id == particle.getId();
    }

    @Override
    public String toString() {
        return String.format("[app.Particle #%d] {x = %f, y = %f, radius = %f, mass = %f}\n",
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
     * @param other particle to calculate the distance to
     * @return distance between the particles
     */
    public double distanceTo(Particle other){
        return Math.sqrt(Math.pow(this.x - other.x, 2) + Math.pow(this.y - other.y, 2));
    }

    /**
     * Computes the predicted distance between the instance of the particle and other particle
     * @param other particle to calculate the distance to
     * @return distance between the particles
     */
    public double predictedDistanceTo(Particle other){
        return Math.sqrt(Math.pow(this.futureX - other.futureX, 2) + Math.pow(this.futureY - other.futureY, 2));
    }

    public void setPrevValues(final double[] values) {
        this.prevAx = values[0];
        this.prevAy = values[1];
        this.prevVx = values[2];
        this.prevVy = values[3];
        this.prevX  = values[4];
        this.prevY  = values[5];
    }

    public void update() {
        /* Updating positions */
        this.prevX   = this.x;
        this.prevY   = this.y;
        this.x       = this.futureX;
        this.y       = this.futureY;
        this.futureX = null;
        this.futureY = null;

        /* Updating velocities */
        this.prevVx   = this.vx;
        this.prevVy   = this.vy;
        this.vx       = this.futureVx;
        this.vy       = this.futureVy;
        this.futureVx = null;
        this.futureVy = null;

        /* Updating accelerations */
        this.prevAx   = this.ax;
        this.prevAy   = this.ay;
        this.ax       = this.futureAx;
        this.ay       = this.futureAy;
        this.futureAx = null;
        this.futureAy = null;
    }
}
