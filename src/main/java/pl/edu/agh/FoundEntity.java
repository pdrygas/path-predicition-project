package pl.edu.agh;

import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.*;

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
        FoundEntity updatedEntity = this;
        updatedEntity.setFPoints(newPoints);
        updatedEntity.setPosition(updatedEntity.calcPosition());

        double xShift = updatedEntity.getPosition().x - this.position.x;
        double yShift = updatedEntity.getPosition().y - this.position.y;

        updatedEntity.shiftBoxByOffset(xShift,yShift);
        updatedEntity.setDistanceFromOrigin(updatedEntity.calcDistFromOrigin());

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
}
