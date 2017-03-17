package gesturedetection;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.nio.Buffer;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.ufl.digitalworlds.gui.DWApp;
import edu.ufl.digitalworlds.j4k.J4K1;
import edu.ufl.digitalworlds.j4k.J4KSDK;

public class KinectViewerApp extends DWApp implements ChangeListener {
    private final static String outputFilePath = "C:/studia/mgr/out/out.csv";

    private Kinect myKinect;
    private ViewerPanel3D main_panel;
    private JComboBox depth_resolution;
    private JComboBox video_resolution;
    private JCheckBox show_video;
    private JCheckBox mask_players;
    private JLabel accelerometer;
    private JLabel state;

    private JButton captureRestAvgButon;

    public void GUIsetup(JPanel p_root) {


        if (System.getProperty("os.arch").toLowerCase().indexOf("64") < 0) {
            if (DWApp.showConfirmDialog("Performance Warning", "<html><center><br>WARNING: You are running a 32bit version of Java.<br>This may reduce significantly the performance of this application.<br>It is strongly adviced to exit this program and install a 64bit version of Java.<br><br>Do you want to exit now?</center>"))
                System.exit(0);
        }

        setLoadingProgress("Uruchamianie", 20);
        myKinect = new Kinect();


        if (!myKinect.start(Kinect.DEPTH | Kinect.COLOR | Kinect.SKELETON | Kinect.XYZ | Kinect.PLAYER_INDEX)) {
            DWApp.showErrorDialog("ERROR", "Błąd podłączenia kamery Kinect");
        }

        depth_resolution = new JComboBox();
        if (myKinect.getDeviceType() == J4KSDK.MICROSOFT_KINECT_1) {
            depth_resolution.addItem("80x60");
            depth_resolution.addItem("320x240");
            depth_resolution.addItem("640x480");
            depth_resolution.setSelectedIndex(1);
        } else if (myKinect.getDeviceType() == J4KSDK.MICROSOFT_KINECT_2) {
            depth_resolution.addItem("512x424");
            depth_resolution.setSelectedIndex(0);
        }
        depth_resolution.addActionListener(this);

        video_resolution = new JComboBox();
        if (myKinect.getDeviceType() == J4KSDK.MICROSOFT_KINECT_1) {
            video_resolution.addItem("640x480");
            video_resolution.addItem("1280x960");
            video_resolution.setSelectedIndex(0);
        } else if (myKinect.getDeviceType() == J4KSDK.MICROSOFT_KINECT_2) {
            video_resolution.addItem("1920x1080");
            video_resolution.setSelectedIndex(0);
        }


        video_resolution.addActionListener(this);

        captureRestAvgButon = new JButton("Zapisz spoczynek");
        captureRestAvgButon.setSelected(false);
        captureRestAvgButon.addActionListener(this);

        show_video = new JCheckBox("Show texture");
        show_video.setSelected(false);
        show_video.addActionListener(this);

        mask_players = new JCheckBox("Mask Players");
        mask_players.setSelected(false);
        mask_players.addActionListener(this);

        JPanel controls = new JPanel(new GridLayout(0, 6));
//
//		controls.add(depth_resolution);
//		controls.add(mask_players);
//		controls.add(near_mode);
//		controls.add(seated_skeleton);
        accelerometer = new JLabel("0,0,0");
//		controls.add(accelerometer);
        state = new JLabel("nie wiem");


//		controls.add(new JLabel("Texture Stream:"));
//		controls.add(video_resolution);
//		controls.add(show_infrared);
        controls.add(new JLabel("Wykryty gest:"));
        controls.add(state);
        controls.add(captureRestAvgButon);
//		controls.add(show_video);
//		controls.add(elevation_angle);
//
//		controls.add(turn_off);
//		controls.add(testNeuralButton);


        setLoadingProgress("Intitializing OpenGL...", 60);
        main_panel = new ViewerPanel3D();
        main_panel.setShowVideo(false);
        myKinect.setViewer(main_panel);
        myKinect.setLabel(accelerometer);
        myKinect.setStateLabel(state);

        p_root.add(main_panel, BorderLayout.CENTER);
        p_root.add(controls, BorderLayout.SOUTH);

    }

    public void GUIclosing() {
        myKinect.stop();
    }

    private void resetKinect() {

        myKinect.stop();
        if (depth_resolution.getSelectedIndex() == 0)
            myKinect.setDepthResolution(80, 60);//  depth_res=J4K1.NUI_IMAGE_RESOLUTION_80x60;
        else if (depth_resolution.getSelectedIndex() == 1)
            myKinect.setDepthResolution(320, 240);//depth_res=J4K1.NUI_IMAGE_RESOLUTION_320x240;
        else if (depth_resolution.getSelectedIndex() == 2)
            myKinect.setDepthResolution(640, 480);//depth_res=J4K1.NUI_IMAGE_RESOLUTION_640x480;
        if (video_resolution.getSelectedIndex() == 0)
            myKinect.setColorResolution(640, 480);//video_res=J4K1.NUI_IMAGE_RESOLUTION_640x480;
        else if (video_resolution.getSelectedIndex() == 1)
            myKinect.setDepthResolution(1280, 960);//video_res=J4K1.NUI_IMAGE_RESOLUTION_1280x960;

        int flags = Kinect.SKELETON;
        flags = flags | Kinect.COLOR;
        flags = flags | Kinect.DEPTH;
        flags = flags | Kinect.XYZ;
        myKinect.updateTextureUsingInfrared(false);

        myKinect.start(flags);
        if (show_video.isSelected()) myKinect.computeUV(true);
        else myKinect.computeUV(false);
    }

    public static void main(String args[]) {

        createMainFrame("Kinect Viewer App");
        app = new KinectViewerApp();
        setFrameSize(730, 570, null);
    }

    @Override
    public void GUIactionPerformed(ActionEvent e) {

        if (e.getSource() == depth_resolution) {
            resetKinect();
        } else if (e.getSource() == video_resolution) {
            resetKinect();
        } else if (e.getSource() == show_video) {
            main_panel.setShowVideo(show_video.isSelected());
            if (show_video.isSelected()) myKinect.computeUV(true);
            else myKinect.computeUV(false);
        } else if (e.getSource() == mask_players) {
            myKinect.maskPlayers(mask_players.isSelected());
        } else if (e.getSource() == captureRestAvgButon) {
            myKinect.getMeasureRestScenario().activate();
        }
    }

    public void stateChanged(ChangeEvent e) {
    }

}
