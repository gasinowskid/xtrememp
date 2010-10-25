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
package xtrememp.ui.slider;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import javax.swing.JSlider;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.basic.BasicSliderUI;
import javax.swing.plaf.basic.BasicSliderUI.TrackListener;
import xtrememp.ControlListener;

/**
 *
 * @author Besmir Beqiri
 */
public class SeekSlider extends JSlider {

    private final ControlListener listener;
    private volatile int oldValue = 0;
    private volatile boolean seekPressed = false;

    public SeekSlider(ControlListener controlListener) {
        super(0, 0, 0);
        this.listener = controlListener;

        MouseListener[] ssMouseListeners = getMouseListeners();
        for (MouseListener ml : ssMouseListeners) {
            if (ml instanceof TrackListener) {
                removeMouseListener(ml);
            }
        }
        MouseMotionListener[] ssMouseMotionListeners = getMouseMotionListeners();
        for (MouseMotionListener mml : ssMouseMotionListeners) {
            if (mml instanceof TrackListener) {
                removeMouseMotionListener(mml);
            }
        }

        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                seekPressed = true;
                updateValue(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                listener.acSeek();
                seekPressed = false;
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {
                updateValue(e);
            }
        });
    }

    protected void updateValue(MouseEvent e) {
        SliderUI sliderUI = getUI();
        if (isEnabled() && sliderUI instanceof BasicSliderUI) {
            BasicSliderUI basicSliderUI = (BasicSliderUI) sliderUI;
            if (getOrientation() == JSlider.HORIZONTAL) {
                setValue(basicSliderUI.valueForXPosition(e.getX()));
            } else {
                setValue(basicSliderUI.valueForYPosition(e.getY()));
            }
            oldValue = getValue();
            listener.acUpdateTime(oldValue);
        }
    }

    public void reset() {
        oldValue = 0;
        setValue(0);
        setMinimum(0);
        setMaximum(0);
    }

    public boolean isPressed() {
        return seekPressed;
    }

    public int getOldValue() {
        return oldValue;
    }
}
