package gesturedetection.data.points;

import gesturedetection.data.GesturePoint;
import gesturedetection.data.InputPoint;


public interface PointBuilderInterface {
    public  GesturePoint calculate(InputPoint... inputPoints);
}
