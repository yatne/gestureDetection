package gesturedetection.scenario;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.GestureData;
import gesturedetection.data.GestureFrame;
import gesturedetection.data.GesturePoint;

/**
 * Created by Carbon Studios on 17.03.2017.
 */
public class MeasureRestingPositionScenario extends Scenario {
    public static final int TIMER = 1000 * 5; //10sec
    public static final int FRAMES_COUNT = 100;

    private GestureData wholeData;
    private GestureFrame avgFrame;

    public int frame = 0;

    public MeasureRestingPositionScenario(DataRecorder recorder) {
        super(recorder);
    }

    public void activate() {
        try {
            Thread.sleep(TIMER);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        wholeData = new GestureData();
        avgFrame = new GestureFrame();
        this.active = true;
    }

    public void onFrame(Skeleton skeleton) {
        if (frame < FRAMES_COUNT) {
            recorder.record(skeleton);
            frame++;
        } else {
            deactivate();
            frame = 0;
        }
    }

    public void deactivate() {
        wholeData = recorder.getData();
        calculateAverage();
        this.active = false;
    }

    private void calculateAverage() {
        int[] count = new int[20];
        for (int i = 0; i < Constants.KINECT_JOINT_COUNT; i++) {
            count[i] = 0;
        }

        for (GestureFrame gestureFrame : wholeData.getFrames()) {
            for (int i = 0; i < Constants.KINECT_JOINT_COUNT; i++) {
                GesturePoint joint = gestureFrame.getJoints().get(i);
                if (joint != null) {
                    count[i]++;
                    if (avgFrame.getJoints().containsKey(i)) {
                        avgFrame.getJoints().get(i).add(joint);
                    }
                    avgFrame.getJoints().put(i,joint);
                }
            }
        }
        for (int i = 0; i < Constants.KINECT_JOINT_COUNT; i++) {
            GesturePoint avgPoint = avgFrame.getJoints().get(i);
            if (avgPoint != null) {
                avgPoint.setX(avgPoint.getX()/count[i]);
                avgPoint.setY(avgPoint.getY()/count[i]);
                avgPoint.setZ(avgPoint.getZ()/count[i]);
            }
        }
    }
}