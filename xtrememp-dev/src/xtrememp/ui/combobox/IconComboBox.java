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
package xtrememp.ui.combobox;

import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.renderers.SubstanceDefaultComboBoxRenderer;

/**
 *
 * @author Besmir Beqiri
 */
public abstract class IconComboBox<T> extends JComboBox {

    public IconComboBox(T[] items) {
        super(items);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void updateUI() {
        if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
            setRenderer(new SubstanceDefaultComboBoxRenderer(this) {

                @Override
                public Component getListCellRendererComponent(JList list,
                        Object value, int index, boolean isSelected,
                        boolean cellHasFocus) {
                    Component result = super.getListCellRendererComponent(list,
                            getCaption((T) value), index, isSelected,
                            cellHasFocus);
                    if (result instanceof JLabel) {
                        ((JLabel) result).setIcon(getItemIcon((T) value));
                    }
                    return result;
                }
            });
        } else {
            setRenderer(new DefaultListCellRenderer() {

                @Override
                public Component getListCellRendererComponent(JList list,
                        Object value, int index, boolean isSelected,
                        boolean cellHasFocus) {
                    Component result = super.getListCellRendererComponent(list,
                            getCaption((T) value), index, isSelected,
                            cellHasFocus);
                    if (result instanceof JLabel) {
                        ((JLabel) result).setIcon(getItemIcon((T) value));
                    }
                    return result;
                }
            });
        }
        super.updateUI();
    }

    public abstract String getCaption(T item);

    public Icon getItemIcon(T item) {
        return null;
    }
}
