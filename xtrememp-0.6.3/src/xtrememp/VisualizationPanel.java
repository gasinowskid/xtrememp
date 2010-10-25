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
package xtrememp;

import xtrememp.visualization.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import xtrememp.player.dsp.DigitalSignalProcessor;

/**
 *
 * @author Besmir Beqiri
 */
public class VisualizationPanel extends JPanel implements ActionListener,
        DigitalSignalProcessor, MouseListener, Runnable {

    private List<Visualization> vList;
    private JPopupMenu selectionMenu;
    private JMenuItem fullScreenMenuItem;
    private FullScreenWindow fullScreenFrame;
    private Visualization visualization;
    private Map<String, Visualization> commandMap;
    private String command = Settings.getVisualization();

    public VisualizationPanel() {
        super(false);
        setOpaque(false);
        visualization = getVisualization(command);
        commandMap = new HashMap<String, Visualization>();
        fullScreenFrame = new FullScreenWindow();
        addMouseListener(this);
    }

    public Visualization getVisualization(String visualization) {
        if (vList == null) {
            vList = new ArrayList<Visualization>(4);
            vList.add(new ScopeAnalyser());
            vList.add(new SpectrumAnalyser());
            vList.add(new VolumeMeter());
            vList.add(new Waveform());
        }
        for (Visualization v : vList) {
            Map<String, String> typesMap = v.getTypes();
            if (typesMap != null) {
                for (String typeName : typesMap.keySet()) {
                    if (typeName.equals(visualization)) {
                        v.setType(typeName);
                        return v;
                    }
                }
            } else if (v.getDisplayName().equals(visualization)) {
                return v;
            }
        }
        return null;
    }

    public void showSelectionMenu(int x, int y) {
        if (selectionMenu == null) {
            selectionMenu = new JPopupMenu("Visualizations");
            ButtonGroup bg = new ButtonGroup();
            for (Visualization v : vList) {
                Map<String, String> typesMap = v.getTypes();
                if (typesMap != null) {
                    JMenu menu = new JMenu(v.getDisplayName());
                    for (String typeName : typesMap.keySet()) {
                        commandMap.put(typeName, v);
                        JRadioButtonMenuItem typeMenuItem = new JRadioButtonMenuItem(typesMap.get(typeName));
                        typeMenuItem.setActionCommand(typeName);
                        typeMenuItem.setSelected(typeName.equals(command));
                        typeMenuItem.addActionListener(this);
                        bg.add(typeMenuItem);
                        menu.add(typeMenuItem);
                    }
                    selectionMenu.add(menu);
                } else {
                    commandMap.put(v.getDisplayName(), v);
                    JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(v.getDisplayName());
                    menuItem.setSelected(v.getDisplayName().equals(command));
                    menuItem.addActionListener(this);
                    bg.add(menuItem);
                    selectionMenu.add(menuItem);
                }
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
        g.setColor(Color.black);
        g.fillRect(0, 0, getWidth(), getHeight());
        if (visualization != null) {
            Graphics graphics = g.create();
            visualization.render(graphics, getWidth(), getHeight());
            graphics.dispose();
        }
    }

    @Override
    public boolean isOpaque() {
        return false;
    }

    @Override
    public void process(float[] leftChannel, float[] rightChannel) {
        if (visualization != null) {
            visualization.leftChannel = leftChannel;
            visualization.rightChannel = rightChannel;
            if (!fullScreenFrame.isFullScreen()) {
                SwingUtilities.invokeLater(this);
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == fullScreenMenuItem) {
            fullScreenFrame.setFullScreen(true);
        } else {
            command = e.getActionCommand();
            visualization = commandMap.get(command);
            if (visualization != null) {
                visualization.setType(command);
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

    @Override
    public void run() {
        repaint();
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
            delay = Math.round(1000 / 30);
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
                        if (visualization != null) {
                            visualization.render((Graphics2D) g, size.width, size.height);
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
