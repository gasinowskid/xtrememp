package org.rom1dep.util.gui.swing.busylabel.event;

/**
 * @author rom1dep
 */
public interface RDBusyLabelEventListener {

    public void animationStarted(RDBusyLabelEvent event);

    public void animationPaused(RDBusyLabelEvent event);

    public void animationStopped(RDBusyLabelEvent event);
}
