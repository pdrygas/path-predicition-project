package pl.edu.agh;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DumpReader {
    private BufferedReader reader;
    private ArrayList<Point> points;

    public DumpReader(String dumpFile) {
        try {
            FileReader file = new FileReader(getClass().getResource("/").getPath() + dumpFile);
            reader = new BufferedReader(file);

            points = new ArrayList<>();
            getPointsFromFile();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Point> getPoints() {
        return points;
    }

    private void getPointsFromFile() {
        String line;
        try {
            while((line = reader.readLine()) != null) {
                String[] values = line.split(";");
                points.add(new Point(new Double(values[1]).intValue(), new Double(values[2]).intValue()));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
