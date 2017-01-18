package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class VideoSimulation {

    private final String videoPath;
    private GUI gui;
    private HeatMap heatMap;
    private Rect detectionArea;
    private Size resizedMatSize;
    private ArrayList<java.awt.Point> points;

    public VideoSimulation(String videoFile, HeatMap heatMap, Rect detectionArea) {
        videoPath = getClass().getResource("/" + videoFile).getPath();

        gui = new GUI();
        points = null;
        resizedMatSize = new Size(250,600);

        this.heatMap = heatMap;
        this.detectionArea = detectionArea;

        gui.addListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point cellPosition = heatMap.cellPosition(new Point(e.getX()*((double) detectionArea.width/resizedMatSize.width),
                                                                    e.getY()*((double) detectionArea.height/resizedMatSize.height)));
                Simulation sim = new Simulation(heatMap, (int) cellPosition.x, (int) cellPosition.y, Simulation.Direction.SOUTH);
                points = sim.getPath();
            }
        });

        run();
    }

    public void run() {
        VideoCapture video = new VideoCapture(new File(videoPath).getAbsolutePath());
        Mat prevMat = new Mat();
        Mat matToShow;

        video.read(prevMat);
        while(video.grab()) {
            Mat mat = new Mat();
            video.retrieve(mat);
            mat = new Mat(mat, detectionArea);

            if(points != null) {
                for(int i = 0; i < points.size() - 1; i++) {
                    Core.line(mat, new Point(points.get(i).x*heatMap.getxStep(), points.get(i).y*heatMap.getyStep()),
                                   new Point(points.get(i+1).x*heatMap.getxStep(), points.get(i+1).y*heatMap.getyStep()),
                              new Scalar(0, 0, 255));
                }
            }

            Mat resizedMat = new Mat();
            matToShow = mat;
            Imgproc.resize(matToShow, resizedMat, resizedMatSize);
            gui.show(resizedMat);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
