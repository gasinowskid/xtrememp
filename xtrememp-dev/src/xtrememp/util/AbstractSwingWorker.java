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
package xtrememp.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingWorker;
import xtrememp.XtremeMP;
import xtrememp.ui.label.BusyLabel;

/**
 *
 * @author Besmir Beqiri
 */
public abstract class AbstractSwingWorker<T, V> extends SwingWorker<T, V> implements PropertyChangeListener {

    private BusyLabel busyLabel;

    public AbstractSwingWorker() {
        busyLabel = XtremeMP.getInstance().getBusyLabel();
        addPropertyChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals("state")) {
            SwingWorker.StateValue state = this.getState();
            switch (state) {
                case STARTED:
                    busyLabel.setBusy(true);
                    break;
                case DONE:
                    busyLabel.setBusy(false);
                    break;
            }
        }
    }
}
