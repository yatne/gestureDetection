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
import java.util.*;

public class ThresholdFindScenario extends Scenario {
    private final int coolOffFrames = 3;
    private String outputFilePath;
    private static int fileNbr = 0;
    private BufferedWriter bwr;
    private File outputFile;
    private StringBuffer sbf;
    private List<ThresholdFindRow> rows;
    private boolean saveToFile = false;
    private boolean anyAboveThreshold = false;
    private GestureFrame noMoveFrame;
    private Map<Integer, Double> jointsAboveThreshold;
    private boolean gestureStartPing = false;
    private boolean gestureEndPing = false;
    private int noGestureFrames = 0;

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
        active = true;
        rows = new ArrayList<ThresholdFindRow>();
        jointsAboveThreshold = new HashMap<Integer, Double>();
    }

    protected void onFrame(Skeleton skeleton) {
        ThresholdFindRow row = new ThresholdFindRow();
        GestureFrame frame = recorder.recordOneFrame(skeleton);
        normalizer.normalizeFrame(frame);
        if (anyAboveThreshold) {
            if (anyJointAboveThreshold(frame, row)) {
                coolOff(-1);
                anyAboveThreshold = true;
            } else {
                if (coolOff(1)) {
                    gestureEndPing = true;
                    anyAboveThreshold = false;
                    finishGesture();
                }
            }
        } else {
            if (anyJointAboveThreshold(frame, row)) {
                gestureStartPing = true;
                anyAboveThreshold = true;
                startNewGesture();
            }
        }
        rows.add(row);
    }

    private boolean coolOff(int i) {
        if ((i > 0 && noGestureFrames <= coolOffFrames) || (i < 0 && noGestureFrames > 0)) {
            noGestureFrames = noGestureFrames + i;
        }
        //System.out.println(noGestureFrames);
        return noGestureFrames >= coolOffFrames;
    }

    public void deactivate() {
        finishGesture();
        active = false;
    }

    private void startNewGesture() {
        rows = new ArrayList<ThresholdFindRow>();
        jointsAboveThreshold = new HashMap<Integer, Double>();
    }

    private void finishGesture() {
        if (saveToFile) {
            saveToFile();
        }
    }

    private void saveToFile() {
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
        for (int i = 0; i < rows.size(); i++) {
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
            updateThresholdMap(deltaX + deltaY + deltaZ, i);
            return true;
        }
        row.setThresholdReached(false);
        return false;
    }

    private void updateThresholdMap(double value, int i) {
        if (jointsAboveThreshold.containsKey(i)) {
            jointsAboveThreshold.put(i, jointsAboveThreshold.get(i) + value);
        } else {
            jointsAboveThreshold.put(i, value);
        }
    }

    public Integer[] getMostAboveThreshold() {
        if (Constants.RECOGNITION_TYPE == 1) {
            return new Integer[]{9, 10, 11};
        } else if (Constants.RECOGNITION_TYPE == 2) {
            return new Integer[]{0, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        } else {
            return new Integer[]{0, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        }
    }

    public boolean isAnyAboveThreshold() {
        return anyAboveThreshold;
    }

    public boolean checkGestureEndPing() {
        if (gestureEndPing) {
            gestureEndPing = false;
            return true;
        } else {
            return false;
        }
    }

    public void setGestureStartPing(boolean gestureStartPing) {
        this.gestureStartPing = gestureStartPing;
    }

    public boolean checkGestureStartPing() {
        if (gestureStartPing) {
            gestureStartPing = false;
            return true;
        } else {
            return false;
        }
    }

    public void setGestureEndPing(boolean gestureEndPing) {
        this.gestureEndPing = gestureEndPing;
    }
}
