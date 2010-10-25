/**
 * Xtreme Media Player a cross-platform media player.
 * Copyright (C) 2005-2008  Besmir Beqiri
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
package xtrememp.skin.button;

import java.awt.Dimension;
import javax.swing.Icon;
import org.jvnet.substance.SubstanceLookAndFeel;
import xtrememp.Settings;
import xtrememp.skin.button.shaper.RoundSquareButtonShaper;
import xtrememp.util.Utilities;

/**
 *
 * @author Besmir Beqiri
 */
public class VolumeButton extends PopdownButton {

    private final Icon volumeHighIcon = Utilities.getIcon("audio-volume-high.png");
    private final Icon volumeMediumIcon = Utilities.getIcon("audio-volume-medium.png");
    private final Icon volumeLowIcon = Utilities.getIcon("audio-volume-low.png");
    private final Icon volumeMutedIcon = Utilities.getIcon("audio-volume-muted.png");

    public VolumeButton(boolean muted) {
        if (muted) {
            setVolumeMutedIcon();
        } else {
            setVolumeIcon(Settings.getGain());
        }
        setPreferredSize(new Dimension(25, 25));
        putClientProperty(SubstanceLookAndFeel.BUTTON_SHAPER_PROPERTY, new RoundSquareButtonShaper());
    }

    public void setVolumeIcon(int volume) {
        if (volume <= 100) {
            setIcon(volumeHighIcon);
        }
        if (volume <= 67) {
            setIcon(volumeMediumIcon);
        }
        if (volume <= 34) {
            setIcon(volumeLowIcon);
        }
        if (volume == 0) {
            setIcon(volumeMutedIcon);
        }
    }

    public void setVolumeMutedIcon() {
        setIcon(volumeMutedIcon);
    }
}
