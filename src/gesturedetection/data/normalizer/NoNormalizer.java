package gesturedetection.data.normalizer;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.data.GestureData;
import gesturedetection.data.GestureFrame;
import gesturedetection.data.GesturePoint;


public class NoNormalizer extends Normalizer {
    public void init(Skeleton skeleton) {

    }

    public GesturePoint normalizePoint(GesturePoint point) {
        return point;
    }

    public GestureFrame normalizeFrame(GestureFrame frame) {
        return frame;
    }

    public GestureData normalizeData(GestureData data) {
        return data;
    }
}
