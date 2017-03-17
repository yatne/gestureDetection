package gesturedetection.data;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.data.points.PointBuilderInterface;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Carbon Studios on 15.03.2017.
 */
public class DataRecorder {
    private PointBuilderInterface pointBuilder;
    private Double normalizeLength;

    private GestureData data = new GestureData();
    private List<InputPoint> currentFrame = new ArrayList<InputPoint>();
    private List<InputPoint> lastFrame = new ArrayList<InputPoint>();

    public DataRecorder(PointBuilderInterface pointBuilder) {
        this.pointBuilder = pointBuilder;
    }

    public void record(Skeleton skeletonFrame){
        lastFrame = currentFrame;
        currentFrame = getDataFromSkeleton(skeletonFrame);
        GestureFrame gestureFrame = new GestureFrame();
        for (InputPoint inputPoint : currentFrame) {
            gestureFrame.putGestureFrame(inputPoint.getSkeletonNode(), pointBuilder.calculate(inputPoint));
        }
        data.addFrame(gestureFrame);
    }

    private List<InputPoint> getDataFromSkeleton(Skeleton skeletonFrame) {
        List<InputPoint> list = new ArrayList<InputPoint>();
        for (int i = 0; i <= 20; i++) {
            if (skeletonFrame.isJointTracked(i)) {
                double[] coords = skeletonFrame.get3DJoint(i);
                InputPoint input = new InputPoint(coords[0], coords[1], coords[2], i);
                list.add(input);
            }
        }
        return list;
    }

    public GestureData getData() {
        return data;
    }

    public void saveDataToFile(){

    }
}
