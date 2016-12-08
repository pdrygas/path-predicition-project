package pl.edu.agh;

import org.opencv.core.Core;

import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        Detector detector = new Detector();
        detector.detect();

        new Visualizer("dump.txt", new Dimension(640, 480));
    }
}
