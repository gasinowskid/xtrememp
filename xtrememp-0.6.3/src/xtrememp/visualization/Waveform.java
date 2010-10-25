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
import java.util.Map;

/**
 *
 * @author Besmir Beqiri
 */
public class Waveform extends Visualization {

    private int width;
    private int height;

    @Override
    public void setType(String name) {
    }

    @Override
    public String getTypeName() {
        return null;
    }

    @Override
    public Map<String, String> getTypes() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Waveform";
    }

    @Override
    public void render(Graphics g, int width, int height) {
        this.width = width;
        this.height = height >> 1;
        renderWaveform(g);
    }

    private void renderWaveform(Graphics g) {
        g.setColor(Color.blue);
        for (int c = 0; c < width && c < leftChannel.length; c++) {
            int temp = (int) (-Math.abs(leftChannel[c]) * (float) height) + height;
            g.drawLine(c, height, c, temp);
            temp = (int) (Math.abs(rightChannel[c]) * (float) height) + height;
            g.drawLine(c, height, c, temp);
        }
    }
}
