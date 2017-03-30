package gesturedetection.scenario;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.GestureFrame;
import gesturedetection.data.GesturePoint;
import gesturedetection.data.ThresholdFindRow;
import gesturedetection.data.normalizer.Normalizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ThresholdFindScenario extends Scenario {
    private String outputFilePath;
    private static int fileNbr = 0;
    private BufferedWriter bwr;
    private File outputFile;
    private StringBuffer sbf;
    private List<ThresholdFindRow> rows;
    private List<GestureFrame> frames;
    private boolean saveToFile = false;
    private boolean anyAboveThreshold = false;
    private GestureFrame noMoveFrame;

    public ThresholdFindScenario(DataRecorder recorder, Normalizer normalizer, String path) {
        super(recorder, normalizer);
        if (path != null) {
            outputFilePath = path;
            saveToFile = true;
        }
    }

    public void activate(GestureFrame noMoveFrame) {
        this.noMoveFrame = noMoveFrame;
        activate();
    }

    public void activate() {
        fileNbr++;
        if (saveToFile) {
            outputFile = new File(outputFilePath + fileNbr + ".csv");
        }
        rows = new ArrayList<ThresholdFindRow>();
        frames = new ArrayList<GestureFrame>();
        active = true;
    }


    protected void onFrame(Skeleton skeleton) {
        ThresholdFindRow row = new ThresholdFindRow();
        GestureFrame frame = recorder.recordOneFrame(skeleton);
        normalizer.normalizeFrame(frame);
        frames.add(frame);
        if (anyJointAboveThreshold(frame, row)) {
            anyAboveThreshold = true;
        } else {
            anyAboveThreshold = false;
        }
        rows.add(row);
    }

    public void deactivate() {
        saveToFile();
        active = false;
    }

    private void saveToFile() {
        sbf = new StringBuffer();
        try {
            bwr = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        for(int i = 0; i<Math.min(rows.size(),frames.size()); i++) {
            saveRowToFile(rows.get(i));
        }
        try {
            bwr.write(sbf.toString());
            bwr.flush();
            bwr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveRowToFile(ThresholdFindRow row) {
        sbf.append(row.rowToString());
        sbf.append(System.getProperty("line.separator"));
    }

    public boolean anyJointAboveThreshold(GestureFrame frame, ThresholdFindRow row) {
        for (int i = 0; i < Constants.KINECT_JOINT_COUNT; i++) {
            if (pointAboveThreshold(frame.getJoint(i), noMoveFrame.getJoint(i), row, i)) {
                return true;
            }
        }
        return false;
    }

    private boolean pointAboveThreshold(GesturePoint joint, GesturePoint noMoveJoint, ThresholdFindRow row, int i) {
        Double[] distance = new Double[3];
        double deltaX = joint.getX() - noMoveJoint.getX();
        distance[0] = deltaX;
        double deltaY = joint.getY() - noMoveJoint.getY();
        distance[1] = deltaY;
        double deltaZ = joint.getZ() - noMoveJoint.getZ();
        distance[2] = deltaZ;
        row.putJoint(i, distance);
        if (Math.abs(deltaX) > Constants.THRESHOLD || Math.abs(deltaY) > Constants.THRESHOLD || Math.abs(deltaZ) > Constants.THRESHOLD) {
            row.setThresholdReached(true);
            return true;
        }
        row.setThresholdReached(false);
        return false;
    }

    public boolean isAnyAboveThreshold() {
        return anyAboveThreshold;
    }
}
