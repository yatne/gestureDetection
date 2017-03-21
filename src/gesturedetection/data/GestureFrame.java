package gesturedetection.data;

import java.util.HashMap;

public class GestureFrame {
    private HashMap<Integer, GesturePoint> joints;
    private boolean normalized;

    public GestureFrame() {
        joints = new HashMap<Integer, GesturePoint>();
    }

    public void putGestureFrame(int i, GesturePoint gesturePoint) {
        joints.put(i, gesturePoint);
    }

    public HashMap<Integer, GesturePoint> getJointsMap() {
        return joints;
    }

    public GesturePoint getJoint(int i) {
        if (joints.containsKey(i)) {
            return joints.get(i);
        } else {
            return new GesturePoint(0,0,0);
        }
    }

    public boolean isNormalized() {
        return normalized;
    }

    public void setNormalized(boolean normalized) {
        this.normalized = normalized;
    }

    public void setJoints(HashMap<Integer, GesturePoint> joints) {
        this.joints = joints;
    }
}
