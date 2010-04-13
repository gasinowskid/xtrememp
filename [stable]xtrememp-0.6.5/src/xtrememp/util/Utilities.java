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
package xtrememp.util;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
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
import org.pushingpixels.substance.api.skin.SkinInfo;
import xtrememp.ui.skin.DarkSapphireSkin;

/**
 *
 * @author Besmir Beqiri
 */
public final class Utilities {

    /**
     * System default locale.
     */
    private static Locale systemLocale;
    /**
     * An array of protocol strings.
     */
    public static final String[] PROTOCOLS = {"http:", "file:", "ftp:", "https:", "ftps:", "jar:"};
    //
    public static final String ZERO_TIMER = "00:00 / 00:00";
    public static final String VISUALIZATION_PANEL = "VISUALIZATION_PANEL";
    public static final String PLAYLIST_MANAGER = "PLAYLIST_MANAGER";
    public static final String DEFAULT_PLAYLIST = "default.xspf";
    //
    public static final Icon APP_256_ICON = getIcon("icon_256.png");
    //
    public static final Icon FOLDER_ICON = getIcon("folder.png");
    public static final Icon FOLDER_REMOTE_ICON = getIcon("folder-remote.png");
    public static final Icon DOCUMENT_OPEN_ICON = getIcon("document-open.png");
    public static final Icon DOCUMENT_SAVE_ICON = getIcon("document-save.png");
    public static final Icon MEDIA_PLAY_ICON = getIcon("media-playback-start.png");
    public static final Icon MEDIA_PAUSE_ICON = getIcon("media-playback-pause.png");
    public static final Icon MEDIA_STOP_ICON = getIcon("media-playback-stop.png");
    public static final Icon MEDIA_PREVIOUS_ICON = getIcon("media-skip-backward.png");
    public static final Icon MEDIA_NEXT_ICON = getIcon("media-skip-forward.png");
    public static final Icon PLAYLIST_REPEAT_ICON = getIcon("media-playlist-repeat.png");
    public static final Icon PLAYLIST_REPEATALL_ICON = getIcon("media-playlist-repeat_all.png");
    public static final Icon PLAYLIST_SHUFFLE_ICON = getIcon("media-playlist-shuffle.png");
    public static final Icon PLAYLIST_SHUFFLE_ALT_ICON = getIcon("media-playlist-shuffle-alt.png");
    public static final Icon AUDIO_VOLUME_HIGH_ICON = getIcon("audio-volume-high.png");
    public static final Icon AUDIO_VOLUME_MEDIUM_ICON = getIcon("audio-volume-medium.png");
    public static final Icon AUDIO_VOLUME_LOW_ICON = getIcon("audio-volume-low.png");
    public static final Icon AUDIO_VOLUME_MUTED_ICON = getIcon("audio-volume-muted.png");
    public static final Icon LIST_ADD_ICON = getIcon("list-add.png");
    public static final Icon LIST_REMOVE_ICON = getIcon("list-remove.png");
    public static final Icon EDIT_CLEAR_ICON = getIcon("edit-clear.png");
    public static final Icon GO_UP_ICON = getIcon("go-up.png");
    public static final Icon GO_DOWN_ICON = getIcon("go-down.png");
    public static final Icon GO_PREVIOUS_ICON = getIcon("go-previous.png");
    public static final Icon GO_NEXT_ICON = getIcon("go-next.png");
    public static final Icon MEDIA_INFO_ICON = getIcon("media-info.png");
    public static final Icon VIEW_FULLSCREEN_ICON = getIcon("view-fullscreen.png");
    public static final Icon MENU_ICON = getIcon("menu.png");
    public static final Icon PREFERENCES_SYSTEM_ICON = getIcon("preferences-system.png");
    public static final Icon AUDIO_CARD_ICON = getIcon("audio-card.png");

    /**
     * Close dialog with ESC key.
     *
     * @param dialog a {@link JDialog} instance.
     */
    public static void closeOnEscape(final JDialog dialog) {
        JRootPane rootPane = dialog.getRootPane();
        String escape = "escape";
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), escape);
        ActionMap actionMap = rootPane.getActionMap();
        actionMap.put(escape, new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
    }

    /**
     * Returns a {@link FloatBuffer} as the result of merging the left and
     * right channel buffers.
     *
     * @param leftChannel the left channel buffer.
     * @param rightChannel the right channel buffer.
     * @return a {@link FloatBuffer} object.
     */
    public static FloatBuffer stereoMerge(FloatBuffer leftChannel, FloatBuffer rightChannel) {
        int capacity = Math.max(leftChannel.capacity(), rightChannel.capacity());
        FloatBuffer stereoBuffer = FloatBuffer.allocate(capacity);
        for (int i = 0, len = leftChannel.capacity(); i < len; i++) {
            float average = (leftChannel.get(i) + rightChannel.get(i)) / 2.0f;
            stereoBuffer.put(average);
        }
        return stereoBuffer.asReadOnlyBuffer();
    }

    /**
     * Creates an info object on a single skin.
     *
     * @param displayName
     *            Skin display name.
     * @param skinClass
     *            Skin class.
     * @param isDefault
     *            Indication whether the specified skin is default.
     * @return SkinInfo object on the specified skin.
     */
    private static SkinInfo create(String displayName, Class<?> skinClass,
            boolean isDefault) {
        SkinInfo result = new SkinInfo(displayName, skinClass.getName());
        result.setDefault(isDefault);
        return result;
    }

    public static Set<SkinInfo> getSkins() {
        Set<SkinInfo> result = new HashSet<SkinInfo>();
        result.add(create(DarkSapphireSkin.NAME, DarkSapphireSkin.class, false));
        return result;
    }

    /**
     * Verify if the current operating system is Windows.
     *
     * @return <code>true</code> if the current OS is Windows,
     *         else <code>false</code>.
     */
    public static boolean isWindowsOS() {
        String os = System.getProperty("os.name").toLowerCase();
        return os != null && os.indexOf("windows") != -1;
    }

    /**
     * Verify if the current operating system is Linux.
     *
     * @return <code>true</code> if the current OS is Linux,
     *         else <code>false</code>.
     */
    public static boolean isLinuxOS() {
        String os = System.getProperty("os.name").toLowerCase();
        return os != null && os.indexOf("linux") != -1;
    }

    /**
     * Verify if the current operating system is running on 64-bit.
     *
     * @return <code>true</code> if the current OS has x64 architecture,
     *         else <code>false</code>.
     */
    public static boolean isRunningX64() {
        String arch = System.getProperty("sun.arch.data.model").toLowerCase();
        return arch != null && arch.indexOf("64") != -1;
    }

    /**
     * @see LanguageBundle#getString(java.lang.String).
     */
    public static String tr(String key) {
        return LanguageBundle.getString(key);
    }

    /**
     * Returns an array of supported locales including the system locale.
     *
     * @return an array of {@link Locale} objects.
     */
    public static Locale[] getLanguages() {
        Locale[] locales = {getSystemLocale(),
            Locale.ENGLISH, Locale.FRENCH, Locale.ITALIAN};
        return locales;
    }

    /**
     * Gets the current value of the default locale for this instance
     * of the Java Virtual Machine.
     * <p>
     * The Java Virtual Machine sets the default locale during startup
     * based on the host environment. This method must be used before
     * <blockquote>
     * <code>{@link Locale#setDefault(java.util.Locale)}</code>
     * </blockquote>
     * method is ever called.
     *
     * @return the default locale for this instance of the Java Virtual Machine.
     */
    public static Locale getSystemLocale() {
        if (systemLocale == null) {
            systemLocale = Locale.getDefault();
        }
        return systemLocale;
    }

    /**
     * Returns a {@link BufferedImage} as the result of decoding
     * an image with the given name.
     *
     * @param name the image name.
     * @return a <code>BufferedImage</code> object, or <code>null</code>.
     */
    public static BufferedImage getImage(String name) {
        BufferedImage image = null;
        if (name != null) {
            try {
                image = ImageIO.read(Utilities.class.getResourceAsStream("/xtrememp/resources/images/" + name));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return image;
    }

    /**
     * Returns an {@link Icon} wrapping the image with the given name.
     *
     * @param name the icon name.
     * @return an {@link Icon} object, or <code>null</code>.
     */
    public static Icon getIcon(String name) {
        return new ImageIcon(getImage(name));
    }

    /**
     * Returns a list containing the application icon images in different sizes.
     *
     * @return a {@link List} object.
     */
    public static List<Image> getIconImages() {
        List<Image> icons = new ArrayList<Image>(3);
        icons.add(getImage("icon_32.png"));
        icons.add(getImage("icon_48.png"));
        icons.add(getImage("icon_64.png"));
        return icons;
    }

    /**
     * Check if the provided {@link String} is <code>null</code> or empty.
     *
     * @param value the {@link String} to check.
     * @return <code>true</code> if the {@link String} is <code>null</code>
     *         or empty, else <code>false</code>.
     */
    public static boolean isNullOrEmpty(String value) {
        if (value != null) {
            return value.isEmpty();
        }
        return true;
    }

    /**
     * Check if the provided {@link String} start with one supported
     * protocol strings.
     *
     * @param input the {@link String} to check.
     * @return <code>true</code> if the {@link String} start with a protocol,
     *         else <code>false</code>.
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
     * Returns a human-readable version of the file size, where the input
     * represents a specific number of bytes.
     *
     * @param size the number of bytes.
     * @return a human-readable display value (includes units).
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
     * Check if the provided {@link String} start with a specified
     * protocol {@link String}.
     *
     * @param input the {@link String} to check.
     * @param input the protocol {@link String}.
     * @return <code>true</code> if the {@link String} start with the
     *         specified protocol, else <code>false</code>.
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
