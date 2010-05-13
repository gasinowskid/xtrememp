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
     * @param dssContext
     */
    public abstract void render(Graphics2D g2d, int width, int height, DssContext dssContext);

    @Override
    public int compareTo(Visualization obj) {
        return this.getDisplayName().compareTo(obj.getDisplayName());
    }
}
