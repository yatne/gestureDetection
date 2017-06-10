package gesturedetection.scenario;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.normalizer.Normalizer;
import gesturedetection.neural.Neural;
import gesturedetection.pca.BasicMatrixForJoint;
import gesturedetection.pca.PCACalculator;
import gesturedetection.pca.SphericalPcaOutput;
import gesturedetection.pca.VectorToAngleCalculator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CalculatePcaScenario extends Scenario {
    private String outputFilePath;
    private static int fileNbr = 0;
    private BufferedWriter bwr;
    private File outputFile;
    private StringBuffer sbf;
    private boolean saveToFile = false;
    private int repetitionMax;
    private int currentRepetition;
    private int currentGesture;
    private int gesturesMax;
    private boolean learn;
    private Neural neural;

    private PCACalculator pcaCalculator;
    private Integer[] joints;
    private List<BasicMatrixForJoint> matrices;

    public CalculatePcaScenario(DataRecorder recorder, Normalizer normalizer, PCACalculator pcaCalculator, String outputFilePath) {
        super(recorder, normalizer);
        this.pcaCalculator = pcaCalculator;
        if (outputFilePath != null) {
            saveToFile = true;
            this.outputFilePath = outputFilePath;
        }
    }

    public void activate() {
        matrices = new ArrayList<BasicMatrixForJoint>();
        this.active = true;
    }

    public String restart(boolean learn, String neuralNetworkBasePath) {
        this.repetitionMax = Constants.REPETITIONS;
        this.gesturesMax = Constants.GESTURES_COUNT;
        this.currentRepetition = 1;
        this.currentGesture = 1;
        this.learn = learn;
        if (neuralNetworkBasePath == null) {
            this.neural = new Neural();
        } else {
            this.neural = new Neural(neuralNetworkBasePath);
        }
        return neural.getNeuralNetworkFilePath();
    }

    protected void onFrame(Skeleton skeleton) {
        recorder.record(skeleton);
    }

    public void deactivate() {
        normalizer.normalizeData(recorder.getData());
        for (int i = 0; i < joints.length; i++) {
            matrices.add(pcaCalculator.calculateBasicVectors(recorder.getData(), joints[i]));
        }
        saveMatricesToFile(matrices);
        List<Double> sphericals = new ArrayList<Double>();
        for (BasicMatrixForJoint matrice : matrices) {
            double[] sph = VectorToAngleCalculator.cartToPolar(matrice.getVector(0).asArray());
            sphericals.add(sph[0]);
            sphericals.add(sph[1]);
        }
        if (this.learn) {
            if (currentRepetition <= repetitionMax) {
                SphericalPcaOutput out = new SphericalPcaOutput(sphericals, currentGesture);
                currentRepetition++;
                neural.addTrainingData(out);
            } else {
                currentGesture++;
                if (currentGesture <= gesturesMax) {
                    currentRepetition = 1;
                    SphericalPcaOutput out = new SphericalPcaOutput(sphericals, currentGesture);
                    currentRepetition++;
                    neural.addTrainingData(out);
                } else {
                    SphericalPcaOutput out = new SphericalPcaOutput(sphericals);
                    neural.train();
                    learn = false;
                    logDoubles(neural.getRespone(out.getJointsCoords()));
                }
            }
        } else {
            SphericalPcaOutput out = new SphericalPcaOutput(sphericals);
            logDoubles(neural.getRespone(out.getJointsCoords()));
        }

        recorder.destroyData();
        this.active = false;
    }

    private void logDoubles(double[] ds){
        for (double d : ds) {
            System.out.print(d);
        }
        System.out.println();
    }

    public void deactivate(Integer[] mostAboveThreshold) {
        this.joints = mostAboveThreshold;
        deactivate();
    }

    private void saveMatricesToFile(List<BasicMatrixForJoint> matrices) {
        fileNbr++;
        if (saveToFile) {
            outputFile = new File(outputFilePath + fileNbr + ".csv");
        }
        sbf = new StringBuffer();
        try {
            bwr = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (BasicMatrixForJoint matrix : matrices) {
            saveMatrixToFile(matrix);
        }

        try {
            bwr.write(sbf.toString());
            bwr.flush();
            bwr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveMatrixToFile(BasicMatrixForJoint matrix) {
        sbf.append("joint: " + matrix.getJoint() + ", matrix: [");
        sbf.append(matrix.getVector(0).getX() + ", ");
        sbf.append(matrix.getVector(1).getX() + ", ");
        sbf.append(matrix.getVector(2).getX() + "; ");
        sbf.append(matrix.getVector(0).getY() + ", ");
        sbf.append(matrix.getVector(1).getY() + ", ");
        sbf.append(matrix.getVector(2).getY() + "; ");
        sbf.append(matrix.getVector(0).getZ() + ", ");
        sbf.append(matrix.getVector(1).getZ() + ", ");
        sbf.append(matrix.getVector(2).getZ() + "] ");
        sbf.append(System.getProperty("line.separator"));
    }
}
