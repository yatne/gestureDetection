package gesturedetection.scenario;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.GestureFrame;
import gesturedetection.data.GesturePoint;
import gesturedetection.data.normalizer.Normalizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static gesturedetection.scenario.MeasureRestingPositionScenario.TIMER;

/**
 * Created by Carbon Studios on 18.03.2017.
 */
public class SaveDataToFileScenario extends Scenario {
    private final static String OUTPUT_FILE_PATH = "C:/studia/mgr/out/gesture_out";
    private static int fileNbr = 0;
    private BufferedWriter bwr;
    private File outputFile;
    private StringBuffer sbf;

    public SaveDataToFileScenario(DataRecorder recorder, Normalizer normalizer) {
        super(recorder, normalizer);
    }

    public void activate() {
        try {
            Thread.sleep(TIMER);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        fileNbr++;
        outputFile = new File(OUTPUT_FILE_PATH + fileNbr + ".csv");
        active = true;
    }

    protected void onFrame(Skeleton skeleton) {
        recorder.record(skeleton);
    }

    public void deactivate() {
        normalizer.normalizeData(recorder.getData());
        saveToFile();
        recorder.destroyData();
        active = false;
    }

    private void saveToFile() {
        sbf = new StringBuffer();
        try {
            bwr = new BufferedWriter(new FileWriter(outputFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (GestureFrame gestureFrame : recorder.getData().getFrames()) {
            saveFrameToFile(gestureFrame);
        }
        try {
            bwr.write(sbf.toString());
            bwr.flush();
            bwr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveFrameToFile(GestureFrame gestureFrame) {
        for (int i = 0; i < Constants.KINECT_JOINT_COUNT; i++) {
            GesturePoint point = gestureFrame.getJoint(i);
            sbf.append(i).append(" ").append(point.toString());
        }
        sbf.append(System.getProperty("line.separator"));
    }

}
