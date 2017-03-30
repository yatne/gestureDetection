package gesturedetection.data;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.normalizer.Normalizer;
import gesturedetection.data.points.PointBuilderInterface;

import java.util.ArrayList;
import java.util.List;

public class DataRecorder {
    private int REL_JOINT = Skeleton.SPINE_MID;
    private PointBuilderInterface pointBuilder;

    private GestureData data = new GestureData();
    private List<InputPoint> currentFrame = new ArrayList<InputPoint>();
    private InputPoint chestPoint;

    public DataRecorder(PointBuilderInterface pointBuilder) {
        this.pointBuilder = pointBuilder;
    }

    public void record(Skeleton skeletonFrame) {
        GestureFrame gestureFrame = recordOneFrame(skeletonFrame);
        data.addFrame(gestureFrame);
    }

    public GestureFrame recordOneFrame(Skeleton skeletonFrame) {
        currentFrame = getDataFromSkeleton(skeletonFrame);
        GestureFrame gestureFrame = new GestureFrame();
        if (chestPoint == null) {
            double[] coords = skeletonFrame.get3DJoint(REL_JOINT);
            chestPoint = new InputPoint(coords[0], coords[1], coords[2], REL_JOINT);
        }
        for (InputPoint inputPoint : currentFrame) {
            if (inputPoint.getSkeletonNode() == REL_JOINT) {
                chestPoint = inputPoint;
            }
            gestureFrame.putGestureFrame(inputPoint.getSkeletonNode(), pointBuilder.calculate(inputPoint, chestPoint));
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

    public void destroyData(){
        this.data = new GestureData();
    }
}
