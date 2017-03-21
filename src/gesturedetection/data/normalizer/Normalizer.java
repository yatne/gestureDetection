package gesturedetection.data.normalizer;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.data.GestureData;
import gesturedetection.data.GestureFrame;
import gesturedetection.data.GesturePoint;

/**
 * Created by Carbon Studios on 20.03.2017.
 */
public abstract class Normalizer {

    protected boolean ellCalculated = false;

    public abstract void init(Skeleton skeleton);
    public abstract GesturePoint normalizePoint(GesturePoint point);
    public abstract GestureFrame normalizeFrame(GestureFrame frame);
    public abstract GestureData normalizeData(GestureData data);

    public boolean isEllCalculated() {
        return ellCalculated;
    }
}
