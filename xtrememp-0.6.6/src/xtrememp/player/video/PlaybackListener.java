/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xtrememp.player.video;

/**
 *
 * @author Deano
 */
public interface PlaybackListener {
    public void playbackBuffering(PlaybackEvent pe);
    public void playbackOpened(PlaybackEvent pe);
    public void playbackEndOfMedia(PlaybackEvent pe);
    public void playbackPlaying(PlaybackEvent pe);
    public void playbackProgress(PlaybackEvent pe);
    public void playbackPaused(PlaybackEvent pe);
    public void playbackStopped(PlaybackEvent pe);
}

