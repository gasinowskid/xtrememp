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
package xtrememp.visualization;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.geom.Point2D;
import java.util.Map;

/**
 *
 * @author Besmir Beqiri
 */
public class VolumeMeter extends Visualization {

    public static final float DEFAULT_VU_METER_DECAY = 0.05f;
    private LinearGradientPaint lgp;
    private float oldLeft;
    private float oldRight;
    private float decay;
    private int width;
    private int height;

    public VolumeMeter() {
        decay = DEFAULT_VU_METER_DECAY;
    }

    /*
     * 
     * @see xtrememp.visual.Visualization#getDisplayName
     */
    @Override
    public String getDisplayName() {
        return "Volume Meter";
    }

    @Override
    public void setType(String type) {
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public Map<String, String> getTypes() {
        return null;
    }

    /*
     * 
     * @see xtrememp.visual.Visualization#render
     */
    @Override
    public synchronized void render(Graphics g, int width, int height) {
        this.width = width;
        this.height = height;
        renderVolumeMeter(g, leftChannel, rightChannel);
    }

    private void renderVolumeMeter(Graphics g, float[] left, float[] right) {
        float wLeft = 0.0f;
        float wRight = 0.0f;

        for (int a = 0; a < left.length; a++) {
            wLeft += Math.abs(left[a]);
            wRight += Math.abs(right[a]);
        }

        wLeft = ((wLeft * 2.0f) / (float) left.length);
        wRight = ((wRight * 2.0f) / (float) right.length);
        if (wLeft > 1.0f) {
            wLeft = 1.0f;
        }

        if (wRight > 1.0f) {
            wRight = 1.0f;
        }

        if (wLeft >= (oldLeft - decay)) {
            oldLeft = wLeft;
        } else {
            oldLeft -= decay;
            if (oldLeft < 0) {
                oldLeft = 0;
            }
        }

        if (wRight >= (oldRight - decay)) {
            oldRight = wRight;
        } else {
            oldRight -= decay;
            if (oldRight < 0) {
                oldRight = 0;
            }
        }

        int wHeight = (height >> 1) - 8;

        if (lgp == null || width != lgp.getEndPoint().getX()) {
            Point2D start = new Point2D.Float(0, 0);
            Point2D end = new Point2D.Float(this.width, 0);
            float[] dist = {0.0f, 0.25f, 0.75f, 1.0f};
            Color[] colors = {Color.green.darker().darker(), Color.green, Color.yellow, Color.red};
            lgp = new LinearGradientPaint(start, end, dist, colors, CycleMethod.REPEAT);
        }

        ((Graphics2D) g).setPaint(lgp);
        g.fillRect(8, 6, (int) (oldLeft * (float) (width - 32)), wHeight);
        g.fillRect(8, wHeight + 10, (int) (oldRight * (float) (width - 32)), wHeight);
    }
}
