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
package xtrememp.playlist.sort;

/**
 *
 * @author rom1dep
 */
public enum HeaderIdentifier {

    TRACK, TITLE, ALBUM, ARTIST, DURATION, GENRE;

    public static String getLangageBundleRef(HeaderIdentifier id) {
        switch (id) {
            case TRACK:
                return "MainFrame.PlaylistHeader.Track";
            case TITLE:
                return "MainFrame.PlaylistHeader.Title";
            case ALBUM:
                return "MainFrame.PlaylistHeader.Album";
            case ARTIST:
                return "MainFrame.PlaylistHeader.Artist";
            case DURATION:
                return "MainFrame.PlaylistHeader.Duration";
            case GENRE:
                return "MainFrame.PlaylistHeader.Genre";
            default:
                return null;
        }
    }
}
