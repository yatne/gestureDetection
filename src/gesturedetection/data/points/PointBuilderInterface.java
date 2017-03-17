package gesturedetection.data.points;

import gesturedetection.data.GesturePoint;
import gesturedetection.data.InputPoint;

/**
 * Created by Carbon Studios on 15.03.2017.
 */
public interface PointBuilderInterface {
    public  GesturePoint calculate(InputPoint... inputPoints);
}
