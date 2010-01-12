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
package xtrememp.ui.label;

import javax.swing.Icon;
import javax.swing.JLabel;
import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.animation.AnimationFacet;
import org.pushingpixels.substance.internal.animation.IconGlowTracker;
import org.pushingpixels.substance.internal.utils.icon.GlowingIcon;

/**
 *
 * @author Besmir Beqiri
 */
public class GlowingIconLabel extends JLabel {

    static {
        AnimationConfigurationManager.getInstance().allowAnimations(AnimationFacet.ICON_GLOW,
                GlowingIconLabel.class);
    }

    private IconGlowTracker iconGlowTracker;

    public GlowingIconLabel(Icon icon) {
        super();

        this.iconGlowTracker = new IconGlowTracker(this);
        this.setIcon(new GlowingIcon(icon, iconGlowTracker));
    }

    @Override
    public void setVisible(boolean flag) {
        super.setVisible(flag);
        if (flag) {
            if(!iconGlowTracker.isPlaying()) {
                iconGlowTracker.play(3);
            }
        }
    }
}
