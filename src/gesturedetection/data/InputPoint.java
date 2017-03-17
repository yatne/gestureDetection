package gesturedetection.data;

public class InputPoint {
    private int skeletonNode;
    private double x;
    private double y;
    private double z;

    public InputPoint(double x, double y, double z, int skeletonNode) {
        this.skeletonNode = skeletonNode;
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

    public int getSkeletonNode() {
        return skeletonNode;
    }

    public void setSkeletonNode(int skeletonNode) {
        this.skeletonNode = skeletonNode;
    }
}
