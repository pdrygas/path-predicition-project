package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

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
    Integer key;

    public Detector(String videoFile, String dumpFile, Rect detectionArea) {
        videoPath = getClass().getResource("/" + videoFile).getPath();
        xmlPath = getClass().getResource("/haarcascade_fullbody.xml").getPath();
        dumper = new DataDumper(dumpFile);
        scaleFactor = 1.03;
        minNeighbors = 1;
        minSize = new Size(0, 0);
        maxSize = new Size(50, 70);
        finder = new FeaturePointsFinder(10);
        tracker = new FeaturePointsTracker();
        foundEntities = new HashMap<Integer, FoundEntity>();
        key = 0;
        gui = new GUI();
        this.detectionArea = detectionArea;
    }

    public void detect() {
        CascadeClassifier classifier = new CascadeClassifier(new File(xmlPath).getAbsolutePath());
        VideoCapture video = new VideoCapture(new File(videoPath).getAbsolutePath());
        Mat prevMat = new Mat();
        Mat matToShow = new Mat();
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
//                Integer key = 0;

                for(HashMap.Entry<Integer, FoundEntity> entry : foundEntities.entrySet()) {
                    Integer currentKey = entry.getKey();
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
                    boolean detectNewFPoints = (i % 2) == 0;

                    FoundEntity prevState = entry.getValue();
                    MatOfPoint2f newFPoints = tracker.trackFeaturePoints(
                            prevMat,mat,entry.getValue(),finder,false);

                    if (newFPoints != null) {
                        updatedEntities.put(currentKey, prevState.getNextState(newFPoints));
                    } else {
                        System.out.println(i);
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
            matToShow = mat;
            drawEntities(matToShow);
            Imgproc.resize(matToShow, resizedMat, new Size(250,600));
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
        //HashMap<Integer, FoundEntity> newEntities = new HashMap<Integer, FoundEntity>();

        /**
         * Detecting new objects
         */
        MatOfRect found = new MatOfRect();

        classifier.detectMultiScale(mat, found, scaleFactor, minNeighbors, 0, minSize, maxSize);
        for (Rect rect:found.toArray()) {
            Mat cropped = new Mat(mat, rect);
            Boolean isNew = true;
            Integer k = this.key;
            /**
             * If rect is overlapping with one of previously tracked entities it will replace its bounding box
             */
            for(HashMap.Entry<Integer, FoundEntity> entry : foundEntities.entrySet()){
                Rect boundingBox = entry.getValue().getBoundingBox();
                if((intersect(boundingBox, rect)).area() > 0.5 * rect.area() || (intersect(boundingBox, rect)).area() > 0.5 * boundingBox.area()){
                    k = entry.getKey();
                    isNew = false;
                }
            }

            MatOfPoint2f newFPoints = finder.findFeaturePoints(cropped, new Point(rect.x, rect.y));
            if (newFPoints != null) {
                this.foundEntities.put(k, new FoundEntity(rect, newFPoints));
                if(isNew) {
                    this.key++;
                }
            }

        }
    }
    private void drawEntities(Mat mat) {
        for (FoundEntity entity : foundEntities.values()) {
            int bbx = entity.getBoundingBox().x;
            int bby = entity.getBoundingBox().y;
            int bbh = entity.getBoundingBox().height;
            int bbw = entity.getBoundingBox().width;
            Core.rectangle(mat, new Point(bbx, bby), new Point(bbx+bbw, bby+bbh), new Scalar(100));
            Core.circle(mat, new Point(entity.getPosition().x, entity.getPosition().y), 5, new Scalar(0,255));
            Arrays.stream(entity.getFPoints().toArray()).forEach(
                    p -> {
                        Core.circle(mat, new Point(p.x, p.y), 5, new Scalar(200));
                    }
            );
            Core.putText(mat,entity.getDistanceFromOrigin()+"",entity.getPosition(),
                    1,1,new Scalar(4));
        }
    }

    private Rect intersect(Rect r1, Rect r2){
        int x = max( r1.x, r2.x );
        int y = max( r1.y, r2.y );
        int width = min(r1.x + r1.width, r2.x + r2.width) - x;
        int height = min(r1.y + r1.height, r2.y + r2.height) - y;
        if (width <= 0 || height <= 0) {
            return new Rect();
        } else {
            return new Rect(x,y,width,height);
        }
    }

    private Rect union(Rect r1, Rect r2){
        int x = min( r1.x, r2.x );
        int y = min( r1.y, r2.y );
        int width = max(r1.x + r1.width, r2.x + r2.width) - x;
        int height = max(r1.y + r1.height, r2.y + r2.height) - y;
        return new Rect(x,y,width,height);
    }

}