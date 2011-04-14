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
package xtrememp.playlist;

import xtrememp.tag.TagInfo;
import xtrememp.tag.TagInfoFactory;
import xtrememp.util.Utilities;

/**
 * This class implements item for playlist.
 * 
 * @author Besmir Beqiri
 */
public class PlaylistItem {

//    private static final Log log = LogFactory.getLog(PlaylistItem.class);
    private TagInfo tagInfo;
    private String name;
    private String displayName;
    private String formatedLength;
    private String location;
    private boolean isFile = false;
    private long duration = -1;

    /**
     * Contructor for playlist item.
     *
     * @param name     Song name to be displayed
     * @param location File or URL
     * @param duration Time length
     * @param isFile   <true>true</true> for File instance
     */
    public PlaylistItem(String name, String location, int duration, boolean isFile) {
        this.name = name;
        this.location = location;
        this.duration = duration;
        this.isFile = isFile;
        formatedLength = getFormattedLength(this.duration);
    }

    /**
     * Set name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set location
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return location
     */
    public String getLocation() {
        return location;
    }

    /**
     * Set duration
     */
    public void setDuration(long duration) {
        this.duration = duration;
    }

    /**
     * @return duration
     */
    public long getDuration() {
        return duration;
    }

    /**
     *
     * @return <code>true</code> if item to play is a file, else <code>false</code>.
     */
    public boolean isFile() {
        return isFile;
    }

    /**
     * Set File flag for playslit item
     * @param isFile <true>true</true> for File instance
     */
    public void setFile(boolean isFile) {
        this.isFile = isFile;
    }

    /**
     * Load and return TagInfo instance
     * @return TagInfo instance
     */
    public TagInfo loadTagInfo() {
        if ((tagInfo == null) && (!Utilities.isNullOrEmpty(location))) {
            tagInfo = TagInfoFactory.getInstance().getTagInfo(location);
            if (isFile) {
                duration = tagInfo.getTrackLength();
                displayName = getFormattedDisplayName();
                formatedLength = getFormattedLength(this.duration);
            }
        }
        return tagInfo;
    }

    /**
     * Return TagInfo instance
     * @return TagInfo instance
     */
    public TagInfo getTagInfo() {
        return tagInfo;
    }

    /**
     * Returns length such as hh:mm:ss
     * @return formatted length
     */
    public String getFormattedLength() {
        return formatedLength;
    }

    /**
     * Returns length such as hh:mm:ss
     * @param duration duration in seconds
     * @return a formatted length
     */
    public String getFormattedLength(long duration) {
        String length = "";
        if (duration > -1) {
            int min = (int) (duration / 60);
            int hours = (int) (min / 60);
            min = min - hours * 60;
            int sec = (int) (duration - min * 60 - hours * 3600);
            // Hours.
            if (hours > 0) {
                length += Utilities.rightPadString(String.valueOf(hours), 2) + ":";
            }
            length += Utilities.rightPadString(String.valueOf(min), '0', 2) + ":" + Utilities.rightPadString(sec + "", '0', 2);
        }
        return length;
    }

    /**
     * Returns item name such as Title - Artist
     * @return a formatted string
     */
    public String getFormattedDisplayName() {
        if (tagInfo == null) {
            return getName();
        } else if (displayName == null) {
            String trnumber = tagInfo.getTrack() == null ? null : tagInfo.getTrack().trim();
            String title = tagInfo.getTitle() == null ? null : tagInfo.getTitle().trim();
            //String artist = tagInfo.getArtist() == null ? null : tagInfo.getArtist().trim();
            if ((!Utilities.isNullOrEmpty(title)) && (!Utilities.isNullOrEmpty(trnumber))) {
                displayName = (trnumber + " - " + title);
            } else if (!Utilities.isNullOrEmpty(title)) {
                displayName = title;
            } else {
                displayName = name;
            }
        }
        return displayName;
    }

    public void setFormattedDisplayName(String displayName) {
        this.name = displayName;
        this.displayName = displayName;
    }

    /**
     * Return item name such as Seconds,Title,Artist
     * @return a formatted string
     */
    public String getM3UExtInf() {
        if (tagInfo == null) {
            return (duration + "," + name);
        } else {
            String title = tagInfo.getTitle() == null ? null : tagInfo.getTitle().trim();
            String artist = tagInfo.getArtist() == null ? null : tagInfo.getArtist().trim();
            if ((!Utilities.isNullOrEmpty(title)) && (!Utilities.isNullOrEmpty(artist))) {
                return (getDuration() + "," + title + " - " + artist);
            } else if (!Utilities.isNullOrEmpty(title)) {
                return (getDuration() + "," + title);
            } else {
                return (duration + "," + name);
            }
        }
    }
}
