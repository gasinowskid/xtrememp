/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2011 Besmir Beqiri
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
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.AudioHeader;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import xtrememp.util.Utilities;

/**
 *
 * @author Besmir Beqiri
 */
public class GenericInfo extends TagInfo {

    protected int bitspersample = AudioSystem.NOT_SPECIFIED;
    protected int framesize = AudioSystem.NOT_SPECIFIED;

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

        try {
            AudioFile audioFile = AudioFileIO.read(input);
            AudioHeader audioHeader = audioFile.getAudioHeader();
            if (audioHeader != null) {
                encodingType = audioHeader.getEncodingType();
                format = audioHeader.getFormat();
                sampleRate = audioHeader.getSampleRate();
                sampleRateAsNumber = audioHeader.getSampleRateAsNumber();
                bitRate = audioHeader.getBitRate();
                bitRateAsNumber = audioHeader.getBitRateAsNumber();
                duration = audioHeader.getTrackLength();
                channels = audioHeader.getChannels();
            }

            Tag tag = audioFile.getTag();
            if (tag != null) {
                title = tag.getFirst(FieldKey.TITLE);
                artist = tag.getFirst(FieldKey.ARTIST);
                album = tag.getFirst(FieldKey.ALBUM);
                year = tag.getFirst(FieldKey.YEAR);
                genre = tag.getFirst(FieldKey.GENRE);
                track = tag.getFirst(FieldKey.TRACK);
                comment = tag.getFirst(FieldKey.COMMENT);
                artwork = tag.getFirstArtwork();
            }
        } catch (CannotReadException ex) {
            throw new IOException(ex);
        } catch (TagException ex) {
            throw new UnsupportedAudioFileException(ex.getMessage());
        } catch (ReadOnlyFileException ex) {
            throw new IOException(ex);
        } catch (InvalidAudioFrameException ex) {
            throw new UnsupportedAudioFileException(ex.getMessage());
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
        encodingType = aff.getType().toString();
        AudioFormat audioFormat = aff.getFormat();
        channelsAsNumber = audioFormat.getChannels();
        sampleRateAsNumber = (int) audioFormat.getSampleRate();
        bitspersample = audioFormat.getSampleSizeInBits();
        framesize = audioFormat.getFrameSize();
        bitRateAsNumber = Math.round(bitspersample * sampleRateAsNumber * channelsAsNumber / 1000);
    }

    @Override
    public String getCodecDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b>Encoding Type: </b>");
        sb.append(getEncodingType().toUpperCase());
        sb.append("<br><b>Format: </b>").append(getFormat());
        sb.append("<br><b>Sampling rate: </b>");
        sb.append(getSampleRate()).append(" Hz");
        sb.append("<br><b>Bitrate: </b>");
        sb.append(getBitRate()).append(" Kbps");
        sb.append("<br><b>Channels: </b>");
        sb.append(getChannels());
        if (size != AudioSystem.NOT_SPECIFIED) {
            sb.append("<br><b>Size: </b>");
            sb.append(Utilities.byteCountToDisplaySize(size));
        }
        sb.append("</html>");
        return sb.toString();
    }
}
