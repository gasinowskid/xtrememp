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
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SwingUtilities;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.api.renderers.SubstanceDefaultComboBoxRenderer;
import org.jvnet.substance.skin.DustCoffeeSkin;
import org.jvnet.substance.skin.DustSkin;
import org.jvnet.substance.skin.RavenGraphiteSkin;
import org.jvnet.substance.skin.SkinInfo;
import org.jvnet.substance.skin.TwilightSkin;
import xtrememp.Settings;

/**
 *
 * @author Besmir Beqiri
 */
public class SkinComboSelector extends JComboBox {

    public SkinComboSelector() {
        super();
        // populate the combobox
        List<SkinInfo> result = new ArrayList<SkinInfo>();
//        result.add(create(DarkSapphireSkin.NAME, DarkSapphireSkin.class, true));
        result.add(create(DustSkin.NAME, DustSkin.class, false));
        result.add(create(DustCoffeeSkin.NAME, DustCoffeeSkin.class, false));
        result.add(create(RavenGraphiteSkin.NAME, RavenGraphiteSkin.class, false));
        result.add(create(TwilightSkin.NAME, TwilightSkin.class, false));
        for (SkinInfo si : result) {
            this.addItem(si);
        }
        // set the current skin as the selected item
        SubstanceSkin currentSkin = SubstanceLookAndFeel.getCurrentSkin();
        for (SkinInfo skinInfo : result) {
            if (skinInfo.getDisplayName().compareTo(
                    currentSkin.getDisplayName()) == 0) {
                this.setSelectedItem(skinInfo);
                break;
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

    /**
     * Creates info object on a single skin.
     *
     * @param displayName
     *            Skin display name.
     * @param skinClass
     *            Skin class.
     * @param isDefault
     *            Indication whether the specified skin is default.
     * @return Info object on the specified skin.
     */
    private SkinInfo create(String displayName, Class<?> skinClass,
            boolean isDefault) {
        SkinInfo result = new SkinInfo(displayName, skinClass.getName());
        result.setDefault(isDefault);
        return result;
    }
}
