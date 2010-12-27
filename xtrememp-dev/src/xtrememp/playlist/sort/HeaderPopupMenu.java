/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package xtrememp.playlist.sort;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JPopupMenu;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author rom1dep
 */
public class HeaderPopupMenu extends JPopupMenu implements ActionListener {

    private JCheckBoxMenuItem[] headerValues;

    public HeaderPopupMenu() {
        super();
        headerValues = new JCheckBoxMenuItem[HeaderIdentifier.values().length];

        for (int i = 0; i < HeaderIdentifier.values().length; i++) {
            headerValues[i] = new JCheckBoxMenuItem(tr(HeaderIdentifier.getLangageBundleRef(HeaderIdentifier.values()[i])));
            headerValues[i].addActionListener(this);
            super.add(headerValues[i]);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//@Todo: handle this
    }
}
