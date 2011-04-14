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
package xtrememp;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import org.jvnet.substance.skin.TwilightSkin;
import xtrememp.ui.skin.DarkSapphireSkin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.util.log.Log4jProperties;
import xtrememp.util.Utilities;
import xtrememp.visualization.spectrum.Spectrogram;

/**
 *
 * @author Besmir Beqiri
 */
public final class Settings {

    private static final Logger logger = LoggerFactory.getLogger(Settings.class);
    private static final String CACHE_DIR = ".xtrememp";
    private static final String SETTINGS_FILE = "settings.xml";
    private static final String PROPERTY_CACHE_DIR = "xtrememp.cache.dir";
    private static final String PROPERTY_PLAYER_AUDIO_GAIN = "xtrememp.player.audio.gain";
    private static final String PROPERTY_PLAYER_AUDIO_PAN = "xtrememp.player.audio.pan";
    private static final String PROPERTY_PLAYER_AUDIO_MUTE = "xtrememp.player.audio.mute";
    private static final String PROPERTY_PLAYER_AUDIO_MIXERNAME = "xtrememp.player.audio.mixer.name";
    private static final String PROPERTY_EQUILAZER_PRESET_INDEX = "xtrememp.equilazer.preset.index";
    private static final String PROPERTY_LAST_DIR = "xtrememp.last.dir";
    private static final String PROPERTY_LAST_VIEW = "xtrememp.last.view";
    private static final String PROPERTY_PLAYLIST_POSITION = "xtrememp.playlist.position";
    private static final String PROPERTY_VISUALIZATION = "xtrememp.visualization";
    private static final String PROPERTY_SKIN = "xtrememp.skin";
    private static final Properties properties = new Properties();

    public static void setLastView(String s) {
        properties.setProperty(PROPERTY_LAST_VIEW, s);
    }

    public static String getLastView() {
        return properties.getProperty(PROPERTY_LAST_VIEW, Utilities.PLAYLIST_MANAGER);
    }

    public static void setLastDir(String s) {
        properties.setProperty(PROPERTY_LAST_DIR, s);
    }

    public static String getLastDir() {
        return properties.getProperty(PROPERTY_LAST_DIR, System.getProperty("user.dir"));
    }

    public static String getVisualization() {
        return properties.getProperty(PROPERTY_VISUALIZATION, Spectrogram.NAME);
    }

    public static void setVisualization(String visualization) {
        properties.setProperty(PROPERTY_VISUALIZATION, visualization);
    }

    public static int getPlaylistPosition() {
        return Integer.parseInt(properties.getProperty(PROPERTY_PLAYLIST_POSITION, "0"));
    }

    public static void setPlaylistPosition(int pos) {
        properties.setProperty(PROPERTY_PLAYLIST_POSITION, Integer.toString(pos));
    }

    public static boolean isMuted() {
        return Boolean.parseBoolean(properties.getProperty(PROPERTY_PLAYER_AUDIO_MUTE, Boolean.toString(false)));
    }

    public static void setMuted(boolean mute) {
        properties.setProperty(PROPERTY_PLAYER_AUDIO_MUTE, Boolean.toString(mute));
    }

    public static int getGain() {
        return Integer.parseInt(properties.getProperty(PROPERTY_PLAYER_AUDIO_GAIN, "100"));
    }

    public static void setGain(int gain) {
        properties.setProperty(PROPERTY_PLAYER_AUDIO_GAIN, Integer.toString(gain));
    }

    public static int getPan() {
        return Integer.parseInt(properties.getProperty(PROPERTY_PLAYER_AUDIO_PAN, "0"));
    }

    public static void setPan(int value) {
        properties.setProperty(PROPERTY_PLAYER_AUDIO_PAN, Integer.toString(value));
    }

    public static String getMixerName() {
        return properties.getProperty(PROPERTY_PLAYER_AUDIO_MIXERNAME, "");
    }

    public static void setMixerName(String name) {
        properties.setProperty(PROPERTY_PLAYER_AUDIO_MIXERNAME, name);
    }

    public static int getEqualizerPresetIndex() {
        return Integer.parseInt(properties.getProperty(PROPERTY_EQUILAZER_PRESET_INDEX, "0"));
    }

    public static void setEqualizerPresetIndex(int value) {
        properties.setProperty(PROPERTY_EQUILAZER_PRESET_INDEX, Integer.toString(value));
    }

    public static String getSkin() {
        //return properties.getProperty(PROPERTY_SKIN, TwilightSkin.class.getName());//
        return properties.getProperty(PROPERTY_SKIN, DarkSapphireSkin.class.getName());//TwilightSkin.class.getName());
    }

    public static void setSkin(String className) {
        properties.setProperty(PROPERTY_SKIN, className);
    }

    public static File getCacheDir() {
        File cacheDir = new File(properties.getProperty(PROPERTY_CACHE_DIR, System.getProperty("user.home")), CACHE_DIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return cacheDir;
    }

    public static void setCacheDir(File parent) {
        properties.setProperty(PROPERTY_CACHE_DIR, parent.getPath());
        // Reload log4j properties
        PropertyConfigurator.configure(new Log4jProperties());
    }

    public static boolean isAutomaticCheckForUpdatesEnabled() {
        return Boolean.parseBoolean(properties.getProperty("xtrememp.update.automatic.checkforupdates", Boolean.toString(true)));
    }

    public static void setAutomaticCheckForUpdatesEnabled(boolean b) {
        properties.setProperty("xtrememp.update.automatic.checkforupdates", Boolean.toString(b));
    }

    /**
     * Gets the bounds of the application main frame in the form of a
     * <code>Rectangle</code> object.
     * 
     * @returna a rectangle indicating this component's bounds
     */
    public static Rectangle getMainFrameBounds() {
        String x = properties.getProperty("xtrememp.mainFrame.x", "200");
        String y = properties.getProperty("xtrememp.mainFrame.y", "200");
        String width = properties.getProperty("xtrememp.mainFrame.width", "676");
        String height = properties.getProperty("xtrememp.mainFrame.height", "540");
        return new Rectangle(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(width), Integer.parseInt(height));
    }

    /**
     * Sets the application main frame new size and location.
     * 
     * @param r the bounding rectangle for this component
     */
    public static void setMainFrameBounds(Rectangle r) {
        properties.setProperty("xtrememp.mainFrame.x", Integer.toString(r.x));
        properties.setProperty("xtrememp.mainFrame.y", Integer.toString(r.y));
        properties.setProperty("xtrememp.mainFrame.width", Integer.toString(r.width));
        properties.setProperty("xtrememp.mainFrame.height", Integer.toString(r.height));
    }

    public static boolean containsKey(Object key) {
        return properties.containsKey(key);
    }

    /**
     * Reads all the properties from the settings file.
     */
    public static void loadSettings() {
        File file = new File(getCacheDir(), SETTINGS_FILE);
        if (file.exists()) {
            try {
                FileInputStream fis = new FileInputStream(file);
                properties.loadFromXML(fis);
                fis.close();
            } catch (IOException ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * Writes all the properties in the settings file.
     */
    public static void storeSettings() {
        try {
            File file = new File(getCacheDir(), SETTINGS_FILE);
            FileOutputStream fos = new FileOutputStream(file);
            properties.storeToXML(fos, "Xtreme Media Player Settings");
            fos.close();
        } catch (IOException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
