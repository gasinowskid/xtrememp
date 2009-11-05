/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2009 Besmir Beqiri
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
package xtrememp.ui.combobox;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultComboBoxRenderer;
import org.pushingpixels.substance.api.skin.SkinInfo;
import xtrememp.Settings;

/**
 *
 * @author Besmir Beqiri
 */
public class SkinComboSelector extends JComboBox {

    public SkinComboSelector() {
        super();
        // populate the combobox
        Map<String, SkinInfo> skinMap = SubstanceLookAndFeel.getAllSkins();
        SubstanceSkin currentSkin = SubstanceLookAndFeel.getCurrentSkin();
        for (final Map.Entry<String, SkinInfo> entry : skinMap.entrySet()) {
            SkinInfo si = entry.getValue();
            this.addItem(si);
            // set the current skin as the selected item
            if (si.getDisplayName().compareTo(currentSkin.getDisplayName()) == 0) {
                this.setSelectedItem(si);
            }
        }
        // set custom renderer to show the skin display name
        this.setRenderer(new SubstanceDefaultComboBoxRenderer(this) {

            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                return super.getListCellRendererComponent(list,
                        ((SkinInfo) value).getDisplayName(), index, isSelected,
                        cellHasFocus);
            }
        });
        // add an action listener to change skin based on user selection
        this.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        SkinInfo skinInfo = (SkinInfo) SkinComboSelector.this.getSelectedItem();
                        SubstanceLookAndFeel.setSkin(skinInfo.getClassName());
                        Settings.setSkin(skinInfo.getClassName());
                    }
                });
            }
        });
    }
}
