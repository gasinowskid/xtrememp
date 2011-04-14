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
 * Frequency based band gain. More gain is applied as the band frequency increases.
 *
 * Based on KJ-DSS project by Kristofer Fudalewski (http://sirk.sytes.net).
 *
 * @author Besmir Beqiri
 */
public class FrequencyBandGain implements BandGain {

    private float bias;

    /**
     * @param bias A level of bias to flaten out the gain curve. The high the
     *             number the less gain is applied to high frequencies.
     */
    public FrequencyBandGain(float bias) {
        this.bias = bias;
    }

    @Override
    public float[] create(FFT fft, float sampleRate) {
        int oss = fft.getOutputSampleSize();
        //Create a frequency table.
        float[] fqt = fft.calculateFrequencyTable(sampleRate);
        float[] bgTable = new float[oss];

        for (int i = 0; i < oss; i++) {
            bgTable[i] = (((fqt[i] / bias) + 512.0f) / 512.0f) * (bias * 1.5f);
        }

        return bgTable;
    }
}
