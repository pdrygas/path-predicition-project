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
        jFrame.setSize(new Dimension(size.width + 50, size.height + 50));
    }

    public void add(Component comp) {
        jFrame.add(comp);
    }
}
