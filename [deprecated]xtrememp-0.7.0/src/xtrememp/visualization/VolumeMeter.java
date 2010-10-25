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
package xtrememp.visualization;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import java.nio.FloatBuffer;
import xtrememp.player.dsp.DssContext;

/**
 *
 * @author Besmir Beqiri
 */
public class VolumeMeter extends Visualization {

    public static final String NAME = "Volume Meter";
    public static final float DEFAULT_VU_METER_DECAY = 0.05f;
    private LinearGradientPaint lgp;
    private float oldLeft;
    private float oldRight;
    private float decay;

    public VolumeMeter() {
        decay = DEFAULT_VU_METER_DECAY;
    }

    /*
     * 
     * @see xtrememp.visual.Visualization#getDisplayName
     */
    @Override
    public String getDisplayName() {
        return NAME;
    }

    /*
     * 
     * @see xtrememp.visual.Visualization#render
     */
    @Override
    public synchronized void render(Graphics2D g2d, int width, int height, DssContext dssContext) {
        float leftLevel = 0.0f;
        float rightLevel = 0.0f;
        int sampleSize = dssContext.getSampleSize();
        FloatBuffer leftChannel = dssContext.getLeftChannelBuffer();
        FloatBuffer rightChannel = dssContext.getRightChannelBuffer();

        for (int i = 0; i < sampleSize; i++) {
            leftLevel += Math.abs(leftChannel.get(i));
            rightLevel += Math.abs(rightChannel.get(i));
        }

        leftLevel = ((leftLevel * 2.0f) / (float) sampleSize);
        rightLevel = ((rightLevel * 2.0f) / (float) sampleSize);

        if (leftLevel > 1.0f) {
            leftLevel = 1.0f;
        }

        if (rightLevel > 1.0f) {
            rightLevel = 1.0f;
        }

        if (leftLevel >= (oldLeft - decay)) {
            oldLeft = leftLevel;
        } else {
            oldLeft -= decay;
            if (oldLeft < 0) {
                oldLeft = 0;
            }
        }

        if (rightLevel >= (oldRight - decay)) {
            oldRight = rightLevel;
        } else {
            oldRight -= decay;
            if (oldRight < 0) {
                oldRight = 0;
            }
        }

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, width, height);

        if (lgp == null || lgp.getEndPoint().getX() != width) {
            Point start = new Point(0, 0);
            Point end = new Point(width, 0);
            float[] dist = {0.0f, 0.25f, 0.75f, 1.0f};
            Color[] colors = {Color.green.darker().darker(), Color.green, Color.yellow, Color.red};
            lgp = new LinearGradientPaint(start, end, dist, colors, CycleMethod.REPEAT);
        }

        g2d.setPaint(lgp);

        int wHeight = (height >> 1) - 8;
        g2d.fillRect(8, 6, (int) (oldLeft * (float) (width - 32)), wHeight);
        g2d.fillRect(8, wHeight + 10, (int) (oldRight * (float) (width - 32)), wHeight);
    }
}
