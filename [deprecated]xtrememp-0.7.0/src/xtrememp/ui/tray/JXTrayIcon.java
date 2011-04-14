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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JDialog;
import javax.swing.JPopupMenu;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import xtrememp.util.Utilities;

/**
 * Based on a blog post from Alexander Potochkin at the following url:
 * http://weblogs.java.net/blog/alexfromsun/archive/2008/02/jtrayicon_updat.html
 *
 * @author Alexander Potochkin
 * @author Besmir Beqiri
 */
public class JXTrayIcon extends TrayIcon {

    private JPopupMenu menu;
    private Window window;
    private PopupMenuListener popupListener;

    public JXTrayIcon(Image image) {
        super(image);
        init();
    }

    public JXTrayIcon(Image image, String tooltip) {
        super(image, tooltip);
        init();
    }

    public JXTrayIcon(Image image, String tooltip, PopupMenu popup) {
        super(image, tooltip, popup);
        init();
    }

    public JXTrayIcon(Image image, String tooltip, JPopupMenu popup) {
        super(image, tooltip);
        init();
        setJPopupMenu(popup);
    }

    private final void init() {
        popupListener = new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                if (window != null) {
                    window.dispose();
                    window = null;
                }
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                if (window != null) {
                    window.dispose();
                    window = null;
                }
            }
        };

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

    private final void showJPopupMenu(MouseEvent e) {
        if (e.isPopupTrigger() && menu != null) {
            if (window == null) {
                if (Utilities.isWindowsOS()) {
                    window = new JDialog((Frame) null);
                    ((JDialog) window).setUndecorated(true);
                } else {
                    window = new JWindow((Frame) null);
                }
                window.setAlwaysOnTop(true);
                Dimension size = menu.getPreferredSize();

                Point centerPoint = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
                if (e.getY() > centerPoint.getY()) {
                    window.setLocation(e.getX(), e.getY() - size.height);
                } else {
                    window.setLocation(e.getX(), e.getY());
                }

                window.setVisible(true);
                menu.show(((RootPaneContainer) window).getContentPane(), 0, 0);
                // popup works only for focused windows
                window.toFront();
            }
        }
    }

    public final JPopupMenu getJPopupMenu() {
        return menu;
    }

    public final void setJPopupMenu(JPopupMenu menu) {
        if (this.menu != null) {
            this.menu.removePopupMenuListener(popupListener);
        }
        this.menu = menu;
        menu.addPopupMenuListener(popupListener);
    }
}
