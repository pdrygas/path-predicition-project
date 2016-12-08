package pl.edu.agh;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Visualizer {

    private JFrame frame;
    private Canvas canvas;
    private BufferedReader reader;

    public Visualizer(String dumpFile, Dimension size) {
        createLayout(size);
        try {
            FileReader file = new FileReader(getClass().getResource("/").getPath() + dumpFile);
            reader = new BufferedReader(file);

            addPointsToCanvas();
            canvas.drawPoints();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void createLayout(Dimension size) {
        frame = new JFrame();
        frame.setLayout(new FlowLayout());

        canvas = new Canvas(size);
        frame.add(canvas);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(size);
    }

    private void addPointsToCanvas() {
        String line;
        try {
            while((line = reader.readLine()) != null) {
                String[] values = line.split(";");
                canvas.addPoint(new Point(new Double(values[1]).intValue(), new Double(values[2]).intValue()));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private class Canvas extends JPanel {
        private ArrayList<Point> points;

        Canvas(Dimension size) {
            setPreferredSize(size);
            points = new ArrayList<>();
        }

        public void addPoint(Point point) {
            points.add(point);
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
