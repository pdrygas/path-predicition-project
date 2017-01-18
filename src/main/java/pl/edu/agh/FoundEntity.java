package pl.edu.agh;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class FoundEntity {
    private Rect boundingBox;
    private MatOfPoint2f fPoints;
    private Point position;
    private double distanceFromOrigin;
    private Point detectionPosition;

    public FoundEntity(Rect rect, MatOfPoint2f points) {
        boundingBox = rect;
        fPoints = points;
        position = this.calcPosition();
        detectionPosition = position;
        distanceFromOrigin = 0;
    }

    public FoundEntity getNextState (MatOfPoint2f newPoints) {
//        MatOfPoint dst = new MatOfPoint();
//        if(newPoints.toList().size() == 0) return null;
//        dst.fromList(newPoints.toList());
        //newPoints.convertTo(dst,CvType.CV_32S);
        FoundEntity updatedEntity = this;
        updatedEntity.setFPoints(newPoints);
        updatedEntity.setPosition(updatedEntity.calcPosition());

        //double xShift = updatedEntity.getPosition().x - this.position.x;
        //double yShift = updatedEntity.getPosition().y - this.position.y;

        //updatedEntity.shiftBoxByOffset(xShift,yShift);
        updatedEntity.setDistanceFromOrigin(updatedEntity.calcDistFromOrigin());
//        updatedEntity.setBoundingBox(Imgproc.boundingRect(dst));
        updatedEntity.setBoundingBox(findBoundingRect(newPoints.toList()));
        return  updatedEntity;
    }

    private void shiftBoxByOffset (double dX, double dY) {
        boundingBox.x += dX;
        boundingBox.y += dY;
    }

    private double calcDistFromOrigin() {
        return Math.hypot(position.x - detectionPosition.x, position.y -detectionPosition.y);
    }

    private Point calcPosition() {
        int avgX = 0, avgY = 0, num = fPoints.toArray().length;
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
