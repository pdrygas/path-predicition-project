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

    private final String videoPath, xmlPath;
    private Size minSize, maxSize;
    private double scaleFactor;
    private int minNeighbors;
    private GUI gui;
    private HeatMap heatMap;
    private Rect detectionArea;
    private Size resizedMatSize;
    private CascadeClassifier classifier;
    private boolean playVideo;

    public VideoSimulation(String videoFile, HeatMap heatMap, Rect detectionArea) {
        videoPath = getClass().getResource("/" + videoFile).getPath();
        xmlPath = getClass().getResource("/haarcascade_fullbody.xml").getPath();
        classifier = new CascadeClassifier(new File(xmlPath).getAbsolutePath());
        scaleFactor = 1.01;
        minNeighbors = 1;
        minSize = new Size(0, 30);
        maxSize = new Size(50, 65);

        gui = new GUI();
        resizedMatSize = new Size(250,600);
        playVideo = true;

        this.heatMap = heatMap;
        this.detectionArea = detectionArea;

        createButton();
        run();
    }

    private void createButton() {
        Button btn = new Button();
        btn.setLabel("Stop");
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(playVideo) {
                    btn.setLabel("Start");
                } else {
                    btn.setLabel("Stop");
                }
                playVideo = !playVideo;
            }
        });
        gui.add(btn);
    }

    private void run() {
        VideoCapture video = new VideoCapture(new File(videoPath).getAbsolutePath());
        Mat mat = new Mat();
        Mat prevMat = new Mat();

        boolean searched = false;

        video.read(prevMat);
        for(;;) {
            System.out.println(""); // MAGIC NOP
            if(playVideo) {
                if(video.grab()) {
                    searched = false;
                    prevMat = mat;

                    video.retrieve(mat);
                    mat = new Mat(mat, detectionArea);
                    drawMat(mat);
                    try {
                        Thread.sleep(1000 / 25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                if(!searched) {
                    MatOfRect foundPrev = detectPeople(new Mat(prevMat, detectionArea));
                    MatOfRect foundCurrent = detectPeople(mat);

                    searched = true;
                    predictPaths(foundPrev, foundCurrent, mat);
                    drawMat(mat);
                }
            }
        }
    }

    private void drawMat(Mat mat) {
        Mat resizedMat = new Mat();
        Imgproc.resize(mat, resizedMat, resizedMatSize);
        gui.show(resizedMat);
    }

    private void drawPath(Point cellPosition, Simulation.Direction direction, Scalar color, Mat mat) {
        Simulation sim = new Simulation(heatMap, (int) cellPosition.x, (int) cellPosition.y, direction);
        ArrayList<java.awt.Point> points = sim.getPath();
        if (points != null) {
            for (int i = 0; i < points.size() - 1; i++) {
                Core.line(mat, new Point(points.get(i).x * heatMap.getxStep(), points.get(i).y * heatMap.getyStep()),
                               new Point(points.get(i + 1).x * heatMap.getxStep(), points.get(i + 1).y * heatMap.getyStep()),
                               color, 2);
            }
        }
    }

    private MatOfRect detectPeople(Mat mat) {
        MatOfRect result = new MatOfRect();
        classifier.detectMultiScale(mat, result, scaleFactor, minNeighbors, 0, minSize, maxSize);
        return result;
    }

    private void predictPaths(MatOfRect foundPrev, MatOfRect foundCurrent, Mat mat) {
        final int MAX_X_DIFF = 3;

        for (Rect oldRect : foundPrev.toArray()) {
            for(Rect newRect : foundCurrent.toArray()) {
                // Check if person appeared in both frames
                if(Math.abs(oldRect.x - newRect.x) < MAX_X_DIFF) {
                    Core.rectangle(mat, new Point(newRect.x, newRect.y), new Point(newRect.x+newRect.width, newRect.y+newRect.height), new Scalar(100));

                    Point cellPosition = heatMap.cellPosition(new Point(newRect.x + newRect.width/2,  newRect.y + newRect.height/2));
                    drawPath(cellPosition, Simulation.Direction.NORTH, new Scalar(0, 0, 255), mat);
                    drawPath(cellPosition, Simulation.Direction.SOUTH, new Scalar(0, 255, 255), mat);
                }
            }
        }
    }
}
