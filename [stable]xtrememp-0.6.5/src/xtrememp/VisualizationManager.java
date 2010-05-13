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
package xtrememp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferStrategy;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JToolBar;
import xtrememp.player.dsp.DigitalSignalProcessor;
import xtrememp.player.dsp.DigitalSignalSynchronizer;
import xtrememp.player.dsp.DssContext;
import xtrememp.ui.button.PopupButton;
import xtrememp.util.Utilities;
import xtrememp.visualization.Visualization;
import xtrememp.visualization.VolumeMeter;
import xtrememp.visualization.Waveform;
import xtrememp.visualization.spectrum.Spectrogram;
import xtrememp.visualization.spectrum.SpectrumBars;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 */
public class VisualizationManager extends JPanel implements ActionListener,
        DigitalSignalProcessor {

    private JPopupMenu selectionMenu;
    private JButton fullScreenButton;
    private JButton previousVisButton;
    private JButton nextVisButton;
    private PopupButton visMenuButton;
    private ButtonGroup visButtonGroup;
    private FullscreenWindow fullscreenWindow;
    private VisualizationPanel visPanel;
    private Visualization currentVis;
    private TreeSet<Visualization> visSet;
    private Map<String, Visualization> visMap;
    private String actionCommand;
    private DssContext dssContext;

    public VisualizationManager() {
        super(new BorderLayout());

        initVisualizations();
        initComponents();
    }

    private void initVisualizations() {
        visSet = new TreeSet<Visualization>();
        visSet.add(new Spectrogram());
        visSet.add(new SpectrumBars());
        visSet.add(new VolumeMeter());
        visSet.add(new Waveform());
    }

    private void initComponents() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        fullScreenButton = new JButton(Utilities.VIEW_FULLSCREEN_ICON);
        fullScreenButton.setToolTipText(tr("MainFrame.VisualizationManager.ViewFullscreen"));
        fullScreenButton.addActionListener(this);
        toolBar.add(fullScreenButton);
        toolBar.addSeparator();
        previousVisButton = new JButton(Utilities.GO_PREVIOUS_ICON);
        previousVisButton.setToolTipText(tr("MainFrame.VisualizationManager.PreviousVisualization"));
        previousVisButton.addActionListener(this);
        toolBar.add(previousVisButton);
        nextVisButton = new JButton(Utilities.GO_NEXT_ICON);
        nextVisButton.setToolTipText(tr("MainFrame.VisualizationManager.NextVisualization"));
        nextVisButton.addActionListener(this);
        toolBar.add(nextVisButton);
        toolBar.addSeparator();
        visMenuButton = new PopupButton(Utilities.MENU_ICON);
        visMenuButton.setToolTipText(tr("MainFrame.VisualizationManager.VisualizationsMenu"));
        selectionMenu = visMenuButton.getPopupMenu();
        visButtonGroup = new ButtonGroup();
        actionCommand = Settings.getVisualization();
        visMap = new HashMap<String, Visualization>();
        for (Visualization v : visSet) {
            visMap.put(v.getDisplayName(), v);
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(v.getDisplayName());
            menuItem.setSelected(v.getDisplayName().equals(actionCommand));
            menuItem.addActionListener(this);
            visButtonGroup.add(menuItem);
            selectionMenu.add(menuItem);
        }
        currentVis = visMap.get(actionCommand);
        toolBar.add(visMenuButton);
        this.add(toolBar, BorderLayout.NORTH);

        visPanel = new VisualizationPanel();
        this.add(visPanel, BorderLayout.CENTER);

        fullscreenWindow = new FullscreenWindow();
    }

    @Override
    public void process(DssContext dssContext) {
        this.dssContext = dssContext;
        if (!fullscreenWindow.isFullScreen()) {
            EventQueue.invokeLater(visPanel);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(fullScreenButton)) {
            fullscreenWindow.setFullScreen(true);
        } else if (source.equals(previousVisButton)) {
            Visualization previousVis = visSet.lower(currentVis);
            if (previousVis == null) {
                previousVis = visSet.last();
            }
            currentVis = previousVis;
            for (Enumeration<AbstractButton> abEnum = visButtonGroup.getElements(); abEnum.hasMoreElements();) {
                AbstractButton aButton = abEnum.nextElement();
                if (aButton.getText().equals(currentVis.getDisplayName())) {
                    aButton.setSelected(true);
                    break;
                }
            }
        } else if (source.equals(nextVisButton)) {
            Visualization nextVis = visSet.higher(currentVis);
            if (nextVis == null) {
                nextVis = visSet.first();
            }
            currentVis = nextVis;
            for (Enumeration<AbstractButton> abEnum = visButtonGroup.getElements(); abEnum.hasMoreElements();) {
                AbstractButton aButton = abEnum.nextElement();
                if (aButton.getText().equals(currentVis.getDisplayName())) {
                    aButton.setSelected(true);
                    break;
                }
            }
        } else {
            actionCommand = e.getActionCommand();
            currentVis = visMap.get(actionCommand);
            if (currentVis != null) {
                Settings.setVisualization(actionCommand);
                visPanel.repaint();
            }
        }
    }

    private class VisualizationPanel extends JPanel implements Runnable {

        public VisualizationPanel() {
            super(null, false);
            setOpaque(false);
            setIgnoreRepaint(true);
            addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        fullscreenWindow.setFullScreen(true);
                    }
                }
            });
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g.create();
            if (currentVis != null && dssContext != null) {
                currentVis.render(g2d, getWidth(), getHeight(), dssContext);
            } else {
                g2d.setColor(Color.black);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
            g2d.dispose();
        }

        @Override
        public void run() {
            repaint();
        }
    }

    private class FullscreenWindow extends Frame implements Runnable {

        private final ScheduledExecutorService execService;
        private final GraphicsDevice device;
        private final DisplayMode displayMode;
        private ScheduledFuture updateScreenFuture;
        private BufferStrategy bufferStrategy;
        private int numBuffers = 2;
        private long delay;
        private volatile boolean isFullScreen = false;

        public FullscreenWindow() {
            super();
            this.setUndecorated(true);
            this.setIgnoreRepaint(true);
            this.setBackground(Color.black);
            this.setAlwaysOnTop(true);
            this.setFocusable(true);
            this.addKeyListener(new KeyAdapter() {

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        setFullScreen(false);
                    }
                }
            });
            this.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        setFullScreen(false);
                    }
                }
            });
            execService = Executors.newSingleThreadScheduledExecutor();
            GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
            device = env.getDefaultScreenDevice();
            displayMode = device.getDisplayMode();
            int refreshRate = displayMode.getRefreshRate();
            if(refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN) {
                refreshRate = DigitalSignalSynchronizer.DEFAULT_FPS;
            }
            delay = Math.round(1000 / refreshRate);
        }

        @Override
        public void run() {
            if (isFullScreen()) {
                Dimension size = this.getSize();
                bufferStrategy = this.getBufferStrategy();
                Graphics2D g2d = null;
                try {
                    g2d = (Graphics2D) bufferStrategy.getDrawGraphics();
                    for (int i = 0; i < numBuffers; i++) {
                        if (!bufferStrategy.contentsLost()) {
                            g2d.setColor(Color.black);
                            g2d.fillRect(0, 0, size.width, size.height);
                            if (currentVis != null && dssContext != null) {
                                currentVis.render(g2d, size.width, size.height, dssContext);
                            }
                        }
                        bufferStrategy.show();
                    }
                } finally {
                    if (g2d != null) {
                        g2d.dispose();
                    }
                }
            }
        }

        public boolean isFullScreen() {
            return isFullScreen;
        }

        public void setFullScreen(boolean flag) {
            XtremeMP.getInstance().getMainFrame().setVisible(!flag);
            if (flag && device.isFullScreenSupported()) {
                device.setFullScreenWindow(this);
                validate();
                createBufferStrategy(numBuffers);
                if (device.isDisplayChangeSupported()) {
                    device.setDisplayMode(displayMode);
                    setSize(new Dimension(displayMode.getWidth(), displayMode.getHeight()));
                    updateScreenFuture = execService.scheduleAtFixedRate(this, 0, delay, TimeUnit.MILLISECONDS);
                }
                isFullScreen = true;
            } else {
                isFullScreen = false;
                updateScreenFuture.cancel(true);
                device.setFullScreenWindow(null);
                dispose();
            }
        }
    }
}
