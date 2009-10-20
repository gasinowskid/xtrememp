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
package xtrememp.util;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
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

/**
 *
 * @author Besmir Beqiri
 */
public final class Utilities {

    /**
     * An array of protocol strings.
     */
    public static final String[] PROTOCOLS = {"http:", "file:", "ftp:", "https:", "ftps:", "jar:"};
    public static final String ZERO_TIMER = "00:00 / 00:00";
    public static final String VISUALIZATION_PANEL = "VISUALIZATION_PANEL";
    public static final String PLAYLIST_MANAGER = "PLAYLIST_MANAGER";
    public static final String DEFAULT_PLAYLIST = "default.xspf";

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
        for (int i = 0, len = left.length; i < len; i++) {
            left[i] = (left[i] + right[i]) / 2.0f;
        }
        return left;
    }

    public static List<Image> getIconImages() {
        List<Image> icons = new ArrayList<Image>();
        try {
            icons.add(ImageIO.read(Utilities.class.getResourceAsStream("/xtrememp/resources/images/icon_32.png")));
            icons.add(ImageIO.read(Utilities.class.getResourceAsStream("/xtrememp/resources/images/icon_48.png")));
            icons.add(ImageIO.read(Utilities.class.getResourceAsStream("/xtrememp/resources/images/icon_64.png")));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return icons;
    }

    public static Locale[] getLanguages() {
        Locale[] locales = {Locale.getDefault(),
            Locale.ENGLISH, Locale.FRENCH, Locale.ITALIAN};
        return locales;
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
