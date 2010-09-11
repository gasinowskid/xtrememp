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

import java.awt.Graphics2D;
import java.nio.FloatBuffer;
import xtrememp.player.dsp.DssContext;

/**
 *
 * @author Besmir Beqiri
 */
public abstract class Visualization implements Comparable<Visualization> {

    /**
     * Returns the display name of <code>this</code> visualization.
     *
     * @return the display name of <code>this</code> visualization.
     */
    public abstract String getDisplayName();

    /**
     * Defines the rendering method.
     * 
     * @param g2d a Graphics object used for painting.
     * @param width width of the rendering area.
     * @param height height of the rendering area.
     * @param dssContext a DssContext object containing a reference to the sample data.
     */
    public abstract void render(Graphics2D g2d, int width, int height, DssContext dssContext);

    /**
     * Returns a {@link FloatBuffer} as the result of merging the channels
     * buffers.
     *
     * @param channelsBufffer the channels buffers.
     * @return A {@link FloatBuffer} object.
     */
    public FloatBuffer channelsMerge(FloatBuffer[] channelsBufffer) {
        int ch = channelsBufffer.length;
        for (int a = 0, cap = channelsBufffer[0].capacity(); a < cap; a++) {
            float mcd = 0;
            for (int b = 0; b < ch; b++) {
                mcd += channelsBufffer[b].get(a);
            }
            channelsBufffer[0].put(a, mcd / (float) ch);
        }
        return channelsBufffer[0].asReadOnlyBuffer();
    }

    @Override
    public int compareTo(Visualization vis) {
        return this.getDisplayName().compareTo(vis.getDisplayName());
    }
}
