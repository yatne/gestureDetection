package gesturedetection.data;

import java.util.ArrayList;
import java.util.List;

public class GestureData {
    private int timeInterval;
    List<GestureFrame> frames;

    public GestureData() {
        frames = new ArrayList<GestureFrame>();
    }

    public void addFrame(GestureFrame frame){
        frames.add(frame);
    }

    public int getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(int timeInterval) {
        this.timeInterval = timeInterval;
    }

    public List<GestureFrame> getFrames() {
        return frames;
    }

    public void setFrames(List<GestureFrame> frames) {
        this.frames = frames;
    }
}
