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

/**
 * Linear based band distribution class.
 * 
 * Based on KJ-DSS project by Kristofer Fudalewski (http://sirk.sytes.net).
 *
 * @author Besmir Beqiri
 */
public class LinearBandDistribution implements BandDistribution {

    @Override
    public Band[] create(int bandCount, FFT fft, float sampleRate) {
        //We actually only use half of the available data because the higher
        //bands are not audible by humans.
        int oss = fft.getOutputSampleSize();
        int r = (int) ((double) oss / (double) bandCount);

        //Create a frequency table.
        float[] fqt = fft.calculateFrequencyTable(sampleRate);
        float lfq = 0.0f;

        Band[] bdTable = new Band[bandCount];
        int band = 0;

        for (double a = r; a <= oss && band < bandCount; a += r) {
            //Build band instance with distribution, frequency range, and gain info.
            bdTable[band] = new Band( (int) a, lfq, fqt[(int) a - r]);
            lfq = fqt[(int) a - r];
            band++;
        }

        return bdTable;
    }
}
