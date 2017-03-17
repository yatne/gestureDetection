package gesturedetection.data.points;

import gesturedetection.data.GesturePoint;
import gesturedetection.data.InputPoint;

public class DoubleRelativeGesturePointBuilder implements PointBuilderInterface {

    //1st - nowPoint, 2nd relative chest point, 3rd before point
    public GesturePoint calculate(InputPoint... inputPoints) {
        GesturePoint point1 = new GesturePoint();
        point1.setX(inputPoints[0].getX() - inputPoints[1].getX());
        point1.setY(inputPoints[0].getY() - inputPoints[1].getY());
        point1.setZ(inputPoints[0].getZ() - inputPoints[1].getZ());
        GesturePoint point2 = new GesturePoint();
        point1.setX(inputPoints[2].getX() - inputPoints[1].getX());
        point1.setY(inputPoints[2].getY() - inputPoints[1].getY());
        point1.setZ(inputPoints[2].getZ() - inputPoints[1].getZ());
        GesturePoint point = new GesturePoint();
        point1.setX(point1.getX() - point2.getX());
        point1.setY(point1.getY() - point2.getY());
        point1.setZ(point1.getZ() - point2.getZ());
        return point;
    }
}