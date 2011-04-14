/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2011 Besmir Beqiri
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
import java.nio.FloatBuffer;
import javax.sound.sampled.SourceDataLine;
import xtrememp.player.dsp.DigitalSignalSynchronizer;
import xtrememp.visualization.spectrum.Band;
import xtrememp.visualization.spectrum.BandDistribution;
import xtrememp.visualization.spectrum.BandGain;
import xtrememp.visualization.spectrum.FFT;
import xtrememp.visualization.spectrum.FlatBandGain;
import xtrememp.visualization.spectrum.FrequencyBandGain;
import xtrememp.visualization.spectrum.LinearBandDistribution;
import xtrememp.visualization.spectrum.LogBandDistribution;

/**
 * Renders a spectrogram.
 *
 * Based on KJ-DSS project by Kristofer Fudalewski (http://sirk.sytes.net).
 *
 * @author Besmir Beqiri
 */
public final class Spectrogram extends Visualization {

    public static final String NAME = "Spectrogram";
    //
    public static final BandDistribution BAND_DISTRIBUTION_LINEAR = new LinearBandDistribution();
    public static final BandDistribution BAND_DISTRIBUTION_LOG = new LogBandDistribution(4, 20.0D);
    public static final BandGain BAND_GAIN_FLAT = new FlatBandGain(4.0F);
    public static final BandGain BAND_GAIN_FREQUENCY = new FrequencyBandGain(4.0F);
    //
    public static final BandDistribution DEFAULT_SPECTROGRAM_BAND_DISTRIBUTION = BAND_DISTRIBUTION_LINEAR;
    public static final BandGain DEFAULT_SPECTROGRAM_BAND_GAIN = BAND_GAIN_FREQUENCY;
    public static final float DEFAULT_SPECTRUM_ANALYSER_GAIN = 5.0F;
    //
    private BandDistribution bandDistribution;
    private BandGain bandGain;
    private Band[] bdTable;
    private float[] bgTable;
    private int bands;
    private int fftSampleSize;
    private float fftSampleRate;
    private FFT fft;
    private float gain;
    private float bandWidth;
    private float[] brgb;
    private float[] frgb;

    public Spectrogram() {
        this.bandDistribution = DEFAULT_SPECTROGRAM_BAND_DISTRIBUTION;
        this.bandGain = DEFAULT_SPECTROGRAM_BAND_GAIN;
        this.gain = DEFAULT_SPECTRUM_ANALYSER_GAIN;

        brgb = new float[3];
        frgb = new float[3];

        setBandCount(DigitalSignalSynchronizer.DEFAULT_SAMPLE_SIZE / 2);
    }

    /**
     * Sets the numbers of bands rendered by the spectrogram.
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
            //Create gain table.
            bgTable = bandGain.create(fft, fftSampleRate);
        }
    }

    @Override
    public void init(int sampleSize, SourceDataLine sourceDataLine) {
        this.fftSampleSize = sampleSize;
        this.fftSampleRate = sourceDataLine.getFormat().getFrameRate();
        this.fft = new FFT(fftSampleSize);

        computeBandTables();
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public void setBackgroundColor(Color backgroundColor) {
        super.setBackgroundColor(backgroundColor);
        freeImage();
    }

    @Override
    public void setForegroundColor(Color foregroundColor) {
        super.setForegroundColor(foregroundColor);
        freeImage();
    }

    @Override
    public synchronized void render(Graphics2D g2d, int width, int height) {
        // FFT processing.
        FloatBuffer[] channelsBuffer = dssContext.getDataNormalized();
        float[] _fft = fft.calculate(channelsMerge(channelsBuffer));
        bandWidth = (float) height / (float) bands;

        // Rendering.
        float y = height;
        int width2 = width - 1;
        int b, bd, i, li = 0, mi;
        float fs, m;
        // Group up available bands using band distribution table.
        for (bd = 0; bd < bands; bd++) {
            // Get band distribution entry.
            i = bdTable[bd].distribution;
            m = 0;
            mi = 0;
            // Find loudest band in group. (Group is from 'li' to 'i').
            for (b = li; b < i; b++) {
                float lf = _fft[b];
                if (lf > m) {
                    m = lf;
                    mi = b;
                }
            }
            li = i;
            // Calculate gain using log, then static gain.
            fs = (m * bgTable[mi]) * gain;
            // Limit over-saturation.
            if (fs > 1.0F) {
                fs = 1.0F;
            }

            // Calculate spectrogram color shifting between foreground and background colors.
            float _fs = 1.0F - fs;
            backgroundColor.getColorComponents(brgb);
            foregroundColor.getColorComponents(frgb);
            Color color = new Color(frgb[0] * fs + brgb[0] * _fs,
                    frgb[1] * fs + brgb[1] * _fs,
                    frgb[2] * fs + brgb[2] * _fs);
            buffGraphics.setColor(color);
            buffGraphics.drawLine(width2, Math.round(y), width2, Math.round(y - bandWidth));

            y -= bandWidth;
        }

        buffGraphics.drawImage(buffImage, -1, 0, null);
    }
}
