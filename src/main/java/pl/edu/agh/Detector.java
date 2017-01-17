package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
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
    private Rect detectionArea;
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
        detectionArea = new Rect(0, 110, 150, 275);
    }

    public void detect() {
        CascadeClassifier classifier = new CascadeClassifier(new File(xmlPath).getAbsolutePath());
        VideoCapture video = new VideoCapture(new File(videoPath).getAbsolutePath());
        Mat prevMat = new Mat();
        boolean detectionNeeded = true;
        boolean trackingEnabled = false;
        int i = 0;

        video.read(prevMat);
        while(video.grab()) {
            Mat mat = new Mat();
            video.retrieve(mat);
            mat = new Mat(mat, detectionArea);
            /**
             * Detection every n frames or when all tracked objects get lost
             */
            if (i == 30 || foundEntities.size() ==0) {
                detectionNeeded = true;
                trackingEnabled = false;
            }
            if (trackingEnabled) {
                HashMap<Integer, FoundEntity> updatedEntities = new HashMap<Integer, FoundEntity>();
                Integer key = 0;

                for(HashMap.Entry<Integer, FoundEntity> entry : foundEntities.entrySet()) {
                    /**
                     * Skipping still entities starts after n frames
                     */
                    if (i >= 15) {
                        if (entry.getValue().getDistanceFromOrigin() <= 1) {
                            continue;
                        }
                    }
                    /**
                     * Every n (2) frames new feature points are found in tracking mechanism
                     */
                    boolean detectNewFPoints = (i % 2) == 0 ? true : false;
                    
                    FoundEntity prevState = entry.getValue();
                    MatOfPoint2f newFPoints = tracker.trackFeaturePoints(
                            prevMat,mat,entry.getValue(),finder,detectNewFPoints );

                    if (newFPoints!= null) {
                        updatedEntities.put(key, prevState.getNextState(newFPoints));
                        key++;
                    }
                }
                foundEntities = updatedEntities;
            }
            if (detectionNeeded) {
                this.detectNewEntities(classifier,mat);

                /**
                 * Reseting frames counter and flags
                 */
                detectionNeeded = false;
                trackingEnabled = true;
                i = 0;
            }

            Mat resizedMat = new Mat();
            drawEntities(mat);
            Imgproc.resize(mat, resizedMat, new Size(250,600));
            gui.show(resizedMat);

            prevMat = mat;
            dumper.dump(foundEntities);
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
            Core.putText(mat,entity.getDistanceFromOrigin()+"",entity.getPosition(),
                    1,1,new Scalar(4));
        }
    }
}
