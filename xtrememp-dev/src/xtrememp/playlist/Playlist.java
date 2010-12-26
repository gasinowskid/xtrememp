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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.playlist.filter.Predicate;
import xtrememp.playlist.filter.TruePredicate;

/**
 * Playlist implementation.
 *
 * @author Besmir Beqiri
 */
public class Playlist {

    private static Logger logger = LoggerFactory.getLogger(Playlist.class);

    public enum PlayMode {

        REPEAT_NONE,
        REPEAT_SINGLE,
        REPEAT_ALL,
        SHUFFLE
    }
    protected final List<PlaylistItem> cachedPlaylist;
    protected final List<PlaylistItem> filteredPlaylist;
    protected final List<PlaylistItem> shuffledList;
    protected final List<PlaylistListener> listeners;
    protected final Random rnd;
    protected Predicate<PlaylistItem> filterPredicate;
    protected PlayMode playMode = PlayMode.REPEAT_ALL;
    protected PlaylistItem cursor;
    protected int shuffledIndex = -1;
    protected boolean isModified = false;

    /**
     * Default constructor.
     */
    public Playlist() {
        cachedPlaylist = new ArrayList<PlaylistItem>();
        filteredPlaylist = new ArrayList<PlaylistItem>();
        shuffledList = new ArrayList<PlaylistItem>();
        listeners = new ArrayList<PlaylistListener>(1);
        rnd = new Random();
        filterPredicate = TruePredicate.getInstance();
    }

    /**
     * Return the current play mode.
     * 
     * @return A {@link PlayMode} object.
     */
    public PlayMode getPlayMode() {
        return playMode;
    }

    /**
     * Set the play mode for the playlist. It changes the behaviour of the
     * playlist on determining the next or previous item to be played or not.
     *
     * @param playMode The {@link PlayMode} to set.
     */
    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
        if (playMode.equals(PlayMode.SHUFFLE)) {
            int size = size();
            switch (size) {
                case 1:
                    shuffledList.add(filteredPlaylist.get(0));
                    break;
                default:
                    for (int i = 0; i < size; i++) {
                        shuffledList.add(rnd.nextInt(shuffledList.size() + 1),
                                filteredPlaylist.get(i));
                    }
                    int cursorShuffledIndex = shuffledList.indexOf(cursor);
                    if (cursorShuffledIndex != -1) {
                        Collections.swap(shuffledList, 0, cursorShuffledIndex);
                    }
                    break;
            }
            shuffledIndex = 0;
        } else {
            shuffledList.clear();
            shuffledIndex = -1;
        }
        firePlayModeChangedEvent();
    }

    /**
     * Appends a playlist item at the end of the playlist.
     *
     * @param item A playlist item.
     * @return <code>true</code> if item was successfully added, else <code>false</code>.
     */
    public boolean addItem(PlaylistItem item) {
        boolean added = cachedPlaylist.add(item);
        if (filterPredicate.evaluate(item)) {
            filteredPlaylist.add(item);
        }
        setModified(added);
        if (playMode.equals(PlayMode.SHUFFLE)) {
            addToShuffledList(item);
        }
        fireItemAddedEvent(item);
        return added;
    }

    /**
     * Adds a playlist item at a given position in the playlist.
     *
     * @param pos The position of the item.
     * @param item A playlist item.
     */
    public void addItemAt(int pos, PlaylistItem item) {
        cachedPlaylist.add(pos, item);
        if (filterPredicate.evaluate(item)) {
            filteredPlaylist.add(pos, item);
        }
        setModified((item == null) ? false : true);
        if (playMode.equals(PlayMode.SHUFFLE)) {
            addToShuffledList(item);
        }
        fireItemAddedEvent(item);
    }

    /**
     * Adds a collection of items to the playlist.
     *
     * @param c A collection of items.
     * @return <code>true</code> if the collection was successfully added,
     *         else <code>false</code>.
     */
    public boolean addAll(Collection<? extends PlaylistItem> c) {
        boolean added = cachedPlaylist.addAll(c);
        for (PlaylistItem item : c) {
            if (filterPredicate.evaluate(item)) {
                filteredPlaylist.add(item);
            }
        }
        setModified(added);
        if (playMode.equals(PlayMode.SHUFFLE)) {
            for (PlaylistItem item : c) {
                addToShuffledList(item);
            }
        }
        for (PlaylistItem item : c) {
            fireItemAddedEvent(item);
        }
        return added;
    }

    /**
     * Removes the specified item from the playlist.
     *
     * @param item A playlist item.
     * @return <code>true</code> if item was successfully removed, else <code>false</code>.
     */
    public boolean removeItem(PlaylistItem item) {
        boolean removed = cachedPlaylist.remove(item);
        filteredPlaylist.remove(item);
        setModified(removed);
        if (playMode.equals(PlayMode.SHUFFLE)) {
            removeFromShuffledList(item);
        }
        fireItemRemovedEvent(item);
        return removed;
    }

    /**
     * Removes a playlist item at a given position from the playlist.
     *
     * @param pos The position of the item.
     * @return The playlist item that was removed.
     */
    public PlaylistItem removeItemAt(int pos) {
        PlaylistItem item = cachedPlaylist.remove(pos);
        filteredPlaylist.remove(item);
        setModified((item == null) ? false : true);
        if (playMode.equals(PlayMode.SHUFFLE)) {
            removeFromShuffledList(item);
        }
        fireItemRemovedEvent(item);
        return item;
    }

    /**
     * Removes a collection of items from the playlist.
     *
     * @param c A collection of items.
     * @return <code>true</code> if this playlist changed as a result of the call.
     */
    public boolean removeAll(Collection<? extends PlaylistItem> c) {
        boolean removed = cachedPlaylist.removeAll(c);
        filteredPlaylist.removeAll(c);
        setModified(removed);
        if (playMode.equals(PlayMode.SHUFFLE)) {
            for (PlaylistItem item : c) {
                removeFromShuffledList(item);
            }
        }
        for (PlaylistItem item : c) {
            fireItemRemovedEvent(item);
        }
        return removed;
    }

    /**
     * Removes all items from the playlist.
     */
    public void clear() {
        Iterator<PlaylistItem> iterator = cachedPlaylist.iterator();
        while (iterator.hasNext()) {
            PlaylistItem item = iterator.next();
            iterator.remove();
            fireItemRemovedEvent(item);
        }
        filteredPlaylist.clear();
        shuffledList.clear();
        shuffledIndex = 0;
        begin();
    }

    /**
     * Sorts the entire playlist based on the given comparator.
     * 
     * @param comparator A {@link Comparator} object.
     */
    public void sort(Comparator<PlaylistItem> comparator) {
        Collections.sort(cachedPlaylist, comparator);
        Collections.sort(filteredPlaylist, comparator);
        setModified(true);
    }

    /**
     * Filters the entire playlist based on the given predicate.
     *
     * @param filterPredicate A {@link Predicate} object.
     */
    public void filter(Predicate<PlaylistItem> filterPredicate) {
        this.filterPredicate = filterPredicate;
        filteredPlaylist.clear();
        if (playMode.equals(PlayMode.SHUFFLE)) {
            shuffledList.clear();
            shuffledIndex = 0;
        }
        for (PlaylistItem pli : cachedPlaylist) {
            if (filterPredicate.evaluate(pli)) {
                filteredPlaylist.add(pli);
                if (playMode.equals(PlayMode.SHUFFLE)) {
                    addToShuffledList(pli);
                }
            }
        }
        setModified(true);
    }

    /**
     * Moves a playlist item to a new position.
     *
     * @param fromPos The current position.
     * @param toPos The new position.
     */
    public void moveItem(int fromPos, int toPos) {
        int newIndex = cachedPlaylist.indexOf(filteredPlaylist.get(toPos));
        PlaylistItem pli = filteredPlaylist.remove(fromPos);
        if (pli != null) {
            filteredPlaylist.add(toPos, pli);
            if (cachedPlaylist.remove(pli)) {
                cachedPlaylist.add(newIndex, pli);
            }
        }
        setModified(true);
    }

    /**
     * Shuffles items in the playlist randomly.
     */
    public void randomize() {
        Collections.shuffle(cachedPlaylist);
        Collections.shuffle(filteredPlaylist);
        setModified(true);
    }

    /**
     * Reverses the order of the items in the playlist.
     */
    public void reverse() {
        Collections.reverse(cachedPlaylist);
        Collections.reverse(filteredPlaylist);
        setModified(true);
    }

    /**
     * Moves the cursor at the begining of the Playlist.
     */
    public void begin() {
        cursor = null;
        if (!filteredPlaylist.isEmpty()) {
            if (playMode.equals(PlayMode.SHUFFLE)) {
                cursor = getShuffledCursor(true);
            } else {
                cursor = filteredPlaylist.get(0);
            }
        }
        setModified(true);
    }

    /**
     * Returns the playlist item at a given position from the playlist.
     *
     * @param pos The position of the item.
     * @return A playlist item.
     */
    public PlaylistItem getItemAt(int pos) {
        return filteredPlaylist.get(pos);
    }

    /**
     * Returns a list of all playlist items the playlist contains.
     *
     * @return A list of playlist items.
     */
    public List<PlaylistItem> listAllItems() {
        return cachedPlaylist;
    }

    /**
     * Returns a filtered list of all playlist items based on the applied filter.
     *
     * @return A list of playlist items.
     */
    public List<PlaylistItem> listItems() {
        return filteredPlaylist;
    }

    /**
     * Returns the number of items (size) in the playlist.
     *
     * @return An integer value.
     */
    public int size() {
        return filteredPlaylist.size();
    }

    /**
     * Computes cursor position (next).
     */
    public void nextCursor() {
        if (playMode.equals(PlayMode.SHUFFLE)) {
            cursor = getShuffledCursor(true);
        } else {
            int cursorPos = getCursorPosition();
            cursorPos++;
            if (cursorPos > size() - 1) {
                cursorPos = 0;
            }
            cursor = getItemAt(cursorPos);
        }
    }

    /**
     * Computes cursor position (previous).
     */
    public void previousCursor() {
        if (playMode.equals(PlayMode.SHUFFLE)) {
            cursor = getShuffledCursor(false);
        } else {
            int cursorPos = getCursorPosition();
            cursorPos--;
            if (cursorPos < 0) {
                cursorPos = size() - 1;
            }
            cursor = getItemAt(cursorPos);
        }
    }

    /**
     * Set the modification flag for the playlist.
     *
     * @param flag if <code>true</code>, sets the modification flag as modified,
     *            if <code>false</code> as not modified.
     */
    public void setModified(boolean flag) {
        isModified = flag;
    }

    /**
     * Returns the playlist item matching to the cursor.
     * 
     * @return A playlist item.
     */
    public PlaylistItem getCursor() {
        return cursor;
    }

    /**
     * Replaces the cursor with a new playlist item.
     *
     * @param newCursor A playlist item.
     */
    public void setCursor(PlaylistItem newCursor) {
        cursor = newCursor;
        if (playMode.equals(PlayMode.SHUFFLE) && (cursor != null)) {
            if (shuffledList.size() > 1
                    && shuffledList.get(shuffledIndex) != cursor) {
                shuffledIndex = (++shuffledIndex > shuffledList.size() - 1) ? 0 : shuffledIndex;
                Collections.swap(shuffledList, shuffledIndex,
                        shuffledList.indexOf(cursor));
            }
        }
    }

    /**
     * Returns the position matching to the cursor.
     *
     * @return An integer value.
     */
    public int getCursorPosition() {
        return indexOf(cursor);
    }

    /**
     * Replaces the cursor with a new playlist item specified by its position.
     *
     * @param pos An integer value.
     */
    public void setCursorPosition(int pos) {
        cursor = getItemAt(pos);
    }

    /**
     * Adds the specified playlist item to the shuffled list in a random position.
     *
     * @param item A playlist item.
     */
    private void addToShuffledList(PlaylistItem item) {
        int randomIndex = shuffledIndex + rnd.nextInt(shuffledList.size() - shuffledIndex + 1);
        shuffledList.add(randomIndex, item);
    }

    /**
     * Removes the first occurrence of the specified playlist item from the
     * shuffled list, if it is present.
     *
     * @param item A playlist item.
     */
    private void removeFromShuffledList(PlaylistItem item) {
        shuffledList.remove(item);
        if (shuffledList.isEmpty()) {
            shuffledIndex = 0;
        } else {
            int maxIndex = shuffledList.size() - 1;
            shuffledIndex = (shuffledIndex > maxIndex) ? maxIndex : shuffledIndex;
        }
    }

    /**
     * Returns a randomly generated cursor position. This method makes sure that
     * all playlist items of this playlist will be selected once before
     * selecting the same item twice.
     * 
     * @param next If <code>true</code> retrive the next cursor position value,
     *             else the previous one.
     * @return An integer value between [0, playlist size - 1], otherwise -1 if
     *         this playlist is empty.
     */
    private PlaylistItem getShuffledCursor(boolean next) {
        if (!shuffledList.isEmpty()) {
            shuffledIndex = (next) ? ((++shuffledIndex > shuffledList.size() - 1)
                    ? 0 : shuffledIndex) : ((--shuffledIndex < 0)
                    ? shuffledList.size() - 1 : shuffledIndex);
            return shuffledList.get(shuffledIndex);
        } else {
            return null;
        }
    }

    /**
     * Returns the index of the specified playlist item.
     *
     * @param item A playlist item.
     * @return An integer value.
     */
    public int indexOf(PlaylistItem item) {
        return filteredPlaylist.indexOf(item);
    }

    /**
     * Checks the modification flag.
     *
     * @return <code>true</code> if the playlist is modified, else <code>false</code>.
     */
    public boolean isModified() {
        return isModified;
    }

    /**
     * Checks if the playlist is empty.
     *
     * @return <code>true</code> if the playlist is empty, else <code>false</code>.
     */
    public boolean isEmpty() {
        return filteredPlaylist.isEmpty();
    }

    /**
     * Adds the specified playlist listener to receive playlist events from this
     * playlist. If the listener is <code>null</code>, no exception is thrown
     * and no action is performed.
     *
     * @param listener A playlist listener.
     */
    public void addPlaylistListener(PlaylistListener listener) {
        if (listener == null) {
            return;
        }
        listeners.add(listener);
        logger.info("Playlist listener added");
    }

    /**
     * Removes the specified playlist listener so that it no longer receives
     * playlist events from this playlist. This method performs no function,
     * nor does it throw an exception, if the listener specified by the argument
     * was not previously added to this playlist.
     * If the listener is <code>null</code>, no exception is thrown and no
     * action is performed.
     * 
     * @param listener A playlist listener.
     */
    public void removePlaylistListener(PlaylistListener listener) {
        if (listener == null) {
            return;
        }
        listeners.remove(listener);
        logger.info("Playlist listener removed");
    }

    /**
     * Notifies all listeners that a playlist item has been added.
     *
     * @param item The playlist item added.
     */
    private void fireItemAddedEvent(PlaylistItem item) {
        PlaylistEvent event = new PlaylistEvent(this, item);
        for (PlaylistListener listener : listeners) {
            listener.playlistItemAdded(event);
        }
        logger.info("Playlist item added: {}", item);
    }

    /**
     * Notifies all listeners that a playlist item has been removed.
     *
     * @param item The playlist item removed.
     */
    private void fireItemRemovedEvent(PlaylistItem item) {
        PlaylistEvent event = new PlaylistEvent(this, item);
        for (PlaylistListener listener : listeners) {
            listener.playlistItemRemoved(event);
        }
        logger.info("Playlist item removed: {}", item);
    }

    /**
     * Notifies all listeners that the play mode has changed.
     */
    private void firePlayModeChangedEvent() {
        PlaylistEvent event = new PlaylistEvent(this);
        for (PlaylistListener listener : listeners) {
            listener.playModeChanged(event);
        }
        logger.info("Play mode changed: {}", playMode);
    }
}
