package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.video.Video;

public class FeaturePointsTracker {
    public MatOfPoint2f trackFeaturePoints(Mat prevFrame,Mat currFrame, FoundEntity entity,FeaturePointsFinder finder, boolean t) {
        Rect box = entity.getBoundingBox();
        MatOfPoint2f prevFPoints;
        if (t) {
            prevFPoints = finder.findFeaturePoints(new Mat(prevFrame, box), new Point(box.x, box.y));
        } else {
            prevFPoints = entity.getFPoints();
        }
        MatOfPoint2f newFPoints = new MatOfPoint2f();
        MatOfByte results = new MatOfByte();
        MatOfFloat errors = new MatOfFloat();

        if (prevFPoints == null) {
            return null;
        }
        Video.calcOpticalFlowPyrLK(prevFrame, currFrame, prevFPoints, newFPoints, results, errors);

        return  newFPoints;
    }
}
