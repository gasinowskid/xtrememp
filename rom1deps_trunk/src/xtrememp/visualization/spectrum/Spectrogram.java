/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2009 Besmir Beqiri
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package xtrememp.visualization.spectrum;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.VolatileImage;
import xtrememp.player.dsp.DigitalSignalSynchronizer;
import xtrememp.player.dsp.DigitalSignalSynchronizer.Context;
import xtrememp.util.Utilities;
import xtrememp.visualization.Visualization;
import xtrememp.visualization.fourier.FFT;

/**
 *
 * @author Besmir Beqiri
 */
public class Spectrogram extends Visualization {

    public static final String NAME = "Spectrogram";
    private FFT fft;
    private Color[] sColors;
    private int fftSampleSize;
    private int bands;
    private int[] samTable;
    private double[] logTable;
    private double[] log10Table;
    private GraphicsConfiguration gc;
    private VolatileImage image1;
    private VolatileImage image2;

    public Spectrogram() {
        setBandCount(DigitalSignalSynchronizer.DEFAULT_SAMPLE_SIZE);
        setFFTSampleSize(DigitalSignalSynchronizer.DEFAULT_SAMPLE_SIZE);

        // Grayscale gradient
        sColors = new Color[256];
        for (int i = 0, len = sColors.length; i < len; i++) {
            int rgb = i * 5;
            if (rgb > 255) {
                rgb = 255;
            }
            //sColors[i] = new Color(rgb, 70, 147);
            sColors[i] = new Color(rgb, 0, 0);
        }

//        initColors();
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    /**
     * Sets the numbers of bands rendered by the spectrum analyser.
     *
     * @param pCount Cannot be more than half the "FFT sample size".
     */
    public synchronized void setBandCount(int count) {
        bands = count;
        computeSAMTable();
    }

    /**
     * Sets the FFT sample size to be just for calculating the spectrum analyser
     * values. The default is 512.
     *
     * @param pSize Cannot be more than the size of the sample provided by the DSP.
     */
    public synchronized void setFFTSampleSize(int size) {
        fftSampleSize = size;
        fft = new FFT(fftSampleSize);

        logTable = new double[size];
        log10Table = new double[size];

        double r = (double) size / 90.0;
        for (int a = 0; a < size; a++) {
            logTable[a] = Math.log(a + 2);
            log10Table[a] = Math.log10(((double) a / r) + 10.0) - 1.0;
        }
        computeSAMTable();
    }

    private void computeSAMTable() {
        if (bands > 0) {
            // -- Calculate a log based band mapping table.
            if (logTable != null) {
                int wHss = (int) fftSampleSize >> 2;
                double r = ((double) fftSampleSize / (double) wHss);
                samTable = new int[bands];

                double a = 0;

                int wLb = 0;
                int wBand = 0;

                for (int b = 0; b < wHss; b++) {
                    int wCb = (int) (log10Table[(int) ((double) b * r)] * bands);
                    if (wCb != wLb) {
                        samTable[wBand] = b;
                        wLb = wCb;
                        wBand++;
                    }
                }

                if (wBand < bands) {
                    samTable[wBand] = wHss - 1;
                    bands = wBand + 1;
                }
            }
        }
    }

    private void createImages(int width, int height) {
        // free image resources
        if (image1 != null) {
            image1.flush();
            image1 = null;
        }

        if (image2 != null) {
            image2.flush();
            image2 = null;
        }

        // create images
        gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getDefaultConfiguration();

        image1 = gc.createCompatibleVolatileImage(width, height);
        image2 = gc.createCompatibleVolatileImage(width, height);

        int valCode1 = image1.validate(gc);
        int valCode2 = image2.validate(gc);
        if (valCode1 == VolatileImage.IMAGE_INCOMPATIBLE ||
                valCode2 == VolatileImage.IMAGE_INCOMPATIBLE) {
            createImages(width, height);
        }

        fillBackground(Color.black);
    }

    private void fillBackground(Color c) {
        if (image1 != null) {
            Graphics2D g2d1 = image1.createGraphics();
            g2d1.setColor(c);
            g2d1.fillRect(0, 0, image1.getWidth(), image1.getHeight());
            g2d1.dispose();
        }

        if (image2 != null) {
            Graphics2D g2d2 = image2.createGraphics();
            g2d2.setColor(c);
            g2d2.fillRect(0, 0, image2.getWidth(), image2.getHeight());
            g2d2.dispose();
        }
    }

    @Override
    public void render(Graphics g, int width, int height, Context dssContext) {
        if (image1 == null || (image1.getWidth() != width || image1.getHeight() != height)) {
            createImages(width, height);
        }
        do {
            int valCode1 = image1.validate(gc);
            int valCode2 = image2.validate(gc);
            if (valCode1 == VolatileImage.IMAGE_RESTORED || valCode2 == VolatileImage.IMAGE_RESTORED) {
                fillBackground(Color.black);
            } else if (valCode1 == VolatileImage.IMAGE_INCOMPATIBLE ||
                    valCode2 == VolatileImage.IMAGE_INCOMPATIBLE) {
                createImages(width, height);
            }

            // rendering
            float[][] channels = dssContext.getDataNormalized();
            float[] stereoChannel = Utilities.stereoMerge(channels[0], channels[1]);
            float[] _fft = fft.calculate(stereoChannel);
            float offset = (float) height / (float) bands;

            Graphics2D g2d1 = image1.createGraphics();
            g2d1.drawImage(image2, -1, 0, null);

            float y = height;
            int width2 = width - 1;
            int i = 0;
            int li = 0;
            for (int bd = 0; bd < bands; bd++) {
                i = samTable[bd];
                float fs = 0;
                float m = 0;
                // Average out nearest bands.
                for (int b = li; b < i; b++) {
                    float lf = _fft[b];
                    if (lf > m) {
                        m = lf;
                    }
                }
                // Log filter.
                li = i;
                fs = (m * (float) (logTable[li])) * 2.0f;
                if (fs > 1.0f) {
                    fs = 1.0f;
                }
                g2d1.setColor(sColors[(int) (fs * 255)]);
                g2d1.drawLine(width2, (int) y, width2, (int) (y - offset));
                y -= offset;
            }
            g2d1.dispose();

            Graphics2D g2d2 = image2.createGraphics();
            g2d2.drawImage(image1, 0, 0, null);
            g2d2.dispose();

            g.drawImage(image2, 0, 0, null);
        } while (image1.contentsLost() || image2.contentsLost());
    }
//    private void initColors() {
//        sColors = new Color[256];
//        for (int i = 0; i < 32; i++) {
//            int rgb = i << 4;
//            if (rgb > 255) {
//                rgb = 255;
//            }
//            sColors[i] = new Color(rgb, 0, 0);
//        }
//        for (int i = 0; i < 32; i++) {
//            int rgb = i << 4;
//            if (rgb > 255) {
//                rgb = 255;
//            }
//            sColors[i + 32] = new Color(255, rgb, 0);
//        }
//        for (int i = 0; i < 192; i++) {
//            sColors[i + 64] = new Color(255, 255, 0);
//        }
//    }
}
