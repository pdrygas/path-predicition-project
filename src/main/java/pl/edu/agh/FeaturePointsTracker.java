package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.video.Video;

public class FeaturePointsTracker {
    public MatOfPoint2f trackFeaturePoints(Mat prevFrame,Mat currFrame, MatOfPoint2f prevFPoints) {
        MatOfPoint2f newFPoints = new MatOfPoint2f();
        MatOfByte results = new MatOfByte();
        MatOfFloat errors = new MatOfFloat();

        Video.calcOpticalFlowPyrLK(prevFrame, currFrame, prevFPoints, newFPoints, results, errors);

        return  newFPoints;
    }
}
