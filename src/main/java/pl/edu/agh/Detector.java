package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

public class Detector {

    private final String videoPath, xmlPath;
    private Size minSize, maxSize;
    private double scaleFactor;
    private int minNeighbors;
    private GUI gui;

    public Detector() {
        videoPath = getClass().getResource("/video.avi").getPath();
        xmlPath = getClass().getResource("/haarcascade_fullbody.xml").getPath();

        scaleFactor = 1.03;
        minNeighbors = 1;
        minSize = new Size(0, 0);
        maxSize = new Size(0, 0);

        gui = new GUI();
    }

    public void detect() {
        Mat mat = new Mat();
        VideoCapture video = new VideoCapture(new File(videoPath).getAbsolutePath());
        CascadeClassifier classifier = new CascadeClassifier(new File(xmlPath).getAbsolutePath());

        while(video.read(mat)) {
            MatOfRect found = new MatOfRect();
            classifier.detectMultiScale(mat, found, scaleFactor, minNeighbors, 0, minSize, maxSize);
            drawRectangles(mat, found);
            gui.show(mat);
        }
    }

    private void drawRectangles(Mat mat, MatOfRect found) {
        for (Rect rect:found.toArray()) {
            Core.rectangle(mat, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
        }
    }
}
