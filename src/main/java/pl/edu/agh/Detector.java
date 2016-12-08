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
        finder = new FeaturePointsFinder(7);
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
        boolean stillObjectsRemovalEnabled = false;
        int i =0;

        while(video.read(prevMat) && video.read(mat)) {
            if (i == 70 || foundEntities.size()==0) {
                detectionNeeded = true;
                trackingEnabled = false;
            }
            if (trackingEnabled) {
                HashMap<Integer, FoundEntity> updatedEntities = new HashMap<Integer, FoundEntity>();
                Integer key = 0;

                for(HashMap.Entry<Integer, FoundEntity> entry : foundEntities.entrySet()) {
                    if (i >= 30) {
                        if (entry.getValue().getDistanceFromOrigin() <= 1) {
                            continue;
                        }
                    }
                    FoundEntity prevState = entry.getValue();
                    boolean t = (i % 5) == 0 ? true : false;
                    MatOfPoint2f newFPoints = tracker.trackFeaturePoints(
                            prevMat,mat,entry.getValue(),finder,t);
                    if (newFPoints!= null) {
                        updatedEntities.put(key, prevState.getNextState(newFPoints));
                        key++;
                    }
                }

                foundEntities = updatedEntities;
            }
            if (detectionNeeded) {
                this.detectNewEntities(classifier,mat);
                detectionNeeded = false;
                trackingEnabled = true;
                i = 0;
            }

            drawEntities(mat);
            dumper.dump(foundEntities);
            gui.show(mat);
            i++;
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
            Arrays.stream(entity.getFPoints().toArray()).forEach(
                    p -> {
                        Core.circle(mat, new Point(p.x, p.y), 5, new Scalar(200));
                    }
            );
            Core.putText(mat,entity.getDistanceFromOrigin()+"",entity.getPosition(),1,1,new Scalar(4));
        }
    }
}
