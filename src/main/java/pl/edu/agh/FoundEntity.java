package pl.edu.agh;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;

import java.util.List;

public class FoundEntity {
    private Rect boundingBox;
    private MatOfPoint2f fPoints;
    private Point position;
    private double distanceFromOrigin;
    private Point detectionPosition;
    private double EPS = 2;
    private int MAX_ITER = 50;
    private Mat currFrame;

    public Point getBoundBoxMiddlePoint() {
        return new Point(boundingBox.x+(boundingBox.width/2), boundingBox.y+(boundingBox.height/2));
    }

    public Mat getCurrFrame() {
        return currFrame;
    }

    public void setCurrFrame(Mat currFrame) {
        this.currFrame = currFrame;
    }

    public FoundEntity(Rect rect, MatOfPoint2f points, Mat currFrame) {
        boundingBox = rect;
        fPoints = points;
        position = this.calcPosition();
        detectionPosition = new Point(rect.x+(rect.width/2), rect.y+(rect.height/2));
        distanceFromOrigin = 0;
        this.currFrame = currFrame;
    }

    public void shiftBoundigBox() {
        Mat projection = new Mat(new Size(currFrame.width(), currFrame.height()), CvType.CV_8U);
        projection.setTo(new Scalar(0));
        for (Point p : fPoints.toArray()) {
            Core.circle(projection, p, 1, new Scalar(255));
        }
        Core.rectangle(projection, new Point(boundingBox.x, boundingBox.y),
                new Point(boundingBox.x + boundingBox.width, boundingBox.y+boundingBox.height), new Scalar(0));
        TermCriteria termCriteria = new TermCriteria(TermCriteria.COUNT+TermCriteria.EPS,MAX_ITER,EPS);
        Video.meanShift(projection, boundingBox, termCriteria);
    }

    public FoundEntity getNextState (MatOfPoint2f newPoints, Mat currFrame) {
        FoundEntity updatedEntity = this;
        updatedEntity.setFPoints(newPoints);
        updatedEntity.setPosition(updatedEntity.calcPosition());
        updatedEntity.setDistanceFromOrigin(updatedEntity.calcDistFromOrigin());
        updatedEntity.setCurrFrame(currFrame);
        updatedEntity.shiftBoundigBox();
        return  updatedEntity;
    }

    private Rect shiftBoxByOffset (double dX, double dY) {
        return new Rect(boundingBox.x + (int)dX, boundingBox.y + (int)dY, boundingBox.width, boundingBox.height);
    }

    private double calcDistFromOrigin() {
        return Math.hypot(position.x - detectionPosition.x, position.y -detectionPosition.y);
    }

    private Point calcPosition() {
        int avgX = 0, avgY = 0, num = fPoints.toArray().length;
        if(num == 0) num = 1;
        for (Point p : fPoints.toArray()) {
            avgX += p.x;
            avgY += p.y;
        }
        return new Point(avgX/num,avgY/num);
    }

    /**
     * Getters/setters
     */
    public Rect getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(Rect boundingBox) {
        this.boundingBox = boundingBox;
    }

    public Point getDetectionPosition() {
        return detectionPosition;
    }

    public void setDetectionPosition(Point detectionPosition) {
        this.detectionPosition = detectionPosition;
    }

    public MatOfPoint2f getFPoints() {
        return fPoints;
    }

    public void setFPoints(MatOfPoint2f fPoints) {
        this.fPoints = fPoints;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }

    public double getDistanceFromOrigin() {
        return distanceFromOrigin;
    }

    public void setDistanceFromOrigin(double distanceFromOrigin) {
        this.distanceFromOrigin = distanceFromOrigin;
    }

    private Rect findBoundingRect(List<Point> points){
        int mostLeft = 1000;
        int mostRight = 0;
        int top = 1000;
        int bottom = 0;

        for(Point point : points){
            if (point.x > mostRight) mostRight = (int)point.x;
            if (point.y > bottom) bottom = (int)point.y;
            if (point.y < top) top = (int)point.y;
            if (point.x < mostLeft) mostLeft = (int)point.x;
        }
        return new Rect(mostLeft, top, mostRight - mostLeft, bottom - top);
    }
}
