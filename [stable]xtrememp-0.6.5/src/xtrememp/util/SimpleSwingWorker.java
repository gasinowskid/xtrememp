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

import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;

/**
 * Wrap the default SwingWorker for simple cases where we are NOT returned a
 * result in such a way that exceptions are not swallowed.
 *
 * @author Besmir Beqiri
 */
public abstract class SimpleSwingWorker {

	private final SwingWorker<Void,Void> worker =
                new SwingWorker<Void,Void>() {
		@Override
		protected Void doInBackground() throws Exception {
			SimpleSwingWorker.this.doInBackground();
			return null;
		}

		@Override
		protected void done() {
			// call get to make sure any exceptions
			// thrown during doInBackground() are
			// thrown again
			try {
				get();
			} catch (final InterruptedException ex) {
				throw new RuntimeException(ex);
			} catch (final ExecutionException ex) {
				throw new RuntimeException(ex.getCause());
			}
			SimpleSwingWorker.this.done();
		}
	};

	public SimpleSwingWorker() {}

	protected abstract Void doInBackground() throws Exception;

	protected abstract void done();

	public void execute() {
		worker.execute();
	}
}