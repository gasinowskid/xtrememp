/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2011 Besmir Beqiri
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
import java.nio.FloatBuffer;
import javax.sound.sampled.SourceDataLine;

/**
 *
 * @author Besmir Beqiri
 */
public final class Waveform extends Visualization {

    public static final String NAME = "Waveform";

    @Override
    public void init(int sampleSize, SourceDataLine sourceDataLine) {
    }

    @Override
    public String getDisplayName() {
        return NAME;
    }

    @Override
    public void setBackgroundColor(Color backgroundColor) {
        super.setBackgroundColor(backgroundColor);
        freeImage();
    }

    @Override
    public void setForegroundColor(Color foregroundColor) {
        super.setForegroundColor(foregroundColor);
        freeImage();
    }

    @Override
    public synchronized void render(Graphics2D g2d, int width, int height) {
        // Channels data.
        FloatBuffer[] channelsBuffer = dssContext.getDataNormalized();
        int sampleSize = dssContext.getSampleSize();
        float leftLevel = 0.0f;
        float rightLevel = 0.0f;
        for (int i = 0; i < sampleSize; i++) {
            leftLevel -= Math.abs(channelsBuffer[0].get(i));
            rightLevel += Math.abs(channelsBuffer[1].get(i));
        }
        // Rendering
        int width2 = width - 1;
        int height2 = height >> 1;

        // Clear previous last lines.
        buffGraphics.setColor(backgroundColor);
        buffGraphics.drawLine(width2, 0, width2, height);
        // Draw center line.
        buffGraphics.setColor(foregroundColor);
        buffGraphics.drawLine(0, height2, width2, height2);
        // Draw last lines.
        int tmp1 = Math.round(leftLevel / (float) sampleSize * (float) height2) + height2;
        buffGraphics.drawLine(width2, height2, width2, tmp1);
        int tmp2 = Math.round(rightLevel / (float) sampleSize * (float) height2) + height2;
        buffGraphics.drawLine(width2, height2, width2, tmp2);
        
        buffGraphics.drawImage(buffImage, -1, 0, null);
    }
}
