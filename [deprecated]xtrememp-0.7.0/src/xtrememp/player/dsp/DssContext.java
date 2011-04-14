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
    private FloatBuffer leftChannel;
    private FloatBuffer rightChannel;
    private int offset = 0;
    private int sampleSize = 0;

    /**
     * Create a DSS context with a fixed sample length.
     *
     * @param sampleSize The sample size.
     */
    public DssContext(SourceDataLine sourceDataLine, int sampleSize) {
        this.sourceDataLine = sourceDataLine;
        this.sampleSize = sampleSize;
        this.leftChannel = FloatBuffer.allocate(sampleSize);
        this.rightChannel = FloatBuffer.allocate(sampleSize);
    }

    public void normalizeData(ByteBuffer audioDataBuffer) {
        AudioFormat audioFormat = sourceDataLine.getFormat();
        int ch = audioFormat.getChannels();
        int fs = audioFormat.getFrameSize();
        int ss = audioFormat.getSampleSizeInBits();
        long lfp = sourceDataLine.getLongFramePosition();
        offset = (int) ((long) (lfp * fs) % (long) (audioDataBuffer.capacity()));
        if (ch == 1) {
            if (ss == 8) {
                for (int a = 0, c = offset; a < sampleSize; a++, c += fs) {
                    if (c >= audioDataBuffer.capacity()) {
                        c = 0;
                    }

                    float value = (float) ((int) audioDataBuffer.get(c) / 128.0f);
                    leftChannel.put(a, value);
                    rightChannel.put(a, value);
                }
            } else if (ss == 16) {
                for (int a = 0, c = offset; a < sampleSize; a++, c += fs) {
                    if (c >= audioDataBuffer.capacity()) {
                        c = 0;
                    }

                    float value = (float) (((int) audioDataBuffer.get(c + 1) << 8) + audioDataBuffer.get(c)) / 32767.0f;
                    leftChannel.put(a, value);
                    rightChannel.put(a, value);
                }
            }
        } else if (ch == 2) {
            if (ss == 8) {
                for (int a = 0, c = offset; a < sampleSize; a++, c += fs) {
                    if (c >= audioDataBuffer.capacity()) {
                        c = 0;
                    }

                    leftChannel.put(a, (float) ((int) audioDataBuffer.get(c) / 128.0f));
                    rightChannel.put(a, (float) ((int) audioDataBuffer.get(c + 1) / 128.0f));
                }
            } else if (ss == 16) {
                for (int a = 0, c = offset; a < sampleSize; a++, c += fs) {
                    if (c >= audioDataBuffer.capacity()) {
                        c = 0;
                    }

                    leftChannel.put(a, (float) (((int) audioDataBuffer.get(c + 1) << 8) + audioDataBuffer.get(c)) / 32767.0f);
                    rightChannel.put(a, (float) (((int) audioDataBuffer.get(c + 3) << 8) + audioDataBuffer.get(c + 2)) / 32767.0f);
                }
            }
        }
    }

    /**
     * Returns a normalized sample from the DSS data buffer.
     *
     * @return normalized data.
     */
    public FloatBuffer getLeftChannelBuffer() {
        return leftChannel.asReadOnlyBuffer();
    }

    /**
     * Returns a normalized sample from the DSS data buffer.
     *
     * @return normalized data.
     */
    public FloatBuffer getRightChannelBuffer() {
        return rightChannel.asReadOnlyBuffer();
    }

    /**
     * Returns the sample size to read from the data buffer.
     *
     * @return int
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * Returns the data buffer offset to start reading from. Please note that the offset + length
     * can be beyond the buffer length. This simply means, the rest of data sample has rolled over
     * to the beginning of the data buffer. See the Normalizer inner class for an example.
     *
     * @return int
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Returns the monitored source data line.
     *
     * @return A source data line.
     */
    public SourceDataLine getSourceDataLine() {
        return sourceDataLine;
    }
}
