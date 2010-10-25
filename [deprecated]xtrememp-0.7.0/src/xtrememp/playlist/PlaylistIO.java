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
package xtrememp.playlist;

import com.melloware.jspiff.jaxp.XspfPlaylist;
import com.melloware.jspiff.jaxp.XspfPlaylistTrackList;
import com.melloware.jspiff.jaxp.XspfTrack;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.tag.TagInfo;
import xtrememp.util.Utilities;

/**
 * Playlist input/output utility.
 * This class provides static utility methods for loading and saving playlist.
 * It support .m3u and .pls playlist format.
 * 
 * @author Besmir Beqiri
 */
public final class PlaylistIO {

    private static final Logger logger = LoggerFactory.getLogger(PlaylistIO.class);

    /**
     * Loads playlist from the specified location.
     * 
     * @param location
     * @return a list of playlist items
     */
    public static List<PlaylistItem> load(String location) throws PlaylistException {
        if (!Utilities.isNullOrEmpty(location)) {
            String filenameExt = FilenameUtils.getExtension(location);
            if (filenameExt.equalsIgnoreCase("m3u")) {
                return loadM3U(location);
            } else if (filenameExt.equalsIgnoreCase("pls")) {
                return loadPLS(location);
            } else if (filenameExt.equalsIgnoreCase("xspf")) {
                return loadXSPF(location);
            }
        }
        return null;
    }

    /**
     * Load playlist in M3U format.
     *
     * @param location
     * @return a list of playlist items.
     */
    protected static List<PlaylistItem> loadM3U(String location) throws PlaylistException {
        List<PlaylistItem> itemList = new ArrayList<PlaylistItem>();
        BufferedReader br = null;
        try {
            // Playlist from URL ? (http:, ftp:, file: ....)
            if (Utilities.startWithProtocol(location)) {
                br = new BufferedReader(new InputStreamReader((new URL(location)).openStream()));
            } else {
                br = new BufferedReader(new FileReader(location));
            }
            String line = null;
            String songName = null;
            String songFile = null;
            String songLength = null;
            while ((line = br.readLine()) != null) {
                if (line.trim().length() == 0) {
                    continue;
                }
                if (line.startsWith("#")) {
                    if (line.toUpperCase().startsWith("#EXTINF")) {
                        int indA = line.indexOf(",", 0);
                        if (indA != -1) {
                            songName = line.substring(indA + 1, line.length());
                        }
                        int indB = line.indexOf(":", 0);
                        if (indB != -1) {
                            if (indB < indA) {
                                songLength = (line.substring(indB + 1, indA)).trim();
                            }
                        }
                    }
                } else {
                    songFile = line;
                    if (songName == null) {
                        songName = songFile;
                    }
                    if (songLength == null) {
                        songLength = "-1";
                    }
                    PlaylistItem pli = null;
                    if (Utilities.startWithProtocol(songFile)) {
                        // URL
                        pli = new PlaylistItem(songName, songFile, Integer.parseInt(songLength), false);
                    } else {
                        // File
                        File f = new File(songFile);
                        if (f.exists()) {
                            pli = new PlaylistItem(songName, songFile, Integer.parseInt(songLength), true);
                        } else {
                            // Try relative path
                            String parent = new File(location).getParent();
                            f = new File(parent, songFile);
                            if (f.exists()) {
                                pli = new PlaylistItem(songName, f.getAbsolutePath(), Integer.parseInt(songLength), true);
                            }
                        }
                    }
                    if (pli != null) {
                        itemList.add(pli);
                    }
                    songFile = null;
                    songName = null;
                    songLength = null;
                }
            }
        } catch (Exception ex) {
            logger.error("Can't load M3U playlist", ex);
            throw new PlaylistException(ex.getMessage(), ex.getCause());
        } finally {
            IOUtils.closeQuietly(br);
        }
        return itemList;
    }

    /**
     * Load playlist in PLS format.
     *
     * @param location
     * @return a list of playlist items.
     */
    protected static List<PlaylistItem> loadPLS(String location) throws PlaylistException {
        List<PlaylistItem> itemList = new ArrayList<PlaylistItem>();
        BufferedReader br = null;
        try {
            // Playlist from URL ? (http:, ftp:, file: ....)
            if (Utilities.startWithProtocol(location)) {
                br = new BufferedReader(new InputStreamReader((new URL(location)).openStream()));
            } else {
                br = new BufferedReader(new FileReader(location));
            }
            String line = null;
            boolean markerFound = false;
            int numberOfEntries = -1;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                lineNumber++;
                if (line.length() > 0) {
                    // Header
                    // The PLS marker string
                    if (!markerFound) {
                        if (!line.equalsIgnoreCase("[playlist]")) {
                            logger.warn("Not a PLS playlist format");
                        }
                        markerFound = true;
                        continue;
                    }

                    if (line.indexOf('=') <= 0) {
                        logger.error("Malformed PLS playlist {}", location);
                        break;
                    }

                    StringTokenizer st = new StringTokenizer(line, "=");
                    String key = st.nextToken().trim();
                    String value = st.nextToken().trim();
                    if (key.equalsIgnoreCase("numberofentries")) {
                        int tmpValue;
                        try {
                            tmpValue = Integer.parseInt(value);
                        } catch (NumberFormatException ex) {
                            logger.error("Error on parsing NumberOfEntries in PLS playlist: {} at line {}", location, lineNumber);
                            break;
                        }

                        if (tmpValue < 0) {
                            logger.warn("Invalid NumberOfEntries in PLS playlist: {} at line {}", location, lineNumber);
                        }

                        // Test if already found.
                        if ((numberOfEntries >= 0) && (numberOfEntries != tmpValue)) {
                            logger.warn("PLS playlist number of entries already specified with a different value");
                        }
                        numberOfEntries = tmpValue;
                    } // Track entry
                    else if (key.toLowerCase().startsWith("file")) {
                        PlaylistItem pli = null;
                        if (Utilities.startWithProtocol(value)) {
                            // URL.
                            pli = new PlaylistItem(value, value, -1, false);
                        } else {
                            // File.
                            File f = new File(value);
                            if (f.exists()) {
                                pli = new PlaylistItem(value, value, -1, true);
                            } else {
                                // Try relative path.
                                String parent = new File(location).getParent();
                                f = new File(parent, value);
                                if (f.exists()) {
                                    pli = new PlaylistItem(value, f.getAbsolutePath(), -1, true);
                                }
                            }
                        }
                        if (pli != null) {
                            itemList.add(pli);
                        }
                    } else if (key.toLowerCase().startsWith("title")) {
                        try {
                            int index = Integer.parseInt(key.substring(5)) - 1;
                            PlaylistItem pli = itemList.get(index);
                            pli.setName(value);
                        } catch (Exception ex) {
                            logger.warn("Corrupted PLS playlist {} at line {}", location, lineNumber);
                            continue;
                        }
                    } else if (key.toLowerCase().startsWith("length")) {
                        try {
                            int duration = Integer.parseInt(value);
                            int index = Integer.parseInt(key.substring(6)) - 1;
                            PlaylistItem pli = itemList.get(index);
                            pli.setDuration(duration);
                        } catch (Exception ex) {
                            logger.warn("Corrupted PLS playlist {} at line {}", location, lineNumber);
                            continue;
                        }
                    } // Footer
                    else if (key.equalsIgnoreCase("version")) {
                        // If present, shall be "2".
                        if (!value.equals("2")) {
                            logger.error("Unknown PLS version " + value);
                            break;
                        }
                    }
                }
            }
            if (numberOfEntries < 0) {
                logger.warn("No number of entries in PLS playlist");
            }
        } catch (Exception ex) {
            logger.error("Can't load PLS playlist", ex);
            throw new PlaylistException(ex.getMessage(), ex.getCause());
        } finally {
            IOUtils.closeQuietly(br);
        }
        return itemList;
    }

    /**
     * Load playlist in XSPF format.
     *
     * @param location
     * @return a list of playlist items.
     */
    protected static List<PlaylistItem> loadXSPF(String location) throws PlaylistException {
        List<PlaylistItem> itemList = new ArrayList<PlaylistItem>();
        XspfPlaylist xspfPlaylist = new XspfPlaylist();
        try {
            // Playlist from URL ? (http:, ftp:, file: ....)
            if (Utilities.startWithProtocol(location)) {
                xspfPlaylist.setup(new URL(location));
            } else {
                xspfPlaylist.setup(new FileReader(location));
            }
            XspfPlaylistTrackList tracks = xspfPlaylist.getPlaylistTrackList();
            for (XspfTrack track : tracks.getTrack()) {
                PlaylistItem pli = null;
                String trackLocation = track.getLocation(0);
                if (Utilities.startWithProtocol(trackLocation)) {
                    // URL.
                    if (Utilities.startWithProtocol(trackLocation, "file:")) {
                        File f = new File(new URI(trackLocation));
                        if (f.exists()) {
                            String baseName = FilenameUtils.getBaseName(f.getName());
                            pli = new PlaylistItem(baseName, f.getAbsolutePath(), -1, true);
                        }
                    } else {
                        String trackTitle = track.getTitle();
                        if (Utilities.isNullOrEmpty(trackTitle)) {
                            pli = new PlaylistItem(trackLocation, trackLocation, -1, false);
                        } else {
                            pli = new PlaylistItem(trackTitle, trackLocation, -1, false);
                        }
                    }
                } else {
                    // File.
                    File f = new File(trackLocation);
                    if (f.exists()) {
                        String baseName = FilenameUtils.getBaseName(f.getName());
                        pli = new PlaylistItem(baseName, f.getAbsolutePath(), -1, true);
                    }
                }
                if (pli != null) {
                    itemList.add(pli);
                }
                trackLocation = null;
            }
        } catch (Exception ex) {
            logger.error("Can't load XSPF playlist", ex);
            throw new PlaylistException(ex.getMessage(), ex.getCause());
        }
        return itemList;
    }

    /**
     * Saves playlist in M3U format.
     * 
     * @param playlist
     * @param location
     * @return <code>true</code> if playlist is successfully saved, else <code>false</code>.
     */
    public static boolean saveM3U(Playlist playlist, String location) throws PlaylistException {
        if (playlist != null) {
            BufferedWriter bw = null;
            try {
                bw = new BufferedWriter(new FileWriter(location));
                bw.write("#EXTM3U");
                bw.newLine();
                for (PlaylistItem pli : playlist.listItems()) {
                    bw.write("#EXTINF:" + pli.getM3UExtInf());
                    bw.newLine();
                    bw.write(pli.getLocation());
                    bw.newLine();
                }
                playlist.setModified(false);
                return true;
            } catch (IOException ex) {
                logger.error("Can't save playlist in M3U format", ex);
                throw new PlaylistException(ex.getMessage(), ex.getCause());
            } finally {
                IOUtils.closeQuietly(bw);
            }
        }
        return false;
    }

    /**
     * Saves playlist in XSPF format.
     * 
     * @param playlist
     * @param location
     * @return <code>true</code> if playlist is successfully saved, else <code>false</code>.
     */
    public static boolean saveXSPF(Playlist playlist, String location) throws PlaylistException {
        if (playlist != null) {
            File xspfFile = new File(location);

            // Create a xspf playlist
            XspfPlaylist xspfPlaylist = new XspfPlaylist();
            xspfPlaylist.setVersion("1");
            xspfPlaylist.setTitle("Playlist");
            xspfPlaylist.setLocation(xspfFile.toURI().toString());

            // Create track list
            XspfPlaylistTrackList tracks = new XspfPlaylistTrackList();
            for (PlaylistItem pli : playlist.listItems()) {
                // Create a track and add to list
                XspfTrack track = new XspfTrack();
                track.setIdentifier(String.valueOf(playlist.indexOf(pli)));
                if (pli.isFile()) {
                    track.setLocation(new File(pli.getLocation()).toURI().toString());
                } else {
                    track.setLocation(pli.getLocation());
                }
                if (pli.isFile()) {
                    TagInfo tagInfo = pli.getTagInfo();
                    if (tagInfo != null) {
                        String title = tagInfo.getTitle();
                        if (!Utilities.isNullOrEmpty(title)) {
                            track.setTitle(title.trim());
                        }
                        String artist = tagInfo.getArtist();
                        if (!Utilities.isNullOrEmpty(artist)) {
                            track.setCreator(artist.trim());
                        }
                        String album = tagInfo.getAlbum();
                        if (!Utilities.isNullOrEmpty(album)) {
                            track.setAlbum(album.trim());
                        }
                        String trackNum = String.valueOf(tagInfo.getTrack()).trim();
                        if (!trackNum.equals("null")) {
                            try {
                                track.setTrackNum(new BigInteger(trackNum));
                            } catch (NumberFormatException ex) {
                                logger.debug("{} is not a valid number", trackNum, ex);
                            }
                        }
                        String genre = tagInfo.getGenre();
                        if (!Utilities.isNullOrEmpty(genre)) {
                            track.setAnnotation(genre.trim());
                        }
                        long duration = pli.getDuration();
                        if (duration >= 0) {
                            try {
                                track.setDuration(new BigInteger(String.valueOf(duration)));
                            } catch (NumberFormatException ex) {
                                logger.debug("{} is not a valid number", duration, ex);
                            }
                        }
                    }
                } else {
                    String name = pli.getName();
                    if (!Utilities.isNullOrEmpty(name)) {
                        track.setTitle(name.trim());
                    }
                }
                tracks.addTrack(track);
            }
            // add track to playlist
            xspfPlaylist.setPlaylistTrackList(tracks);

            // save to file
            OutputFormat format = OutputFormat.createPrettyPrint();
            FileWriter fw = null;
            Document doc = null;
            try {
                fw = new FileWriter(xspfFile);
                XMLWriter xmlWriter = new XMLWriter(fw, format);
                doc = DocumentHelper.parseText(xspfPlaylist.makeTextDocument());
                xmlWriter.write(doc);
                return true;
            } catch (Exception ex) {
                logger.error("Can't save playlist in XSPF format", ex);
                throw new PlaylistException(ex.getMessage(), ex.getCause());
            } finally {
                IOUtils.closeQuietly(fw);
            }
        }
        return false;
    }
}
