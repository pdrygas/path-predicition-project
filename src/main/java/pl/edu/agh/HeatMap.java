package pl.edu.agh;

import java.awt.*;
import java.util.ArrayList;

public class HeatMap {
    private int xMin, xMax, yMin, yMax;
    private int[][] cells;
    private ArrayList<Point> points;

    public HeatMap(String dumpFile, int cellsX, int cellsY) {
        DumpReader reader = new DumpReader(dumpFile);

        cells = new int[cellsX][cellsY];
        points = reader.getPoints();

        fillCells();
    }

    public int[][] getCells() {
        return cells;
    }

    private void fillCells() {
        findExtremes();

        double xSize = (double) (xMax - xMin) / cells.length;
        double ySize = (double) (yMax - yMin) / cells[0].length;
        for(Point p : points) {
            int xPos = (int) Math.round((p.x - xMin) / xSize);
            int yPos = (int) Math.round((p.y - yMin) / ySize);

            if(xPos >= cells.length) {
                System.out.println(p.x + " " + xMin + " " + xSize);
                xPos = cells.length - 1;
            }
            if(yPos >= cells[0].length) {
                yPos = cells[0].length - 1;
            }
            cells[xPos][yPos]++;
        }
    }

    private void findExtremes() {
        xMax = -1;
        yMax = -1;
        xMin = Integer.MAX_VALUE;
        yMin = Integer.MAX_VALUE;
        for(Point p : points) {
            if(p.x > xMax) {
                xMax = p.x;
            }
            if(p.y > yMax) {
                yMax = p.y;
            }
            if(p.x < xMin) {
                xMin = p.x;
            }
            if(p.y < yMin) {
                yMin = p.y;
            }
        }
    }
}
