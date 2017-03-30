package gesturedetection.data.normalizer;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.GestureData;
import gesturedetection.data.GestureFrame;
import gesturedetection.data.GesturePoint;

public class EllNormalizer extends Normalizer {
    protected static double ell = 0;

    public GesturePoint normalizePoint(GesturePoint point) {
        point.setX(point.getX() / ell);
        point.setY(point.getY() / ell);
        point.setZ(point.getZ() / ell);
        return point;
    }

    public double getEll(Skeleton skeleton) {
        if (ell == 0) {
            calculateEll(skeleton);
        }
        return ell;
    }

    public void calculateEll(Skeleton skeleton) {
        if (skeleton.isJointTrackedOrInferred(Skeleton.SPINE_MID)) {
            if (skeleton.isJointTrackedOrInferred(Skeleton.ELBOW_LEFT) && skeleton.isJointTrackedOrInferred(Skeleton.WRIST_LEFT)) {
                ell = calculateDistance(skeleton.get3DJoint(Skeleton.ELBOW_LEFT), skeleton.get3DJoint(Skeleton.WRIST_LEFT));
                ellCalculated = true;
            } else if (skeleton.isJointTrackedOrInferred(Skeleton.ELBOW_RIGHT) && skeleton.isJointTrackedOrInferred(Skeleton.WRIST_RIGHT)) {
                ell = calculateDistance(skeleton.get3DJoint(Skeleton.ELBOW_RIGHT), skeleton.get3DJoint(Skeleton.WRIST_RIGHT));
                ellCalculated = true;
            }
        }
    }

    private double calculateDistance(double[] p1, double[] p2) {
        double dx = p1[0] - p2[0];
        double dy = p1[1] - p2[1];
        double dz = p1[2] - p2[2];
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public void init(Skeleton skeleton) {
        getEll(skeleton);
    }

    @Override
    public GestureFrame normalizeFrame(GestureFrame frame) {
        if (!frame.isNormalized()) {
            for (int i = 0; i < Constants.KINECT_JOINT_COUNT; i++) {
                if (frame.getJointsMap().get(i) != null) {
                    frame.getJointsMap().put(i, normalizePoint(frame.getJointsMap().get(i)));
                }
            }
            frame.setNormalized(true);
        }
        return frame;
    }

    @Override
    public GestureData normalizeData(GestureData data) {
        for (GestureFrame gestureFrame : data.getFrames()) {
            normalizeFrame(gestureFrame);
        }
        return data;
    }


}

