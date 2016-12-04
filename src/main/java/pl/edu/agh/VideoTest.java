package pl.edu.agh;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.objdetect.HOGDescriptor;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.List;

/**
 * Created by mn on 25.11.16.
 */

class DetectSilhouetteDemo{
    public BufferedImage Mat2BufferedImage(Mat m){
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;

    }

    public void run() {

        String url = getClass().getResource("/video.mp4").getPath();
        final VideoCapture videoCapture = new VideoCapture(new File(url).getAbsolutePath());
        final Size frameSize = new Size(
                (int) videoCapture.get(Highgui.CV_CAP_PROP_FRAME_WIDTH),
                (int) videoCapture.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT)
        );

        final Mat mat = new Mat();
        final HOGDescriptor hog = new HOGDescriptor();
        final MatOfFloat descriptors = HOGDescriptor.getDefaultPeopleDetector();
        hog.setSVMDetector(descriptors);
        final MatOfRect foundLocations = new MatOfRect();
        final MatOfDouble foundWeights = new MatOfDouble();
        final Size winStride = new Size(8, 8);
        final Size padding = new Size(32, 32);

        final Point rectPoint1 = new Point();
        final Point rectPoint2 = new Point();
        final Point fontPoint = new Point();
        final Scalar rectColor = new Scalar(0, 255, 0);
        final Scalar fontColor = new Scalar(255, 255, 255);


        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize((int)frameSize.width+50, (int)frameSize.height+50);
        JLabel lbl=new JLabel();
        frame.add(lbl);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        while(videoCapture.read(mat)){
            hog.detectMultiScale(mat, foundLocations, foundWeights, 0.0,
                    winStride, padding, 1.05, 2.0, false);

            System.out.println(String.format("Detected %s people", foundLocations.toArray().length));
            if (foundLocations.rows() > 0) {
                List<Double> weightList = foundWeights.toList();
                List<Rect> rectList = foundLocations.toList();
                int i = 0;
                for (Rect rect : rectList) {
                    rectPoint1.x = rect.x;
                    rectPoint1.y = rect.y;
                    rectPoint2.x = rect.x + rect.width;
                    rectPoint2.y = rect.y + rect.height;

                    Core.rectangle(mat, rectPoint1, rectPoint2, rectColor, 2);
                    fontPoint.x = rect.x;
                    fontPoint.y = rect.y - 4;

                    Core.putText(mat,
                            String.format("%1.2f", weightList.get(i++)),
                            fontPoint, Core.FONT_HERSHEY_PLAIN, 1.5, fontColor,
                            2, Core.LINE_AA, false);
                }
            }
            lbl.setIcon(new ImageIcon(Mat2BufferedImage(mat)));

        }

    }
}

public class VideoTest{
    public static void main(String []args){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new DetectSilhouetteDemo().run();
    }
}