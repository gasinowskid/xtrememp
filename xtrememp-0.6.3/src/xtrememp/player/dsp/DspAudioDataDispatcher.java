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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Besmir Beqiri
 */
public class DspAudioDataDispatcher implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(DspAudioDataDispatcher.class);
    /**
     * Default sample size value
     */
    public static final int DEFAULT_SAMPLE_SIZE = 4 * 512;
    private final Lock lock = new ReentrantLock();
    private final Condition lockCondition = lock.newCondition();
    private int sampleSize;
    private byte[] audioData;
    private float[] leftChannel;
    private float[] rightChannel;
//    private SignalProcessor signalProcessor;
    private List<DigitalSignalProcessor> dspList = new ArrayList<DigitalSignalProcessor>();

    /**
     * Default constructor
     */
    public DspAudioDataDispatcher() {
        this(DEFAULT_SAMPLE_SIZE);
    }

    /**
     * @param sampleSize The sample size to extract from audio data sent to the SourceDataLine.
     * @param framesPerSecond The desired refresh rate per second of registered DSP's.
     */
    public DspAudioDataDispatcher(int sampleSize) {
        this.sampleSize = sampleSize;
        this.leftChannel = new float[sampleSize];
        this.rightChannel = new float[sampleSize];

//        signalProcessor = new SignalProcessor();
//        signalProcessor.start();
    }

    /**
     * Adds a DSP to the DSPADC
     *
     * @param signalProcessor
     */
    public void add(DigitalSignalProcessor signalProcessor) {
        if (signalProcessor != null && !dspList.contains(signalProcessor)) {
            dspList.add(signalProcessor);
            logger.info("Added dsp {}", signalProcessor);
        }
    }

    /**
     * Removes the specified DSP from this DSPADC if it exists.
     *
     * @param signalProcessor
     */
    public void remove(DigitalSignalProcessor signalProcessor) {
        dspList.remove(signalProcessor);
        logger.info("Removed dsp {}", signalProcessor);
    }

    /**
     * Stores audio data to the consumer
     * @param audioData a byte array containing data to be stored
     * @param length the length of the valid data in the array
     */
    public void storeAudioData(byte[] audioData, int length) {
        lock.lock();
        try {
            if (audioData == null) {
                return;
            }
            this.audioData = Arrays.copyOf(audioData, length);
            lockCondition.signal();
        } finally {
            lock.unlock();
        }
    }

    /**
     * @return the sampleSize
     */
    public int getSampleSize() {
        return sampleSize;
    }

    /**
     * @param sampleSize the sampleSize to set
     */
    public void setSampleSize(int sampleSize) {
        this.sampleSize = sampleSize;
    }

    @Override
    public void run() {
        lock.lock();
        try {
            while (true) {
                lockCondition.await();
                int length = audioData.length;
                int c = 0;
                for (int a = 0; a < sampleSize; a++, c += 4) {
                    // Separate channels and normalize ( 16-bit stereo )
                    if (c >= length) {
                        c -= length;
                    }
                    if (c > length) {
                        c = 0;
                    }
                    leftChannel[a] = (((int) audioData[c + 1] << 8) + audioData[c]) / 32767.0f;
                    rightChannel[a] = (((int) audioData[c + 3] << 8) + audioData[c + 2]) / 32767.0f;
                }

                // Dispatch sample data to digtal signal processors.
                for (DigitalSignalProcessor dsp : dspList) {
                    dsp.process(leftChannel, rightChannel);
                }
            }
        } catch (InterruptedException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            lock.unlock();
        }
        logger.info("{} completed", Thread.currentThread().getName());
    }

//    private class SignalProcessor extends Thread {
//
//        public SignalProcessor() {
//            super("SignalProcessor");
//        }
//
//        @Override
//        public void run() {
//            lock.lock();
//            try {
//                while (true) {
//                    lockCondition.await();
//                    int length = audioData.length;
//                    int c = 0;
//                    for (int a = 0; a < sampleSize; a++, c += 4) {
//                        // Separate channels and normalize ( 16-bit stereo )
//                        if (c >= length) {
//                            c -= length;
//                        }
//                        if (c > length) {
//                            c = 0;
//                        }
//                        leftChannel[a] = (((int) audioData[c + 1] << 8) + audioData[c]) / 32767.0f;
//                        rightChannel[a] = (((int) audioData[c + 3] << 8) + audioData[c + 2]) / 32767.0f;
//                    }
//
//                    // Dispatch sample data to digtal signal processors.
//                    for (DigitalSignalProcessor dsp : dspList) {
//                        dsp.process(leftChannel, rightChannel);
//                    }
//                }
//            } catch (InterruptedException ex) {
//                logger.error(ex.getMessage(), ex);
//            } finally {
//                lock.unlock();
//            }
//            logger.info("{} completed", Thread.currentThread().getName());
//        }
//    }
}
