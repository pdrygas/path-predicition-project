package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

public class FeaturePointsFinder {
    private int minCardinality;

    public FeaturePointsFinder(int threshold) {
        this.minCardinality = threshold;
    };

    public MatOfPoint2f findFeaturePoints(Mat image, Point offset) {
        Mat grayed = new Mat();
        MatOfPoint rawFPoints = new MatOfPoint();
        MatOfPoint2f resultFPoints = new MatOfPoint2f();

        /**
         * Calculating raw, relative feature points
         */
        Imgproc.cvtColor(image,grayed,Imgproc.COLOR_BGR2GRAY);
        Imgproc.goodFeaturesToTrack(grayed,rawFPoints,80,0.25,1);

        /**
         * Insufficient number of found feature points results in returning null
         */
        if (rawFPoints.toArray().length < minCardinality) {
            return null;
        }

        /**
         * Creating float equivalent of points (with coords absolute to global image)
         */
        rawFPoints.convertTo(resultFPoints, CvType.CV_32F);
        Point[] coords = resultFPoints.toArray();

        for (int i = 0; i < coords.length; i++)
        {
            coords[i].x += offset.x;
            coords[i].y += offset.y;
        }
        resultFPoints.fromArray(coords);

        return resultFPoints;
    }
}
