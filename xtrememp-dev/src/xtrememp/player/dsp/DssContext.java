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
package xtrememp.player.dsp;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Besmir Beqiri
 */
public class DssContext {

    private SourceDataLine sourceDataLine;
    private AudioFormat audioFormat;
    private FloatBuffer[] channelsBuffer;
    private int offset;
    private int sampleSize;
    private int channels;
    private int frameSize;
    private int ssib;
    private int channelSize;
    private float audioSampleSize;

    /**
     * Create a DSS context from a source data line with a fixed sample size.
     *
     * @param sourceDataLine The source data line.
     * @param sampleSize The sample size.
     */
    public DssContext(SourceDataLine sourceDataLine, int sampleSize) {
        this.sourceDataLine = sourceDataLine;
        this.audioFormat = sourceDataLine.getFormat();
        this.sampleSize = sampleSize;

        channels = audioFormat.getChannels();
        frameSize = audioFormat.getFrameSize();
        ssib = audioFormat.getSampleSizeInBits();
        channelSize = frameSize / channels;
        audioSampleSize = (1 << (ssib - 1));

        this.channelsBuffer = new FloatBuffer[channels];
        for (int ch = 0; ch < channels; ch++) {
            channelsBuffer[ch] = FloatBuffer.allocate(sampleSize);
        }
    }

    public void normalizeData(ByteBuffer audioDataBuffer) {
        long lfp = sourceDataLine.getLongFramePosition();
        offset = (int) ((long) (lfp * frameSize) % (long) (audioDataBuffer.capacity()));

        // -- Loop through audio data.
        for (int sp = 0, pos = offset; sp < sampleSize; sp++, pos += frameSize) {
            if (pos >= audioDataBuffer.capacity()) {
                pos = 0;
            }

            // -- Loop through channels.
            for (int ch = 0, cdp = 0; ch < channels; ch++, cdp += channelSize) {

                // -- Sign least significant byte. (PCM_SIGNED)
                float sm = (audioDataBuffer.get(pos + cdp) & 0xFF) - 128.0F;

                for (int bt = 8, bp = 1; bt < ssib; bt += 8) {
                    sm += audioDataBuffer.get(pos + cdp + bp) << bt;
                    bp++;
                }

                // -- Store normalized data.
                channelsBuffer[ch].put(sp, sm / audioSampleSize);
            }
        }
    }

    /**
     * Returns a normalized sample from the DSS data buffer.
     *
     * @return An array of {@link FloatBuffer}.
     */
    public FloatBuffer[] getDataNormalized() {
        return channelsBuffer;
    }

    /**
     * Returns the sample size to read from the data buffer.
     *
     * @return The sample size to read from the data buffer
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Returns the data buffer offset to start reading from. Please note that the offset + length
     * can be beyond the buffer length. This simply means, the rest of data sample has rolled over
     * to the beginning of the data buffer.
     *
     * @return The data buffer offset to start reading from.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the monitored source data line.
     *
     * @return A {@link SourceDataLine} object.
     */
    public SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }
}
