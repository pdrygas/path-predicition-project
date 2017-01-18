package pl.edu.agh;

import org.opencv.core.Core;
import org.opencv.core.Rect;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        String videoFile = "video.avi";
        String dumpFile = "dump.txt";
        Rect detectionArea = new Rect(0, 110, 150, 275);

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Detector detector = new Detector(videoFile, dumpFile, detectionArea);
        detector.detect();

        HeatMap heatMap = new HeatMap("dump.txt", 40, 80, new Dimension(detectionArea.width, detectionArea.height));
        new Visualizer("dump.txt", new Dimension(220, 320));
        new Mesh(heatMap, new Dimension(600, 800));
    }
}
