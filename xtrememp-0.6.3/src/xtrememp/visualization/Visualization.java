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

import java.awt.Graphics;
import java.util.Map;
import xtrememp.player.dsp.DspAudioDataDispatcher;

/**
 *
 * @author Besmir Beqiri
 */
public abstract class Visualization {

    public float[] leftChannel = new float[DspAudioDataDispatcher.DEFAULT_SAMPLE_SIZE];
    public float[] rightChannel = new float[DspAudioDataDispatcher.DEFAULT_SAMPLE_SIZE];

    public abstract void setType(String name);

    public abstract String getTypeName();

    public abstract Map<String, String> getTypes();

    /**
     * Returns the display name of <code>this</code> visualization.
     *
     * @return the display name of <code>this</code> visualization.
     */
    public abstract String getDisplayName();

    /**
     * Defines the rendering method.
     * 
     * @param g a Graphics object used for painting.
     * @param width width of the rendering area.
     * @param height height of the rendering area.
     */
    public abstract void render(Graphics g, int width, int height);
}
