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
 * Interface for band distribution types. Band distribution refers to combining
 * band data into groups therefore reducing the number of visible bands. For
 * example, a traditional 10 band spectrum analyzer contains only 10 visible
 * frequency bands sampled from a potentially more than hundreds or more
 * frequency bands. In order to distribute the bands into only 10, several
 * different distributions can be typically be used such as log or simply linear
 * distribution.
 *
 * @see LinearBandDistribution
 * @see LogBandDistribution
 * 
 * Based on KJ-DSS project by Kristofer Fudalewski (http://sirk.sytes.net).
 *
 * @author Besmir Beqiri
 */
public interface BandDistribution {

    /**
     * @param bandCount  The desired number of visible bands.
     * @param fft        The FFT instance used for the spectrum analyser.
     * @param sampleRate The sample rate of the data to process.
     *
     * @return A band distribution table.
     */
    Band[] create(int bandCount, FFT fft, float sampleRate);
}
