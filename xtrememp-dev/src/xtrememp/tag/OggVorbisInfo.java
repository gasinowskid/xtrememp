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
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.generic.GenericAudioHeader;
import org.jaudiotagger.audio.ogg.util.OggInfoReader;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTag;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import xtrememp.util.Utilities;

/**
 * This class gives information (audio format and comments) about Ogg Vorbis input or URL.
 * 
 * @author Besmir Beqiri
 */
public class OggVorbisInfo extends TagInfo {

    protected String vendor = null;
    protected int serial = AudioSystem.NOT_SPECIFIED;
    protected int version = AudioSystem.NOT_SPECIFIED;
    protected int minbitrate = AudioSystem.NOT_SPECIFIED;
    protected int maxbitrate = AudioSystem.NOT_SPECIFIED;
    protected int averagebitrate = AudioSystem.NOT_SPECIFIED;

    /**
     * Load and parse Ogg Vorbis info from a file.
     *
     * @param file
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    @Override
    public void load(File file) throws IOException, UnsupportedAudioFileException {
        size = file.length();
        location = file.getPath();

        try {
            AudioFile oggFile = AudioFileIO.read(file);
            VorbisCommentTag vcTag = (VorbisCommentTag) oggFile.getTag();
            OggInfoReader oir = new OggInfoReader();
            GenericAudioHeader gah = oir.read(new RandomAccessFile(file, "r"));

            if (gah != null) {
                encodingType = gah.getEncodingType();
                channelsAsNumber = gah.getChannelNumber();
                sampleRateAsNumber = gah.getSampleRateAsNumber();
                bitRateAsNumber = (int) gah.getBitRateAsNumber();
                duration = gah.getTrackLength();
            }
            if (vcTag != null) {
                vendor = vcTag.getVendor();
                title = vcTag.getFirst(FieldKey.TITLE);
                artist = vcTag.getFirst(FieldKey.ARTIST);
                album = vcTag.getFirst(FieldKey.ALBUM);
                year = vcTag.getFirst(FieldKey.YEAR);
                genre = vcTag.getFirst(FieldKey.GENRE);
                track = vcTag.getFirst(FieldKey.TRACK);
                comment = vcTag.getFirst(FieldKey.COMMENT);
            }
        } catch (CannotReadException ex) {
            throw new IOException(ex);
        } catch (TagException ex) {
            throw new UnsupportedAudioFileException("Not Ogg Vorbis audio format");
        } catch (ReadOnlyFileException ex) {
            throw new IOException(ex);
        } catch (InvalidAudioFrameException ex) {
            throw new UnsupportedAudioFileException("Not Ogg Vorbis audio format");
        }
    }

    /**
     * Load and parse Ogg Vorbis info from an URL.
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
        loadExtendedInfo(aff);
    }

    /**
     * Load and parse Ogg Vorbis info from a input stream.
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
     * Load info from an AudioFileFormat.
     *
     * @param aff the audio file format 
     * @throws UnsupportedAudioFileException
     */
    protected void loadInfo(AudioFileFormat aff) throws UnsupportedAudioFileException {
        encodingType = aff.getType().toString();
        if (!encodingType.equalsIgnoreCase("ogg")) {
            throw new UnsupportedAudioFileException("Not Ogg Vorbis audio format");
        }
        if (aff instanceof TAudioFileFormat) {
            Map props = ((TAudioFileFormat) aff).properties();
            if (props.containsKey("ogg.channels")) {
                channelsAsNumber = ((Integer) props.get("ogg.channels")).intValue();
            }
            if (props.containsKey("ogg.frequency.hz")) {
                sampleRateAsNumber = ((Integer) props.get("ogg.frequency.hz")).intValue();
            }
            if (props.containsKey("ogg.bitrate.nominal.bps")) {
                bitRateAsNumber = ((Integer) props.get("ogg.bitrate.nominal.bps")).intValue();
            }
            averagebitrate = (int) bitRateAsNumber;
            if (props.containsKey("ogg.bitrate.max.bps")) {
                maxbitrate = ((Integer) props.get("ogg.bitrate.max.bps")).intValue();
            }
            if (props.containsKey("ogg.bitrate.min.bps")) {
                minbitrate = ((Integer) props.get("ogg.bitrate.min.bps")).intValue();
            }
            if (props.containsKey("ogg.version")) {
                version = ((Integer) props.get("ogg.version")).intValue();
            }
            if (props.containsKey("ogg.serial")) {
                serial = ((Integer) props.get("ogg.serial")).intValue();
            }
            if (props.containsKey("ogg.comment.encodedby")) {
                vendor = (String) props.get("ogg.comment.encodedby");
            }
            comment = "";
            if (props.containsKey("copyright")) {
                comment.concat((String) props.get("copyright"));
            }
            if (props.containsKey("title")) {
                title = (String) props.get("title");
            }
            if (props.containsKey("author")) {
                artist = (String) props.get("author");
            }
            if (props.containsKey("album")) {
                album = (String) props.get("album");
            }
            if (props.containsKey("date")) {
                year = (String) props.get("date");
            }
            if (props.containsKey("comment")) {
                comment.concat((String) props.get("comment"));
            }
            if (props.containsKey("duration")) {
                duration = Math.round((((Long) props.get("duration")).longValue()) / 1000000);
            }
            if (props.containsKey("ogg.comment.genre")) {
                genre = (String) props.get("ogg.comment.genre");
            }
            if (props.containsKey("ogg.comment.track")) {
                track = (String) props.get("ogg.comment.track");
            }
            if (props.containsKey("ogg.comment.ext.1")) {
                comment.concat((String) props.get("ogg.comment.ext.1"));
            }
            if (props.containsKey("ogg.comment.ext.2")) {
                comment.concat((String) props.get("ogg.comment.ext.2"));
            }
            if (props.containsKey("ogg.comment.ext.3")) {
                comment.concat((String) props.get("ogg.comment.ext.3"));
            }
        }
    }

    /**
     * Load extended info from AudioFileFormat.
     *
     * @param aff
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected void loadExtendedInfo(AudioFileFormat aff) throws IOException, UnsupportedAudioFileException {
        encodingType = aff.getType().toString();
        if (!encodingType.equalsIgnoreCase("ogg")) {
            throw new UnsupportedAudioFileException("Not Ogg Vorbis audio format");
        }
        if (aff instanceof TAudioFileFormat) {
            //Map props = ((TAudioFileFormat) aff).properties();
            // How to load icecast meta data (if any) ??
        }
    }

    public int getSerial() {
        return serial;
    }

    public int getVersion() {
        return version;
    }

    public int getMinBitrate() {
        return minbitrate;
    }

    public int getMaxBitrate() {
        return maxbitrate;
    }

    public int getAverageBitrate() {
        return averagebitrate;
    }

    public long getSize() {
        return size;
    }

    public String getVendor() {
        return vendor;
    }

    public String getLocation() {
        return location;
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
        sb.append("<br><b>Vendor: </b>");
        sb.append(getVendor());
        if (size > 0) {
            sb.append("<br><b>Size: </b>");
            sb.append(Utilities.byteCountToDisplaySize(size));
        }
        sb.append("</html>");
        return sb.toString();
    }
}
