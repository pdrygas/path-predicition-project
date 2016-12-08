package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.ArrayList;
import java.util.List;

public class FeaturePointsTracker {
    public MatOfPoint2f trackFeaturePoints(Mat prevFrame,Mat currFrame, MatOfPoint2f prevFPoints) {
        Mat prevFrameGray = new Mat();
        Imgproc.cvtColor(prevFrame, prevFrameGray, Imgproc.COLOR_RGB2GRAY);
        Mat currFrameGray = new Mat();
        Imgproc.cvtColor(currFrame, currFrameGray, Imgproc.COLOR_RGB2GRAY);
        MatOfPoint2f newFPoints = new MatOfPoint2f();
        MatOfPoint2f newerFPoints = new MatOfPoint2f();
        MatOfByte results = new MatOfByte();
        MatOfFloat errors = new MatOfFloat();

        MatOfByte results1 = new MatOfByte();
        MatOfFloat errors1 = new MatOfFloat();
        if(prevFPoints.empty()) return new MatOfPoint2f();
        Video.calcOpticalFlowPyrLK(prevFrameGray, currFrameGray, prevFPoints, newFPoints, results, errors);
        Video.calcOpticalFlowPyrLK(currFrameGray, prevFrameGray, newFPoints, newerFPoints, results1, errors1);
        List<Point> resultList = new ArrayList<>();

        Point[] arr = newFPoints.toArray();
        for (int i = 0; i < arr.length; i++){
            if(results1.toArray()[i] == 1 && results.toArray()[i] == 1){
                resultList.add(arr[i]);
            }
        }

        MatOfPoint2f resultPoints = new MatOfPoint2f();
        resultPoints.fromList(resultList);
        return  resultPoints;
    }
}
