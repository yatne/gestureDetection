package gesturedetection.common;

public class Constants {

    public static final int KINECT_JOINT_COUNT = 20;
    public static final int FRAME_SLEEP = 4;
    public static final double THRESHOLD = 0.9;
    public static final int MOVING_JOINTS_COUNT = 3;

    public static String neuralFileName;

    public static int REPETITIONS;
    public static int GESTURES_COUNT;
    public static int RECOGNITION_TYPE;

    public static boolean learn = true;

    public static int getInputCount(){
        if (RECOGNITION_TYPE == 1) {
            return 6;
        } else if (RECOGNITION_TYPE == 2) {
            return 20;
        } else {
            return 38;
        }
    }
}
