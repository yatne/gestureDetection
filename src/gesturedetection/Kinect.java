package gesturedetection;

import javax.swing.JLabel;
import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.points.BasicGesturePointBuilder;
import gesturedetection.neural.Neural;
import gesturedetection.scenario.MeasureRestingPositionScenario;
import gesturedetection.scenario.Scenario;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Kinect extends J4KSDK {
    private final static String OUTPUT_FILE_PATH = "C:/studia/mgr/out/out";
    private static int fileNbr = 0;
    private Neural neural;
    private File outputFile;
    private StringBuffer sbf;
    private BufferedWriter bwr;
    ViewerPanel3D viewer = null;
    JLabel label = null;
    boolean mask_players = false;
    private boolean saveToFile = false;
    private boolean recognise = false;
    private String state = "nie wiem";
    JLabel stateLabel = null;

    DataRecorder recorder = new DataRecorder(new BasicGesturePointBuilder());
    Scenario measureRestScenario = new MeasureRestingPositionScenario(recorder);

    public void maskPlayers(boolean flag) {
        mask_players = flag;
    }

    public Kinect() {
        super();
        neural = new Neural();
    }

    public Kinect(byte type) {
        super(type);
    }

    public void setViewer(ViewerPanel3D viewer) {
        this.viewer = viewer;
    }

    public void setLabel(JLabel l) {
        this.label = l;
    }

    private boolean use_infrared = false;

    public void updateTextureUsingInfrared(boolean flag) {
        use_infrared = flag;
    }

    @Override
    public void onDepthFrameEvent(short[] depth_frame, byte[] player_index, float[] XYZ, float[] UV) {

        if (viewer == null || label == null) return;
        float a[] = getAccelerometerReading();
        label.setText(((int) (a[0] * 100) / 100f) + "," + ((int) (a[1] * 100) / 100f) + "," + ((int) (a[2] * 100) / 100f));
        DepthMap map = new DepthMap(getDepthWidth(), getDepthHeight(), XYZ);

        map.setMaximumAllowedDeltaZ(0.5);

        if (UV != null && !use_infrared) map.setUV(UV);
        else if (use_infrared) map.setUVuniform();
        if (mask_players) {
            map.setPlayerIndex(depth_frame, player_index);
            map.maskPlayers();
        }
        viewer.map = map;
    }

    @Override
    public void onSkeletonFrameEvent(boolean[] flags, float[] positions, float[] orientations, byte[] state) {
        if (viewer == null || viewer.skeletons == null) return;
        Skeleton skeleton = null;
        for (int i = 0; i < getSkeletonCountLimit(); i++) {
            skeleton = Skeleton.getSkeleton(i, flags, positions, orientations, state, this);
            if (skeleton.isTracked()) {
                //         recorder.record(skeleton);
                measureRestScenario.takeFrame(skeleton);
//                if (saveToFile && frame >= FRAME_SLEEP) {
//                    frame = 0;
//                    saveLineToFile(skeleton);
//                }
//                if (recognise && frame >= FRAME_SLEEP) {
//                    frame = 0;
//                    recognise(skeleton);
//                }
            }
            viewer.skeletons[i] = skeleton;
        }
    }

    private void recognise(Skeleton skeleton) {
        double[] in = new double[51];
        Double ell = calculateEll(skeleton);
        int ignored = 0;
        if (ell != null) {
            for (int i = 0; i < Constants.KINECT_JOINT_COUNT; i++) {
                if (i != Skeleton.SPINE_MID && i != Skeleton.FOOT_LEFT && i != Skeleton.FOOT_RIGHT) {
                    double[] jointArr = getJointCordsArray(i, skeleton, ell);
                    in[(3 * i) - (3 * ignored)] = jointArr[0];
                    in[(3 * i) + 1 - (3 * ignored)] = jointArr[1];
                    in[(3 * i) + 2 - (3 * ignored)] = jointArr[2];
                } else {
                    ignored++;
                }
            }
            double[] out = neural.getRespone(in);
            interpret(out);
        }
    }

    private void interpret(double[] out) {
        if (out[0] == 0 && out[1] == 0) {
            state = "STANIE";
        } else if (out[0] == 0 && out[1] == 1) {
            state = "T";
        } else if (out[0] == 1 && out[1] == 0) {
            state = "RECE DO GORY";
        } else if (out[0] == 1 && out[1] == 1) {
            state = "RECE POD BOKI";
        }
        stateLabel.setText(state);
    }

    private void saveLineToFile(Skeleton skeleton) {
        Double ell = calculateEll(skeleton);
        if (ell != null) {
            for (int i = 0; i <= 19; i++) {
                if (i != Skeleton.SPINE_MID && i != Skeleton.FOOT_LEFT && i != Skeleton.FOOT_RIGHT) {
                    sbf.append(getJointCords(i, skeleton, ell));
                }
            }
            sbf.append(System.getProperty("line.separator"));
        }
    }

    private Double calculateEll(Skeleton s) {
        if (s.isJointTrackedOrInferred(Skeleton.SPINE_MID)) {
            if (s.isJointTrackedOrInferred(Skeleton.SHOULDER_LEFT) && s.isJointTrackedOrInferred(Skeleton.WRIST_LEFT)) {
                return calculateDistnce(s.get3DJoint(Skeleton.SHOULDER_LEFT), s.get3DJoint(Skeleton.WRIST_LEFT));
            } else if (s.isJointTrackedOrInferred(Skeleton.SHOULDER_RIGHT) && s.isJointTrackedOrInferred(Skeleton.WRIST_RIGHT)) {
                return calculateDistnce(s.get3DJoint(Skeleton.SHOULDER_RIGHT), s.get3DJoint(Skeleton.WRIST_RIGHT));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private double[] getJointCordsArray(int i, Skeleton s, Double ell) {
        if (s.isJointTrackedOrInferred(i)) {
            return normalize(s.get3DJoint(i), s.get3DJoint(Skeleton.SPINE_MID), ell);
        } else {
            return new double[]{0, 0, 0};
        }
    }


    private String getJointCords(int i, Skeleton s, Double ell) {
        String st = "";
        if (s.isJointTrackedOrInferred(i)) {
            for (double d : normalize(s.get3DJoint(i), s.get3DJoint(Skeleton.SPINE_MID), ell)) {
                st = st + d + ",";
            }
            return st;
        } else {
            return "0,0,0,";
        }
    }

    private double[] normalize(double[] joint, double[] spineMid, double ell) {
        for (int i = 0; i <= 2; i++) {
            joint[i] = (joint[i] - spineMid[i]) / ell;
        }
        return joint;
    }

    private double calculateDistnce(double[] p1, double[] p2) {
        double dx = p1[0] - p2[0];
        double dy = p1[1] - p2[1];
        double dz = p1[2] - p2[2];
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
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

    @Override
    public void onColorFrameEvent(byte[] data) {
        if (viewer == null || viewer.videoTexture == null || use_infrared) return;
        viewer.videoTexture.update(getColorWidth(), getColorHeight(), data);
    }

    @Override
    public void onInfraredFrameEvent(short[] data) {
        if (viewer == null || viewer.videoTexture == null || !use_infrared) return;
        int sz = getInfraredWidth() * getInfraredHeight();
        byte bgra[] = new byte[sz * 4];
        int idx = 0;
        int iv = 0;
        short sv = 0;
        byte bv = 0;
        for (int i = 0; i < sz; i++) {
            sv = data[i];
            iv = sv >= 0 ? sv : 0x10000 + sv;
            bv = (byte) ((iv & 0xfff8) >> 6);
            bgra[idx] = bv;
            idx++;
            bgra[idx] = bv;
            idx++;
            bgra[idx] = bv;
            idx++;
            bgra[idx] = 0;
            idx++;
        }

        viewer.videoTexture.update(getInfraredWidth(), getInfraredHeight(), bgra);
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

    public boolean isRecognise() {
        return recognise;
    }

    public void setRecognise(boolean recognise) {
        this.recognise = recognise;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public JLabel getStateLabel() {
        return stateLabel;
    }

    public void setStateLabel(JLabel stateLabel) {
        this.stateLabel = stateLabel;
    }

    public Scenario getMeasureRestScenario() {
        return measureRestScenario;
    }

    public void setMeasureRestScenario(Scenario measureRestScenario) {
        this.measureRestScenario = measureRestScenario;
    }
}
