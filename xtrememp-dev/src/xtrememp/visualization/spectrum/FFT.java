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
package xtrememp.visualization.spectrum;

import java.nio.FloatBuffer;

/**
 * Fast Fourier Transformation class used for calculating the realtime spectrum
 * analyzer.
 * 
 * Based on KJ-DSS project by Kristofer Fudalewski (http://sirk.sytes.net).
 *
 * @author Besmir Beqiri
 */
public class FFT {

    private float[] xre;
    private float[] xim;
    private float[] mag;
    private float[] fftSin;
    private float[] fftCos;
    private int[] fftBr;
    private int ss, ss2, nu;

    /**
     * @param sampleSize The amount of the sample provided to the "calculate"
     * method to use during FFT calculations, this is used to prepare the
     * calculation tables in advance. This value is automatically rounded up to
     * the nearest power of 2.
     */
    public FFT(int sampleSize) {
        nu = (int) Math.ceil(Math.log(sampleSize) / Math.log(2.0D));

        // -- Calculate the nearest sample size to a power of 2.
        ss = (int) Math.pow(2.0D, nu);
        ss2 = ss >> 1;

        // -- Allocate calculation buffers.
        xre = new float[ss];
        xim = new float[ss];
        mag = new float[ss2];

        // -- Allocate FFT SIN/COS tables.
        fftSin = new float[nu * ss2];
        fftCos = new float[nu * ss2];

        prepareTables();
    }

    /**
     * Bit swapping method.
     */
    private int bitrev(int j, int nu) {
        int j1 = j;
        int k = 0;

        for (int i = 0; i < nu; i++) {
            int j2 = j1 >> 1;
            k = (k << 1) + j1 - (j2 << 1);
            j1 = j2;
        }

        return k;
    }

    /**
     * Converts sound data over time into pressure values. (FFT)
     *
     * @param sample the sample to compute FFT values on.
     * @return the results of the calculation, normalized between 0.0 and 1.0.
     */
    public float[] calculate(FloatBuffer sample) {
        int n2 = ss2;

        // -- Fill buffer.
        for (int a = 0, len = sample.capacity(); a < len; a++) {
            xre[a] = sample.get(a);
            xim[a] = 0.0f;
        }

        // -- Clear the remainder of the buffer.
        for (int a = sample.capacity(); a < ss; a++) {
            xre[a] = 0.0f;
            xim[a] = 0.0f;
        }

        int x = 0;
        for (int l = 0; l < nu; l++) {
            for (int k = 0; k < ss; k += n2) {
                for (int i = 0; i < n2; i++) {
                    // -- Tabled sin/cos
                    final float c = fftCos[x];
                    final float s = fftSin[x];

                    final int kn2 = k + n2;

                    final float tr = xre[kn2] * c + xim[kn2] * s;
                    final float ti = xim[kn2] * c - xre[kn2] * s;

                    xre[kn2] = xre[k] - tr;
                    xim[kn2] = xim[k] - ti;
                    xre[k] += tr;
                    xim[k] += ti;

                    k++;
                    x++;
                }
            }
            n2 >>= 1;
        }

        // -- Reorder output.
        for (int k = 0; k < ss; k++) {
            // -- Use tabled BR values.
            final int r = fftBr[k];
            if (r > k) {
                final float tr = xre[k];
                xre[k] = xre[r];
                xre[r] = tr;

                final float ti = xim[k];
                xim[k] = xim[r];
                xim[r] = ti;
            }
        }

        // -- Calculate magnitude.
        for (int i = 0; i < ss2; i++) {
            mag[i] = Math.abs(((float) (Math.sqrt((xre[i] * xre[i]) + (xim[i] * xim[i]))) / ss));
        }

        return mag;
    }

    /**
     * Calculates a table of frequencies represented by the amplitude data
     * returned by the 'calculate' method. Each element states the end of the
     * frequency range of the corresponding FFT band (or bin). For example:
     *
     * Range of band 0 =                 0.0 hz to frequencyTable[ 0 ] hz
     * Range of band 1 = frequencyTable[ 0 ] hz to frequencyTable[ 1 ] hz
     * Range of band 2 = frequencyTable[ 1 ] hz to frequencyTable[ 2 ] hz
     *   ... and so on.
     *
     * Calculation uses the sample size rounded to the nearest power of 2 of
     * the FFT instance and the sample rate parameter to build this table.
     *
     * @param  sampleRate The sample rate used to calculate the frequency table.
     *                    Usually the sample rate of the input to the FFT
     *                    calculate method.
     * @return An array of frequency limits for each band.
     */
    public float[] calculateFrequencyTable(float sampleRate) {
        float fr = sampleRate / 2.0f;

        //Calculate band width.
        float bw = fr / ss2;

        //Store for frequency table.
        float[] ft = new float[(int) ss2];

        //Build band range table.
        int b = 0;
        for (float fp = (bw / 2.0f); fp <= fr; fp += bw) {
            ft[b] = fp;
            b++;
        }

        return ft;
    }

    /**
     * Returns the sample size this FFT instance uses for processing.
     * It is automatically rounded to the nearest power of 2.
     *
     * @return The sample size used by the calculate method.
     */
    public int getInputSampleSize() {
        return ss;
    }

    /**
     * Returns the sample size this FFT instance returns after processing.
     * It is automatically rounded to the nearest power of 2.
     *
     * @return The sample size returned by the calculate method.
     */
    public int getOutputSampleSize() {
        return ss2;
    }

    /**
     * Pre-calculates SIN/COS and bitrev tables in memory.
     */
    private void prepareTables() {
        int n2 = ss2;
        int nu1 = nu - 1;
        int k = 0;
        int x = 0;

        // -- Prepare SIN/COS tables.
        for (int l = 0; l < nu; l++) {
            while (k < ss) {
                for (int i = 0; i < n2; i++) {
                    double p = bitrev(k >> nu1, nu);
                    double arg = (Math.PI * p * 2.0D) / (double) ss;

                    fftSin[x] = (float) Math.sin(arg);
                    fftCos[x] = (float) Math.cos(arg);

                    k++;
                    x++;
                }
                k += n2;
            }
            k = 0;
            nu1--;
            n2 >>= 1;
        }

        // -- Prepare bitrev table.
        fftBr = new int[ss];
        for (k = 0; k < ss; k++) {
            fftBr[k] = bitrev(k, nu);
        }
    }
}
