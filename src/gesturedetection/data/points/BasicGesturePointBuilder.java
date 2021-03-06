package gesturedetection.data.points;

import gesturedetection.data.GesturePoint;
import gesturedetection.data.InputPoint;

public class BasicGesturePointBuilder implements PointBuilderInterface {

    public GesturePoint calculate(InputPoint... inputPoints) {
        GesturePoint point = new GesturePoint();
        point.setX(inputPoints[0].getX());
        point.setY(inputPoints[0].getY());
        point.setZ(inputPoints[0].getZ());
        return point;
    }
}
