package gesturedetection.data;

import gesturedetection.common.Constants;

import java.util.HashMap;
import java.util.List;

public class ThresholdFindRow {
    private HashMap<Integer, Double[]> jointDistanceMap;
    private boolean thresholdReached;

    public ThresholdFindRow() {
        jointDistanceMap = new HashMap<Integer, Double[]>();
    }

    public ThresholdFindRow(HashMap<Integer, Double[]> jointDistanceMap, boolean thresholdReached) {
        this.jointDistanceMap = jointDistanceMap;
        this.thresholdReached = thresholdReached;
    }

    public void putJoint(int i, Double[] distance) {
        jointDistanceMap.put(i, distance);
    }

    public String rowToString(){
        String row ="";
        for (int i = 0; i < Constants.KINECT_JOINT_COUNT; i++) {
            if (jointDistanceMap.containsKey(i)) {
                row = row + jointDistanceMap.get(i)[0] + ", " + jointDistanceMap.get(i)[1] + ", " + jointDistanceMap.get(i)[2] + "; ";
            } else {
                row = row + "0, 0, 0; ";
            }
        }
        if (thresholdReached) {
            row = row + "true;";
        } else {
            row = row + "false;";
        }
        return row;
    }

    public boolean isThresholdReached() {
        return thresholdReached;
    }

    public void setThresholdReached(boolean thresholdReached) {
        this.thresholdReached = thresholdReached;
    }

    public HashMap<Integer, Double[]> getJointDistanceMap() {
        return jointDistanceMap;
    }

    public void setJointDistanceMap(HashMap<Integer, Double[]> jointDistanceMap) {
        this.jointDistanceMap = jointDistanceMap;
    }
}
