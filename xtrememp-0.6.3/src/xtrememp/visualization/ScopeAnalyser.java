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
import java.util.HashMap;
import java.util.Map;
import xtrememp.util.Utilities;

/**
 *
 * @author Besmir Beqiri
 */
public class ScopeAnalyser extends Visualization {

    private Type type;

    public enum Type {

        SCOPE("Scope") {

            private int colorSize = 360;
            private int colorIndex = 0;

            @Override
            public void render(Graphics g, int width, int height,
                    float[] leftChannel, float[] rightChannel) {
//                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                float[] stereoChannel = Utilities.stereoMerge(leftChannel, rightChannel);
                colorIndex = (colorIndex == colorSize - 1) ? 0 : colorIndex + 1;
                g.setColor(Color.getHSBColor(colorIndex / (float) colorSize, 1.0f, 1.0f));
                int last = (int) (stereoChannel[0] * (float) height) + height;
                for (int c = 0; c < width && c < stereoChannel.length; c++) {
                    int temp = (int) (stereoChannel[c] * (float) height) + height;
                    g.drawLine(c, last, c + 1, temp);
                    last = temp;
                }
            }
        },
        STEREO_SCOPE("Stereo Scope") {

            @Override
            public void render(Graphics g, int width, int height,
                    float[] leftChannel, float[] rightChannel) {
//                ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                height = height >> 1;
                int lastLeft = (int) (leftChannel[0] * (float) height) + height;
                int lastRight = (int) (rightChannel[0] * (float) height) + 3 * height;
                for (int c = 0; c < width && c < leftChannel.length; c++) {
                    int tempLeft = (int) (leftChannel[c] * (float) height) + height;
                    g.setColor(Color.blue);
                    g.drawLine(c, lastLeft, c + 1, tempLeft);
                    lastLeft = tempLeft;
                    int tempRight = (int) (rightChannel[c] * (float) height) + 3 * height;
                    g.setColor(Color.red);
                    g.drawLine(c, lastRight, c + 1, tempRight);
                    lastRight = tempRight;
                }
            }
        },
        SCOPE_LINES("Scope Lines") {

            private int colorSize = 360;
            private int colorIndex = 0;

            @Override
            public void render(Graphics g, int width, int height,
                    float[] leftChannel, float[] rightChannel) {
                float[] stereoChannel = Utilities.stereoMerge(leftChannel, rightChannel);
                colorIndex = (colorIndex == colorSize - 1) ? 0 : colorIndex + 1;
                g.setColor(Color.getHSBColor(colorIndex / (float) colorSize, 1.0f, 1.0f));
                for (int c = 0; c < width && c < stereoChannel.length; c++) {
                    int temp = (int) (stereoChannel[c] * (float) height) + height;
                    g.drawLine(c, height, c, temp);
                }
            }
        };
        private String displayName;

        Type(String displayName) {
            this.displayName = displayName;
        }

        public abstract void render(Graphics g, int width, int height,
                float[] leftChannel, float[] rightChannel);

        @Override
        public String toString() {
            return displayName;
        }
    }

    public ScopeAnalyser() {
        this(Type.SCOPE);
    }

    public ScopeAnalyser(Type type) {
        this.type = type;
    }

    /*
     * 
     * @see xtrememp.visual.Visualization#getDisplayName
     */
    @Override
    public String getDisplayName() {
        return "Scope Analyser";
    }

    @Override
    public void setType(String name) {
        this.type = Type.valueOf(name);
    }

    @Override
    public String getTypeName() {
        return type.name();
    }

    @Override
    public Map<String, String> getTypes() {
        Map<String, String> typesMap = new HashMap<String, String>();
        for (Type t : Type.values()) {
            typesMap.put(t.name(), t.toString());
        }
        return typesMap;
    }

    /*
     * 
     * @see xtrememp.visual.Visualization#render
     */
    @Override
    public void render(Graphics g, int width, int height) {
        type.render(g, width, height / 2, leftChannel, rightChannel);
    }
}
