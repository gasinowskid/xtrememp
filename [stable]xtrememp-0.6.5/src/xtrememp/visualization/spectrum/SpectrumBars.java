/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2010 Besmir Beqiri
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
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.nio.FloatBuffer;
import xtrememp.player.dsp.DigitalSignalSynchronizer;
import xtrememp.player.dsp.DssContext;
import xtrememp.util.Utilities;
import xtrememp.visualization.Visualization;
import xtrememp.visualization.fourier.FFT;

/**
 *
 * @author Besmir Beqiri
 */
public class SpectrumBars extends Visualization {

    public static final String NAME = "Spectrum Bars";
    public static final int DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT = 20;
    public static final int DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY = 25;
    public static final float DEFAULT_SPECTRUM_ANALYSER_DECAY = 0.02f;
    private int[] peaks = new int[DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT];
    private int[] peaksDelay = new int[DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT];
    private Color peakColor = Color.white;
    private boolean peaksEnabled = true;
    private float decay = DEFAULT_SPECTRUM_ANALYSER_DECAY;
    private int peakDelay = DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY;
    private int bands;
    private int fftSampleSize;
    private FFT fft;
    private float[] old_FFT;
    private int[] samTable;
    private double[] logTable;
    private double[] log10Table;
    private LinearGradientPaint lgp;

    public SpectrumBars() {
        setBandCount(DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT);
        setFFTSampleSize(DigitalSignalSynchronizer.DEFAULT_SAMPLE_SIZE);
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
        old_FFT = new float[bands];

        logTable = new double[size];
        log10Table = new double[size];

        double r = (double) size / 90.0;
        for (int a = 0; a < size; a++) {
            logTable[a] = Math.log(a + 2);
            log10Table[a] = Math.log10(((double) a / r) + 10.0) - 1.0;
        }
        computeSAMTable();
    }

    @Override
    public synchronized void render(Graphics2D g2d, int width, int height, DssContext dssContext) {
        FloatBuffer leftChannel = dssContext.getLeftChannelBuffer();
        FloatBuffer rightChannel = dssContext.getRightChannelBuffer();
        FloatBuffer stereoChannel = Utilities.stereoMerge(leftChannel, rightChannel);
        float[] _fft = fft.calculate(stereoChannel);
        float barWidth = (float) width / (float) bands;
        float c = 0;
        int i = 0;
        int li = 0;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);

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
//            fs = (fs * (float) Math.log(bd + 2));
//            fs = Math.min(fs, 1.0f);
            li = i;
            fs = (m * (float) (logTable[li])) * 2.0f;
            if (fs > 1.0f) {
                fs = 1.0f;
            }
            // Compute decay...
            if (fs >= (old_FFT[bd] - decay)) {
                old_FFT[bd] = fs;
            } else {
                old_FFT[bd] -= decay;
                if (old_FFT[bd] < 0) {
                    old_FFT[bd] = 0;
                }
                fs = old_FFT[bd];
            }

            if (lgp == null || lgp.getEndPoint().getY() != height) {
                Point start = new Point(0, 0);
                Point end = new Point(0, height);
                float[] dist = {0.0f, 0.25f, 0.75f, 1.0f};
                Color[] colors = {Color.red, Color.yellow, Color.green, Color.green.darker().darker()};
                lgp = new LinearGradientPaint(start, end, dist, colors, CycleMethod.REPEAT);
            }

            g2d.setPaint(lgp);
            renderSpectrumBar(g2d, (int) c, height, (int) barWidth - 1, (int) (fs * height), bd);
            c += barWidth;
        }
    }

    private void renderSpectrumBar(Graphics2D g2d, int x, int y, int w, int h, int band) {
        g2d.fillRect(x, y - h, w, y);
        if ((peakColor != null) && (peaksEnabled == true)) {
            g2d.setColor(peakColor);
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
            g2d.fillRect(x, y - peaks[band], w, 1);
        }
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
}
