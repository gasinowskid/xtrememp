package org.rom1dep.util.gui.swing.busylabel;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.swing.JLabel;
import org.rom1dep.util.gui.swing.busylabel.event.RDBusyLabelEvent;
import org.rom1dep.util.gui.swing.busylabel.event.RDBusyLabelEventListener;

/**
 * @author rom1dep
 */
public class RDBusyLabel extends JLabel implements Runnable, Pipe {

    ResizeHandler handler = new ResizeHandler(this);
    private BufferedImage baseImage;
    private BufferedImage animImage;
    private BufferedImage[] animImages;
    private RDBusyLabelEventListener listener;
    private RDBusyLabelEvent event;
    private volatile boolean isBusy = false;
    private AnimationType animType = AnimationType.ROTATE;

    public RDBusyLabel(Dimension dim) {
        super.setMinimumSize(dim);
        super.setPreferredSize(dim);
        super.setMaximumSize(dim);//temporary
        super.addComponentListener(handler);
    }

    public void addEventListener(RDBusyLabelEventListener newListener) {
        listener = newListener;
    }

    public void loadBaseImage(BufferedImage image) {
        baseImage = image;
    }

    public void loadAnimImage(BufferedImage image) {
        animImage = image;
    }

    public void setAnimationType(AnimationType type) {
        animType = type;
    }

    public AnimationType getAnimationType() {
        return animType;
    }

    public void setBusy(boolean aFlag) {
        if (aFlag == isBusy) {
            return;//nothing to do...
        } else {
            if (aFlag) {
                isBusy = true;
                start();
            } else {
                isBusy = false;
                stop();
            }
        }
    }

    public boolean isBusy() {
        return isBusy;
    }

    private void start() {
        new Thread(this).start();
        if (listener != null) {
            listener.animationStarted(null);
        }
    }

    private void stop() {
        repaint();
        if (listener != null) {
            listener.animationStopped(null);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (baseImage == null) {
            return;
        } else {
            g.drawImage(getDrawingImage(), 0, 0, getDrawingImage().getWidth(), getDrawingImage().getHeight(), this);
        }
    }

    private BufferedImage getDrawingImage() {
        if (!isBusy) {
            return baseImage;
        } else {
            return animImages[currentIndex];
        }
    }

    @Override
    public void initImages() {
        //int newDim = Math.min(baseImage.getWidth(), baseImage.getHeight());
        int newDim = animImage.getWidth();
        super.setSize(newDim, newDim);
        switch (animType) {
            case ROTATE_3D:
                makeRotate3DImages(newDim);
                break;
            case ROTATE:
            default:
                makeRotateImages(newDim);
                break;
        }
    }

    private void makeRotateImages(int newDim) {
        //Successives rotations of 2.Pi/8
        modulo = 8;
        gap = 100;
        animImages = new BufferedImage[8];
        AffineTransform rot = new AffineTransform();
        animImages[0] = animImage;
        for (int i = 1; i < 8; i++) {
            rot.rotate(i * Math.PI / 4, newDim / 2, newDim / 2);
            animImages[i] = new BufferedImage(newDim, newDim, animImage.TYPE_INT_ARGB);
            Graphics2D buffer = animImages[i].createGraphics();
            buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            buffer.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            buffer.drawImage(animImage, rot, null);
            buffer.dispose();
            rot.setToIdentity();
        }
    }

    private void makeRotate3DImages(int newDim) {
        //16 Images
        modulo = 16;
        gap = 75;
        animImages = new BufferedImage[modulo];
        AffineTransform rot3d = new AffineTransform();
        animImages[0] = animImage;
        for (int i = 0; i < modulo; i++) {
            rot3d.setTransform(Math.cos(2 * i * Math.PI / modulo), 0, 0, 1, (newDim / 2) * (1 - Math.cos(2 * i * Math.PI / modulo)), 0);
            animImages[i] = new BufferedImage(newDim, newDim, animImage.TYPE_INT_ARGB);
            Graphics2D buffer = animImages[i].createGraphics();
            buffer.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            buffer.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            buffer.drawImage(animImage, rot3d, null);
            buffer.dispose();
            rot3d.setToIdentity();
        }
    }
    //
    private static int currentIndex;
    private static int modulo = 1;
    private static int gap = 100;

    public void run() {
        try {
            if (animImages == null) {
                initImages();
            }
            while (isBusy) {
                currentIndex = ++currentIndex % modulo;
                repaint();
                Thread.sleep(gap);
                Thread.yield();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class ResizeHandler implements ComponentListener {

    Pipe pipe_dest;

    public ResizeHandler(Pipe pipe) {
        pipe_dest = pipe;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        //pipe_dest.initImages();
    }

    public void componentMoved(ComponentEvent e) {
    }

    public void componentShown(ComponentEvent e) {
    }

    public void componentHidden(ComponentEvent e) {
    }
}

interface Pipe {

    public void initImages();
}
