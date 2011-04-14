/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2008  Besmir Beqiri
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
package xtrememp.visualization;

import java.util.Map;
import xtrememp.visualization.fourier.FFT;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import xtrememp.util.Utilities;

/**
 *
 * @author Besmir Beqiri
 */
public class SpectrumAnalyser extends Visualization {

    public static final int DEFAULT_SPECTRUM_ANALYSER_FFT_SAMPLE_SIZE = 512;
    public static final int DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT = 10;
    public static final int DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY = 10;
    public static final float DEFAULT_SPECTRUM_ANALYSER_DECAY = 0.05f;
    private Type type;

    public enum Type {

        SPECTROGRAM("Spectrogram") {

            private int fftSampleSize = DEFAULT_SPECTRUM_ANALYSER_FFT_SAMPLE_SIZE;
            private FFT fft = new FFT(fftSampleSize);
            private Color[] sColors = initColors();
            private BufferedImage sbi1;
            private BufferedImage sbi2;

            @Override
            public void render(Graphics g, int width, int height,
                    float[] leftChannel, float[] rightChannel) {
                float[] stereoChannel = Utilities.stereoMerge(leftChannel, rightChannel);
                float[] _fft = fft.calculate(stereoChannel);
                int fftLength = _fft.length;
                float offset = (float) width / (float) fftLength;
                if (sbi1 == null || width != sbi1.getWidth() || height != sbi1.getHeight()) {
                    sbi1 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    sbi2 = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2d1 = sbi1.createGraphics();
                    g2d1.setColor(Color.black);
                    g2d1.fillRect(0, 0, sbi1.getWidth(), sbi1.getHeight());
                    g2d1.dispose();
                }
                float x = 0;
                Graphics2D g2d1 = sbi1.createGraphics();
                for (int a = 0; a < fftLength; a++) {
                    // Log filter.
                    float fs = _fft[a] * (float) Math.log(a + 2);
                    fs = Math.min(fs, 1.0f);
                    g2d1.setColor(sColors[(int) (fs * 255)]);
                    g2d1.drawLine((int) x, height - 1, (int) (x + offset), height - 1);
                    x += offset;
                }
                g2d1.dispose();
                Graphics2D g2d2 = sbi2.createGraphics();
                g2d2.setPaintMode();
                g2d2.drawImage(sbi1, 0, -1, null);
                g2d2.dispose();
                g.setPaintMode();
                g.drawImage(sbi2, 0, 0, null);
                sbi1 = sbi2;
            }

            private Color[] initColors() {
                Color[] colors = new Color[256];
                for (int i = 0; i < 32; i++) {
                    int rgb = i << 4;
                    if (rgb > 255) {
                        rgb = 255;
                    }
                    colors[i] = new Color(rgb, 0, 0);
                }
                for (int i = 0; i < 32; i++) {
                    int rgb = i << 4;
                    if (rgb > 255) {
                        rgb = 255;
                    }
                    colors[i + 32] = new Color(255, rgb, 0);
                }
                for (int i = 0; i < 192; i++) {
                    colors[i + 64] = new Color(255, 255, 0);
                }
                return colors;
            }
        },
        //        SPECTRUM("Spectrum") {
        //
        //            @Override
        //            public void render(Graphics g2d1, int width, int height,
        //                    float[] leftChannel, float[] rightChannel) {
        //                Point2D start = new Point2D.Float(0, 0);
        //                Point2D end = new Point2D.Float(0, height);
        //                float[] dist = {0.0f, 0.25f, 0.5f, 1.0f};
        //                Color[] colors = {Color.red, Color.yellow, Color.green, Color.green.darker()};
        //                LinearGradientPaint lgp = new LinearGradientPaint(start, end, dist, colors);
        //                ((Graphics2D) g2d1).setPaint(lgp);
        //                float[] stereoChannel = Utilities.stereoMerge(leftChannel, rightChannel);
        //                float[] _fft = fft.calculate(stereoChannel);
        //                int fftLength = _fft.length;
        //                float offset = (float) width / (float) fftLength;
        //                int last = height;
        //                float c = 0;
        //                for (int a = 0; a < fftLength; a++) {
        //                    // Log filter.
        //                    float fs = _fft[a] * (float) Math.log(a + 2);
        //                    fs = Math.min(fs, 1.0f);
        //                    // Compute decay...
        //                    if (fs >= (old_FFT[a] - decay)) {
        //                        old_FFT[a] = fs;
        //                    } else {
        //                        old_FFT[a] -= decay;
        //                        if (old_FFT[a] < 0) {
        //                            old_FFT[a] = 0;
        //                        }
        //                        fs = old_FFT[a];
        //                    }
        //                    int temp = height - (int) (fs * height);
        //                    g2d1.drawLine((int) c, last, (int) (c + offset), temp);
        //                    c += offset;
        //                    last = temp;
        //                }
        //            }
        //        },
        SPECTRUM_BARS("Spectrum Bars") {

            private int[] peaks = new int[DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT];
            private int[] peaksDelay = new int[DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT];
            private Color peakColor = Color.white;
            private boolean peaksEnabled = true;
            private float decay = DEFAULT_SPECTRUM_ANALYSER_DECAY;
            private int bands = DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT;
            private int peakDelay = DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY;
            private int fftSampleSize = DEFAULT_SPECTRUM_ANALYSER_FFT_SAMPLE_SIZE;
            private FFT fft = new FFT(fftSampleSize);
            private float[] old_FFT = new float[fftSampleSize];
            private LinearGradientPaint lgp;

            @Override
            public void render(Graphics g, int width, int height,
                    float[] leftChannel, float[] rightChannel) {
                float c = 0;
                float[] stereoChannel = Utilities.stereoMerge(leftChannel, rightChannel);
                float[] _fft = fft.calculate(stereoChannel);
                float barWidth = (float) width / (float) bands;
                float saMultiplier = (fftSampleSize / 2) / bands;
                for (int a = 0, bd = 0; bd < bands; a += saMultiplier, bd++) {
                    float fs = 0;
                    // Average out nearest fftLength.
                    for (int b = 0; b < saMultiplier; b++) {
                        fs += _fft[a + b];
                    }
                    // Log filter.
                    fs = (fs * (float) Math.log(bd + 2));
                    fs = Math.min(fs, 1.0f);
                    // Compute decay...
                    if (fs >= (old_FFT[a] - decay)) {
                        old_FFT[a] = fs;
                    } else {
                        old_FFT[a] -= decay;
                        if (old_FFT[a] < 0) {
                            old_FFT[a] = 0;
                        }
                        fs = old_FFT[a];
                    }
                    if (lgp == null || height != lgp.getEndPoint().getY()) {
                        Point2D start = new Point2D.Float(0, 0);
                        Point2D end = new Point2D.Float(0, height);
                        float[] dist = {0.0f, 0.25f, 0.75f, 1.0f};
                        Color[] colors = {Color.red, Color.yellow, Color.green, Color.green.darker().darker()};
                        lgp = new LinearGradientPaint(start, end, dist, colors, CycleMethod.REPEAT);
                    }
                    ((Graphics2D) g).setPaint(lgp);
                    renderSpectrumBar(g, (int) c, height, (int) barWidth - 1, (int) (fs * height), bd);
                    c += barWidth;
                }
            }

            private void renderSpectrumBar(Graphics g, int x, int y, int w, int h, int band) {
                g.fillRect(x, y - h, w, y);
                if ((peakColor != null) && (peaksEnabled == true)) {
                    g.setColor(peakColor);
                    if (h > peaks[band]) {
                        peaks[band] = h;
                        peaksDelay[band] = peakDelay;
                    } else {
                        peaksDelay[band]--;
                        if (peaksDelay[band] < 0) {
                            peaks[band]--;
                        }
                        if (peaks[band] < 0) {
                            peaks[band] = 0;
                        }
                    }
                    g.fillRect(x, y - peaks[band], w, 1);
                }
            }
        };
        private String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public abstract void render(Graphics g, int width, int height,
                float[] leftChannel, float[] rightChannel);

        @Override
        public String toString() {
            return displayName;
        }
    }

    public SpectrumAnalyser() {
        this(Type.SPECTROGRAM);
    }

    public SpectrumAnalyser(Type type) {
        this.type = type;
    }

    /*
     * 
     * @see xtrememp.visual.Visualization#getDisplayName
     */
    @Override
    public String getDisplayName() {
        return "Spectrum Analyser";
    }

    @Override
    public void setType(String type) {
        this.type = Type.valueOf(type);
    }

    @Override
    public String getTypeName() {
        return type.name();
    }

    @Override
    public Map<String, String> getTypes() {
        Map<String, String> typesMap = new HashMap<String, String>();
        for (Type t : Type.values()) {
            typesMap.put(t.name(), t.toString());
        }
        return typesMap;
    }

    /*
     * 
     * @see xtrememp.visual.Visualization#render
     */
    @Override
    public synchronized void render(Graphics g, int width, int height) {
        type.render(g, width, height, leftChannel, rightChannel);
    }
}
