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
package xtrememp.ui.button.shaper;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.AbstractButton;
import javax.swing.border.Border;
import org.pushingpixels.substance.api.shaper.StandardButtonShaper;
import org.pushingpixels.substance.api.shaper.SubstanceButtonShaper;
import org.pushingpixels.substance.internal.ui.SubstanceButtonUI;
import org.pushingpixels.substance.internal.utils.border.SubstanceButtonBorder;

/**
 *
 * @author Besmir Beqiri
 */
public abstract class ButtonShaper implements SubstanceButtonShaper {
    
    @Override
    public Dimension getPreferredSize(AbstractButton button, Dimension uiPreferredSize) {
        if (button.getClientProperty(SubstanceButtonUI.BORDER_COMPUTED) == null) {
            boolean isBorderComputing = (button
                    .getClientProperty(SubstanceButtonUI.BORDER_COMPUTING) != null);
            Border border = button.getBorder();
            int uiw = uiPreferredSize.width;
            int uih = uiPreferredSize.height;
            if (border instanceof SubstanceButtonBorder) {
                Insets bi = border.getBorderInsets(button);
                if (!isBorderComputing)
                    button.setBorder(null);
                uiPreferredSize.setSize(uiw - bi.left - bi.right, uih - bi.top
                        - bi.bottom);
            } else {
                Insets bi = border.getBorderInsets(button);
                if (!isBorderComputing)
                    button.setBorder(null);
                uiPreferredSize.setSize(uiw - bi.left - bi.right, uih - bi.top
                        - bi.bottom);
            }
            if (!isBorderComputing) {
                button.setBorder(this.getButtonBorder(button));
                button.putClientProperty(SubstanceButtonUI.BORDER_COMPUTED,
                        new String());
            }
        }
        return uiPreferredSize;
    }
    
    @Override
    public Border getButtonBorder(AbstractButton button) {
        return new SubstanceButtonBorder(StandardButtonShaper.class) {
            @Override
            public Insets getBorderInsets(Component c) {
                return new Insets(0, 0, 0, 0);
            }
        };
    }
    
    @Override
    public boolean isProportionate() {
        return true;
    }
    
}