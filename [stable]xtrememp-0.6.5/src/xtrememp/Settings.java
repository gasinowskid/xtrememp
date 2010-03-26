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
package xtrememp;

import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.ui.skin.DarkSapphireSkin;
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
    private static final String PROPERTY_PLAYLIST_REPEATMODE = "xtrememp.playlist.repeatmode";
    private static final String PROPERTY_PLAYLIST_SHUFFLE = "xtrememp.playlist.shuffle";
    private static final String PROPERTY_EQUILAZER_PRESET_INDEX = "xtrememp.equilazer.preset.index";
    private static final String PROPERTY_LAST_DIR = "xtrememp.last.dir";
    private static final String PROPERTY_LAST_VIEW = "xtrememp.last.view";
    private static final String PROPERTY_PLAYLIST_POSITION = "xtrememp.playlist.position";
    private static final String PROPERTY_VISUALIZATION = "xtrememp.visualization";
    private static final String PROPERTY_LANGUAGE_INDEX = "xtrememp.language.index";
    private static final String PROPERTY_SKIN = "xtrememp.skin";
    private static final String PROPERTY_UPDATES_AUTOMATIC = "xtrememp.update.automatic";
    private static final String PROPERTY_MAINFRAME_X = "xtrememp.mainframe.x";
    private static final String PROPERTY_MAINFRAME_Y = "xtrememp.mainframe.y";
    private static final String PROPERTY_MAINFRAME_WIDTH = "xtrememp.mainframe.width";
    private static final String PROPERTY_MAINFRAME_HEIGHT = "xtrememp.mainframe.height";
    private static final Properties properties = new Properties();

    public static void setRepeatMode(String mode) {
        properties.setProperty(PROPERTY_PLAYLIST_REPEATMODE, mode);
    }

    public static String getRepeatMode() {
        return properties.getProperty(PROPERTY_PLAYLIST_REPEATMODE, Utilities.PLAYING_MODE_REPEAT_ALL);
    }

    public static void setPlayingModeShuffle(boolean aFlag) {
        properties.setProperty(PROPERTY_PLAYLIST_SHUFFLE, Boolean.toString(aFlag));
    }

    /**
     * Get the previously stored shuffle state
     * @return true when shuffle was on, false otherwise. Default value is false.
     */
    public static boolean getShuffleState() {
        return Boolean.parseBoolean(properties.getProperty(PROPERTY_PLAYLIST_SHUFFLE, Boolean.toString(false)));
    }

    public static void setLanguageIndex(int value) {
        properties.setProperty(PROPERTY_LANGUAGE_INDEX, Integer.toString(value));
    }

    public static int getLanguageIndex() {
        return Integer.parseInt(properties.getProperty(PROPERTY_LANGUAGE_INDEX, "0"));
    }

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
        return properties.getProperty(PROPERTY_SKIN, DarkSapphireSkin.class.getName());
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

    public static boolean isAutomaticUpdatesEnabled() {
        return Boolean.parseBoolean(properties.getProperty(PROPERTY_UPDATES_AUTOMATIC, Boolean.toString(true)));
    }

    public static void setAutomaticUpdatesEnabled(boolean b) {
        properties.setProperty(PROPERTY_UPDATES_AUTOMATIC, Boolean.toString(b));
    }

    /**
     * Gets the bounds of the application main frame in the form of a
     * <code>Rectangle</code> object.
     * 
     * @returna a rectangle indicating this component's bounds
     */
    public static Rectangle getMainFrameBounds() {
        String x = properties.getProperty(PROPERTY_MAINFRAME_X, "200");
        String y = properties.getProperty(PROPERTY_MAINFRAME_Y, "200");
        String width = properties.getProperty(PROPERTY_MAINFRAME_WIDTH, "676");
        String height = properties.getProperty(PROPERTY_MAINFRAME_HEIGHT, "540");
        return new Rectangle(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(width), Integer.parseInt(height));
    }

    /**
     * Sets the application main frame new size and location.
     * 
     * @param r the bounding rectangle for this component
     */
    public static void setMainFrameBounds(Rectangle r) {
        properties.setProperty(PROPERTY_MAINFRAME_X, Integer.toString(r.x));
        properties.setProperty(PROPERTY_MAINFRAME_Y, Integer.toString(r.y));
        properties.setProperty(PROPERTY_MAINFRAME_WIDTH, Integer.toString(r.width));
        properties.setProperty(PROPERTY_MAINFRAME_HEIGHT, Integer.toString(r.height));
    }

    public static boolean isEmpty() {
        return properties.isEmpty();
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
