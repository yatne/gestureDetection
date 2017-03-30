package pca;

import gesturedetection.data.GestureData;
import gesturedetection.data.GestureFrame;
import gesturedetection.data.GesturePoint;
import gesturedetection.pca.PCACalculator;
import org.junit.Test;

public class PCACalculatorTest {

    @Test
    public void calculatePCATest() {
        GestureData data = createTestData();
        PCACalculator calculator = new PCACalculator();
        calculator.calculateBasicVectors(data, 1);
    }

    private GestureData createTestData() {
        GestureData data = new GestureData();
        GestureFrame frame = new GestureFrame();
        GesturePoint point1 = new GesturePoint(1, 1, 1);
        GesturePoint point2 = new GesturePoint(2, 43, 31);
        frame.putGestureFrame(1, point1);
        frame.putGestureFrame(2, point2);
        data.addFrame(frame);

        GestureFrame frame2 = new GestureFrame();
        GesturePoint point3 = new GesturePoint(1, 2, 3);
        GesturePoint point4 = new GesturePoint(21, 53, 12);
        frame2.putGestureFrame(1, point3);
        frame2.putGestureFrame(2, point4);
        data.addFrame(frame2);

        GestureFrame frame3 = new GestureFrame();
        GesturePoint point5 = new GesturePoint(1, 2, 5);
        GesturePoint point6 = new GesturePoint(23, 11, 44);
        frame3.putGestureFrame(1, point5);
        frame3.putGestureFrame(2, point6);
        data.addFrame(frame3);

        GestureFrame frame4 = new GestureFrame();
        GesturePoint point7 = new GesturePoint(2, 6, 9);
        GesturePoint point8 = new GesturePoint(65, 34, 12);
        frame4.putGestureFrame(1, point7);
        frame4.putGestureFrame(2, point8);
        data.addFrame(frame4);

        return data;
    }

}
