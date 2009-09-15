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
package xtrememp;

import javax.sound.sampled.SourceDataLine;
import xtrememp.player.dsp.DigitalSignalSynchronizer.Context;
import xtrememp.visualization.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeSet;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import xtrememp.player.dsp.DigitalSignalProcessor;
import xtrememp.visualization.spectrum.Spectrogram;
import xtrememp.visualization.spectrum.SpectrumBars;

/**
 *
 * @author Besmir Beqiri
 */
public class VisualizerPanel extends JComponent implements ActionListener,
        DigitalSignalProcessor, MouseListener, Runnable {

    private JPopupMenu selectionMenu;
    private JMenuItem fullScreenMenuItem;
    private FullScreenWindow fullScreenFrame;
    private Visualization visualization;
    private Set<Visualization> vSet;
    private Map<String, Visualization> vMap;
    private String command;
    private Context dssContext;

    public VisualizerPanel() {
        setOpaque(false);

        command = Settings.getVisualization();
        visualization = getVisualization(command);
        vMap = new HashMap<String, Visualization>();
        fullScreenFrame = new FullScreenWindow();

        addMouseListener(this);
    }

    public Visualization getVisualization(String displayName) {
        if (vSet == null) {
            vSet = new TreeSet<Visualization>();
            vSet.add(new Spectrogram());
            vSet.add(new VolumeMeter());
            vSet.add(new Waveform());
            vSet.add(new SpectrumBars());
        }
        for (Visualization v : vSet) {
            if (v.getDisplayName().equals(displayName)) {
                return v;
            }
        }
        return null;
    }

    public void showSelectionMenu(int x, int y) {
        if (selectionMenu == null) {
            selectionMenu = new JPopupMenu("Visualizations");
            ButtonGroup bg = new ButtonGroup();
            for (Visualization v : vSet) {
                vMap.put(v.getDisplayName(), v);
                JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(v.getDisplayName());
                menuItem.setSelected(v.getDisplayName().equals(command));
                menuItem.addActionListener(this);
                bg.add(menuItem);
                selectionMenu.add(menuItem);
            }
            selectionMenu.addSeparator();
            fullScreenMenuItem = new JMenuItem("Fullscreen");
            fullScreenMenuItem.addActionListener(this);
            selectionMenu.add(fullScreenMenuItem);
        }
        selectionMenu.show(this, x, y);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics graphics = g.create();
        if (visualization != null && dssContext != null) {
            visualization.render(graphics, getWidth(), getHeight(), dssContext);
        } else {
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, getWidth(), getHeight());
        }
        graphics.dispose();
    }

    @Override
    public void initialize(int sampleSize, SourceDataLine sourceDataLine) {
//        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void process(Context dssContext) {
        this.dssContext = dssContext;
        if (!fullScreenFrame.isFullScreen()) {
            EventQueue.invokeLater(this);
        }
    }

    @Override
    public void run() {
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == fullScreenMenuItem) {
            fullScreenFrame.setFullScreen(true);
        } else {
            command = e.getActionCommand();
            visualization = vMap.get(command);
            if (visualization != null) {
                Settings.setVisualization(command);
                repaint();
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getClickCount() == 2) {
            fullScreenFrame.setFullScreen(true);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            showSelectionMenu(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private class FullScreenTimerTask extends TimerTask {

        private FullScreenWindow fullScreenWindow;

        public FullScreenTimerTask(FullScreenWindow fullScreenWindow) {
            this.fullScreenWindow = fullScreenWindow;
        }

        @Override
        public void run() {
            if (fullScreenWindow.isFullScreen()) {
                fullScreenWindow.renderFullScreen();
            }
        }
    }

    private class FullScreenWindow extends Window {

        private GraphicsDevice device;
        private DisplayMode originalDM;
        private BufferStrategy bufferStrategy;
        private Timer timer;
        private long delay;
        private boolean isFullScreen = false;
        private int numBuffers = 2;

        public FullScreenWindow() {
            super((Frame) null);
            this.setIgnoreRepaint(true);
            this.setBackground(Color.black);
            this.setAlwaysOnTop(true);
            this.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        setFullScreen(false);
                    }
                }
            });
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            device = env.getScreenDevices()[0];
            originalDM = device.getDisplayMode();
            delay = Math.round(1000 / 60);
        }

        public boolean isFullScreen() {
            return isFullScreen;
        }

        public synchronized void renderFullScreen() {
            Dimension size = this.getSize();
            bufferStrategy = this.getBufferStrategy();
            Graphics g = null;
            try {
                g = bufferStrategy.getDrawGraphics();
                for (int i = 0; i < numBuffers; i++) {
                    if (!bufferStrategy.contentsLost()) {
                        g.setColor(Color.black);
                        g.fillRect(0, 0, size.width, size.height);
                        if (visualization != null && dssContext != null) {
                            visualization.render(g, size.width, size.height, dssContext);
                        }
                    }
                    bufferStrategy.show();
                }
            } catch (Exception e) {
            } finally {
                if (g != null) {
                    g.dispose();
                }
            }
        }

        public synchronized void setFullScreen(boolean flag) {
            if (flag && device.isFullScreenSupported()) {
                device.setFullScreenWindow(this);
                this.validate();
                this.createBufferStrategy(numBuffers);
                if (device.isDisplayChangeSupported()) {
                    device.setDisplayMode(originalDM);
                    this.setSize(new Dimension(originalDM.getWidth(), originalDM.getHeight()));
                    if (timer == null) {
                        timer = new Timer("FullScreen", true);
                        timer.schedule(new FullScreenTimerTask(this), 0, delay);
                    }
                }
                isFullScreen = true;
            } else {
                isFullScreen = false;
                if (timer != null) {
                    timer.cancel();
                    timer = null;
                }
                device.setFullScreenWindow(null);
                this.dispose();
            }
        }
    }
}
