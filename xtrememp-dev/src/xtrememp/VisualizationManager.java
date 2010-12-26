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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
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
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.SkinChangeListener;
import org.pushingpixels.substance.internal.utils.border.SubstanceBorder;
import xtrememp.player.dsp.DigitalSignalSynchronizer;
import xtrememp.ui.button.PopupButton;
import xtrememp.util.Utilities;
import xtrememp.visualization.Spectrogram;
import xtrememp.visualization.SpectrumBars;
import xtrememp.visualization.Visualization;
import xtrememp.visualization.VolumeMeter;
import xtrememp.visualization.Waveform;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 */
public final class VisualizationManager extends JPanel implements ActionListener,
        SkinChangeListener {

    private JPopupMenu selectionMenu;
    private JButton fullScreenButton;
    private JButton prevVisButton;
    private JButton nextVisButton;
    private PopupButton visMenuButton;
    private ButtonGroup visButtonGroup;
    private FullscreenWindow fullscreenWindow;
    private JPanel visPanel;
    private DigitalSignalSynchronizer dss;
    private Visualization currentVis;
    private TreeSet<Visualization> visSet;
    private Map<String, Visualization> visMap;

    public VisualizationManager(DigitalSignalSynchronizer dss) {
        super(new BorderLayout());

        this.dss = dss;

        initVisualizations();
        initComponents();
        skinChanged();
    }

    private void initVisualizations() {
        visSet = new TreeSet<Visualization>();
        visSet.add(new Spectrogram());
        visSet.add(new SpectrumBars());
        visSet.add(new VolumeMeter());
        visSet.add(new Waveform());

        for (Visualization v : visSet) {
            v.setBorder(new SubstanceBorder());
        }
    }

    private void initComponents() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        fullScreenButton = new JButton(Utilities.VIEW_FULLSCREEN_ICON);
        fullScreenButton.setToolTipText(tr("MainFrame.VisualizationManager.ViewFullscreen"));
        fullScreenButton.addActionListener(this);
        toolBar.add(fullScreenButton);
        toolBar.addSeparator();
        prevVisButton = new JButton(Utilities.GO_PREVIOUS_ICON);
        prevVisButton.setToolTipText(tr("MainFrame.VisualizationManager.PreviousVisualization"));
        prevVisButton.addActionListener(this);
        toolBar.add(prevVisButton);
        nextVisButton = new JButton(Utilities.GO_NEXT_ICON);
        nextVisButton.setToolTipText(tr("MainFrame.VisualizationManager.NextVisualization"));
        nextVisButton.addActionListener(this);
        toolBar.add(nextVisButton);
        toolBar.addSeparator();
        visMenuButton = new PopupButton(Utilities.MENU_ICON);
        visMenuButton.setToolTipText(tr("MainFrame.VisualizationManager.VisualizationsMenu"));
        selectionMenu = visMenuButton.getPopupMenu();
        visButtonGroup = new ButtonGroup();
        visMap = new HashMap<String, Visualization>();
        visPanel = new JPanel(new CardLayout());
        visPanel.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    fullscreenWindow.setFullScreen(true);
                }
            }
        });
        for (Visualization v : visSet) {
            visMap.put(v.getDisplayName(), v);
            JRadioButtonMenuItem menuItem = new JRadioButtonMenuItem(v.getDisplayName());
            menuItem.addActionListener(this);
            visButtonGroup.add(menuItem);
            selectionMenu.add(menuItem);
            visPanel.add(v, v.getDisplayName());
        }
        toolBar.add(visMenuButton);
        this.add(toolBar, BorderLayout.NORTH);
        this.add(visPanel, BorderLayout.CENTER);

        showVisualization(Settings.getVisualization());

        fullscreenWindow = new FullscreenWindow();

        SubstanceLookAndFeel.registerSkinChangeListener(this);
    }

    public void setDssEnabled(boolean flag) {
        if (flag) {
            dss.add(currentVis);
        } else {
            dss.remove(currentVis);
        }
    }

    public void showVisualization(String visDisplayName) {
        showVisualization(visMap.get(visDisplayName), true);
    }

    private void showVisualization(Visualization newVis, boolean updateSelected) {
        if (newVis == null) {
            throw new IllegalArgumentException("Visualization is null.");
        }
        //Remove the old visualization and add the new one to the DSS.
        if (currentVis != null) {
            dss.remove(currentVis);
        }
        currentVis = newVis;
        dss.add(currentVis);
        //Retrieve visualization display name.
        String visDisplayName = currentVis.getDisplayName();
        //Show the visualization.
        CardLayout cardLayout = (CardLayout) (visPanel.getLayout());
        cardLayout.show(visPanel, visDisplayName);
        if (updateSelected) {
            //Set selected state on the button related to this visualization.
            for (Enumeration<AbstractButton> abEnum = visButtonGroup.getElements(); abEnum.hasMoreElements();) {
                AbstractButton aButton = abEnum.nextElement();
                if (aButton.getText().equals(visDisplayName)) {
                    aButton.setSelected(true);
                    break;
                }
            }
        }
        //
        visPanel.repaint();
        //
        Settings.setVisualization(visDisplayName);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(fullScreenButton)) {
            fullscreenWindow.setFullScreen(true);
        } else if (source.equals(prevVisButton)) {
            Visualization prevVis = visSet.lower(currentVis);
            if (prevVis == null && !visSet.isEmpty()) {
                prevVis = visSet.last();
            }
            showVisualization(prevVis, true);
        } else if (source.equals(nextVisButton)) {
            Visualization nextVis = visSet.higher(currentVis);
            if (nextVis == null && !visSet.isEmpty()) {
                nextVis = visSet.first();
            }
            showVisualization(nextVis, true);
        } else {
            showVisualization(visMap.get(e.getActionCommand()), false);
        }
    }

    @Override
    public void skinChanged() {
        SubstanceColorScheme activeColorScheme = SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.GENERAL);
        SubstanceColorScheme backgroundColorScheme = SubstanceLookAndFeel.getCurrentSkin().getBackgroundColorScheme(DecorationAreaType.GENERAL);
        SubstanceColorScheme colorScheme = activeColorScheme.isDark() ? backgroundColorScheme : activeColorScheme;

        for (Visualization v : visSet) {
            v.setBackgroundColor(colorScheme.getBackgroundFillColor());
            v.setForegroundColor(colorScheme.getForegroundColor());
            v.repaint();
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
            if (refreshRate == DisplayMode.REFRESH_RATE_UNKNOWN) {
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
                            if (currentVis != null) {
                                currentVis.render(g2d, size.width, size.height);
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
