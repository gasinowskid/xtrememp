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
package xtrememp.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.nio.FloatBuffer;
import java.text.DecimalFormat;
import javax.sound.sampled.SourceDataLine;
import xtrememp.visualization.spectrum.Band;
import xtrememp.visualization.spectrum.BandDistribution;
import xtrememp.visualization.spectrum.BandGain;
import xtrememp.visualization.spectrum.FFT;
import xtrememp.visualization.spectrum.FlatBandGain;
import xtrememp.visualization.spectrum.FrequencyBandGain;
import xtrememp.visualization.spectrum.LinearBandDistribution;
import xtrememp.visualization.spectrum.LogBandDistribution;

/**
 * Renders a spectrum analyzer.
 *
 * Based on KJ-DSS project by Kristofer Fudalewski (http://sirk.sytes.net).
 *
 * @author Besmir Beqiri
 */
public final class SpectrumBars extends Visualization {

    public static final String NAME = "Spectrum Bars";
    //
    public static final BandDistribution BAND_DISTRIBUTION_LINEAR = new LinearBandDistribution();
    public static final BandDistribution BAND_DISTRIBUTION_LOG = new LogBandDistribution(4, 20.0D);
    public static final BandGain BAND_GAIN_FLAT = new FlatBandGain(4.0F);
    public static final BandGain BAND_GAIN_FREQUENCY = new FrequencyBandGain(4.0F);
    //
    public static final BandDistribution DEFAULT_SPECTRUM_ANALYSER_BAND_DISTRIBUTION = BAND_DISTRIBUTION_LOG;
    public static final BandGain DEFAULT_SPECTRUM_ANALYSER_BAND_GAIN = BAND_GAIN_FREQUENCY;
    public static final int DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT = 20;
    public static final int DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY = 25;
    public static final float DEFAULT_SPECTRUM_ANALYSER_DECAY = 0.02F;
    public static final float DEFAULT_SPECTRUM_ANALYSER_GAIN = 1.0F;
    //
    protected BandDistribution bandDistribution;
    protected BandGain bandGain;
    protected Band[] bdTable;
    protected float[] bgTable;
    protected int bands;
    protected int fftSampleSize;
    protected float fftSampleRate;
    protected FFT fft;
    private float decay;
    private float gain;
    private int[] peaks;
    private int[] peaksDelay;
    private int peakDelay;
    private Color peakColor;
    private boolean peaksEnabled = true;
    private float bandWidth;
    private boolean showFrequencies = true;
    private float[] old_FFT;
    private LinearGradientPaint lgp;

    public SpectrumBars() {
        this.bandDistribution = DEFAULT_SPECTRUM_ANALYSER_BAND_DISTRIBUTION;
        this.bandGain = DEFAULT_SPECTRUM_ANALYSER_BAND_GAIN;
        this.decay = DEFAULT_SPECTRUM_ANALYSER_DECAY;
        this.gain = DEFAULT_SPECTRUM_ANALYSER_GAIN;
        this.peaks = new int[DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT];
        this.peaksDelay = new int[DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT];
        this.peakDelay = DEFAULT_SPECTRUM_ANALYSER_PEAK_DELAY;
        this.peakColor = foregroundColor;

        setBandCount(DEFAULT_SPECTRUM_ANALYSER_BAND_COUNT);
    }

    /**
     * Sets the numbers of bands rendered by the spectrum analyser.
     *
     * @param count Cannot be more than half the "FFT sample size".
     */
    public synchronized void setBandCount(int count) {
        bands = count;
        computeBandTables();
    }

    private void computeBandTables() {
        if (bands > 0 && fftSampleSize > 0 & fft != null) {
            //Create band table.
            bdTable = bandDistribution.create(bands, fft, fftSampleRate);
            bands = bdTable.length;
            //Resolve band descriptions.
            resolveBandDescriptions(bdTable);
            //Create gain table.
            bgTable = bandGain.create(fft, fftSampleRate);
        }
    }

    private void resolveBandDescriptions(Band[] bandTable) {
        DecimalFormat df = new DecimalFormat("###.#");

        for (Band band : bandTable) {
            if (band.frequency >= 1000.0F) {
                band.description = df.format(band.frequency / 1000.0F) + "k";
            } else {
                band.description = df.format(band.frequency);
            }
        }
    }

    @Override
    public void init(int sampleSize, SourceDataLine sourceDataLine) {
        this.fftSampleSize = sampleSize;
        this.fftSampleRate = sourceDataLine.getFormat().getFrameRate();
        this.fft = new FFT(fftSampleSize);
        this.old_FFT = new float[bands];

        computeBandTables();
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public synchronized void render(Graphics2D g2d, int width, int height) {
        float c = 0;
        int b, bd, i, li = 0, mi;
        float fs, m;
        int bm = 1;
        //Preparation used for rendering band frequencies.
        if (showFrequencies) {
            bm = Math.round(32.0F / bandWidth);
            if (bm == 0) {
                bm = 1;
            }
        }
        //FFT processing.
        FloatBuffer[] channelsBuffer = dssContext.getDataNormalized();
        float[] _fft = fft.calculate(channelsMerge(channelsBuffer));
        bandWidth = (float) width / (float) bands;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);

        //Group up available bands using band distribution table.
        for (bd = 0; bd < bands; bd++) {
            //Get band distribution entry.
            i = bdTable[bd].distribution;
            m = 0;
            mi = 0;
            //Find loudest band in group. (Group is from 'li' to 'i').
            for (b = li; b < i; b++) {
                float lf = _fft[b];
                if (lf > m) {
                    m = lf;
                    mi = b;
                }
            }
            li = i;
            //Calculate gain using log, then static gain.
            fs = (m * bgTable[mi]) * gain;
            //Limit over-saturation.
            if (fs > 1.0F) {
                fs = 1.0F;
            }
            //Compute decay.
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
                float[] dist = {0.0F, 0.25F, 0.75F, 1.0F};
                Color[] colors = {Color.red, Color.yellow, Color.green, Color.green.darker().darker()};
                lgp = new LinearGradientPaint(start, end, dist, colors, CycleMethod.REPEAT);
            }

            g2d.setPaint(lgp);
            renderSpectrumBar(g2d, Math.round(c), height, Math.round(bandWidth) - 1,
                    Math.round(fs * height), bd, bdTable[bd], showFrequencies && (bd % bm) == 0);
            c += bandWidth;
        }
    }

    private void renderSpectrumBar(Graphics2D g2d, int x, int y, int w, int h, int bd, Band band, boolean renderFrequency) {
        //Render spectrum bar.
        g2d.fillRect(x, y - h, w, y);
        //Render peak.
        if ((peakColor != null) && (peaksEnabled == true)) {
            g2d.setColor(peakColor);
            if (h > peaks[bd]) {
                peaks[bd] = h;
                peaksDelay[bd] = peakDelay;
            } else {
                peaksDelay[bd]--;
                if (peaksDelay[bd] < 0) {
                    peaks[bd]--;
                }
                if (peaks[bd] < 0) {
                    peaks[bd] = 0;
                }
            }
            g2d.fillRect(x, y - peaks[bd], w, 1);
        }
        //Render frequency string.
        if (renderFrequency) {
            g2d.setColor(Color.white);
            int sx = x + ((w - g2d.getFontMetrics().stringWidth(band.description)) >> 1);
            g2d.drawLine(x + (w >> 1), y, x + (w >> 1), y - (g2d.getFontMetrics().getHeight() - g2d.getFontMetrics().getAscent()));
            g2d.drawString(band.description, sx, y - g2d.getFontMetrics().getHeight());
        }
    }
}
