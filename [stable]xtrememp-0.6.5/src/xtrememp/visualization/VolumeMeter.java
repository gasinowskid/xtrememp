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
package xtrememp.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.Point;
import xtrememp.player.dsp.DigitalSignalSynchronizer.Context;

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
    public synchronized void render(Graphics g, int width, int height, Context dssContext) {
        float leftLevel = 0.0f;
        float rightLevel = 0.0f;
        int length = dssContext.getLength();
        float[][] channels = dssContext.getDataNormalized();

        for (int a = 0; a < length; a++) {
            leftLevel += Math.abs(channels[0][a]);
            rightLevel += Math.abs(channels[1][a]);
        }

        leftLevel = ((leftLevel * 2.0f) / (float) length);
        rightLevel = ((rightLevel * 2.0f) / (float) length);

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

        g.setColor(Color.black);
        g.fillRect(0, 0, width, height);

        if (lgp == null || lgp.getEndPoint().getX() != width) {
            Point start = new Point(0, 0);
            Point end = new Point(width, 0);
            float[] dist = {0.0f, 0.25f, 0.75f, 1.0f};
            Color[] colors = {Color.green.darker().darker(), Color.green, Color.yellow, Color.red};
            lgp = new LinearGradientPaint(start, end, dist, colors, CycleMethod.REPEAT);
        }

        ((Graphics2D) g).setPaint(lgp);

        int wHeight = (height >> 1) - 8;
        g.fillRect(8, 6, (int) (oldLeft * (float) (width - 32)), wHeight);
        g.fillRect(8, wHeight + 10, (int) (oldRight * (float) (width - 32)), wHeight);
    }
}
