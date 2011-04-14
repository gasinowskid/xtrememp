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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.SourceDataLine;

/**
 * @author Kris Fudalewski
 * @author Besmir Beqiri
 *
 * This class provides synchronization between a digital signal processor and speaker output. 
 */
public class DigitalSignalSynchronizer implements LineListener {

    private static final boolean DEBUG_MODE = false;
    private static final int DEFAULT_FPS = 60;
    public static final int DEFAULT_SAMPLE_SIZE = 2048;
    private static final int DEFAULT_OVERRUN_PROTECTION = 8192;
    private static final int DEFAULT_WRITE_CHUNK_SIZE = 8192;
    private final Lock lock = new ReentrantLock();
    private SourceDataLine sourceDataLine;
    private Timer timer;
    private int sampleSize = 1024;
    private int framesPerSecond;
    private int position;
    private byte[] audioDataBuffer;
    private Context context;
    private Normalizer normalizer;
    private Synchronizer synchronizer;
    private List<DigitalSignalProcessor> dspList;
    private Map<String, Object> debugInfo;

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

        if (DEBUG_MODE) {
            debugInfo = new HashMap<String, Object>();
        }
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
     * Creates and returns a normalizer object that can be used to normalizer audio data.
     *
     * @return A normalizer instance.
     */
    public Normalizer getNormalizer() {
        return normalizer;
    }

    /**
     * Removes the specified DSP from this DSPAC if it exists.
     *
     * @param A class implementing the KJDigitalSignalProcessor interface.
     */
    public void remove(DigitalSignalProcessor pSignalProcessor) {
        dspList.remove(pSignalProcessor);
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
            audioDataBuffer = new byte[sdl.getBufferSize() + DEFAULT_OVERRUN_PROTECTION];
            position = 0;
            // -- Reset normalizer.
            normalizer = new Normalizer(sampleSize);
            // -- Initialize DSP registered with this DSS.
            for (DigitalSignalProcessor dsp : dspList) {
                try {
                    dsp.initialize(sampleSize, sdl);
                } catch (Exception pEx) {
                    pEx.printStackTrace();
                }
            }
            // -- Create a new synchronizer...
            synchronizer = new Synchronizer(framesPerSecond);
            // -- ... and then start it.
            long delay = Math.round(1000 / framesPerSecond);
            timer.scheduleAtFixedRate(synchronizer, 0, delay);

            if (DEBUG_MODE) {
                debugInfo.put("FPS", framesPerSecond);
                debugInfo.put("SS", sampleSize);
                debugInfo.put("BS", audioDataBuffer.length);
            }
        }
    }

    /**
     * Stop monitoring the currect SourceDataLine and release resources.
     */
    public synchronized void stop() {
        if (synchronizer != null) {
            lock.lock();
            try {
                synchronizer.cancel();
                synchronizer = null;
                audioDataBuffer = null;
            } finally {
                lock.unlock();
            }
        }
    }

    protected void storeAudioData(byte[] audioData, int offset, int length) {
        if (audioDataBuffer == null) {
            return;
        }

        int overrun = 0;

        if (position + length > audioDataBuffer.length - 1) {
            overrun = (position + length) - audioDataBuffer.length;
            length = audioDataBuffer.length - position;
        }

        System.arraycopy(audioData, offset, audioDataBuffer, position, length);

        if (overrun > 0) {
            System.arraycopy(audioData, offset + length, audioDataBuffer, 0, overrun);
            position = overrun;
        } else {
            position += length;
        }

        if (DEBUG_MODE) {
            debugInfo.put("SP", position);
            debugInfo.put("SO", offset);
            debugInfo.put("SL", length);
            debugInfo.put("OR", overrun);
        }
    }

    /**
     * Writes the entire specified buffer to the monitored source data line an any registered DSPs.
     *
     * @param audioData Data to write.
     */
    public void writeAudioData(byte[] audioData) {
        writeAudioData(audioData, 0, audioData.length);
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

        lock.lock();
        try {
            for (int o = offset; o < offset + length; o += DEFAULT_WRITE_CHUNK_SIZE) {
                int wcs = DEFAULT_WRITE_CHUNK_SIZE;

                if (o + wcs >= audioData.length) {
                    wcs = audioData.length - o;
                }

                sourceDataLine.write(audioData, o, wcs);
                storeAudioData(audioData, o, wcs);
            }
        } finally {
            lock.unlock();
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

        int offset;
        int length;

        /**
         * Create a DSS context with a fixed sample length.
         *
         * @param pLength The sample length.
         */
        public Context(int pLength) {
            length = pLength;
        }

        /**
         * Returns the data buffer of this DSS.
         *
         * @return Data buffer.
         */
        public byte[] getDataBuffer() {
            return audioDataBuffer;
        }

        /**
         * Returns a normalized sample from the DSS data buffer. This method can be used to make a DSP
         * based on KJ DSP v1.1 work properly again.
         *
         * @return Normalized data.
         */
        public float[][] getDataNormalized() {
            return getNormalizer().normalize(audioDataBuffer, offset, length, sourceDataLine);
        }

        /**
         * Return debugging information of the DSS if debugging is enabled.
         *
         * @return A map with debugging information.
         */
        public Map<String, Object> getDebugInfo() {
            return debugInfo;
        }

        /**
         * Returns the sample length to read from the data buffer.
         *
         * @return int
         */
        public int getLength() {
            return length;
        }

        /**
         * Returns the data buffer offset to start reading from. Please note that the offset + length
         * can be beyond the buffere length. This simply means, the rest of data sample has rolled over
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

    public class Normalizer {

        private int sampleSize;
        private float[] left;
        private float[] right;
        private float[][] channels;

        public Normalizer(int sampleSize) {
            this.sampleSize = sampleSize;
            this.left = new float[sampleSize];
            this.right = new float[sampleSize];
            this.channels = new float[][]{left, right};
        }

        public float[][] normalize(byte[] data, int position, int length, SourceDataLine sdl) {
            if (data != null && sdl != null) {
                int ch = sdl.getFormat().getChannels();
                int fs = sdl.getFormat().getFrameSize();
                int ss = sdl.getFormat().getSampleSizeInBits();

                if (ch == 1) {
                    if (ss == 8) {
                        for (int a = 0, c = position; a < sampleSize; a++, c += fs) {
                            if (c >= data.length) {
                                c = 0;
                            }

                            left[a] = (float) ((int) data[c] / 128.0f);
                            right[a] = left[a];
                        }
                    } else if (ss == 16) {
                        for (int a = 0, c = position; a < sampleSize; a++, c += fs) {
                            if (c >= data.length) {
                                c = 0;
                            }

                            left[a] = (float) (((int) data[c + 1] << 8) + data[c]) / 32767.0f;
                            right[a] = left[a];
                        }
                    }
                } else if (ch == 2) {
                    if (ss == 8) {
                        for (int a = 0, c = position; a < sampleSize; a++, c += fs) {
                            if (c >= data.length) {
                                c = 0;
                            }

                            left[a] = (float) ((int) data[c] / 128.0f);
                            right[a] = (float) ((int) data[c + 1] / 128.0f);
                        }
                    } else if (ss == 16) {
                        for (int a = 0, c = position; a < sampleSize; a++, c += fs) {
                            if (c >= data.length) {
                                c = 0;
                            }

                            left[a] = (float) (((int) data[c + 1] << 8) + data[c]) / 32767.0f;
                            right[a] = (float) (((int) data[c + 3] << 8) + data[c + 2]) / 32767.0f;
                        }
                    }
                }
            }
            return channels;
        }
    }
    
    private class Synchronizer extends TimerTask {

        private volatile boolean process = false;
        private int frameSize;

        public Synchronizer(int fps) {
            frameSize = sourceDataLine.getFormat().getFrameSize();
        }

        private int calculateSamplePosition() {
            if (DEBUG_MODE) {
                debugInfo.put("FP", sourceDataLine.getLongFramePosition());
                debugInfo.put("DP", (long) (sourceDataLine.getLongFramePosition() * frameSize) % (long) (audioDataBuffer.length));
            }
            return (int) ((long) (sourceDataLine.getLongFramePosition() * frameSize) % (long) (audioDataBuffer.length));
        }

        @Override
        public void run() {
            if (process) {
                try {
                    context.offset = calculateSamplePosition();

                    // -- Keep thread from hogging CPU.
                    if (dspList.isEmpty()) {
                        Thread.sleep(10);
                    } else {
                        // -- Dispatch sample data to digtal signal processors.
                        for (DigitalSignalProcessor dsp : dspList) {
                            dsp.process(context);
                        }
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
    }
}
