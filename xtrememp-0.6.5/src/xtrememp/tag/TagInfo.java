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
package xtrememp.tag;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * TagInfo interface define needed features for song information.
 * 
 * @author Besmir Beqiri
 */
public interface TagInfo {

    /**
     * Load tag information from an input stream.
     * 
     * @param input 
     * @throws java.io.IOException 
     * @throws javax.sound.sampled.UnsupportedAudioFileException 
     */
    public void load(InputStream input) throws IOException, UnsupportedAudioFileException;

    /**
     * Load tag information from an URL.
     * 
     * @param input 
     * @throws java.io.IOException 
     * @throws javax.sound.sampled.UnsupportedAudioFileException 
     */
    public void load(URL input) throws IOException, UnsupportedAudioFileException;

    /**
     * Load tag information from a file.
     * 
     * @param input
     * @throws java.io.IOException
     * @throws javax.sound.sampled.UnsupportedAudioFileException
     */
    public void load(File input) throws IOException, UnsupportedAudioFileException;

    /**
     * Get encoding type
     *
     * @return encoding type
     */
    public String getEncodingType();

    /**
     * Get codec details
     *
     * @return encoding type
     */
    public String getCodecDetails();

    /**
     * Get sample rate
     *
     * @return sample rate
     */
    public int getSampleRate();

    /**
     * Get nominal bitrate
     *
     * @return bitrate in bps
     */
    public int getBitRate();

    /**
     * Get channels.
     *
     * @return channels
     */
    public int getChannels();

    /**
     * Get track length in seconds.
     *
     * @return track length in seconds
     */
    public int getTrackLength();

    /**
     * Get the title of the song.
     *
     * @return the title of the song
     */
    public String getTitle();

    /**
     * Get the artist that performed the song
     *
     * @return the artist that performed the song
     */
    public String getArtist();

    /**
     * Get the name of the album upon which the song resides
     *
     * @return the album name
     */
    public String getAlbum();

    /**
     * Get the track number of this track on the album
     *
     * @return the track number
     */
    public String getTrack();

    /**
     * Get the genre string of the music
     *
     * @return the genre string
     */
    public String getGenre();

    /**
     * Get the year the track was released
     *
     * @return the year the track was released
     */
    public String getYear();

    /**
     * Get any comments provided about the song
     *
     * @return the comments
     */
    public String getComment();
}