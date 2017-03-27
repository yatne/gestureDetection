package gesturedetection.pca;

import gesturedetection.data.GestureData;
import gesturedetection.data.GesturePoint;

public class PCACalculator {

    PrincipalComponentAnalysis pca;

    public Latent calculateLatent(GestureData data, int jointNr) {
  //      data = cutBeginningAndEnd(data);
        pca = new PrincipalComponentAnalysis();
        pca.setup(data.getFrames().size(), 3);
        for (int i=0 ; i<data.getFrames().size(); i++) {
            GesturePoint joint = data.getFrames().get(i).getJoint(jointNr);
            pca.addSample(new double[]{joint.getX(), joint.getY(), joint.getZ()});
        }
        pca.computeBasis(3);
        double[] a = pca.getBasisVector(0);
        double[] b = pca.getBasisVector(1);
        double[] c = pca.getBasisVector(2);
        return new Latent(1,2,3);
    }

    private GestureData cutBeginningAndEnd(GestureData data) {
        GestureData newGestureData = new GestureData();
        if (data.getFrames().size() > 20) {
            for (int i = 3; i < data.getFrames().size() - 3; i++) {
                newGestureData.addFrame(data.getFrames().get(i));
            }
        }
        return newGestureData;
    }

}
