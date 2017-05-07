package pca;

import gesturedetection.pca.VectorToAngleCalculator;
import org.junit.Test;

public class VectorToAngleTest {

    @Test
    public void testCalculation() {
        double[] cart = new double[]{0.1828,0.9376,-0.2958};
        double[] polar = VectorToAngleCalculator.cartToPolar(cart);
        System.out.println(polar[0] +" "+ polar[1] + " " + polar[2]);

        cart = new double[]{0.1828,-0.2892,-0.9397};
        polar = VectorToAngleCalculator.cartToPolar(cart);
        System.out.println(polar[0] +" "+ polar[1] + " " + polar[2]);

        cart = new double[]{0.8246,-1.4531,-0.4566};
        polar = VectorToAngleCalculator.cartToPolar(cart);
        System.out.println(polar[0] +" "+ polar[1] + " " + polar[2]);
    }
}
