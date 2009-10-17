package xtrememp.playlist;

/**
 * @author rom1dep
 */
public interface PlaylistChangeListener {

    /*
     * Called when the model (Playlist) and the view (as shown by the user in the JTable component)
     * has to be synchronized (ie. when the users adds items into the playlist or when shuffle...)
     */
    public void synchronize();
}
