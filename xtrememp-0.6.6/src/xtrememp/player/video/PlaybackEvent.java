/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xtrememp.player.video;

import java.util.Map;

/**
 * @author Besmir Beqiri
 */
public class PlaybackEvent {

    private videoplayer source;
    private Playback state;
    private long position;
    private Map properties;

    public PlaybackEvent(videoplayer source, Playback state, long position, Map properties) {
        this.source = source;
        this.state = state;
        this.position = position;
        this.properties = properties;
    }

    public videoplayer getSource() {
        return source;
    }

    public void setSource(videoplayer source) {
        this.source = source;
    }

    public Playback getState() {
        return state;
    }

    public void setState(Playback state) {
        this.state = state;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public Map getProperties() {
        return properties;
    }

    public void setProperties(Map properties) {
        this.properties = properties;
    }
}