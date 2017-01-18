package pl.edu.agh;

import java.awt.*;
import java.util.ArrayList;

public class Simulation {
    public enum Direction {
        NORTH, SOUTH
    }

    private int x, y;
    private Direction direction;
    private int[][] cells;

    public Simulation(HeatMap heatMap, int x, int y, Direction direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;

        cells = heatMap.getCells();
    }

    public ArrayList<Point> getPath() {
        ArrayList<Point> positions = new ArrayList<>();

        positions.add(new Point(x, y));
        while(y >= 0 && y < cells[0].length - 1) {
            if(direction == Direction.SOUTH) {
                y += 1;
            } else if(direction == Direction.NORTH) {
                y -= 1;
            }
            nextPosition();
            positions.add(new Point(x, y));

        }
        positions.add(new Point(x, y));

        return positions;
    }

    private void nextPosition() {
        int xNew = x;
        int max = cells[x][y];
        if(x - 1 >= 0 && cells[x-1][y] > max) {
            max = cells[x-1][y];
            xNew = x-1;
        }
        if(x + 1 < cells.length && cells[x+1][y] > max) {
            xNew = x+1;
        }
        x = xNew;
    }
}
