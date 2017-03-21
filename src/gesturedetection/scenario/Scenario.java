package gesturedetection.scenario;

import edu.ufl.digitalworlds.j4k.Skeleton;
import gesturedetection.data.DataRecorder;
import gesturedetection.data.normalizer.Normalizer;
import sun.text.normalizer.NormalizerImpl;

/**
 * Created by Carbon Studios on 17.03.2017.
 */
public abstract class Scenario {

    protected boolean active = false;
    protected DataRecorder recorder;
    protected Normalizer normalizer;

    public Scenario(DataRecorder recorder, Normalizer normalizer) {
        this.recorder = recorder;
        this.normalizer = normalizer;
    }

    public void takeFrame(Skeleton skeleton) {
        if (active) {
            onFrame(skeleton);
        }
    }

    public abstract void activate();
    protected abstract void onFrame(Skeleton skeleton);
    public abstract void deactivate();

    public boolean isActive() {
        return active;
    }
}
