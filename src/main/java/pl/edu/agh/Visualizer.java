package pl.edu.agh;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Visualizer extends Frame {

    private Canvas canvas;

    public Visualizer(String dumpFile, Dimension size) {
        super(size);

        DumpReader reader = new DumpReader(dumpFile);
        canvas = new Canvas(size);
        super.add(canvas);

        canvas.setPoints(reader.getPoints());
        canvas.drawPoints();
    }

    private class Canvas extends JPanel {
        private ArrayList<Point> points;

        Canvas(Dimension size) {
            setPreferredSize(size);
            points = new ArrayList<>();
        }

        public void setPoints(ArrayList<Point> points) {
            this.points = points;
        }

        public void drawPoints() {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            for(int i = 0; i < points.size(); i++) {
                g.drawRect(points.get(i).x, points.get(i).y, 1, 1);
            }
        }
    }
}
