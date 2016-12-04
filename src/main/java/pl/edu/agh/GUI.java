package pl.edu.agh;

import org.opencv.core.Mat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class GUI {
    private JFrame frame;
    private JLabel label;

    public GUI() {
        frame = new JFrame();
        frame.setLayout(new FlowLayout());

        label = new JLabel();
        frame.add(label);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    public void show(Mat mat) {
        BufferedImage img = matToBufferedImage(mat);
        frame.setSize(img.getWidth(), img.getHeight());
        label.setIcon(new ImageIcon(img));
    }

    private BufferedImage matToBufferedImage(Mat mat) {
        int type = matType(mat);
        byte[] bytes = getBytes(mat);
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();

        System.arraycopy(bytes, 0, targetPixels, 0, bytes.length);
        return image;
    }

    private int matType(Mat mat) {
        if(mat.channels() > 1) {
            return BufferedImage.TYPE_3BYTE_BGR;
        }
        return BufferedImage.TYPE_BYTE_GRAY;
    }

    private byte[] getBytes(Mat mat) {
        int bufferSize = mat.channels() * mat.cols() * mat.rows();
        byte[] bytes = new byte[bufferSize];
        mat.get(0, 0, bytes);

        return bytes;
    }
}
