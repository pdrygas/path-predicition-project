package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

public class Detector {

    private final String videoPath, xmlPath;
    private Size minSize, maxSize;
    private double scaleFactor;
    private int minNeighbors;
    private GUI gui;
    private FeaturePointsFinder finder;
    private FeaturePointsTracker tracker;
    private DataDumper dumper;
    HashMap<Integer,FoundEntity> foundEntities;

    public Detector() {
        videoPath = getClass().getResource("/video.avi").getPath();
        xmlPath = getClass().getResource("/haarcascade_fullbody.xml").getPath();
        dumper = new DataDumper("dump.txt");

        scaleFactor = 1.03;
        minNeighbors = 1;
        minSize = new Size(0, 0);
        maxSize = new Size(50, 70);
        finder = new FeaturePointsFinder(10);
        tracker = new FeaturePointsTracker();
        foundEntities = new HashMap<Integer, FoundEntity>();

        gui = new GUI();
    }

    public void detect() {
        CascadeClassifier classifier = new CascadeClassifier(new File(xmlPath).getAbsolutePath());
        VideoCapture video = new VideoCapture(new File(videoPath).getAbsolutePath());
        Mat mat = new Mat();
        Mat prevMat = new Mat();
        boolean detectionNeeded = true;
        boolean trackingEnabled = false;
        int i =0;

        while(video.read(prevMat) && video.read(mat)) {
            i++;
            if (trackingEnabled) {
                HashMap<Integer,FoundEntity> newFoundEntities = new HashMap<Integer, FoundEntity>();

                for(HashMap.Entry<Integer, FoundEntity> entry : foundEntities.entrySet()) {
                    MatOfPoint2f prevFPoints = entry.getValue().getfPoints();
                    Rect prevBoundingBox = entry.getValue().getBoundingBox();
                    MatOfPoint2f newFPoints = tracker.trackFeaturePoints(
                            prevMat,mat,prevFPoints);

                    if ((i % 19) == 0) {
                        detectionNeeded = true;
                        trackingEnabled = false;
                        break;
                    } else {
                        newFoundEntities.put(entry.getKey(),new FoundEntity(prevBoundingBox,newFPoints));
                    }
                }
                foundEntities = newFoundEntities;
            }
            if (detectionNeeded) {
                this.detectNewEntities(classifier,mat);
                detectionNeeded = false;
                trackingEnabled =true;
            }

            drawEntities(mat);
            dumper.dump(foundEntities);
            gui.show(mat);
        }
    }

    private void detectNewEntities(CascadeClassifier classifier, Mat mat) {
        /**
         * Clearing detected objects
         */
        this.foundEntities= new HashMap<Integer, FoundEntity>();

        /**
         * Detecting new objects
         */
        MatOfRect found = new MatOfRect();
        Integer key = 0;

        classifier.detectMultiScale(mat, found, scaleFactor, minNeighbors, 0, minSize, maxSize);
        for (Rect rect:found.toArray()) {
            Mat cropped = new Mat(mat, rect);

            MatOfPoint2f newFPoints = finder.findFeaturePoints(cropped,new Point(rect.x,rect.y));
            if (newFPoints != null) {
                this.foundEntities.put(key, new FoundEntity(rect,newFPoints));
                key++;
            }
        }
    }
    private void drawEntities(Mat mat) {
        for (FoundEntity entity : foundEntities.values()) {
            Arrays.stream(entity.getfPoints().toArray()).forEach(
                    p -> Core.circle(mat, new Point(p.x, p.y),5,new Scalar(200)));
        }
    }
}
