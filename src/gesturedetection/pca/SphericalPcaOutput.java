package gesturedetection.pca;

import java.util.List;

public class SphericalPcaOutput {

    private double[] jointsCoords;
    private int output;

    public SphericalPcaOutput(double[] jointsCoords, int output) {
        this.jointsCoords = jointsCoords;
        this.output = output;
    }

    public SphericalPcaOutput(List<Double> coords) {
        jointsCoords = new double[coords.size()];
        for (int i = 0; i < coords.size(); i++) {
            jointsCoords[i] = coords.get(i);
        }
    }

    public SphericalPcaOutput(List<Double> coords, int output) {
        jointsCoords = new double[coords.size()];
        for (int i = 0; i < coords.size(); i++) {
            jointsCoords[i] = coords.get(i);
        }
        this.output = output;
    }

    public double[] getJointsCoords() {
        return jointsCoords;
    }

    public void setJointsCoords(double[] jointsCoords) {
        this.jointsCoords = jointsCoords;
    }

    public int getOutput() {
        return output;
    }

    public void setOutput(int output) {
        this.output = output;
    }
}
