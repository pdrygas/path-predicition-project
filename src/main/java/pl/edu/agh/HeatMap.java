package pl.edu.agh;

import org.opencv.core.Point;

import java.awt.*;
import java.util.ArrayList;

public class HeatMap {
    private int[][] cells;
    private double xStep, yStep;
    private ArrayList<java.awt.Point> points;

    public HeatMap(String dumpFile, int cellsX, int cellsY, Dimension size) {
        DumpReader reader = new DumpReader(dumpFile);

        cells = new int[cellsX][cellsY];
        xStep = (double) size.width / cells.length;
        yStep = (double) size.height / cells[0].length;
        points = reader.getPoints();

        fillCells();
    }

    public int[][] getCells() {
        return cells;
    }

    public double getxStep() {
        return xStep;
    }

    public double getyStep() {
        return yStep;
    }

    public Point cellPosition(Point coords) {
        int xPos = (int) Math.round(coords.x / xStep);
        int yPos = (int) Math.round(coords.y / yStep);

        if(xPos >= cells.length) {
            xPos = cells.length - 1;
        }
        if(yPos >= cells[0].length) {
            yPos = cells[0].length - 1;
        }

        return new Point(xPos, yPos);
    }

    private void fillCells() {
        for(java.awt.Point p : points) {
            Point pos = cellPosition(new Point(p.x, p.y));
            cells[(int) pos.x][(int) pos.y]++;
        }
    }
}
