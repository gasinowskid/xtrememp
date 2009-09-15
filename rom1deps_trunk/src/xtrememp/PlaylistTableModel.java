/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2009 Besmir Beqiri
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
package xtrememp;

import xtrememp.playlist.*;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Besmir Beqiri
 */
public class PlaylistTableModel extends AbstractTableModel {

    public static final int TRACK_COLUMN = 0;
    public static final int TITLE_COLUMN = 1;
    public static final int ARTIST_COLUMN = 2;
    public static final int ALBUM_COLUMN = 3;
    public static final int TIME_COLUMN = 4;
//    public static final int GENRE_COLUMN = 4;
    public static final int COLUMN_COUNT = 5;
    private final Playlist playlist;
    private final List<PlaylistItem> playlistItems;
    private final String[] columnNames = {"#", "Title", "Artist", "Album", "Duration"};
    private static final String UNKNOWN = "<unknown>";

    public PlaylistTableModel(Playlist playlist) {
        this.playlist = playlist;
        this.playlistItems = playlist.listItems();
    }

    public void add(List<PlaylistItem> newItems) {
        int first = playlistItems.size();
        int last = first + newItems.size() - 1;
        playlistItems.addAll(newItems);
        this.fireTableRowsInserted(first, last);
    }

    public void add(PlaylistItem item) {
        int index = playlistItems.size();
        playlistItems.add(item);
        this.fireTableRowsInserted(index, index);
    }

    public void removeItemAt(int index) {
        playlist.removeItemAt(index);
        this.fireTableRowsDeleted(index, index);
    }

    public void removeAll(Vector<PlaylistItem> v) {
        playlist.removeAll(v);
        this.fireTableDataChanged();
    }

    public void clear() {
        playlist.clear();
        this.fireTableDataChanged();
    }

    public void randomizePlaylist() {
        playlist.randomize();
        this.fireTableDataChanged();
    }

    public PlaylistItem getPlaylistItem(int rowIndex) {
        return playlistItems.get(rowIndex);
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columnNames[columnIndex];
    }

    @Override
    public int getRowCount() {
        return playlistItems.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMN_COUNT;
    }

    @Override
    public Class getColumnClass(int columnIndex) {
        if (columnIndex == 0) {
            return Integer.class;
        } else {
            return String.class;
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (!playlistItems.isEmpty()) {
            PlaylistItem item = playlistItems.get(rowIndex);
            switch (columnIndex) {
                case TRACK_COLUMN:
                    try {
                        return item.getTagInfo().getTrack() == null ? 0 : item.getTagInfo().getTrack().matches("") ? 0 : Integer.parseInt(item.getTagInfo().getTrack());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                case TITLE_COLUMN:
                    return item.getTagInfo().getTitle() == null ? item.getName() : item.getTagInfo().getTitle().matches("") ? item.getName() : item.getTagInfo().getTitle();
                case ARTIST_COLUMN:
                    return item.getTagInfo().getArtist() == null ? UNKNOWN : item.getTagInfo().getArtist().matches("") ? UNKNOWN : item.getTagInfo().getArtist();
                case ALBUM_COLUMN:
                    return item.getTagInfo().getAlbum() == null ? UNKNOWN : item.getTagInfo().getAlbum().matches("") ? UNKNOWN : item.getTagInfo().getAlbum();
                case TIME_COLUMN:
                    return item.getFormattedLength(item.getDuration());
//                case GENRE_COLUMN:
//                    return " " + item.getTagInfo().getGenre();
            }
        }
        return null;
    }

    public void moveRow(int start, int end, int to) {
        int shift = to - start;
        int first, last;
        if (shift < 0) {
            first = to;
            last = end;
        } else {
            first = start;
            last = to + end - start;
        }
        Collections.rotate(playlistItems.subList(first, last + 1), shift);
        fireTableRowsUpdated(first, last);
    }
}
