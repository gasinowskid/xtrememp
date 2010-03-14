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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Playlist implementation.
 * This class implements BasePlaylist interface.
 *
 * @author Besmir Beqiri
 */
public class Playlist {

    private static Logger logger = LoggerFactory.getLogger(Playlist.class);

    public enum PlayingMode {

        REPEAT_OFF("Repeat off"),
        REPEAT_ONE("Repeat one"),
        REPEAT_ALL("Repeat all");
        private String pmString;

        PlayingMode(String pmString) {
            this.pmString = pmString;
        }

        @Override
        public String toString() {
            return pmString;
        }
    }
    protected final List<PlaylistItem> playlist;
    protected final List<PlaylistListener> listeners;
    protected PlayingMode playingMode = PlayingMode.REPEAT_OFF;
    protected int cursorPos = -1;
    protected boolean isModified = false;

    /**
     * Default constructor
     */
    public Playlist() {
        playlist = Collections.synchronizedList(new ArrayList<PlaylistItem>(1));
        listeners = new ArrayList<PlaylistListener>(1);
    }

    /**
     * @return the playingMode
     */
    public PlayingMode getPlayingMode() {
        return playingMode;
    }

    /**
     * @param playingMode the playingMode to set
     */
    public void setPlayingMode(PlayingMode playingMode) {
        this.playingMode = playingMode;
        firePlayingModeChangedEvent(getCursor());
    }

    /**
     * Append item at the end of the playlist.
     * @param item a playlist item.
     * @return <code>true</code> if item was successfully added, else <code>false</code>.
     */
    public boolean addItem(PlaylistItem item) {
        boolean added = playlist.add(item);
        setModified(added);
        fireItemAddedEvent(item);
        return added;
    }

    /**
     * Adds item at a given position in the playlist.
     * @param item a playlist item.
     * @param pos the position of the item.
     */
    public void addItemAt(int pos, PlaylistItem item) {
        playlist.add(pos, item);
        setModified((item == null) ? false : true);
        fireItemAddedEvent(item);
    }

    /**
     * Adds a collection of items to the playlist.
     * @param c a collection of items.
     * @return <code>true</code> if the collection was successfully added,
     *         else <code>false</code>.
     */
    public boolean addAll(Collection<? extends PlaylistItem> c) {
        boolean added = playlist.addAll(c);
        setModified(added);
        for (PlaylistItem item : c) {
            fireItemAddedEvent(item);
        }
        return added;
    }

    /**
     * Removes the specified item from the playlist.
     * @param item a playlist item.
     * @return <code>true</code> if item was successfully removed, else <code>false</code>.
     */
    public boolean removeItem(PlaylistItem item) {
        boolean removed = playlist.remove(item);
        setModified(removed);
        fireItemRemovedEvent(item);
        return removed;
    }

    /**
     * Removes item at a given position from the playlist.
     * @param pos the position of the item.
     * @return item that was removed.
     */
    public PlaylistItem removeItemAt(int pos) {
        PlaylistItem item = playlist.remove(pos);
        setModified((item == null) ? false : true);
        fireItemRemovedEvent(item);
        return item;
    }

    /**
     * Removes a collection of items from the playlist.
     * @param c a collection of items.
     * @return <code>true</code> if this playlist changed as a result of the call
     */
    public boolean removeAll(Collection<? extends PlaylistItem> c) {
        boolean removed = playlist.removeAll(c);
        setModified(removed);
        for (PlaylistItem item : c) {
            fireItemRemovedEvent(item);
        }
        return removed;
    }

    /**
     * Removes all items from the playlist.
     */
    public void clear() {
        synchronized (playlist) {
            Iterator<PlaylistItem> iterator = playlist.iterator();
            while (iterator.hasNext()) {
                PlaylistItem item = iterator.next();
                iterator.remove();
                fireItemRemovedEvent(item);
            }
        }
        begin();
    }

    /**
     * Shuffles items in the playlist randomly
     */
    public void randomize() {
        Collections.shuffle(playlist);
        setModified(true);
    }

    /**
     * Reverses the order of the items in the playlist.
     */
    public void reverse() {
        Collections.reverse(playlist);
        setModified(true);
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

    public void addPlaylistListener(PlaylistListener listener) {
        listeners.add(listener);
    }

    public void removePlaylistListener(PlaylistListener listener) {
        listeners.remove(listener);
    }

    private void fireItemAddedEvent(PlaylistItem item) {
        PlaylistEvent event = new PlaylistEvent(this, item);

        for (PlaylistListener listener : listeners) {
            listener.playlistItemAdded(event);
        }
    }

    private void fireItemRemovedEvent(PlaylistItem item) {
        PlaylistEvent event = new PlaylistEvent(this, item);

        for (PlaylistListener listener : listeners) {
            listener.playlistItemRemoved(event);
        }
    }

    private void firePlayingModeChangedEvent(PlaylistItem item) {
        PlaylistEvent event = new PlaylistEvent(this, item);

        for (PlaylistListener listener : listeners) {
            listener.playingModeChanged(event);
        }
    }
}
