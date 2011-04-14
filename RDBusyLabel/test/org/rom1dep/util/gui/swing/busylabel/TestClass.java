package org.rom1dep.util.gui.swing.busylabel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * @author rom1dep
 */
public class TestClass extends JFrame implements MouseListener {

    private static JPanel rootpane;
    private static RDBusyLabel busylab;
    private static final Dimension preferedSize = new Dimension(64, 64);
    private static final String rsrcPath = "/org/rom1dep/util/gui/swing/busylabel/resources/";

    public TestClass() {
        super("TestFrame");
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootpane = initRootPane();
        busylab.addMouseListener(this);
        super.setLayout(new BorderLayout());
        super.add(rootpane, BorderLayout.CENTER);
        super.pack();
    }

    private static JPanel initRootPane() {
        JPanel temp = new JPanel(new BorderLayout());
        temp.add(busylab = initBusyLabel(), BorderLayout.CENTER);
        return temp;
    }

    private static RDBusyLabel initBusyLabel() {
        RDBusyLabel labtemp = new RDBusyLabel(preferedSize);
        try {
            labtemp.loadBaseImage(loadImage(TestClass.class.getResourceAsStream(rsrcPath + "testimg_sleep.png")));
            labtemp.loadAnimImage(loadImage(TestClass.class.getResourceAsStream(rsrcPath + "testimg.png")));
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
        labtemp.setAnimationType(AnimationType.ROTATE_3D);
        return labtemp;
    }

    private static BufferedImage loadImage(InputStream istream) throws IOException {
        return ImageIO.read(istream);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                new TestClass().setVisible(true);
            }
        });
    }

    public void mouseClicked(MouseEvent e) {
        busylab.setBusy(!busylab.isBusy());
    }

    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
