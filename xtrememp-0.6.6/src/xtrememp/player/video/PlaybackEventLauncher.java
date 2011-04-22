/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xtrememp.player.video;

/**
 *
 * @author Deano
 */
import java.util.Map;

/**
 *
 * @author Besmir Beqiri
 */
public class PlaybackEventLauncher extends Thread {

    private videoplayer source;
    private Playback state;
    private long position;
    private Map properties;
    private PlaybackListener listener;

    public PlaybackEventLauncher(videoplayer source, Playback state, long position, Map properties, PlaybackListener listener) {
        super();
        this.source = source;
        this.state = state;
        this.position = position;
        this.properties = properties;
        this.listener = listener;
    }

    @Override
    public void run() {
        if (listener != null) {
            switch (state) {
                case BUFFERING:
                    listener.playbackBuffering(new PlaybackEvent(source, state, position, properties));
                    break;
                case OPENED:
                    listener.playbackOpened(new PlaybackEvent(source, state, position, properties));
                    break;
                case EOM:
                    listener.playbackEndOfMedia(new PlaybackEvent(source, state, position, properties));
                    break;
                case PLAYING:
                    listener.playbackPlaying(new PlaybackEvent(source, state, position, properties));
                    break;
                case PAUSED:
                    listener.playbackPaused(new PlaybackEvent(source, state, position, properties));
                    break;
                case STOPPED:
                    listener.playbackStopped(new PlaybackEvent(source, state, position, properties));
                    break;
            }
        }
    }
}
