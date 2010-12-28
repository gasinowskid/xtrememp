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
package xtrememp.tag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * TagInfo interface define needed features for song information.
 * 
 * @author Besmir Beqiri
 */
public abstract class TagInfo {

    protected String encodingType = null;
    protected String format = null;
    protected String location = null;
    protected String channels = null;
    protected String sampleRate = null;
    protected String bitRate = null;
    protected String track = null;
    protected String year = null;
    protected String genre = null;
    protected String title = null;
    protected String artist = null;
    protected String album = null;
    protected String comment = null;
    protected int channelsAsNumber = AudioSystem.NOT_SPECIFIED;
    protected int sampleRateAsNumber = AudioSystem.NOT_SPECIFIED;
    protected long bitRateAsNumber = AudioSystem.NOT_SPECIFIED;
    protected long duration = AudioSystem.NOT_SPECIFIED;
    protected long size = AudioSystem.NOT_SPECIFIED;

    /**
     * Load tag information from an input stream.
     * 
     * @param input 
     * @throws java.io.IOException 
     * @throws javax.sound.sampled.UnsupportedAudioFileException 
     */
    public abstract void load(InputStream input) throws IOException, UnsupportedAudioFileException;

    /**
     * Load tag information from an URL.
     * 
     * @param input 
     * @throws java.io.IOException 
     * @throws javax.sound.sampled.UnsupportedAudioFileException 
     */
    public abstract void load(URL input) throws IOException, UnsupportedAudioFileException;

    /**
     * Load tag information from a file.
     * 
     * @param input
     * @throws java.io.IOException
     * @throws javax.sound.sampled.UnsupportedAudioFileException
     */
    public abstract void load(File input) throws IOException, UnsupportedAudioFileException;

    /**
     * Get codec details
     *
     * @return encoding type
     */
    public abstract String getCodecDetails();

    /**
     * Get encoding type.
     *
     * @return encoding type
     */
    public String getEncodingType() {
        return (encodingType == null) ? null : encodingType.trim();
    }

    /**
     * Get format.
     *
     * @return format
     */
    public String getFormat() {
        return (format == null) ? null : format.trim();
    }

    /**
     * Get sample rate.
     *
     * @return sample rate
     */
    public int getSampleRate() {
        return sampleRateAsNumber;
    }

    /**
     * Get sample rate.
     *
     * @return sample rate
     */
    public int getSampleRateAsNumber() {
        return sampleRateAsNumber;
    }

    /**
     * Get nominal bitrate.
     *
     * @return bitrate in bps
     */
    public String getBitRate() {
        return bitRate;
    }

    /**
     * Get nominal bitrate as number.
     *
     * @return bitrate as number in bps
     */
    public long getBitRateAsNumber() {
        return bitRateAsNumber;
    }

    /**
     * Get channels.
     *
     * @return channels
     */
    public String getChannels() {
        return channels;
    }

    /**
     * Get channels as number.
     *
     * @return channels as number
     */
    public int getChannelsAsNumber() {
        if (channels.contains("Stereo")) {
            channelsAsNumber = 2;
        } else if (channels.contains("Joint Stereo")) {
            channelsAsNumber = 2;
        } else if (channels.contains("Dual")) {
            channelsAsNumber = 2;
        } else if (channels.contains("Mono")) {
            channelsAsNumber = 1;
        } else {
            channelsAsNumber = Integer.parseInt(channels);
        }
        return channelsAsNumber;
    }

    /**
     * Get track length in seconds.
     *
     * @return track length in seconds
     */
    public long getTrackLength() {
        return duration;
    }

    /**
     * Get the title of the song.
     *
     * @return the title of the song
     */
    public String getTitle() {
        return (title == null) ? null : title.trim();
    }

    /**
     * Get the artist that performed the song.
     *
     * @return the artist name
     */
    public String getArtist() {
        return (artist == null) ? null : artist.trim();
    }

    /**
     * Get the name of the album upon which the song resides.
     *
     * @return the album name
     */
    public String getAlbum() {
        return (album == null) ? null : album.trim();
    }

    /**
     * Get the track number of this track on the album.
     *
     * @return the track number
     */
    public String getTrack() {
        if (track != null) {
            if (track.contains("/")) {
                track = track.substring(0, track.indexOf("/"));
            }
        }
        return (track == null) ? null : track.trim();
    }

    /**
     * Get the genre string of the music.
     *
     * @return the genre
     */
    public String getGenre() {
        return (genre == null) ? null : genre.trim();
    }

    /**
     * Get the year the track was released.
     *
     * @return the year
     */
    public String getYear() {
        return (year == null) ? null : year.trim();
    }

    /**
     * Get any comments provided about the song.
     *
     * @return the comments
     */
    public String getComment() {
        return (comment == null) ? null : comment.trim();
    }
}
