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
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.apache.commons.io.FilenameUtils;
import xtrememp.util.Utilities;

/**
 *
 * @author Besmir Beqiri
 */
public class GenericInfo implements TagInfo {

    protected String type = null;
    protected int channels = 0;
    protected int bitspersample = 0;
    protected int framesize = 0;
    protected float samplerate = 0;
    protected int bitrate = 0;
    protected int duration = -1;
    protected long size = -1;
    protected String location = null;
    protected String title = null;

    /**
     * Load and parse info from a File.
     *
     * @param input
     * @throws IOException
     */
    @Override
    public void load(File input) throws IOException, UnsupportedAudioFileException {
        size = input.length();
        location = input.getPath();
        title = FilenameUtils.getBaseName(input.getName());
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(input);
        loadInfo(aff);
        if (bitspersample > 0) {
            duration = Math.round(size / (samplerate * channels * (bitspersample / 8)));
        } else {
            duration = Math.round(size / (samplerate * framesize));
        }
    }

    /**
     * Load and parse info from an URL.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    @Override
    public void load(URL input) throws IOException, UnsupportedAudioFileException {
        location = input.toString();
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(input);
        loadInfo(aff);
    }

    /**
     * Load and parse info from an input stream.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    @Override
    public void load(InputStream input) throws IOException, UnsupportedAudioFileException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(input);
        loadInfo(aff);
    }

    /**
     * Load info from AudioFileFormat.
     *
     * @param aff
     * @throws javax.sound.sampled.UnsupportedAudioFileException
     */
    protected void loadInfo(AudioFileFormat aff) throws UnsupportedAudioFileException {
        type = aff.getType().toString();
        AudioFormat format = aff.getFormat();
        channels = format.getChannels();
        samplerate = format.getSampleRate();
        bitspersample = format.getSampleSizeInBits();
        framesize = format.getFrameSize();
        bitrate = Math.round(bitspersample * samplerate * channels / 1000);
    }

    @Override
    public String getEncodingType() {
        return type;
    }

    @Override
    public String getCodecDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b>Encoding Type: </b>");
        sb.append(getEncodingType().toUpperCase());
        sb.append("<br><b>Sampling rate: </b>");
        sb.append(getSampleRate()).append(" Hz");
        sb.append("<br><b>Bitrate: </b>");
        sb.append(getBitRate()).append(" Kbps");
        sb.append("<br><b>Channels: </b>");
        sb.append(getChannels());
        if (size != -1) {
            sb.append("<br><b>Size: </b>");
            sb.append(Utilities.byteCountToDisplaySize(size));
        }
        sb.append("</html>");
        return sb.toString();
    }

    @Override
    public int getSampleRate() {
        return (int) samplerate;
    }

    @Override
    public int getBitRate() {
        return bitrate;
    }

    @Override
    public int getChannels() {
        return channels;
    }

    @Override
    public int getTrackLength() {
        return duration;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getArtist() {
        return "";
    }

    @Override
    public String getAlbum() {
        return "";
    }

    @Override
    public String getTrack() {
        return "";
    }

    @Override
    public String getGenre() {
        return "";
    }

    @Override
    public String getYear() {
        return "";
    }

    @Override
    public String getComment() {
        return "";
    }
}
