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
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.image.VolatileImage;
import xtrememp.player.dsp.DigitalSignalSynchronizer.Context;

/**
 *
 * @author Besmir Beqiri
 */
public class Waveform extends Visualization {

    public static final String NAME = "Waveform";
    private GraphicsConfiguration gc;
    private VolatileImage image1;
    private VolatileImage image2;

    @Override
    public String getDisplayName() {
        return NAME;
    }

    private void createImages(int width, int height) {
        // free image resources
        if (image1 != null) {
            image1.flush();
            image1 = null;
        }

        if (image2 != null) {
            image2.flush();
            image2 = null;
        }

        // create images
        gc = GraphicsEnvironment.getLocalGraphicsEnvironment().
                getDefaultScreenDevice().getDefaultConfiguration();

        image1 = gc.createCompatibleVolatileImage(width, height);
        image2 = gc.createCompatibleVolatileImage(width, height);

        int valCode1 = image1.validate(gc);
        int valCode2 = image2.validate(gc);
        if (valCode1 == VolatileImage.IMAGE_INCOMPATIBLE ||
                valCode2 == VolatileImage.IMAGE_INCOMPATIBLE) {
            createImages(width, height);
        }

        fillBackground(Color.black);
    }

    private void fillBackground(Color c) {
        if (image1 != null) {
            Graphics2D g2d1 = image1.createGraphics();
            g2d1.setColor(c);
            g2d1.fillRect(0, 0, image1.getWidth(), image1.getHeight());
            g2d1.dispose();
        }

        if (image2 != null) {
            Graphics2D g2d2 = image2.createGraphics();
            g2d2.setColor(c);
            g2d2.fillRect(0, 0, image2.getWidth(), image2.getHeight());
            g2d2.dispose();
        }
    }

    @Override
    public void render(Graphics g, int width, int height, Context dssContext) {
        if (image1 == null || (image1.getWidth() != width || image1.getHeight() != height)) {
            createImages(width, height);
        }
        do {
            int valCode1 = image1.validate(gc);
            int valCode2 = image2.validate(gc);
            if (valCode1 == VolatileImage.IMAGE_RESTORED || valCode2 == VolatileImage.IMAGE_RESTORED) {
                fillBackground(Color.black);
            } else if (valCode1 == VolatileImage.IMAGE_INCOMPATIBLE ||
                    valCode2 == VolatileImage.IMAGE_INCOMPATIBLE) {
                createImages(width, height);
            }

            // rendering
            Graphics2D g2d1 = image1.createGraphics();
            g2d1.drawImage(image2, -1, 0, null);
            int width2 = width - 1;
            int height2 = height >> 1;
            int length = dssContext.getLength();
            float leftLevel = 0.0f;
            float rightLevel = 0.0f;
            float[][] channels = dssContext.getDataNormalized();

            for (int a = 0; a < length; a++) {
                leftLevel -= Math.abs(channels[0][a]);
                rightLevel += Math.abs(channels[1][a]);
            }

            // clear previous last lines
            g2d1.setColor(Color.black);
            g2d1.drawLine(width2, 0, width2, height);
            // draw center line
            g2d1.setColor(Color.blue);
            g2d1.drawLine(0, height2, width2, height2);
            // draw last lines
            int tmp1 = Math.round(leftLevel / (float) length * (float) height2) + height2;
            g2d1.drawLine(width2, height2, width2, tmp1);
            int tmp2 = Math.round(rightLevel / (float) length * (float) height2) + height2;
            g2d1.drawLine(width2, height2, width2, tmp2);
            g2d1.dispose();

            Graphics2D g2d2 = image2.createGraphics();
            g2d2.drawImage(image1, 0, 0, null);
            g2d2.dispose();

            g.drawImage(image2, 0, 0, null);
        } while (image1.contentsLost() || image2.contentsLost());
    }
}
