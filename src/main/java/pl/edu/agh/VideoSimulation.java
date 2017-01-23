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
    private ArrayList<java.awt.Point> points;
    private boolean playVideo;

    public VideoSimulation(String videoFile, HeatMap heatMap, Rect detectionArea) {
        videoPath = getClass().getResource("/" + videoFile).getPath();
        xmlPath = getClass().getResource("/haarcascade_fullbody.xml").getPath();
        scaleFactor = 1.01;
        minNeighbors = 1;
        minSize = new Size(0, 30);
        maxSize = new Size(50, 70);

        gui = new GUI();
        points = null;
        resizedMatSize = new Size(250,600);
        playVideo = true;

        this.heatMap = heatMap;
        this.detectionArea = detectionArea;

        Button btn = new Button();
        btn.setLabel("Stop");
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                playVideo = !playVideo;
            }
        });
        gui.add(btn);

        run();
    }

    public void run() {
        CascadeClassifier classifier = new CascadeClassifier(new File(xmlPath).getAbsolutePath());
        VideoCapture video = new VideoCapture(new File(videoPath).getAbsolutePath());
        Mat matToShow;
        Mat mat = new Mat();
        Mat prevMat = new Mat();

        boolean searched = false;
        int j = 0;

        video.read(mat);
        for(;;) {
            System.out.println(""); // MAGIC NOP
            if(playVideo) {
                if(video.grab()) {
                    searched = false;
                    if(j > 5) {
                        prevMat = mat;
                        j = 0;
                    }

                    video.retrieve(mat);
                    mat = new Mat(mat, detectionArea);

//                    if (points != null) {
//                        for (int i = 0; i < points.size() - 1; i++) {
//                            Core.line(mat, new Point(points.get(i).x * heatMap.getxStep(), points.get(i).y * heatMap.getyStep()),
//                                    new Point(points.get(i + 1).x * heatMap.getxStep(), points.get(i + 1).y * heatMap.getyStep()),
//                                    new Scalar(0, 0, 255));
//                        }
//                    }

                    Mat resizedMat = new Mat();
                    matToShow = mat;
                    Imgproc.resize(matToShow, resizedMat, resizedMatSize);
                    gui.show(resizedMat);
                    try {
                        Thread.sleep(1000 / 25);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                   j++;
                }
            } else {
                if(!searched) {
                    searched = true;

                    MatOfRect foundPrev = new MatOfRect();
                    MatOfRect foundCurrent = new MatOfRect();

                    Mat tmpMat = new Mat(prevMat, detectionArea);
                    classifier.detectMultiScale(tmpMat, foundPrev, scaleFactor, minNeighbors, 0, minSize, maxSize);
                    classifier.detectMultiScale(mat, foundCurrent, scaleFactor, minNeighbors, 0, minSize, maxSize);



                    final double diff = 3;
                    for (Rect oldRect : foundPrev.toArray()) {
                        for(Rect newRect : foundCurrent.toArray()) {
                            if(Math.abs(oldRect.x - newRect.x) < diff) {
                                Core.rectangle(mat, new Point(newRect.x, newRect.y), new Point(newRect.x+newRect.width, newRect.y+newRect.height), new Scalar(100));
//                                Core.rectangle(mat, new Point(oldRect.x, oldRect.y), new Point(oldRect.x+oldRect.width, oldRect.y+oldRect.height), new Scalar(0, 160, 0));

                                Point cellPosition = heatMap.cellPosition(new Point(newRect.x+ newRect.width / 2,  newRect.y + newRect.height / 2));

                                Simulation sim = new Simulation(heatMap, (int) cellPosition.x, (int) cellPosition.y, Simulation.Direction.NORTH);
                                points = sim.getPath();
                                if (points != null) {
                                    for (int i = 0; i < points.size() - 1; i++) {
                                        Core.line(mat, new Point(points.get(i).x * heatMap.getxStep(), points.get(i).y * heatMap.getyStep()),
                                                new Point(points.get(i + 1).x * heatMap.getxStep(), points.get(i + 1).y * heatMap.getyStep()),
                                                new Scalar(0, 0, 255), 2);
                                    }
                                }
                                sim = new Simulation(heatMap, (int) cellPosition.x, (int) cellPosition.y, Simulation.Direction.SOUTH);
                                points = sim.getPath();
                                if (points != null) {
                                    for (int i = 0; i < points.size() - 1; i++) {
                                        Core.line(mat, new Point(points.get(i).x * heatMap.getxStep(), points.get(i).y * heatMap.getyStep()),
                                                new Point(points.get(i + 1).x * heatMap.getxStep(), points.get(i + 1).y * heatMap.getyStep()),
                                                new Scalar(0, 255, 255), 2);
                                    }
                                }
                            }
                        }
                    }

                    Mat resizedMat = new Mat();
                    matToShow = mat;
                    Imgproc.resize(matToShow, resizedMat, resizedMatSize);
                    gui.show(resizedMat);
                }
            }
        }
    }
}
