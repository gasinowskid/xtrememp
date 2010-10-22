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
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.nio.FloatBuffer;
import javax.swing.JComponent;
import xtrememp.player.dsp.DigitalSignalProcessor;
import xtrememp.player.dsp.DssContext;

/**
 *
 * @author Besmir Beqiri
 */
public abstract class Visualization extends JComponent implements Comparable<Visualization>,
        DigitalSignalProcessor, Runnable {

    protected Color backgroundColor = Color.black;
    protected Color foregroundColor = Color.white;
    protected DssContext dssContext;

    public Visualization() {
        super();
        setOpaque(false);
        setIgnoreRepaint(true);
    }

    @Override
    public void process(DssContext dssContext) {
        this.dssContext = dssContext;
        EventQueue.invokeLater(this);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (dssContext != null) {
            Dimension size = getSize();
            render(g2d, size.width, size.height);
        } else {
            g2d.setColor(backgroundColor);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        g2d.dispose();
    }

    @Override
    public void run() {
        repaint();
    }

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
     * @param width Width of the rendering area.
     * @param height Height of the rendering area.
     * @param dssContext A DssContext object containing a reference to the sample data.
     */
    public abstract void render(Graphics2D g2d, int width, int height);

    /**
     * @return the backgroundColor
     */
    public Color getBackgroundColor() {
        return backgroundColor;
    }

    /**
     * @param backgroundColor the backgroundColor to set
     */
    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    /**
     * @return the foregroundColor
     */
    public Color getForegroundColor() {
        return foregroundColor;
    }

    /**
     * @param foregroundColor the foregroundColor to set
     */
    public void setForegroundColor(Color foregroundColor) {
        this.foregroundColor = foregroundColor;
    }

    /**
     * Returns a {@link FloatBuffer} as the result of merging the channels
     * buffers.
     *
     * @param channelsBuffer The channels buffer.
     * @return A {@link FloatBuffer} object.
     */
    public FloatBuffer channelsMerge(FloatBuffer[] channelsBuffer) {
        int ch = channelsBuffer.length;
        for (int a = 0, cap = channelsBuffer[0].capacity(); a < cap; a++) {
            float mcd = 0;
            for (int b = 0; b < ch; b++) {
                mcd += channelsBuffer[b].get(a);
            }
            channelsBuffer[0].put(a, mcd / (float) ch);
        }
        return channelsBuffer[0].asReadOnlyBuffer();
    }

    @Override
    public int compareTo(Visualization vis) {
        return this.getDisplayName().compareTo(vis.getDisplayName());
    }
}
