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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.VolatileImage;
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
    protected BandDistribution bandDistribution;
    protected BandGain bandGain;
    protected Band[] bdTable;
    protected float[] bgTable;
    protected int bands;
    protected int fftSampleSize;
    protected float fftSampleRate;
    protected FFT fft;
    private float gain;
    private float bandWidth;
    private float[] brgb;
    private float[] frgb;
    private GraphicsConfiguration gc;
    private VolatileImage image1;
    private VolatileImage image2;

    public Spectrogram() {
        this.bandDistribution = DEFAULT_SPECTROGRAM_BAND_DISTRIBUTION;
        this.bandGain = DEFAULT_SPECTROGRAM_BAND_GAIN;
        this.gain = DEFAULT_SPECTRUM_ANALYSER_GAIN;

        brgb = new float[3];
        frgb = new float[3];

        setBandCount(DigitalSignalSynchronizer.DEFAULT_SAMPLE_SIZE / 2);
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
        image1 = null;
    }

    @Override
    public void setForegroundColor(Color foregroundColor) {
        super.setForegroundColor(foregroundColor);
        image1 = null;
    }

    @Override
    public synchronized void render(Graphics2D g2d, int width, int height) {
        if (image1 == null || (image1.getWidth() != width || image1.getHeight() != height)) {
            createImages(width, height);
        }
        do {
            int valCode1 = image1.validate(gc);
            int valCode2 = image2.validate(gc);
            if (valCode1 == VolatileImage.IMAGE_RESTORED || valCode2 == VolatileImage.IMAGE_RESTORED) {
                fillBackground(backgroundColor);
            } else if (valCode1 == VolatileImage.IMAGE_INCOMPATIBLE
                    || valCode2 == VolatileImage.IMAGE_INCOMPATIBLE) {
                createImages(width, height);
            }

            //FFT processing.
            FloatBuffer[] channelsBuffer = dssContext.getDataNormalized();
            float[] _fft = fft.calculate(channelsMerge(channelsBuffer));
            bandWidth = (float) height / (float) bands;

            Graphics2D g2d1 = image1.createGraphics();
            g2d1.drawImage(image2, -1, 0, null);

            float y = height;
            int width2 = width - 1;
            int b, bd, i, li = 0, mi;
            float fs, m;
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

                //Calculate spectrogram color shifting between foreground and background colors.
                float _fs = 1.0F - fs;
                backgroundColor.getColorComponents(brgb);
                foregroundColor.getColorComponents(frgb);
                Color color = new Color(frgb[0] * fs + brgb[0] * _fs,
                        frgb[1] * fs + brgb[1] * _fs,
                        frgb[2] * fs + brgb[2] * _fs);
                g2d1.setColor(color);
                g2d1.drawLine(width2, Math.round(y), width2, Math.round(y - bandWidth));
                
                y -= bandWidth;
            }
            g2d1.dispose();

            Graphics2D g2d2 = image2.createGraphics();
            g2d2.drawImage(image1, 0, 0, null);
            g2d2.dispose();

            g2d.drawImage(image2, 0, 0, null);
        } while (image1.contentsLost() || image2.contentsLost());
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
        if (valCode1 == VolatileImage.IMAGE_INCOMPATIBLE
                || valCode2 == VolatileImage.IMAGE_INCOMPATIBLE) {
            createImages(width, height);
        }

        fillBackground(backgroundColor);
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
}
