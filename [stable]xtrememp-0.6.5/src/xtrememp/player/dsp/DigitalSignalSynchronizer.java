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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
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
public class DigitalSignalSynchronizer implements LineListener, Runnable {

    public static final int DEFAULT_FPS = 60;
    public static final int DEFAULT_SAMPLE_SIZE = 2 * 1024;
    private final List<DigitalSignalProcessor> dspList;
    private final ScheduledExecutorService execService;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock rLock = lock.readLock();
    private final Lock wLock = lock.writeLock();
    private ScheduledFuture schedFuture;
    private int sampleSize = DEFAULT_SAMPLE_SIZE;
    private int framesPerSecond = DEFAULT_FPS;
    private SourceDataLine sourceDataLine;
    private ByteBuffer audioDataBuffer;
    private DssContext dssContext;

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
        this.dspList = Collections.synchronizedList(new ArrayList<DigitalSignalProcessor>());
        this.execService = Executors.newSingleThreadScheduledExecutor();
    }

    /**
     * Adds a DSP to the DSS and forwards any audio data to it at the specified frame rate.
     *
     * @param dsp A class implementing the DigitalSignalProcessor interface.
     */
    public void add(DigitalSignalProcessor dsp) {
        synchronized (dspList) {
            dspList.add(dsp);
            dspList.notifyAll();
        }
    }

    /**
     * Removes the specified DSP from this DSS if it exists.
     *
     * @param dsp A class implementing the DigitalSignalProcessor interface.
     */
    public void remove(DigitalSignalProcessor dsp) {
        synchronized (dspList) {
            dspList.remove(dsp);
        }
    }

    /**
     * Start monitoring the specified SourceDataLine.
     *
     * @param sdl a SourceDataLine.
     */
    protected void open(SourceDataLine sdl) {
        // -- Stop processing previous source data line.
        if (schedFuture != null && !schedFuture.isCancelled()) {
            stop();
        }

        sourceDataLine = sdl;
        dssContext = new DssContext(sourceDataLine, sampleSize);
        audioDataBuffer = ByteBuffer.allocate(sdl.getBufferSize());
    }

    protected void start() {
        long delay = Math.round(1000 / framesPerSecond);
        schedFuture = execService.scheduleWithFixedDelay(this, 0, delay, TimeUnit.MILLISECONDS);
    }

    protected boolean isRunning() {
        if (schedFuture != null) {
            return !schedFuture.isDone();
        }
        return false;
    }

    /**
     * Stop monitoring the current SourceDataLine and release resources.
     */
    protected void stop() {
        if (schedFuture != null) {
            schedFuture.cancel(true);
        }
    }

    protected void close() {
        if (schedFuture != null) {
            schedFuture.cancel(true);
        }
        if (audioDataBuffer != null) {
            audioDataBuffer.clear();
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
    public void run() {
        synchronized (dspList) {
            if (!dspList.isEmpty()) {
                rLock.lock();
                try {
                    dssContext.normalizeData(audioDataBuffer);
                } finally {
                    rLock.unlock();
                }
                // -- Dispatch sample data to digtal signal processors.
                for (DigitalSignalProcessor dsp : dspList) {
                    dsp.process(dssContext);
                }
            } else {
                try {
                    dspList.wait();
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                }
            }
        }
    }

    @Override
    public void update(LineEvent event) {
        LineEvent.Type type = event.getType();
        wLock.lock();
        try {
            if (type.equals(LineEvent.Type.OPEN)) {
                open((SourceDataLine) event.getLine());
            } else if (type.equals(LineEvent.Type.START)) {
                start();
            } else if (type.equals(LineEvent.Type.STOP)) {
                stop();
            } else if (type.equals(LineEvent.Type.CLOSE)) {
                close();
            }
        } finally {
            wLock.unlock();
        }
    }
}
