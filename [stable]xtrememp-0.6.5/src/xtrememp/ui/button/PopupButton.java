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
package xtrememp.ui.button;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 *
 * @author Besmir Beqiri
 */
public class PopupButton extends JToggleButton {

    private JPopupMenu popupMenu;
    private boolean shouldHandlePopupWillBecomeInvisible = true;

    public PopupButton() {
        setFocusable(false);
        addMouseListener(createButtonMouseListener());

        popupMenu = new JPopupMenu();
        popupMenu.addPopupMenuListener(createPopupMenuListener());

        // install a special client property on the button to prevent it from
        // closing of the popup when is pressed.
        JComboBox box = new JComboBox();
        Object preventHide = box.getClientProperty("doNotCancelPopup");
        putClientProperty("doNotCancelPopup", preventHide);
    }
    
    public PopupButton(Icon icon) {
        this();
        setIcon(icon);
    }

    private MouseListener createButtonMouseListener() {
        return new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (popupMenu.isShowing()) {
                    hidePopupMenu();
                } else {
                    showPopupMenu();
                }
            }
        };
    }

    private PopupMenuListener createPopupMenuListener() {
        return new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (shouldHandlePopupWillBecomeInvisible) {
                    setSelected(false);
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                // the popup menu has been canceled externally (either by
                // pressing escape or clicking off of the popup menu). update
                // the button's state to reflect the menu dismissal.
                setSelected(false);
            }
        };
    }

    private void hidePopupMenu() {
        shouldHandlePopupWillBecomeInvisible = false;
        popupMenu.setVisible(false);
        shouldHandlePopupWillBecomeInvisible = true;

    }

    private void showPopupMenu() {
        popupMenu.show(this, 0, this.getHeight());
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }
}
