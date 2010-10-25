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
package xtrememp.playlist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Playlist implementation.
 * This class implements BasePlaylist interface.
 *
 * @author Besmir Beqiri
 */
public class Playlist {

//    private static Log log = LogFactory.getLog(Playlist.class);
    protected List<PlaylistItem> playlist = null;
    protected int cursorPos = -1;
    protected boolean isModified = false;

    /**
     * Default constructor
     */
    public Playlist() {
        playlist = Collections.synchronizedList(new ArrayList<PlaylistItem>());
    }

    /**
     * Adds item at a given position in the playlist.
     * @param pli a playlist item.
     * @param pos the position of the item.
     */
    public void addItemAt(int pos, PlaylistItem pli) {
        playlist.add(pos, pli);
        setModified(true);
    }

    /**
     * Removes the specified item from the playlist.
     * @param pli a playlist item.
     * @return <code>true</code> if item was successfully removed, else <code>false</code>.
     */
    public boolean removeItem(PlaylistItem pli) {
        boolean removed = playlist.remove(pli);
        setModified(removed);
        return removed;
    }

    /**
     * Removes item at a given position from the playlist.
     * @param pos the position of the item.
     * @return item that was removed.
     */
    public PlaylistItem removeItemAt(int pos) {
        PlaylistItem pli = playlist.remove(pos);
        setModified((pli == null) ? false : true);
        return pli;
    }

    /**
     * Removes a collection of items from the playlist.
     * @param v a collection of items.
     * @return <code>true</code> if this playlist changed as a result of the call
     */
    public boolean removeAll(List<PlaylistItem> v) {
        boolean removed = playlist.removeAll(v);
        setModified(removed);
        return removed;
    }

    /**
     * Removes all items from the playlist.
     */
    public void clear() {
        playlist.clear();
        cursorPos = -1;
        setModified(true);
    }

    /**
     * Append item at the end of the playlist.
     * @param pli a playlist item.
     * @return <code>true</code> if item was successfully added, else <code>false</code>.
     */
    public boolean addItem(PlaylistItem pli) {
        boolean added = playlist.add(pli);
        setModified(added);
        return added;
    }

    /**
     * Shuffles items in the playlist randomly
     */
    public void randomize() {
        Collections.shuffle(playlist);
        begin();
    }

    /**
     * Reverses the order of the items in the playlist.
     */
    public void reverse() {
        Collections.reverse(playlist);
        begin();
    }

    /**
     * Moves the cursor at the begining of the Playlist.
     */
    public void begin() {
        cursorPos = -1;
        if (size() > 0) {
            cursorPos = 0;
        }
        setModified(true);
    }

    /**
     * Returns item at a given position from the playlist.
     * @param pos the position of the item.
     * @return a playlist item.
     */
    public PlaylistItem getItemAt(int pos) {
        return playlist.get(pos);
    }

    /**
     * Returns a collection of playlist items.
     * @return a collection of playlist items.
     */
    public List<PlaylistItem> listItems() {
        return playlist;
    }

    /**
     * Returns the number of items in the playlist.
     * @return the value of the playlist size.
     */
    public int size() {
        return playlist.size();
    }

    /**
     * Computes cursor position (next).
     */
    public void nextCursor() {
        cursorPos++;
        if (cursorPos > playlist.size() - 1) {
            cursorPos = 0;
        }
    }

    /**
     * Computes cursor position (previous).
     */
    public void previousCursor() {
        cursorPos--;
        if (cursorPos < 0) {
            cursorPos = playlist.size() - 1;
        }
    }

    /**
     * Set the modification flag for the playlist
     * @param set if <code>true</code>, sets the modification flag as modified,
     *            if <code>false</code> as not modified.
     */
    public void setModified(boolean set) {
        isModified = set;
    }

    /**
     * Returns item matching to the cursor.
     * @return a playlist item.
     */
    public PlaylistItem getCursor() {
        if ((cursorPos < 0) || (cursorPos >= playlist.size())) {
            return null;
        }
        return getItemAt(cursorPos);
    }

    /**
     * Set the cursor at the specified index.
     * @param index the index value.
     */
    public void setCursor(int index) {
        cursorPos = index;
    }

    /**
     * Returns item position matching to the cursor.
     * @return the cursor position.
     */
    public int getCursorPosition() {
        return cursorPos;
    }

    /**
     * Returns index of playlist item.
     * @param pli a playlist item.
     * @return the index value.
     */
    public int indexOf(PlaylistItem pli) {
        return playlist.indexOf(pli);
    }

    /**
     * Checks the modification flag
     * @return <code>true</code> if the playlist is modified, else <code>false</code>.
     */
    public boolean isModified() {
        return isModified;
    }

    /**
     * Checks if playlist is empty
     * @return <code>true</code> if the playlist is empty, else <code>false</code>.
     */
    public boolean isEmpty() {
        return playlist.isEmpty();
    }
}
