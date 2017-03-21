package gesturedetection.data;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.normalizer.Normalizer;
import gesturedetection.data.points.PointBuilderInterface;

import java.util.ArrayList;
import java.util.List;

public class DataRecorder {
    private PointBuilderInterface pointBuilder;

    private GestureData data = new GestureData();
    private List<InputPoint> currentFrame = new ArrayList<InputPoint>();
    private List<InputPoint> lastFrame = new ArrayList<InputPoint>();

    public DataRecorder(PointBuilderInterface pointBuilder) {
        this.pointBuilder = pointBuilder;
    }

    public void record(Skeleton skeletonFrame) {
        GestureFrame gestureFrame = recordOneFrame(skeletonFrame);
        data.addFrame(gestureFrame);
    }

    private GestureFrame recordOneFrame(Skeleton skeletonFrame) {
        lastFrame = currentFrame;
        currentFrame = getDataFromSkeleton(skeletonFrame);
        GestureFrame gestureFrame = new GestureFrame();
        for (InputPoint inputPoint : currentFrame) {
            gestureFrame.putGestureFrame(inputPoint.getSkeletonNode(), pointBuilder.calculate(inputPoint));
        }
        return gestureFrame;
    }

    private List<InputPoint> getDataFromSkeleton(Skeleton skeletonFrame) {
        List<InputPoint> list = new ArrayList<InputPoint>();
        for (int i = 0; i <= 20; i++) {
            if (skeletonFrame.isJointTracked(i)) {
                double[] coords = skeletonFrame.get3DJoint(i);
                InputPoint input = new InputPoint(coords[0], coords[1], coords[2], i);
                list.add(input);
            }
        }
        return list;
    }

    public GestureData getData() {
        return data;
    }

    public boolean anyJointAboveThreshold(GestureFrame noMoveFrame, Normalizer normalizer, Skeleton skeleton) {
        GestureFrame frame = recordOneFrame(skeleton);
        normalizer.normalizeFrame(frame);
        for (int i = 0; i < Constants.KINECT_JOINT_COUNT; i++) {
            if (pointAboveThreshold(frame.getJoint(i), noMoveFrame.getJoint(i))){
                return true;
            }
        }
        return false;
    }

    private boolean pointAboveThreshold(GesturePoint joint, GesturePoint noMoveJoint) {
        double deltaX = joint.x - noMoveJoint.x;
        double deltaY = joint.y - noMoveJoint.y;
        double deltaZ = joint.z - noMoveJoint.z;
        if (Math.abs(deltaX) > Constants.THRESHOLD || Math.abs(deltaY) > Constants.THRESHOLD || Math.abs(deltaZ) > Constants.THRESHOLD) {
            return true;
        }
        return false;
    }
}
