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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.flac.FlacInfoReader;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.metadata.StreamInfo;
import xtrememp.util.Utilities;

/**
 * This class gives information (audio format and comments) about Flac input or URL.
 * 
 * @author Besmir Beqiri
 */
public class FlacInfo extends TagInfo {

    protected StreamInfo info = null;
    protected int bitspersample = AudioSystem.NOT_SPECIFIED;

    /**
     * Load and parse Flac info from File.
     *
     * @param file
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    @Override
    public void load(File file) throws IOException, UnsupportedAudioFileException {
        size = file.length();
        location = file.getPath();

        FileInputStream is = new FileInputStream(file);
        FLACDecoder decoder = new FLACDecoder(is);
        decoder.readMetadata();
        info = decoder.getStreamInfo();
        
        try {
            AudioFile flacFile = AudioFileIO.read(file);
            FlacTag flacTag = (FlacTag) flacFile.getTag();
            FlacInfoReader fir = new FlacInfoReader();
            GenericAudioHeader gah = fir.read(new RandomAccessFile(file, "r"));

            if (gah != null) {
                encodingType = gah.getEncodingType();
                channelsAsNumber = gah.getChannelNumber();
                sampleRateAsNumber = gah.getSampleRateAsNumber();
                bitRateAsNumber = (int) gah.getBitRateAsNumber();
                duration = gah.getTrackLength();
            }
            if (flacTag != null) {
                title = flacTag.getFirst(FieldKey.TITLE);
                artist = flacTag.getFirst(FieldKey.ARTIST);
                album = flacTag.getFirst(FieldKey.ALBUM);
                year = flacTag.getFirst(FieldKey.YEAR);
                genre = flacTag.getFirst(FieldKey.GENRE);
                track = flacTag.getFirst(FieldKey.TRACK);
                comment = flacTag.getFirst(FieldKey.COMMENT);
            }
        } catch (CannotReadException ex) {
            throw new IOException(ex);
        } catch (TagException ex) {
            throw new UnsupportedAudioFileException("Not Flac audio format");
        } catch (ReadOnlyFileException ex) {
            throw new IOException(ex);
        } catch (InvalidAudioFrameException ex) {
            throw new UnsupportedAudioFileException("Not Flac audio format");
        }
    }

    /**
     * Load and parse Flac info from an URL.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    @Override
    public void load(URL input) throws IOException, UnsupportedAudioFileException {
        location = input.toString();
        FLACDecoder decoder = new FLACDecoder(input.openStream());
        decoder.readMetadata();
        info = decoder.getStreamInfo();
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(input);
        loadInfo(aff);
    }

    /**
     * Load and parse Flac info from an input stream.
     *
     * @param input
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    @Override
    public void load(InputStream input) throws IOException, UnsupportedAudioFileException {
        FLACDecoder decoder = new FLACDecoder(input);
        decoder.readMetadata();
        info = decoder.getStreamInfo();
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(input);
        loadInfo(aff);
    }

    /**
     * Load Flac info from an AudioFileFormat.
     *
     * @param aff the audio file format
     * @throws javax.sound.sampled.UnsupportedAudioFileException
     */
    protected void loadInfo(AudioFileFormat aff) throws UnsupportedAudioFileException {
        encodingType = aff.getType().toString();
        if (!encodingType.equalsIgnoreCase("flac")) {
            throw new UnsupportedAudioFileException("Not Flac audio format");
        }
        channelsAsNumber = info.getChannels();
        sampleRateAsNumber = info.getSampleRate();
        bitspersample = info.getBitsPerSample();
        duration = Math.round(info.getTotalSamples() / info.getSampleRate());
    }

    public long getSize() {
        return size;
    }

    public String getLocation() {
        return location;
    }

    public int getBitsPerSample() {
        return bitspersample;
    }

    @Override
    public String getCodecDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b>Encoding Type: </b>");
        sb.append(getEncodingType().toUpperCase());
        sb.append("<br><b>Sampling rate: </b>");
        sb.append(getSampleRateAsNumber()).append(" Hz");
        sb.append("<br><b>Bitrate: </b>");
        sb.append(getBitRateAsNumber()).append(" Kbps");
        sb.append("<br><b>Channels: </b>");
        sb.append(getChannelsAsNumber());
        if (size > 0) {
            sb.append("<br><b>Size: </b>");
            sb.append(Utilities.byteCountToDisplaySize(size));
        }
        sb.append("</html>");
        return sb.toString();
    }
}
