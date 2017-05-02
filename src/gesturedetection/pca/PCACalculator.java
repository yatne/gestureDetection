package gesturedetection.pca;

import gesturedetection.data.GestureData;
import gesturedetection.data.GesturePoint;

public class PCACalculator {

    private static final int REMOVED_FRAMES_COUNT = 1;
    private PrincipalComponentAnalysis pca;

    public BasicMatrixForJoint calculateBasicVectors(GestureData data, int jointNr) {
        BasicVector[] vectors = new BasicVector[3];
        data = cutBeginningAndEnd(data);
        pca = new PrincipalComponentAnalysis();
        pca.setup(data.getFrames().size(), 3);
        for (int i = 0; i < data.getFrames().size(); i++) {
            GesturePoint joint = data.getFrames().get(i).getJoint(jointNr);
            pca.addSample(new double[]{joint.getX(), joint.getY(), joint.getZ()});
        }
        pca.computeBasis(3);
        vectors[0] = new BasicVector(pca.getBasisVector(0)[0], pca.getBasisVector(0)[1], pca.getBasisVector(0)[2]);
        vectors[1] = new BasicVector(pca.getBasisVector(1)[0], pca.getBasisVector(1)[1], pca.getBasisVector(1)[2]);
        vectors[2] = new BasicVector(pca.getBasisVector(2)[0], pca.getBasisVector(2)[1], pca.getBasisVector(2)[2]);
        BasicMatrixForJoint matrix = new BasicMatrixForJoint(vectors);
        matrix.setJoint(jointNr);
        return matrix;
    }

    private GestureData cutBeginningAndEnd(GestureData data) {
        GestureData newGestureData = new GestureData();
        if (data.getFrames().size() > 20) {
            for (int i = REMOVED_FRAMES_COUNT; i < data.getFrames().size() - REMOVED_FRAMES_COUNT; i++) {
                newGestureData.addFrame(data.getFrames().get(i));
            }
            return newGestureData;
        }
        return data;
    }

}
