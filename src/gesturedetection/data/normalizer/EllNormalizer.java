package gesturedetection.data.normalizer;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.data.GesturePoint;

/**
 * Created by Carbon Studios on 18.03.2017.
 */
public class EllNormalizer {
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
            if (skeleton.isJointTrackedOrInferred(Skeleton.SHOULDER_LEFT) && skeleton.isJointTrackedOrInferred(Skeleton.WRIST_LEFT)) {
                ell = calculateDistance(skeleton.get3DJoint(Skeleton.SHOULDER_LEFT), skeleton.get3DJoint(Skeleton.WRIST_LEFT));
            } else if (skeleton.isJointTrackedOrInferred(Skeleton.SHOULDER_RIGHT) && skeleton.isJointTrackedOrInferred(Skeleton.WRIST_RIGHT)) {
                ell = calculateDistance(skeleton.get3DJoint(Skeleton.SHOULDER_RIGHT), skeleton.get3DJoint(Skeleton.WRIST_RIGHT));
            }
        }
    }

    private double calculateDistance(double[] p1, double[] p2) {
        double dx = p1[0] - p2[0];
        double dy = p1[1] - p2[1];
        double dz = p1[2] - p2[2];
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }
}

