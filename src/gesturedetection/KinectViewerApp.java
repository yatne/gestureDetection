package gesturedetection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.ufl.digitalworlds.gui.DWApp;
import gesturedetection.common.Constants;
import gesturedetection.gui.NumberTextBox;

public class KinectViewerApp extends DWApp implements ChangeListener {
    private final static String outputFilePath = "C:/studia/mgr/out/out.csv";

    private Kinect myKinect;
    private ViewerPanel3D main_panel;
    private JLabel accelerometer;
    private JLabel preState;
    private JLabel state;
    private JLabel framesCunter;

    private JButton learnGesturesButton;
    private JButton recogniseGesturesButton;

    private NumberTextBox recordGesturesCount;
    private NumberTextBox repetitionCount;

    private JTextField neuralPathField;
    private JButton loadNeuralButton;

    private JButton captureRestAvgButon;
    private JButton saveDataToFileButton;

    JRadioButton oneHandRadio;
    JRadioButton upperHalfRadio;
    JRadioButton wholeBodyRadio;

    public void GUIsetup(JPanel p_root) {
        setLoadingProgress("Uruchamianie", 20);
        myKinect = new Kinect(this);

        if (!myKinect.start(Kinect.DEPTH | Kinect.COLOR | Kinect.SKELETON | Kinect.XYZ | Kinect.PLAYER_INDEX)) {
            DWApp.showErrorDialog("ERROR", "Błąd podłączenia kamery Kinect");
        }

        captureRestAvgButon = new JButton("Kalibracja stanu spoczynkowego");
        captureRestAvgButon.setSelected(false);
        captureRestAvgButon.addActionListener(this);

        saveDataToFileButton = new JButton("Zapis do pliku");
        saveDataToFileButton.setSelected(false);
        saveDataToFileButton.addActionListener(this);

        loadNeuralButton = new JButton("Wczytaj sieć");
        loadNeuralButton.setSelected(false);
        loadNeuralButton.addActionListener(this);

        learnGesturesButton = new JButton("Nauczaj gestów");
        learnGesturesButton.setSelected(false);
        learnGesturesButton.addActionListener(this);

        recogniseGesturesButton = new JButton("Rozpoznawaj gesty");
        recogniseGesturesButton.setSelected(false);
        recogniseGesturesButton.addActionListener(this);

        recordGesturesCount = new NumberTextBox();
        recordGesturesCount.setText("3");
        repetitionCount = new NumberTextBox();
        repetitionCount.setText("5");
        neuralPathField = new JTextField();

        oneHandRadio = new JRadioButton("jedna ręka");
        oneHandRadio.setSelected(true);
        oneHandRadio.addActionListener(this);
        upperHalfRadio = new JRadioButton("tułów");
        upperHalfRadio.addActionListener(this);
        wholeBodyRadio = new JRadioButton("całe ciało");
        wholeBodyRadio.addActionListener(this);

        JPanel controls = new JPanel(new GridLayout(0, 2));

        accelerometer = new JLabel("0,0,0");
        preState = new JLabel("Wykryty gest:");
        state = new JLabel("Przekalibruj!");
        framesCunter = new JLabel("0");


        JPanel infoPanel = new JPanel(new GridLayout(1, 2));
        controls.add(infoPanel);
        infoPanel.add(preState);
        infoPanel.add(state);

        JPanel recognitionTypePanel = new JPanel(new GridLayout(1, 3));
        controls.add(recognitionTypePanel);
        recognitionTypePanel.add(oneHandRadio);
        recognitionTypePanel.add(upperHalfRadio);
        recognitionTypePanel.add(wholeBodyRadio);

        //controls.add(framesCunter);
        //controls.add(captureRestAvgButon);
        //controls.add(saveDataToFileButton);

        JPanel countsPanel = new JPanel(new GridLayout(2, 2));
        controls.add(countsPanel);
        countsPanel.add(new JLabel("ilość gestów"));
        countsPanel.add(recordGesturesCount);
        countsPanel.add(new JLabel("ilość powtórzeń"));
        countsPanel.add(repetitionCount);

        JPanel neuralPanel = new JPanel(new GridLayout(2, 2));
        controls.add(neuralPanel);
        neuralPanel.add(new JLabel("ścieżka do sieci"));
        neuralPanel.add(neuralPathField);
        neuralPanel.add(new JLabel(""));
        neuralPanel.add(loadNeuralButton);

        controls.add(learnGesturesButton);
        controls.add(recogniseGesturesButton);

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
        if (e.getSource() == oneHandRadio || e.getSource() == upperHalfRadio || e.getSource() == wholeBodyRadio) {
            handleRadios(e.getSource());
        }
        if(upperHalfRadio.isSelected()) {
            Constants.RECOGNITION_TYPE = 2;
        } else if (wholeBodyRadio.isSelected()){
            Constants.RECOGNITION_TYPE = 3;
        } else {
            Constants.RECOGNITION_TYPE = 1;
        }
        Constants.REPETITIONS = Integer.parseInt(repetitionCount.getText());
        Constants.GESTURES_COUNT = Integer.parseInt(recordGesturesCount.getText());
        if (e.getSource() == captureRestAvgButon) {
            myKinect.getMeasureRestScenario().activate();
        }
        if (e.getSource() == saveDataToFileButton) {
            if (myKinect.getSaveDataToFileScenario().isActive()) {
                myKinect.getSaveDataToFileScenario().deactivate();
            }
            myKinect.getSaveDataToFileScenario().activate();
        }

    }

    private void handleRadios(Object o) {
        if (o == oneHandRadio) {
            upperHalfRadio.setSelected(false);
            wholeBodyRadio.setSelected(false);
        } else if (o == upperHalfRadio) {
            oneHandRadio.setSelected(false);
            wholeBodyRadio.setSelected(false);
        } else if (o == wholeBodyRadio) {
            oneHandRadio.setSelected(false);
            upperHalfRadio.setSelected(false);
        }
    }

    public void setFramesTaken(int framesTaken) {
        this.framesCunter.setText(String.valueOf(framesTaken));
    }

    public void stateChanged(ChangeEvent e) {
    }

    public void changeStateMsg(String msg) {
        state.setText(msg);
    }

    public void changePreStateMsg(String msg) {
        preState.setText(msg);
    }

    public void changeInfoMsg(String pre, String state) {
        this.state.setText(state);
        this.preState.setText(pre);
    }

}
