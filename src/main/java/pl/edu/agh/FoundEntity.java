package pl.edu.agh;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;

public class FoundEntity {
    private Rect boundingBox;
    private MatOfPoint2f fPoints;

    public FoundEntity(Rect rect,MatOfPoint2f points) {
        boundingBox = rect;
        fPoints = points;
    }
    public MatOfPoint2f getfPoints() {
        return fPoints;
    }

    public void setfPoints(MatOfPoint2f fPoints) {
        this.fPoints = fPoints;
    }

    public Rect getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Rect boundingBox) {
        this.boundingBox = boundingBox;
    }
}
