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

import org.tritonus.share.sampled.file.TAudioFileFormat;

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
import org.jaudiotagger.audio.mp3.MP3AudioHeader;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import xtrememp.util.Utilities;

/**
 * This class gives information (audio format and comments) about MPEG input or URL.
 * 
 * @author Besmir Beqiri
 */
public class MpegInfo implements TagInfo {

    protected String type = null;
    protected String encoder = null;
    protected String channelsMode = null;
    protected String version = null;
    protected String layer = null;
    protected String emphasis = null;
    protected String vendor = null;
    protected String location = null;
    protected int channels = 0;
    protected int samplerate = 0;
    protected int bitrate = 0;
    protected int totals = -1;
    protected long size = -1;
    protected boolean copyright = false;
    protected boolean crc = false;
    protected boolean original = false;
    protected boolean privat = false;
    protected boolean vbr = false;
    protected String track = null;
    protected String year = null;
    protected String genre = null;
    protected String title = null;
    protected String artist = null;
    protected String album = null;
    protected String comment = null;

    /**
     * Load and parse MP3 info from a file.
     *
     * @param input
     * @throws IOException
     */
    @Override
    public void load(File input) throws IOException, UnsupportedAudioFileException {
        AudioFileFormat aff = AudioSystem.getAudioFileFormat(input);
//        loadInfo(aff);
        type = aff.getType().toString();
        if (!type.equalsIgnoreCase("mp3")) {
            throw new UnsupportedAudioFileException("Not MP3 audio format");
        }
        size = input.length();
        location = input.getPath();
        MP3AudioHeader audioHeader = null;
        Tag mpegTag = null;
        try {
            MP3File mp3File = (MP3File) AudioFileIO.read(input);
            audioHeader = mp3File.getMP3AudioHeader();
            mpegTag = mp3File.getTag();

            if (audioHeader != null) {
                type = audioHeader.getEncodingType();
                encoder = audioHeader.getEncoder();
                channelsMode = audioHeader.getChannels();
                if (channelsMode.equals("Stereo")) {
                    channels = 2;
                } else if (channelsMode.equals("Joint Stereo")) {
                    channels = 2;
                } else if (channelsMode.equals("Dual")) {
                    channels = 2;
                } else if (channelsMode.equals("Mono")) {
                    channels = 1;
                } else {
                    channels = 0;
                }
                samplerate = audioHeader.getSampleRateAsNumber();
                bitrate = (int) audioHeader.getBitRateAsNumber();
                layer = audioHeader.getMpegLayer();
                version = audioHeader.getMpegVersion();
                crc = audioHeader.isProtected();
                vbr = audioHeader.isVariableBitRate();
                copyright = audioHeader.isCopyrighted();
                original = audioHeader.isOriginal();
                privat = audioHeader.isPrivate();
                emphasis = audioHeader.getEmphasis();
                totals = audioHeader.getTrackLength();
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
        } catch (Exception ex) {
            ex.printStackTrace();
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
        type = aff.getType().toString();
        if (!type.equalsIgnoreCase("mp3")) {
            throw new UnsupportedAudioFileException("Not MP3 audio format");
        }
        if (aff instanceof TAudioFileFormat) {
            Map props = ((TAudioFileFormat) aff).properties();
            if (props.containsKey("mp3.channels")) {
                channels = ((Integer) props.get("mp3.channels")).intValue();
            }
            if (props.containsKey("mp3.frequency.hz")) {
                samplerate = ((Integer) props.get("mp3.frequency.hz")).intValue();
            }
            if (props.containsKey("mp3.bitrate.nominal.bps")) {
                bitrate = ((Integer) props.get("mp3.bitrate.nominal.bps")).intValue() / 1000;
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
                    channelsMode = "Stereo";
                } else if (mode == 1) {
                    channelsMode = "Joint Stereo";
                } else if (mode == 2) {
                    channelsMode = "Dual Channel";
                } else if (mode == 3) {
                    channelsMode = "Single Channel";
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
            if (props.containsKey("duration")) {
                totals = Math.round((((Long) props.get("duration")).longValue()) / 1000000);
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
        type = aff.getType().toString();
        if (!type.equalsIgnoreCase("mp3")) {
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

    public String getChannelsMode() {
        return channelsMode;
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
        StringBuffer sb = new StringBuffer();
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
        sb.append(getSampleRate() + " Hz");
        sb.append("<br><b>Bitrate: </b>");
        sb.append(getBitRate() + " Kbps");
        sb.append("<br><b>Channels: </b>");
        sb.append(getChannels());
        sb.append("<br><b>Channels mode: </b>");
        sb.append(getChannelsMode());
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
        if (size != -1) {
            sb.append("<br><b>Size: </b>");
            sb.append(Utilities.byteCountToDisplaySize(size));
        }
        sb.append("</html>");
        return sb.toString();
    }

    /*-- TagInfo Implementation --*/
    @Override
    public int getChannels() {
        return channels;
    }

    @Override
    public int getSampleRate() {
        return samplerate;
    }

    @Override
    public int getBitRate() {
        return bitrate;
    }

    @Override
    public int getTrackLength() {
        return totals;
    }

    @Override
    public String getTitle() {
        return (title == null) ? null : title.trim();
    }

    @Override
    public String getArtist() {
        return (artist == null) ? null : artist.trim();
    }

    @Override
    public String getAlbum() {
        return (album == null) ? null : album.trim();
    }

    @Override
    public String getTrack() {
        if (track != null) {
            if (track.contains("/")) {
                track = track.substring(0, track.indexOf("/"));
            }
        }
        return (track == null) ? null : track.trim();
    }

    @Override
    public String getGenre() {
        return (genre == null) ? null : genre.trim();
    }

    @Override
    public String getComment() {
        return (comment == null) ? null : comment.trim();
    }

    @Override
    public String getYear() {
        return (year == null) ? null : year.trim();
    }

    @Override
    public String getEncodingType() {
        return (type == null) ? null : type.trim();
    }
}
