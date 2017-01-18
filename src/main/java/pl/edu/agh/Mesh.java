package pl.edu.agh;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.SyncFailedException;
import java.util.ArrayList;

public class Mesh extends Frame {

    private Canvas canvas;

    public Mesh(String dumpFile, Dimension size) {
        super(size);

        HeatMap heatMap = new HeatMap(dumpFile, 20, 40);
        canvas = new Canvas(size);
        canvas.setCells(heatMap.getCells());
        canvas.drawPoints();

        super.add(canvas);
    }

    private class Canvas extends JPanel {
        private int[][] cells;

        Canvas(Dimension size) {
            setPreferredSize(size);
        }

        public void setCells(int[][] cells) {
            this.cells = cells;
        }

        public void drawPoints() {
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Dimension size = getPreferredSize();
            int xStep = size.width / cells.length;
            int yStep = size.height / cells[0].length;

            drawMesh(g, size, xStep, yStep);
            g.setColor(Color.RED);
            drawCells(g, xStep, yStep);
        }

        private void drawMesh(Graphics g, Dimension size, int xStep, int yStep) {
            for(int i = xStep; i < size.width; i += xStep) {
                g.drawLine(i, 0, i, size.height);
            }
            for(int i = yStep; i < size.height; i += yStep) {
                g.drawLine(0, i, size.width, i);
            }
        }

        private void drawCells(Graphics g, int xStep, int yStep) {
            for(int i = 0; i < cells.length; i++) {
                for(int j = 0; j < cells[0].length; j++) {
                    if(cells[i][j] > 0) {
                        g.drawString(cells[i][j] + "", i * xStep, j * yStep);
                    }
                }
            }
        }
    }
}
