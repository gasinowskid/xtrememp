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

import java.util.Comparator;
import xtrememp.playlist.PlaylistItem;
import xtrememp.util.Utilities;

/**
 * Playlist item track comparator.
 *
 * @author Besmir Beqiri & rom1dep
 */
public class TrackComparator implements Comparator<PlaylistItem> {

    @Override
    public int compare(PlaylistItem pli1, PlaylistItem pli2) {
        String trackStr1 = pli1.getTagInfo().getTrack();
        String trackStr2 = pli2.getTagInfo().getTrack();
        
        int track1 = -1;
        int track2 = -1;

        if(!Utilities.isNullOrEmpty(trackStr1)) {
            track1 = Integer.parseInt(trackStr1);
        }

        if(!Utilities.isNullOrEmpty(trackStr2)) {
            track2 = Integer.parseInt(trackStr2);
        }

        return (track1 < track2 ? -1 : (track1 == track2 ? 0 : 1));
    }

    @Override
    public int hashCode() {
        return "TrackComparator".hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj)
                || ((obj != null) && (obj instanceof TrackComparator));
    }
}
