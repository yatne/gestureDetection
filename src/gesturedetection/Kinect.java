package gesturedetection;

import javax.swing.JLabel;
import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.normalizer.EllNormalizer;
import gesturedetection.data.points.BasicGesturePointBuilder;
import gesturedetection.scenario.MeasureRestingPositionScenario;
import gesturedetection.scenario.SaveDataToFileScenario;
import gesturedetection.scenario.Scenario;

public class Kinect extends J4KSDK {

    ViewerPanel3D viewer = null;
    JLabel label = null;
    boolean mask_players = false;
    JLabel stateLabel = null;

    private int frameNumber = 0;

    private DataRecorder recorder = new DataRecorder(new BasicGesturePointBuilder());
    private EllNormalizer normalizer = new EllNormalizer();
    private MeasureRestingPositionScenario measureRestScenario = new MeasureRestingPositionScenario(recorder, normalizer);
    private Scenario saveDataToFileScenario = new SaveDataToFileScenario(recorder, normalizer);

    public Kinect() {
        super();
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
        Skeleton skeleton;
        for (int i = 0; i < getSkeletonCountLimit(); i++) {
            skeleton = Skeleton.getSkeleton(i, flags, positions, orientations, state, this);
            if (skeleton.isTracked()) {
                if (frameNumber > Constants.FRAME_SLEEP){
                    frameNumber = 0;
                    onTrackedSkeletonLogic(skeleton);
                } else {
                    frameNumber++;
                }
            }
            viewer.skeletons[i] = skeleton;
        }
    }

    public void onTrackedSkeletonLogic(Skeleton skeleton) {
        measureRestScenario.takeFrame(skeleton);
        if (recorder.anyJointAboveThreshold(measureRestScenario.getAvgFrame(), normalizer, skeleton)){
            saveDataToFileScenario.takeFrame(skeleton);
        } else {
            if (saveDataToFileScenario.isActive()) {
                saveDataToFileScenario.deactivate();
            }
        }
    }

    private boolean anyJointAboveThreshold(Skeleton skeleton) {
        return false;
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

    public void setStateLabel(JLabel stateLabel) {
        this.stateLabel = stateLabel;
    }

    public Scenario getMeasureRestScenario() {
        return measureRestScenario;
    }


    public Scenario getSaveDataToFileScenario() {
        return saveDataToFileScenario;
    }

}
