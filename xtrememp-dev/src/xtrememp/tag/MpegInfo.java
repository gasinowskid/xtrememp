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

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.tritonus.share.sampled.file.TAudioFileFormat;
import xtrememp.util.Utilities;

/**
 * This class gives information (audio format and comments) about MPEG input or URL.
 * 
 * @author Besmir Beqiri
 */
public class MpegInfo extends TagInfo {

    protected String encoder = null;
    protected String version = null;
    protected String layer = null;
    protected String emphasis = null;
    protected String vendor = null;
    protected boolean copyright = false;
    protected boolean crc = false;
    protected boolean original = false;
    protected boolean privat = false;
    protected boolean vbr = false;

    /**
     * Load and parse MP3 info from a file.
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
            MP3File mp3File = (MP3File) AudioFileIO.read(file);
            MP3AudioHeader audioHeader = mp3File.getMP3AudioHeader();
            Tag mpegTag = mp3File.getTag();

            if (audioHeader != null) {
                encodingType = audioHeader.getEncodingType();
                encoder = audioHeader.getEncoder();
                channels = audioHeader.getChannels();
                if (channels.equals("Stereo")) {
                    channelsAsNumber = 2;
                } else if (channels.equals("Joint Stereo")) {
                    channelsAsNumber = 2;
                } else if (channels.equals("Dual")) {
                    channelsAsNumber = 2;
                } else if (channels.equals("Mono")) {
                    channelsAsNumber = 1;
                } else {
                    channelsAsNumber = 0;
                }
                sampleRateAsNumber = audioHeader.getSampleRateAsNumber();
                bitRateAsNumber = (int) audioHeader.getBitRateAsNumber();
                layer = audioHeader.getMpegLayer();
                version = audioHeader.getMpegVersion();
                crc = audioHeader.isProtected();
                vbr = audioHeader.isVariableBitRate();
                copyright = audioHeader.isCopyrighted();
                original = audioHeader.isOriginal();
                privat = audioHeader.isPrivate();
                emphasis = audioHeader.getEmphasis();
                duration = audioHeader.getTrackLength();
            }
            if (mpegTag != null) {
                title = mpegTag.getFirst(FieldKey.TITLE);
                artist = mpegTag.getFirst(FieldKey.ARTIST);
                album = mpegTag.getFirst(FieldKey.ALBUM);
                year = mpegTag.getFirst(FieldKey.YEAR);
                genre = mpegTag.getFirst(FieldKey.GENRE);
                track = mpegTag.getFirst(FieldKey.TRACK);
                comment = mpegTag.getFirst(FieldKey.COMMENT);
            }
        } catch (CannotReadException ex) {
            throw new IOException(ex);
        } catch (TagException ex) {
            throw new UnsupportedAudioFileException("Not MP3 audio format");
        } catch (ReadOnlyFileException ex) {
            throw new IOException(ex);
        } catch (InvalidAudioFrameException ex) {
            throw new UnsupportedAudioFileException("Not MP3 audio format");
        }
    }

    /**
     * Load and parse MPEG info from an URL.
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
        loadShoutastInfo(aff);
    }

    /**
     * Load and parse MPEG info from an input stream.
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
     * @param aff the audio file format
     */
    protected void loadInfo(AudioFileFormat aff) throws UnsupportedAudioFileException {
        encodingType = aff.getType().toString();
        if (!encodingType.equalsIgnoreCase("mp3")) {
            throw new UnsupportedAudioFileException("Not MP3 audio format");
        }
        if (aff instanceof TAudioFileFormat) {
            Map props = ((TAudioFileFormat) aff).properties();
            if (props.containsKey("mp3.channels")) {
                channelsAsNumber = ((Integer) props.get("mp3.channels")).intValue();
            }
            if (props.containsKey("mp3.frequency.hz")) {
                sampleRateAsNumber = ((Integer) props.get("mp3.frequency.hz")).intValue();
            }
            if (props.containsKey("mp3.bitrate.nominal.bps")) {
                bitRateAsNumber = ((Integer) props.get("mp3.bitrate.nominal.bps")).intValue() / 1000;
            }
            if (props.containsKey("mp3.version.layer")) {
                layer = "Layer " + props.get("mp3.version.layer");
            }
            if (props.containsKey("mp3.version.mpeg")) {
                version = (String) props.get("mp3.version.mpeg");
                if (version.equals("1")) {
                    version = "MPEG1";
                } else if (version.equals("2")) {
                    version = "MPEG2-LSF";
                } else if (version.equals("2.5")) {
                    version = "MPEG2.5-LSF";
                }
            }
            if (props.containsKey("mp3.mode")) {
                int mode = ((Integer) props.get("mp3.mode")).intValue();
                if (mode == 0) {
                    channels = "Stereo";
                } else if (mode == 1) {
                    channels = "Joint Stereo";
                } else if (mode == 2) {
                    channels = "Dual Channel";
                } else if (mode == 3) {
                    channels = "Single Channel";
                }
            }
            if (props.containsKey("mp3.crc")) {
                crc = ((Boolean) props.get("mp3.crc")).booleanValue();
            }
            if (props.containsKey("mp3.vbr")) {
                vbr = ((Boolean) props.get("mp3.vbr")).booleanValue();
            }
            if (props.containsKey("mp3.copyright")) {
                copyright = ((Boolean) props.get("mp3.copyright")).booleanValue();
            }
            if (props.containsKey("mp3.original")) {
                original = ((Boolean) props.get("mp3.original")).booleanValue();
            }
            if (props.containsKey("duration")) {
                duration = Math.round((((Long) props.get("duration")).longValue()) / 1000000);
            }
            emphasis = "none";
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
            if (props.containsKey("mp3.id3tag.genre")) {
                genre = (String) props.get("mp3.id3tag.genre");
            }
            if (props.containsKey("mp3.id3tag.track")) {
                track = (String) props.get("mp3.id3tag.track");
            }
        }
    }

    /**
     * Load Shoutcast info from AudioFileFormat.
     *
     * @param aff
     * @throws IOException
     * @throws UnsupportedAudioFileException
     */
    protected void loadShoutastInfo(AudioFileFormat aff) throws IOException, UnsupportedAudioFileException {
        encodingType = aff.getType().toString();
        if (!encodingType.equalsIgnoreCase("mp3")) {
            throw new UnsupportedAudioFileException("Not MP3 audio format");
        }
        if (aff instanceof TAudioFileFormat) {
            Map props = ((TAudioFileFormat) aff).properties();
            // Try shoutcast meta data (if any).
            Iterator it = props.keySet().iterator();
            comment = "";
            while (it.hasNext()) {
                String key = (String) it.next();
                if (key.startsWith("mp3.shoutcast.metadata.")) {
                    String value = (String) props.get(key);
                    key = key.substring(23, key.length());
                    if (key.equalsIgnoreCase("icy-name")) {
                        title = value;
                    } else if (key.equalsIgnoreCase("icy-genre")) {
                        genre = value;
                    } else {
                        comment.concat(key + "=" + value);
                    }
                }
            }
        }
    }

    public String getEncoder() {
        return encoder;
    }

    public String getMpegLayer() {
        return layer;
    }

    public String getMpegVersion() {
        return version;
    }

    public boolean isVariableBitRate() {
        return vbr;
    }

    public boolean isProtected() {
        return crc;
    }

    public boolean isOriginal() {
        return original;
    }

    public boolean isCopyrighted() {
        return copyright;
    }

    public boolean isPrivate() {
        return privat;
    }

    public String getEmphasis() {
        return emphasis;
    }

    public long getSize() {
        return size;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public String getCodecDetails() {
        StringBuilder sb = new StringBuilder();
        sb.append("<html><b>Encoding type: </b>");
        sb.append(getEncodingType().toUpperCase());
        if (!Utilities.isNullOrEmpty(getEncoder())) {
            sb.append("<br><b>Encoder: </b>");
            sb.append(getEncoder());
        }
        sb.append("<br><b>Version: </b>");
        sb.append(getMpegVersion());
        sb.append("<br><b>Layer: </b>");
        sb.append(getMpegLayer());
        sb.append("<br><b>Sampling rate: </b>");
        sb.append(getSampleRateAsNumber()).append(" Hz");
        sb.append("<br><b>Bitrate: </b>");
        sb.append(getBitRateAsNumber()).append(" Kbps");
        sb.append("<br><b>Channels: </b>");
        sb.append(getChannelsAsNumber());
        sb.append("<br><b>Channels mode: </b>");
        sb.append(getChannels());
        sb.append("<br><b>CRC: </b>");
        sb.append(isProtected());
        sb.append("<br><b>Variable Bitrate: </b>");
        sb.append(isVariableBitRate());
        sb.append("<br><b>Copyrighted: </b>");
        sb.append(isCopyrighted());
        sb.append("<br><b>Private: </b>");
        sb.append(isPrivate());
        sb.append("<br><b>Emphasis: </b>");
        sb.append(getEmphasis());
        if (size > 0) {
            sb.append("<br><b>Size: </b>");
            sb.append(Utilities.byteCountToDisplaySize(size));
        }
        sb.append("</html>");
        return sb.toString();
    }
}
