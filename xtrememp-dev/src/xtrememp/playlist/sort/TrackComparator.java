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

/**
 * Playlist item track comparator.
 *
 * @author Besmir Beqiri & rom1dep
 */
public class TrackComparator implements Comparator<PlaylistItem> {

    @Override
    public int compare(PlaylistItem pli1, PlaylistItem pli2) {
        String tg1 = pli1.getTagInfo().getTrack();
        String tg2 = pli2.getTagInfo().getTrack();
        if (tg1.contains("/")) {
            tg1 = tg1.substring(0, tg1.indexOf("/"));
        }
        if (tg2.contains("/")) {
            tg2 = tg2.substring(0, tg2.indexOf("/"));
        }
        int ntg1 = Integer.parseInt(tg1);
        int ntg2 = Integer.parseInt(tg2);

        if (ntg1 < 0) {
            ntg1 = 0;
        }
        if (ntg2 < 0) {
            ntg2 = 0;
        }

        return Integer.valueOf(ntg1).compareTo(Integer.valueOf(ntg2));
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
