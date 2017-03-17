package gesturedetection.data;

import java.util.HashMap;

/**
 * Created by Carbon Studios on 15.03.2017.
 */
public class GestureFrame {
    private HashMap<Integer, GesturePoint> joints;

    public GestureFrame() {
        joints = new HashMap<Integer, GesturePoint>();
    }

    public void putGestureFrame(int i, GesturePoint gesturePoint) {
        joints.put(i, gesturePoint);
    }

    public HashMap<Integer, GesturePoint> getJoints() {
        return joints;
    }

    public void setJoints(HashMap<Integer, GesturePoint> joints) {
        this.joints = joints;
    }
}
