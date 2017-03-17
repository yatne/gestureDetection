package gesturedetection.scenario;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.data.DataRecorder;

/**
 * Created by Carbon Studios on 17.03.2017.
 */
public abstract class Scenario {

    protected boolean active = false;
    protected DataRecorder recorder;

    public Scenario(DataRecorder recorder) {
        this.recorder = recorder;
    }

    public void takeFrame(Skeleton skeleton) {
        if (active) {
            onFrame(skeleton);
        }
    }

    public abstract void activate();
    protected abstract void onFrame(Skeleton skeleton);
    public abstract void deactivate();

}
