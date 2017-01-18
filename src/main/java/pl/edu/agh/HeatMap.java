package pl.edu.agh;

import java.awt.*;
import java.util.ArrayList;

public class HeatMap {
    private int[][] cells;
    private ArrayList<Point> points;
    private Dimension size;

    public HeatMap(String dumpFile, int cellsX, int cellsY, Dimension size) {
        DumpReader reader = new DumpReader(dumpFile);

        cells = new int[cellsX][cellsY];
        points = reader.getPoints();
        this.size = size;

        fillCells();
    }

    public int[][] getCells() {
        return cells;
    }

    private void fillCells() {
        double xSize = (double) size.width / cells.length;
        double ySize = (double) size.height / cells[0].length;

        for(Point p : points) {
            int xPos = (int) Math.round(p.x / xSize);
            int yPos = (int) Math.round(p.y / ySize);

            if(xPos >= cells.length) {
                xPos = cells.length - 1;
            }
            if(yPos >= cells[0].length) {
                yPos = cells[0].length - 1;
            }
            cells[xPos][yPos]++;
        }
    }
}
