/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2008  Besmir Beqiri
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
import java.net.MalformedURLException;
import java.net.URL;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.util.Utilities;

/**
 * This class is a factory for TagInfo.
 * 
 * @author Besmir Beqiri
 */
public class TagInfoFactory {

    private static Logger logger = LoggerFactory.getLogger(TagInfoFactory.class);
    private static TagInfoFactory instance = null;

    /**
     * 
     * @return Instance of TagInfoFactory
     */
    public static synchronized TagInfoFactory getInstance() {
        if (instance == null) {
            instance = new TagInfoFactory();
        }
        return instance;
    }

    /**
     * Return tag info from a given URL.
     *
     * @param location
     * @return TagInfo structure for given URL
     */
    public TagInfo getTagInfo(URL location) {
        TagInfo taginfo;
        try {
            taginfo = new MpegInfo();
            taginfo.load(location);
        } catch (UnsupportedAudioFileException ex) {
            // Not Mpeg Format
            taginfo = null;
        } catch (IOException ex) {
                logger.debug(ex.getMessage(), ex);
            taginfo = null;
        }
        if (taginfo == null) {
            // Check Ogg Vorbis format.
            try {
                taginfo = new OggVorbisInfo();
                taginfo.load(location);
            } catch (UnsupportedAudioFileException ex) {
                // Not Ogg Vorbis Format
                taginfo = null;
            } catch (IOException ex) {
                logger.debug(ex.getMessage(), ex);
                taginfo = null;
            }
        }
        if (taginfo == null) {
            // Check Flac format.
            try {
                taginfo = new FlacInfo();
                taginfo.load(location);
            } catch (UnsupportedAudioFileException ex) {
                // Not Flac Format
                taginfo = null;
            } catch (IOException ex) {
                logger.debug(ex.getMessage(), ex);
                taginfo = null;
            }
        }
        if (taginfo == null) {
            // Check Generic format.
            try {
                taginfo = new GenericInfo();
                taginfo.load(location);
            } catch (UnsupportedAudioFileException ex) {
                // Not Generic Format
                taginfo = null;
            } catch (IOException ex) {
                logger.debug(ex.getMessage(), ex);
                taginfo = null;
            }
        }
        return taginfo;
    }

    /**
     * Return tag info from a given String.
     *
     * @param location
     * @return TagInfo structure for given location
     */
    public TagInfo getTagInfo(String location) {
        if (Utilities.startWithProtocol(location)) {
            try {
                return getTagInfo(new URL(location));
            } catch (MalformedURLException ex) {
                logger.debug(ex.getMessage(), ex);
                return null;
            }
        } else {
            return getTagInfo(new File(location));
        }
    }

    /**
     * Get TagInfo for given file.
     *
     * @param location
     * @return TagInfo structure for given location
     */
    public TagInfo getTagInfo(File location) {
        TagInfo taginfo;
        // Check Mpeg format.
        try {
            taginfo = new MpegInfo();
            taginfo.load(location);
        } catch (UnsupportedAudioFileException ex) {
            // Not Mpeg Format
            taginfo = null;
        } catch (IOException ex) {
                logger.debug(ex.getMessage(), ex);
            taginfo = null;
        }
        if (taginfo == null) {
            // Check Ogg Vorbis format.
            try {
                taginfo = new OggVorbisInfo();
                taginfo.load(location);
            } catch (UnsupportedAudioFileException ex) {
                // Not Ogg Vorbis Format
                taginfo = null;
            } catch (IOException ex) {
                logger.debug(ex.getMessage(), ex);
                taginfo = null;
            }
        }
        if (taginfo == null) {
            // Check Flac format.
            try {
                taginfo = new FlacInfo();
                taginfo.load(location);
            } catch (UnsupportedAudioFileException ex) {
                // Not Flac Format
                taginfo = null;
            } catch (IOException ex) {
                logger.debug(ex.getMessage(), ex);
                taginfo = null;
            }
        }
        if (taginfo == null) {
            // Check Generic format.
            try {
                taginfo = new GenericInfo();
                taginfo.load(location);
            } catch (UnsupportedAudioFileException ex) {
                // Not Generic Format
                taginfo = null;
            } catch (IOException ex) {
                logger.debug(ex.getMessage(), ex);
                taginfo = null;
            }
        }
        return taginfo;
    }
}
