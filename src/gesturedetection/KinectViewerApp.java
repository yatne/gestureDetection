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
    private JLabel accelerometer;
    private JLabel state;

    private JButton captureRestAvgButon;

    public void GUIsetup(JPanel p_root) {
        setLoadingProgress("Uruchamianie", 20);
        myKinect = new Kinect();

        if (!myKinect.start(Kinect.DEPTH | Kinect.COLOR | Kinect.SKELETON | Kinect.XYZ | Kinect.PLAYER_INDEX)) {
            DWApp.showErrorDialog("ERROR", "Błąd podłączenia kamery Kinect");
        }

        captureRestAvgButon = new JButton("Kalibracja stanu spoczynkowego");
        captureRestAvgButon.setSelected(false);
        captureRestAvgButon.addActionListener(this);

        JPanel controls = new JPanel(new GridLayout(0, 6));

        accelerometer = new JLabel("0,0,0");
        state = new JLabel("nie wiem");

        controls.add(new JLabel("Wykryty gest:"));
        controls.add(state);
        controls.add(captureRestAvgButon);

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
        int flags = Kinect.SKELETON;
        flags = flags | Kinect.COLOR;
        flags = flags | Kinect.DEPTH;
        flags = flags | Kinect.XYZ;
        myKinect.updateTextureUsingInfrared(false);
        myKinect.start(flags);
        myKinect.computeUV(false);
    }

    public static void main(String args[]) {

        createMainFrame("Kinect Viewer App");
        app = new KinectViewerApp();
        setFrameSize(730, 570, null);
    }

    @Override
    public void GUIactionPerformed(ActionEvent e) {

       if (e.getSource() == captureRestAvgButon) {
            myKinect.getMeasureRestScenario().activate();
        }
    }

    public void stateChanged(ChangeEvent e) {
    }

}
