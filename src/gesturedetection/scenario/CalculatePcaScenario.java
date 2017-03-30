package gesturedetection.scenario;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.normalizer.Normalizer;
import gesturedetection.pca.BasicMatrixForJoint;
import gesturedetection.pca.PCACalculator;

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

    public void activate(Integer[] joints) {
        this.joints = joints;
        activate();
    }

    public void activate() {
        matrices = new ArrayList<BasicMatrixForJoint>();
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
        recorder.destroyData();
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
        sbf.append(matrix.getVector(0).getX()+", ");
        sbf.append(matrix.getVector(1).getX()+", ");
        sbf.append(matrix.getVector(2).getX()+"; ");
        sbf.append(matrix.getVector(0).getY()+", ");
        sbf.append(matrix.getVector(1).getY()+", ");
        sbf.append(matrix.getVector(2).getY()+"; ");
        sbf.append(matrix.getVector(0).getZ()+", ");
        sbf.append(matrix.getVector(1).getZ()+", ");
        sbf.append(matrix.getVector(2).getZ()+"] ");
        sbf.append(System.getProperty("line.separator"));
    }
}
