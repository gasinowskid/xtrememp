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

import java.util.ArrayList;
import java.util.List;

/**
 * Log based band distribution class.
 * 
 * Based on KJ-DSS project by Kristofer Fudalewski (http://sirk.sytes.net).
 *
 * @author Besmir Beqiri
 */
public class LogBandDistribution implements BandDistribution {

    private double lso;
    private int sso;

    /**
     * Create a log band distribution instance supplying a sub-sonic offset and
     * a log scale offset. The sub-sonic offset allows the first 'n' most bands
     * to be combined into the first band group, while the remaining bands will
     * follow the log distribution curve. The log scale offset refers to at what
     * point in the log scale to use for distribution calculations. The lower
     * the number, the few bands per group for the first few band groups.
     *
     * @param subSonicOffset Groups the first 'n' bands into the sub-sonic band
     *                       group. (default: 5)
     * @param logScaleOffset Starting point on the log scale. (default: 20.0)
     */
    public LogBandDistribution(int subSonicOffset, double logScaleOffset) {
        sso = subSonicOffset;
        lso = logScaleOffset;
    }

    @Override
    public Band[] create(int bandCount, FFT fft, float sampleRate) {
        //Check the output size from the FFT instance to build the band table.
        int hss = fft.getOutputSampleSize() - sso;

        double o = Math.log(lso);
        double r = (double) (bandCount - 1) / (Math.log(hss + lso) - o);

        //Create a frequency table.
        float[] fqt = fft.calculateFrequencyTable(sampleRate);
        float lfq = fqt[sso];
        int lcb = 1;

        List<Band> bands = new ArrayList<Band>();
        //Subsonic bands group.
        bands.add(new Band(sso, 0, lfq));

        //Divid reset of bands using log.
        for (int b = 0; b < hss; b++) {
            //Calculate current band.
            double cb = ((Math.log((double) b + lso) - o) * r) + 1.0D;
            if (Math.round(cb) != lcb) {
                bands.add(new Band(b + sso, lfq, fqt[b + sso]));
                lfq = fqt[b + sso];
                lcb = (int) Math.round(cb);
            }
        }

        //Fill in last entry if necessary.
        if (bands.size() < bandCount) {
            bands.add(new Band((hss - 1) + sso, lfq, fqt[(hss - 1) + sso]));
        }

        return bands.toArray(new Band[bands.size()]);
    }
}
