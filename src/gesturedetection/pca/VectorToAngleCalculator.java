package gesturedetection.pca;

import static java.lang.Math.acos;
import static java.lang.Math.atan2;
import static java.lang.Math.sqrt;

public class VectorToAngleCalculator {

    public static double[] cartToPolar(double[] vector) {
        double x = vector[0];
        double y = vector[1];
        double z = vector[2];
        double r = sqrt(x * x + y * y + z * z);
        double azimuth = atan2(y,x);
        double elevation = atan2(z, sqrt((x*x + y*y)));
        return new double[]{azimuth, elevation, r};
    }


}
