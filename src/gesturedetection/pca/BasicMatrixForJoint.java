package gesturedetection.pca;

public class BasicMatrixForJoint {
    private BasicVector[] vectors;
    private int joint;

    public BasicMatrixForJoint(BasicVector[] vectors) {
        this.vectors = vectors;
    }

    public BasicVector getVector(int index) {
        return vectors[index];
    }

    public BasicVector[] getVectors() {
        return vectors;
    }

    public int getJoint() {
        return joint;
    }

    public void setJoint(int joint) {
        this.joint = joint;
    }
}
