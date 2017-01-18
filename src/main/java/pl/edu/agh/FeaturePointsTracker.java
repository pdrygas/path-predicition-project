package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import java.util.ArrayList;
import java.util.List;

public class FeaturePointsTracker {
    public MatOfPoint2f trackFeaturePoints(Mat prevFrame,Mat currFrame,FoundEntity entity,
                                           FeaturePointsFinder finder,boolean detectNewFPoints) {
        /**
         * Convert frames to greyscale
         */
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
        Rect box = entity.getBoundingBox();
        if(box.x <=0){
            box = new Rect(1, box.y, box.width + (box.x - 1), box.height);
        }
        if(box.y <=0){
            box = new Rect(box.x, 1, box.width , box.height + (box.y - 1));
        }
        if(box.x + box.width >= currFrame.width()){
            box = new Rect(box.x, box.y, box.width - (box.x + box.width + 1 - currFrame.width()) , box.height);
        }
        if(box.y + box.height >= currFrame.height()){
            box = new Rect(box.x, box.y, box.width, box.height - (box.y + box.height + 1 - currFrame.height()));
        }
        MatOfPoint2f prevFPoints;


        if (detectNewFPoints) {
            prevFPoints = finder.findFeaturePoints(new Mat(prevFrame, box), new Point(box.x, box.y));
            /**
             * FeaturePointsFinder may return null if not enough feature points are found
             */
            if (prevFPoints == null) {
                return null;
            }
        } else {
            prevFPoints = entity.getFPoints();
        }

        /**
         * Calculating optical flow and filtering best result points using Forward-Backward Error method
         */
        Video.calcOpticalFlowPyrLK(prevFrameGray, currFrameGray, prevFPoints, newFPoints, results, errors);
        Video.calcOpticalFlowPyrLK(currFrameGray, prevFrameGray, newFPoints, newerFPoints, results1, errors1);
        List<Point> resultList = new ArrayList<>();

        Point[] arr = newFPoints.toArray();
        for (int i = 0; i < arr.length; i++) {
            if (results1.toArray()[i] == 1 && results.toArray()[i] == 1) {
                resultList.add(arr[i]);
            }
        }

        MatOfPoint2f resultPoints = new MatOfPoint2f();
        resultPoints.fromList(resultList);
        return resultPoints;
    }
}