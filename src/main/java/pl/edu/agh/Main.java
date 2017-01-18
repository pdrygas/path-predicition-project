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
//        Detector detector = new Detector(videoFile, dumpFile, detectionArea);
//        detector.detect();

        HeatMap heatMap = new HeatMap(dumpFile, 10, 20, new Dimension(detectionArea.width, detectionArea.height));
        new Visualizer(dumpFile, new Dimension(220, 320));
        new Mesh(heatMap, new Dimension(600, 800), new Simulation(heatMap, 6, 0, Simulation.Direction.SOUTH));
        new VideoSimulation(videoFile, heatMap, detectionArea);
    }
}
