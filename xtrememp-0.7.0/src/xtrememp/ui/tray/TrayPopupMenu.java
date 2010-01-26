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

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import xtrememp.ControlListener;
import xtrememp.util.Utilities;

/**
 * @author rom1dep
 */
public class TrayPopupMenu extends JPopupMenu implements ActionListener, MouseListener {

    public static final long serialVersionUID = 0l;//@Bad
    //
    private ControlListener ctrlListener;
    private TrayEventListener trayEvtListener;
    //
    private static JLabel labInfo;
    private static JCheckBoxMenuItem itemPlayPause;
    private static JMenuItem itemPrev;
    private static JMenuItem itemNext;
    private static JMenuItem itemStop;
    private static JMenuItem itemMinimize;
    private static JMenuItem itemQuit;
    //
    private static boolean isFrameVisible = true;
    private static final String TXT_MINIMIZE = "Minimize", TXT_MAXIMIZE = "Maximize";

    public TrayPopupMenu(ControlListener ctrlListener, TrayEventListener trayListener) {
        this.ctrlListener = ctrlListener;
        this.trayEvtListener = trayListener;

        super.addMouseListener(this);

        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                createTrayPopup();
            }
        });
    }

    private void createTrayPopup() {//@TODO: add i18n + add icons
        labInfo = new JLabel("XtremeMP 0.7.0");
        labInfo.setFont(labInfo.getFont().deriveFont(Font.BOLD));
        labInfo.setIcon(Utilities.MENU_ICON);//TODO: add the right icon...
        labInfo.setFocusable(false);

        itemPrev = new JMenuItem("Previous Track");
        itemPrev.addActionListener(this);

        itemPlayPause = new JCheckBoxMenuItem("Play/Pause");
        itemPlayPause.addActionListener(this);

        itemStop = new JMenuItem("Stop");
        itemStop.addActionListener(this);

        itemNext = new JMenuItem("Next Track");
        itemNext.addActionListener(this);

        itemMinimize = new JMenuItem(TXT_MINIMIZE);
        itemMinimize.addActionListener(this);

        itemQuit = new JMenuItem("Quit");
        itemQuit.addActionListener(this);

        super.add(labInfo);
        super.add(itemPrev);
        super.add(itemPlayPause);
        super.add(itemStop);
        super.add(itemNext);
        //There is a bug here : the rootcomponent losts focus when hovering this JSeparator → TrayPopupMenu.setVisible(false) is called
        super.addSeparator();
        super.add(itemMinimize);
        super.add(itemQuit);

        this.pack();
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource().equals(itemPrev)) {
            ctrlListener.acPrevious();
        } else if (ae.getSource().equals(itemPlayPause)) {
            ctrlListener.acPlayPause();
        } else if (ae.getSource().equals(itemStop)) {
            ctrlListener.acStop();
        } else if (ae.getSource().equals(itemNext)) {
            ctrlListener.acNext();
        } else if (ae.getSource().equals(itemMinimize)) {
            if (isFrameVisible = !isFrameVisible) {
                trayEvtListener.trayMaximize();
            } else {
                trayEvtListener.trayMinimize();
            }
            fireFrameVisibleStateChanged();
        }
        if (ae.getSource().equals(itemQuit)) {
            trayEvtListener.trayQuit();
        }
    }

    public void fireFrameVisibleStateChanged(boolean newFrameVisibleState) {
        isFrameVisible = newFrameVisibleState;
        fireFrameVisibleStateChanged();
    }

    public void fireFrameVisibleStateChanged() {
        //maybe overengineered here, because the component is likely undisplayed when this is called,
        //but let's stay reasonable...
        EventQueue.invokeLater(new Runnable() {

            public void run() {
                if (isFrameVisible) {
                    itemMinimize.setText(TXT_MINIMIZE);
                } else {
                    itemMinimize.setText(TXT_MAXIMIZE);
                }
            }
        });
    }

    public void mouseClicked(MouseEvent me) {
    }

    public void mousePressed(MouseEvent me) {
    }

    public void mouseReleased(MouseEvent me) {
    }
//TODO: code a timer-based on mechanism to escape the popup menu

    public void mouseEntered(MouseEvent me) {
    }

    public void mouseExited(MouseEvent me) {
        setVisible(false);
    }
}
