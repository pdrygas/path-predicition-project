package pl.edu.agh;

import org.opencv.core.Point;

import java.io.*;
import java.util.HashMap;

public class DataDumper {
    private PrintWriter writer;

    public DataDumper(String filename) {
        try {
            File file = new File(getClass().getResource("/").getPath() + filename);
            writer = new PrintWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void dump(HashMap<Integer, FoundEntity> entities) {
        for(HashMap.Entry<Integer, FoundEntity> entry : entities.entrySet()) {
            Point avgPoint = averagePoint(entry.getValue().getFPoints().toArray());
            writer.println(entry.getKey() + ";" + avgPoint.x + ";" + avgPoint.y);
            writer.flush();
        }
    }

    private Point averagePoint(Point[] points) {
        double x = 0;
        double y = 0;

        for(Point point : points) {
            x += point.x;
            y += point.y;
        }

        return new Point((x/points.length), (y/points.length));
    }
}
