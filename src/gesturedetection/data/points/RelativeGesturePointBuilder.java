package gesturedetection.data.points;

import gesturedetection.data.GesturePoint;
import gesturedetection.data.InputPoint;

/**
 * Created by Carbon Studios on 15.03.2017.
 */
public class RelativeGesturePointBuilder implements PointBuilderInterface {

    public GesturePoint calculate(InputPoint... inputPoints) {
        GesturePoint point = new GesturePoint();
        point.setX(inputPoints[0].getX() - inputPoints[1].getX());
        point.setY(inputPoints[0].getY() - inputPoints[1].getY());
        point.setZ(inputPoints[0].getZ() - inputPoints[1].getZ());
        return point;
    }
}
