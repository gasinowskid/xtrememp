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
package xtrememp.ui.tray;

import java.awt.Frame;
import java.awt.Image;
import java.awt.TrayIcon;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

/**
 * Based on a blog post from Alexander Potochkin at the following url:
 * http://weblogs.java.net/blog/alexfromsun/archive/2008/02/jtrayicon_updat.html
 *
 * @author Alexander Potochkin
 * @author Besmir Beqiri
 */
public class JXTrayIcon extends TrayIcon {

    private JPopupMenu menu;
    private static JDialog dialog;

    static {
        dialog = new JDialog((Frame) null, "TrayDialog");
        dialog.setUndecorated(true);
        dialog.setAlwaysOnTop(true);
    }
    private static PopupMenuListener popupListener = new PopupMenuListener() {

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            dialog.setVisible(false);
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            dialog.setVisible(false);
        }
    };

    public JXTrayIcon(Image image, String tooltip) {
        super(image, tooltip);
        addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                showJPopupMenu(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                showJPopupMenu(e);
            }
        });
    }

    private void showJPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger() && menu != null) {
            int adjustedY = e.getY() - menu.getPreferredSize().height;
            dialog.setLocation(e.getX(), adjustedY < 0 ? e.getY() : adjustedY);
            dialog.setVisible(true);
            menu.show(dialog.getContentPane(), 0, 0);
            // popup works only for focused windows
            dialog.toFront();
        }
    }

    public JPopupMenu getJPopupMenu() {
        return menu;
    }

    public void setJPopupMenu(JPopupMenu menu) {
        if (this.menu != null) {
            this.menu.removePopupMenuListener(popupListener);
        }
        this.menu = menu;
        menu.addPopupMenuListener(popupListener);
    }
}
