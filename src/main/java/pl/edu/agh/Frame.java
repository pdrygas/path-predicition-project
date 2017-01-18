package pl.edu.agh;

import javax.swing.*;
import java.awt.*;

public class Frame {

    protected JFrame jFrame;

    public Frame(Dimension size) {
        jFrame = new JFrame();
        jFrame.setLayout(new FlowLayout());

        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        jFrame.setSize(size);
    }

    public void add(Component comp) {
        jFrame.add(comp);
    }
}
