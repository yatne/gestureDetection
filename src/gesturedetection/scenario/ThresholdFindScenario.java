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
            if (anyJointAboveThreshold(frame,row)) {
                anyAboveThreshold = true;
            } else {
                gestureEndPing = true;
                anyAboveThreshold = false;
                finishGesture();
            }
        } else {
            if (anyJointAboveThreshold(frame,row)) {
                gestureStartPing = true;
                anyAboveThreshold = true;
                startNewGesture();
            }
        }
        rows.add(row);
    }

    public void deactivate() {
        finishGesture();
        active = false;
    }

    private void startNewGesture(){
        rows = new ArrayList<ThresholdFindRow>();
        jointsAboveThreshold = new HashMap<Integer, Double>();
    }

    private void finishGesture(){
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
        for(int i = 0; i<rows.size(); i++) {
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
        if (jointsAboveThreshold.containsKey(i)){
            jointsAboveThreshold.put(i, jointsAboveThreshold.get(i) + value);
        } else {
            jointsAboveThreshold.put(i, value);
        }
    }

    public Integer[] getMostAboveThreshold(int count) {
        Map<Integer, Double> map = jointsAboveThreshold;
        while (map.size() > count) {
            double min = 100000;
            int minId = -1;
            for (Integer integer : map.keySet()) {
                if (map.get(integer) < min) {
                    min = map.get(integer);
                    minId = integer;
                }
            }
            map.remove(minId);
        }
        return map.keySet().toArray(new Integer[jointsAboveThreshold.size()>=count?count:jointsAboveThreshold.size()]);
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
