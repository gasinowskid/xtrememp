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
package xtrememp.util;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Area;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.utils.SubstanceCoreUtilities;
import org.jvnet.substance.utils.SubstanceImageCreator;
import xtrememp.skin.XtremeDarkSapphireSkin;

/**
 *
 * @author Besmir Beqiri
 */
public final class Utilities {
    
    /**
     * An array of protocol strings.
     */
    public static final String[] PROTOCOLS = {"http:", "file:", "ftp:", "https:", "ftps:", "jar:"};

    public static void closeOnEscape(final JDialog dialog) {
        // Close dialog with ESC key
        JRootPane rPane = dialog.getRootPane();
        InputMap iMap = rPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");
        ActionMap aMap = rPane.getActionMap();
        aMap.put("escape", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    }

    public static float[] stereoMerge(float[] left, float[] right) {
        for (int i = 0; i < left.length; i++) {
            left[i] = (left[i] + right[i]) / 2.0f;
        }
        return left;
    }

    public static RenderingHints getRenderingHints() {
        RenderingHints hints = new RenderingHints(
                RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        hints.put(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        hints.put(RenderingHints.KEY_COLOR_RENDERING,
                RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        hints.put(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);

        return hints;
    }

    public static Image getLogoImage(int size, float stroke) {

        boolean isDark = SubstanceLookAndFeel.getCurrentSkin().getMainDefaultColorScheme().isDark();
        SubstanceColorScheme colorScheme = isDark ? SubstanceLookAndFeel.getCurrentSkin().getMainDefaultColorScheme()
                : SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme();
        Color topBorderColor = isDark ? colorScheme.getForegroundColor()
                : colorScheme.getDarkColor();
        Color bottomBorderColor = isDark ? colorScheme.getUltraLightColor()
                : colorScheme.getMidColor();

        BufferedImage image = SubstanceCoreUtilities.getBlankImage(size, size);
        Graphics2D graphics = image.createGraphics();
        graphics.setRenderingHints(getRenderingHints());

        Area area = getLogoImageArea(size);

//        graphics.setColor(new Color(0, 0, 0, 32));
//        graphics.draw(new RoundRectangle2D.Double(0, 0, size - 1, size - 1,
//                size, size));

//        int x[] = {size / 3 + 1, size / 3 + 1, 3 * size / 4 + 1,
//            3 * size / 4 + 1
//        };
//        int y[] = {3 * size / 4 + 2, size / 4 + 1, size / 2 + 1,
//            size / 2 + 2
//        };
//        Polygon poly = new Polygon(x, y, 4);
//        graphics.drawPolygon(poly);

        Shape oldShape = graphics.getClip();
        graphics.setClip(area);
        SubstanceImageCreator.paintRectangularBackground(null, graphics, 0, 0, size,
                size, SubstanceLookAndFeel.getCurrentSkin().getMainActiveColorScheme(), 1.0f, false);
        graphics.setClip(oldShape);

        graphics.setPaint(new GradientPaint(0, 0, topBorderColor, 0, size - 1, bottomBorderColor, true));
        graphics.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.draw(area);
        graphics.dispose();

        return image;
    }

    public static List<Image> getLogoImages(int[] sizes) {
        List<Image> logos = new ArrayList<Image>();
        for (int i : sizes) {
            logos.add(getLogoImage(i, 1.0f));
        }
        return logos;
    }

    public static Vector<String> getSupportedLanguages() {
        Vector<String> v = new Vector<String>();
        v.add("English");
        return v;
    }

    public static Vector<String> getSkins() {
        Vector<String> v = new Vector<String>();
        SubstanceSkin ss = new XtremeDarkSapphireSkin();
        v.add(ss.getDisplayName());
        return v;
    }

    private static Area getLogoImageArea(int size) {
        Shape roundShape = new RoundRectangle2D.Double(1, 1, size - 3, size - 3, size, size);
        Area area = new Area(roundShape);
        int xP[] = {size / 3, size / 3, 3 * size / 4, 3 * size / 4};
        int yP[] = {3 * size / 4, size / 4 - 1, size / 2 - 1, size / 2};
        Polygon poly = new Polygon(xP, yP, 4);
        area.subtract(new Area(poly));
        return area;
    }

    /**
     * Returns a human-readable version of the file size, where the input
     * represents a specific number of bytes.
     *
     * @param size the number of bytes
     * @return a human-readable display value (includes units)
     */
    public static String byteCountToDisplaySize(long size) {
        long ONE_KB = 1024;
        long ONE_MB = ONE_KB * ONE_KB;
        long ONE_GB = ONE_KB * ONE_MB;

        if (size > ONE_GB) {
            return String.valueOf(size / ONE_GB) + " GB";
        } else if (size > ONE_MB) {
            return String.valueOf(size / ONE_MB) + " MB";
        } else if (size > ONE_KB) {
            return String.valueOf(size / ONE_KB) + " KB";
        } else {
            return String.valueOf(size) + " bytes";
        }
    }

    /**
     * Returns an icon.
     *
     * @param name the icon name
     * @return an icon
     */
    public static Icon getIcon(String name) {
        Icon icon = null;
        if (name != null) {
            try {
                icon = new ImageIcon(ImageIO.read(Utilities.class.getResourceAsStream("/xtrememp/resources/images/" + name)));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return icon;
    }

    /**
     * Check if the provided string is <code>null</code> or empty.
     *
     * @param value the string to check
     * @return <code>true</code> if the string is null or empty, else <code>false</code>
     */
    public static boolean isNullOrEmpty(String value) {
        if (value != null) {
            return value.isEmpty();
        }
        return true;
    }

    /**
     * Check if the provided string start with one supported protocol strings.
     *
     * @param input the string to check
     * @return <code>true</code> if the string start with a protocol, else <code>false</code>
     */
    public static boolean startWithProtocol(String input) {
        if (input != null) {
            input = input.toLowerCase();
            for (String protocol : PROTOCOLS) {
                if (input.startsWith(protocol)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Check if the provided string start with a specified protocol string.
     *
     * @param input the string to check
     * @param input the protocol string
     * @return <code>true</code> if the string start with the specified protocol, else <code>false</code>
     */
    public static boolean startWithProtocol(String input, String protocol) {
        if (input != null) {
            input = input.toLowerCase();
            if (input.startsWith(protocol)) {
                return true;
            }
        }
        return false;
    }

    public static String rightPadString(String s, int length) {
        return (rightPadString(s, ' ', length));
    }

    public static String rightPadString(String s, char padChar, int length) {
        int slen, numPads = 0;
        if (s == null) {
            s = "";
            numPads = length;
        } else if ((slen = s.length()) > length) {
            s = s.substring(length);
        } else if (slen < length) {
            numPads = length - slen;
        }
        if (numPads == 0) {
            return (s);
        }
        char[] c = new char[numPads];
        Arrays.fill(c, padChar);
        return new String(c) + s;
    }
}
