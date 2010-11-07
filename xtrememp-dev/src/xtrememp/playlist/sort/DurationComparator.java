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
 * Playlist item duration comparator.
 *
 * @author Besmir Beqiri
 */
public class DurationComparator implements Comparator<PlaylistItem> {

    @Override
    public int compare(PlaylistItem pli1, PlaylistItem pli2) {
        long duration1 = pli1.getDuration();
        long duration2 = pli2.getDuration();

        return (duration1 < duration2 ? -1 : (duration1 == duration2 ? 0 : 1));
    }

    @Override
    public int hashCode() {
        return "DurationComparator".hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return (this == obj)
                || ((obj != null) && (obj instanceof DurationComparator));
    }
}
