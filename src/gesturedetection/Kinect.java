package gesturedetection;

import javax.swing.JLabel;

import edu.ufl.digitalworlds.j4k.DepthMap;
import edu.ufl.digitalworlds.j4k.J4KSDK;
import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.common.Constants;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.normalizer.EllNormalizer;
import gesturedetection.data.normalizer.NoNormalizer;
import gesturedetection.data.points.BasicGesturePointBuilder;
import gesturedetection.data.points.RelativeGesturePointBuilder;
import gesturedetection.pca.PCACalculator;
import gesturedetection.scenario.*;

public class Kinect extends J4KSDK {

    private static final String OUTPUT_PATH_1 = "C:/studia/mgr/out/full_data_";
    private static final String OUTPUT_PATH_2 = "C:/studia/mgr/out/basic_vectors_";

    ViewerPanel3D viewer = null;
    JLabel label = null;
    boolean mask_players = false;
    JLabel stateLabel = null;
    private KinectViewerApp app;

    private int frameNumber = 0;
    private int framesTaken = 0;

    private DataRecorder recorder = new DataRecorder(new RelativeGesturePointBuilder());
    private DataRecorder dataRecorder = new DataRecorder(new RelativeGesturePointBuilder());
    private EllNormalizer normalizer = new EllNormalizer();
    private MeasureRestingPositionScenario measureRestScenario = new MeasureRestingPositionScenario(recorder, normalizer);
    private SaveDataToFileScenario saveDataToFileScenario = new SaveDataToFileScenario(dataRecorder, normalizer, OUTPUT_PATH_1);
    private ThresholdFindScenario thresholdFindScenario = new ThresholdFindScenario(recorder, normalizer, OUTPUT_PATH_1);
    private PCACalculator pcaCalculator = new PCACalculator();
    private CalculatePcaScenario calculatePcaScenario = new CalculatePcaScenario(recorder, normalizer, pcaCalculator, OUTPUT_PATH_2);

    public Kinect(KinectViewerApp app) {
        super();
        this.app = app;
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
                if (frameNumber > Constants.FRAME_SLEEP) {
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
        if (measureRestScenario.isDone()) {
            thresholdFindScenario.activate(measureRestScenario.getAvgFrame());
            measureRestScenario.setDone(false);
        }
        if (thresholdFindScenario.isActive()) {
            thresholdFindScenario.takeFrame(skeleton);
            app.changeStateMsg(thresholdFindScenario.isAnyAboveThreshold()?"JEST":"NIE MA");
            if (thresholdFindScenario.checkGestureStartPing()) {
                calculatePcaScenario.activate();
                saveDataToFileScenario.activate();
            }
            if (thresholdFindScenario.checkGestureEndPing()) {
                calculatePcaScenario.deactivate(thresholdFindScenario.getMostAboveThreshold(Constants.MOVING_JOINTS_COUNT));
                saveDataToFileScenario.deactivate();
            }
            framesTaken ++;
        }
        if (saveDataToFileScenario.isActive()) {
            saveDataToFileScenario.takeFrame(skeleton);
        }
        if (calculatePcaScenario.isActive()) {
            calculatePcaScenario.takeFrame(skeleton);
        }
        app.setFramesTaken(framesTaken);
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
