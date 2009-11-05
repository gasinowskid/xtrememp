/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2009 Besmir Beqiri
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
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;

/**
 * @author Besmir Beqiri
 *
 * This class provides synchronization between a digital signal processor and speaker output. 
 */
public class DigitalSignalSynchronizer implements LineListener {

    public static final int DEFAULT_FPS = 60;
    public static final int DEFAULT_SAMPLE_SIZE = 2 * 1024;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rLock = lock.readLock();
    private final Lock wLock = lock.writeLock();
    private int sampleSize = DEFAULT_SAMPLE_SIZE;
    private int framesPerSecond = DEFAULT_FPS;
    private SourceDataLine sourceDataLine;
    private Timer timer;
    private ByteBuffer audioDataBuffer;
    private Context context;
    private Synchronizer synchronizer;
    private List<DigitalSignalProcessor> dspList;

    /**
     * Default constructor.
     */
    public DigitalSignalSynchronizer() {
        this(DEFAULT_SAMPLE_SIZE, DEFAULT_FPS);
    }

    /**
     * @param sampleSize The sample size to extract from audio data sent to the SourceDataLine.
     * @param framesPerSecond The desired refresh rate per second of registered DSP's.
     */
    public DigitalSignalSynchronizer(int sampleSize, int framesPerSecond) {
        this.sampleSize = sampleSize;
        this.framesPerSecond = framesPerSecond;
        this.dspList = new ArrayList<DigitalSignalProcessor>();
        this.timer = new Timer();
    }

    /**
     * Adds a DSP to the DSS and forwards any audio data to it at the specified frame rate.
     *
     * @param signalProcessor A class implementing the DigitalSignalProcessor interface.
     */
    public void add(DigitalSignalProcessor signalProcessor) {
        if (sourceDataLine != null) {
            signalProcessor.initialize(sampleSize, sourceDataLine);
        }
        dspList.add(signalProcessor);
    }

    /**
     * Removes the specified DSP from this DSS if it exists.
     *
     * @param A class implementing the DigitalSignalProcessor interface.
     */
    public void remove(DigitalSignalProcessor signalProcessor) {
        dspList.remove(signalProcessor);
    }

    /**
     * Start monitoring the specified SourceDataLine.
     *
     * @param pSdl A SourceDataLine.
     */
    public synchronized void start(SourceDataLine sdl) {
        // -- Stop processing previous source data line.
        if (synchronizer != null) {
            stop();
        }

        if (synchronizer == null) {
            sourceDataLine = sdl;
//            sampleSize = (int) (Math.round(sourceDataLine.getFormat().getFrameRate() / (float) fps));
            context = new Context(sampleSize);
            // -- Allocate a little more memory than the SDL buffer to prevent buffer overlapping.
            audioDataBuffer = ByteBuffer.allocate(sdl.getBufferSize());
            // -- Initialize DSP registered with this DSS.
            for (DigitalSignalProcessor dsp : dspList) {
                try {
                    dsp.initialize(sampleSize, sdl);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            // -- Create a new synchronizer...
            synchronizer = new Synchronizer();
            // -- ... and then start it.
            long delay = Math.round(1000 / framesPerSecond);
            timer.scheduleAtFixedRate(synchronizer, 0, delay);
        }
    }

    /**
     * Stop monitoring the currect SourceDataLine and release resources.
     */
    public synchronized void stop() {
        if (synchronizer != null) {
            wLock.lock();
            try {
                synchronizer.cancel();
                synchronizer = null;
                audioDataBuffer.clear();
            } finally {
                wLock.unlock();
            }
        }
    }

    /**
     * Writes part of specified buffer to the monitored source data line an any registered DSPs.
     *
     * @param audioData Data to write.
     * @param offset Offset to start reading from the buffer.
     * @param length The length from the specified offset to read.
     */
    public void writeAudioData(byte[] audioData, int offset, int length) {
        if (audioDataBuffer == null) {
            return;
        }

        wLock.lock();
        try {
            if (audioDataBuffer.remaining() < length) {
                audioDataBuffer.clear();
            }
            audioDataBuffer.put(audioData, offset, length);
        } finally {
            wLock.unlock();
        }
    }

    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
        if (type.equals(LineEvent.Type.OPEN)) {
            start((SourceDataLine) event.getLine());
        } else if (type.equals(LineEvent.Type.START)) {
            synchronizer.setEnabled(true);
        } else if (type.equals(LineEvent.Type.STOP)) {
            synchronizer.setEnabled(false);
        } else if (type.equals(LineEvent.Type.CLOSE)) {
            stop();
        }
    }

    public class Context {

        private int offset;
        private int sampleSize;
        private float[] left;
        private float[] right;
        private float[][] channels;

        /**
         * Create a DSS context with a fixed sample length.
         *
         * @param pLength The sample length.
         */
        public Context(int sampleSize) {
            this.sampleSize = sampleSize;
            this.left = new float[sampleSize];
            this.right = new float[sampleSize];
            this.channels = new float[][]{left, right};
        }

        /**
         * Returns the data buffer of this DSS.
         *
         * @return Data buffer.
         */
        public ByteBuffer getDataBuffer() {
            return audioDataBuffer;
        }

        public void normalizeData() {
            int ch = sourceDataLine.getFormat().getChannels();
            int fs = sourceDataLine.getFormat().getFrameSize();
            int ss = sourceDataLine.getFormat().getSampleSizeInBits();
            long lfp = sourceDataLine.getLongFramePosition();
            offset = (int) ((long) (lfp * fs) % (long) (audioDataBuffer.capacity()));

            rLock.lock();
            try {
                if (ch == 1) {
                    if (ss == 8) {
                        for (int a = 0, c = offset; a < sampleSize; a++, c += fs) {
                            if (c >= audioDataBuffer.capacity()) {
                                c = 0;
                            }

                            left[a] = (float) ((int) audioDataBuffer.get(c) / 128.0f);
                            right[a] = left[a];
                        }
                    } else if (ss == 16) {
                        for (int a = 0, c = offset; a < sampleSize; a++, c += fs) {
                            if (c >= audioDataBuffer.capacity()) {
                                c = 0;
                            }

                            left[a] = (float) (((int) audioDataBuffer.get(c + 1) << 8) + audioDataBuffer.get(c)) / 32767.0f;
                            right[a] = left[a];
                        }
                    }
                } else if (ch == 2) {
                    if (ss == 8) {
                        for (int a = 0, c = offset; a < sampleSize; a++, c += fs) {
                            if (c >= audioDataBuffer.capacity()) {
                                c = 0;
                            }

                            left[a] = (float) ((int) audioDataBuffer.get(c) / 128.0f);
                            right[a] = (float) ((int) audioDataBuffer.get(c + 1) / 128.0f);
                        }
                    } else if (ss == 16) {
                        for (int a = 0, c = offset; a < sampleSize; a++, c += fs) {
                            if (c >= audioDataBuffer.capacity()) {
                                c = 0;
                            }

                            left[a] = (float) (((int) audioDataBuffer.get(c + 1) << 8) + audioDataBuffer.get(c)) / 32767.0f;
                            right[a] = (float) (((int) audioDataBuffer.get(c + 3) << 8) + audioDataBuffer.get(c + 2)) / 32767.0f;
                        }
                    }
                }
            } finally {
                rLock.unlock();
            }
        }

        /**
         * Returns a normalized sample from the DSS data buffer.
         *
         * @return Normalized data.
         */
        public float[][] getDataNormalized() {
            return channels;
        }

        /**
         * Returns the sample length to read from the data buffer.
         *
         * @return int
         */
        public int getLength() {
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

    private class Synchronizer extends TimerTask {

        private volatile boolean process = false;

        public Synchronizer() {
        }

        @Override
        public void run() {
            if (process) {
                try {
                    context.normalizeData();
                    // -- Dispatch sample data to digtal signal processors.
                    for (DigitalSignalProcessor dsp : dspList) {
                        dsp.process(context);
                    }
                } catch (Exception ex) {
                    System.err.println(" DSP Exception: ");
                    ex.printStackTrace();
                }
            }
        }

        public void setEnabled(boolean b) {
            process = b;
        }

        public boolean isEnabled() {
            return process;
        }
    }
}
