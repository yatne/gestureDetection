package gesturedetection.data;

public class GesturePoint {
    protected double x;
    protected double y;
    protected double z;

    public GesturePoint() {
    }

    public GesturePoint(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void add(GesturePoint joint) {
        this.x = this.x + joint.getX();
        this.y = this.y + joint.getY();
        this.z = this.z + joint.getZ();
    }

    @Override
    public String toString() {
        return x + " " + y + " " + z + " ";
    }
}
