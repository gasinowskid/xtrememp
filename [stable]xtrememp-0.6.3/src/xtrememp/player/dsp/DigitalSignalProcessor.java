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
package xtrememp.player.dsp;

/**
 * @author Besmir Beqiri
 */
public interface DigitalSignalProcessor {

    /**
     * Called by the DspAudioDataConsumer.
     *
     * @param leftChannel Audio data for the left channel.
     * @param rightChannel Audio data for the right channel.
     */
    void process(float[] leftChannel, float[] rightChannel);
}
