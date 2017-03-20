package gesturedetection.scenario;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.normalizer.Normalizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Carbon Studios on 18.03.2017.
 */
public class SaveDataToFileScenario extends Scenario {
    private final static String OUTPUT_FILE_PATH = "C:/studia/mgr/out/out";
    private static int fileNbr = 0;
    private boolean saveToFile = false;
    private BufferedWriter bwr;
    private File outputFile;
    private StringBuffer sbf;

    public SaveDataToFileScenario(DataRecorder recorder, Normalizer normalizer) {
        super(recorder, normalizer);
    }

    public void activate() {

    }

    protected void onFrame(Skeleton skeleton) {

    }

    public void deactivate() {

    }
    public boolean isSaveToFile() {
        return saveToFile;
    }

    public void setSaveToFile(boolean saveToFile) {
        this.saveToFile = saveToFile;
        if (saveToFile) {
            ++fileNbr;
            outputFile = new File(OUTPUT_FILE_PATH + fileNbr + ".csv");
            sbf = new StringBuffer();
            try {
                bwr = new BufferedWriter(new FileWriter(outputFile));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void saveLineToFile(Skeleton skeleton) {
//        Double ell = normalizer.getEll(skeleton);
//        if (ell != null) {
//            for (int i = 0; i <= 19; i++) {
//                if (i != Skeleton.SPINE_MID && i != Skeleton.FOOT_LEFT && i != Skeleton.FOOT_RIGHT) {
//                    sbf.append(getJointCords(i, skeleton, ell));
//                }
//            }
//            sbf.append(System.getProperty("line.separator"));
//        }
    }

    public void endAndSave() {
        saveToFile = false;
        try {
            bwr.write(sbf.toString());
            bwr.flush();
            bwr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
