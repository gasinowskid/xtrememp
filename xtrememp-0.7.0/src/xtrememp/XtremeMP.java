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

import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import java.awt.event.ItemEvent;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.sound.sampled.AudioSystem;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.PropertyConfigurator;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.painter.BusyPainter;
import org.pushingpixels.lafwidget.animation.AnimationConfigurationManager;
import org.pushingpixels.lafwidget.animation.AnimationFacet;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceConstants;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.player.audio.AudioPlayer;
import xtrememp.player.audio.PlaybackEvent;
import xtrememp.player.audio.PlayerException;
import xtrememp.player.audio.PlaybackListener;
import xtrememp.playlist.Playlist;
import xtrememp.playlist.PlaylistIO;
import xtrememp.playlist.PlaylistItem;
import xtrememp.playlist.PlaylistException;
import xtrememp.ui.button.NextButton;
import xtrememp.ui.button.PlayPauseButton;
import xtrememp.ui.button.PreviousButton;
import xtrememp.ui.button.StopButton;
import xtrememp.ui.button.VolumeButton;
import xtrememp.ui.slider.SeekSlider;
import xtrememp.tag.TagInfo;
import xtrememp.ui.tray.JXTrayIcon;
import xtrememp.update.SoftwareUpdate;
import xtrememp.update.Version;
import xtrememp.util.AbstractSwingWorker;
import xtrememp.util.LanguageBundle;
import xtrememp.util.file.AudioFileFilter;
import xtrememp.util.log.Log4jProperties;
import xtrememp.util.file.PlaylistFileFilter;
import xtrememp.util.Utilities;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 * 
 * Special thanks to rom1dep for the changes applied to this class.
 */
public class XtremeMP implements ActionListener, ControlListener,
        PlaybackListener, IntellitypeListener {

    private static final Logger logger = LoggerFactory.getLogger(XtremeMP.class);
    private final AudioFileFilter audioFileFilter = new AudioFileFilter();
    private final PlaylistFileFilter playlistFileFilter = new PlaylistFileFilter();
    private static XtremeMP instance;
    private JFrame mainFrame;
    private JMenuBar menuBar;
    private JMenu fileMenu;
    private JMenu playerMenu;
    private JMenu viewMenu;
    private JMenu helpMenu;
    private JMenuItem openMenuItem;
    private JMenuItem openURLMenuItem;
    private JMenuItem openPlaylistMenuItem;
    private JMenuItem savePlaylistMenuItem;
    private JMenuItem preferencesMenuItem;
    private JMenuItem exitMenuItem;
    private JMenuItem nextMenuItem;
    private JMenuItem playPauseMenuItem;
    private JMenuItem stopMenuItem;
    private JMenuItem previousMenuItem;
    private JMenuItem randomizePlaylistMenuItem;
    private JRadioButtonMenuItem playlistManagerMenuItem;
    private JRadioButtonMenuItem visualizationMenuItem;
    private JMenuItem updateMenuItem;
    private JMenuItem aboutMenuItem;
    private JXBusyLabel busyLabel;
    private JPanel mainPanel;
    private VisualizationManager visualizationPanel;
    private JPanel controlPanel;
    private AudioPlayer audioPlayer;
    private Playlist playlist;
    private PlaylistManager playlistManager;
    private PreferencesDialog preferencesDialog;
    private StopButton stopButton;
    private PreviousButton previousButton;
    private PlayPauseButton playPauseButton;
    private NextButton nextButton;
    private VolumeButton volumeButton;
    private JSlider volumeSlider;
    private JLabel timeLabel;
    private JLabel statusLabel;
    private SeekSlider seekSlider;
    private PlaylistItem currentPli;
    private SystemTray sysTray;
    private JXTrayIcon trayIcon;
    private JPopupMenu trayMenu;
    private JMenuItem hideShowTrayMenuItem;
    private JMenuItem playPauseTrayMenuItem;
    private JMenuItem stopTrayMenuItem;
    private JMenuItem nextTrayMenuItem;
    private JMenuItem previousTrayMenuItem;
    private JMenuItem exitTrayMenuItem;

    private XtremeMP() {
    }

    public static XtremeMP getInstance() {
        if (instance == null) {
            instance = new XtremeMP();
        }
        return instance;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public JXBusyLabel getBusyLabel() {
        return busyLabel;
    }

    public AudioPlayer getAudioPlayer() {
        return audioPlayer;
    }

    public void init(List<String> arguments) {
        // Process arguments
//        for (String arg : arguments) {
//        }

        if (JIntellitype.isJIntellitypeSupported()) {
            JIntellitype.getInstance().addIntellitypeListener(this);
        }

        audioPlayer = new AudioPlayer(this);
        String mixerName = Settings.getMixerName();
        if (!Utilities.isNullOrEmpty(mixerName)) {
            audioPlayer.setMixerName(mixerName);
        }
        // Launch gui
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
                UIManager.put(SubstanceLookAndFeel.FOCUS_KIND, SubstanceConstants.FocusKind.NONE);
                SubstanceLookAndFeel.setSkin(Settings.getSkin());
                mainFrame = new JFrame(tr("Application.title"));
                mainFrame.setIconImages(Utilities.getIconImages());
                mainFrame.setBounds(Settings.getMainFrameBounds());
                mainFrame.addWindowListener(new WindowAdapter() {

                    @Override
                    public void windowClosing(WindowEvent ev) {
                        exit();
                    }
                });
                createMenuBar();
                createMainPanels();
                createTrayIcon();
                mainFrame.setMinimumSize(new Dimension(controlPanel.getPreferredSize().width + 50, 200));
                // center the frame
                if (Settings.isEmpty()) {
                    mainFrame.setLocationRelativeTo(null);
                }
                mainFrame.setVisible(true);

                File playlistFile = new File(Settings.getCacheDir(), Utilities.DEFAULT_PLAYLIST);
                if (playlistFile.exists()) {
                    playlistManager.loadPlaylist(playlistFile.getAbsolutePath());
                }
                playlist = playlistManager.getPlaylist();
            }
        });
    }

    public static void main(String[] args) throws Exception {
        List<String> arguments = Arrays.asList(args);
//        boolean debug = arguments.contains("-debug");
        // Load Settings
        Settings.loadSettings();
        // Load log4j properties
        PropertyConfigurator.configure(new Log4jProperties());
        // Enable uncaught exception catching
        try {
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

                @Override
                public void uncaughtException(Thread t, Throwable e) {
                    logger.error(t.getName(), e);
                }
            });
        } catch (Throwable t) {
            logger.error(t.getMessage(), t);
        }
        // Close error stream
        System.err.close();

        // Set language
        Locale locale = Utilities.getLanguages()[Settings.getLanguageIndex()];
        Locale.setDefault(locale);
        LanguageBundle.setLanguage(locale);

        // Animation configurations
        AnimationConfigurationManager.getInstance().disallowAnimations(AnimationFacet.ICON_GLOW, JTable.class);
        AnimationConfigurationManager.getInstance().disallowAnimations(AnimationFacet.ROLLOVER, JTable.class);
        AnimationConfigurationManager.getInstance().disallowAnimations(AnimationFacet.SELECTION, JTable.class);

        // Init
        getInstance().init(arguments);

        // Check for updates
        if (Settings.isAutomaticUpdatesEnabled()) {
            // wait 5 sec
            SoftwareUpdate.scheduleCheckForUpdates(5 * 1000);
        }
    }

    protected void exit() {
        // Save current settings
        Settings.setMainFrameBounds(mainFrame.getBounds());
        Settings.setPlaylistPosition(playlist.getCursorPosition());
        Settings.storeSettings();
        try {
            File playlistFile = new File(Settings.getCacheDir(), Utilities.DEFAULT_PLAYLIST);
            PlaylistIO.saveXSPF(playlist, playlistFile.getAbsolutePath());
        } catch (PlaylistException ex) {
            logger.error("Can't save default playlist", ex);
        }
        // Release system resources
        audioPlayer.stop();
        logger.info("Exit application...");
        System.exit(0);
    }

    protected void createMenuBar() {
        menuBar = new JMenuBar();

        // File Menu
        String fileMenuStr = tr("MainFrame.Menu.File");
        fileMenu = new JMenu(fileMenuStr);
        fileMenu.setMnemonic(fileMenuStr.charAt(0));

        openMenuItem = new JMenuItem(tr("MainFrame.Menu.File.OpenFile"));
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        openMenuItem.setIcon(Utilities.FOLDER_ICON);
        openMenuItem.addActionListener(this);
        fileMenu.add(openMenuItem);

        openURLMenuItem = new JMenuItem(tr("MainFrame.Menu.File.OpenURL"));
        openURLMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK));
        openURLMenuItem.setIcon(Utilities.FOLDER_REMOTE_ICON);
        openURLMenuItem.addActionListener(this);
        fileMenu.add(openURLMenuItem);

        fileMenu.addSeparator();

        openPlaylistMenuItem = new JMenuItem(tr("MainFrame.Menu.File.OpenPlaylist"));
        openPlaylistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        openPlaylistMenuItem.setIcon(Utilities.DOCUMENT_OPEN_ICON);
        openPlaylistMenuItem.addActionListener(this);
        fileMenu.add(openPlaylistMenuItem);

        savePlaylistMenuItem = new JMenuItem(tr("MainFrame.Menu.File.SavePlaylist"));
        savePlaylistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
        savePlaylistMenuItem.setIcon(Utilities.DOCUMENT_SAVE_ICON);
        savePlaylistMenuItem.addActionListener(this);
        fileMenu.add(savePlaylistMenuItem);

        fileMenu.addSeparator();

        preferencesMenuItem = new JMenuItem(tr("MainFrame.Menu.File.Preferences"));
        preferencesMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK));
        preferencesMenuItem.addActionListener(this);
        fileMenu.add(preferencesMenuItem);

        fileMenu.addSeparator();

        exitMenuItem = new JMenuItem(tr("MainFrame.Menu.File.Exit"));
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK));
        exitMenuItem.addActionListener(this);
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        // Player Menu
        String playerMenuStr = tr("MainFrame.Menu.Player");
        playerMenu = new JMenu(playerMenuStr);
        playerMenu.setMnemonic(playerMenuStr.charAt(0));

        playPauseMenuItem = new JMenuItem("Play/Pause");
        playPauseMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_DOWN_MASK));
        playPauseMenuItem.addActionListener(this);
        playerMenu.add(playPauseMenuItem);

        stopMenuItem = new JMenuItem(tr("MainFrame.Menu.Player.Stop"));
        stopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_B, InputEvent.CTRL_DOWN_MASK));
//        stopMenuItem.setIcon(Utilities.MEDIA_STOP_ICON);
        stopMenuItem.addActionListener(this);
        playerMenu.add(stopMenuItem);

        previousMenuItem = new JMenuItem(tr("MainFrame.Menu.Player.Previous"));
        previousMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK));
//        previousMenuItem.setIcon(Utilities.MEDIA_PREVIOUS_ICON);
        previousMenuItem.addActionListener(this);
        playerMenu.add(previousMenuItem);

        nextMenuItem = new JMenuItem(tr("MainFrame.Menu.Player.Next"));
        nextMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
//        nextMenuItem.setIcon(Utilities.MEDIA_NEXT_ICON);
        nextMenuItem.addActionListener(this);
        playerMenu.add(nextMenuItem);

        playerMenu.addSeparator();

        randomizePlaylistMenuItem = new JMenuItem(tr("MainFrame.Menu.Player.Randomize"));
        randomizePlaylistMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
        randomizePlaylistMenuItem.setIcon(Utilities.PLAYLIST_SHUFFLE_ICON);
        randomizePlaylistMenuItem.addActionListener(this);
        playerMenu.add(randomizePlaylistMenuItem);

        menuBar.add(playerMenu);

        // View Menu
        String viewMenuStr = tr("MainFrame.Menu.View");
        viewMenu = new JMenu(viewMenuStr);
        viewMenu.setMnemonic(viewMenuStr.charAt(0));

        playlistManagerMenuItem = new JRadioButtonMenuItem(tr("MainFrame.Menu.View.PlaylistManager"));
        playlistManagerMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK));
        playlistManagerMenuItem.addActionListener(this);
        viewMenu.add(playlistManagerMenuItem);

        visualizationMenuItem = new JRadioButtonMenuItem(tr("MainFrame.Menu.View.Visualizations"));
        visualizationMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_DOWN_MASK));
        visualizationMenuItem.addActionListener(this);
        viewMenu.add(visualizationMenuItem);

        ButtonGroup viewBG = new ButtonGroup();
        viewBG.add(playlistManagerMenuItem);
        viewBG.add(visualizationMenuItem);

        menuBar.add(viewMenu);

        // Help menu
        String helpMenuStr = tr("MainFrame.Menu.Help");
        helpMenu = new JMenu(helpMenuStr);
        helpMenu.setMnemonic(helpMenuStr.charAt(0));

        updateMenuItem = new JMenuItem(tr("MainFrame.Menu.Help.CheckForUpdates"));
        updateMenuItem.addActionListener(this);
        helpMenu.add(updateMenuItem);
        helpMenu.addSeparator();

        aboutMenuItem = new JMenuItem(tr("MainFrame.Menu.Help.About"));
        aboutMenuItem.addActionListener(this);
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        menuBar.add(Box.createHorizontalGlue());

        busyLabel = new JXBusyLabel(new Dimension(18, 18));
        BusyPainter busyPainter = busyLabel.getBusyPainter();
        busyPainter.setTrailLength(4);
        busyPainter.setHighlightColor(Color.darkGray);
        menuBar.add(busyLabel);
        menuBar.add(Box.createHorizontalStrut(8));

        mainFrame.setJMenuBar(menuBar);
    }

    protected void createMainPanels() {
        mainPanel = new JPanel(new CardLayout());
        playlistManager = new PlaylistManager(this);
        visualizationPanel = new VisualizationManager();
        if (Settings.getLastView().equals(Utilities.VISUALIZATION_PANEL)) {
            mainPanel.add(visualizationPanel, Utilities.VISUALIZATION_PANEL);
            mainPanel.add(playlistManager, Utilities.PLAYLIST_MANAGER);
            audioPlayer.getDspAudioDataConsumer().add(visualizationPanel);
            visualizationMenuItem.setSelected(true);
        } else {
            mainPanel.add(playlistManager, Utilities.PLAYLIST_MANAGER);
            mainPanel.add(visualizationPanel, Utilities.VISUALIZATION_PANEL);
            playlistManagerMenuItem.setSelected(true);
        }
        mainFrame.getContentPane().setLayout(new MigLayout("fill"));
        mainFrame.getContentPane().add(mainPanel, "grow");

        JPanel southPanel = new JPanel(new MigLayout("fill", "[center]"));
        SubstanceLookAndFeel.setDecorationType(southPanel, DecorationAreaType.TOOLBAR);
        seekSlider = new SeekSlider(this);
        seekSlider.setEnabled(false);
        southPanel.add(seekSlider, "north, gap 4 4 1 0");

        controlPanel = new JPanel(new MigLayout("gap 0, ins 0", "[center]"));
        stopButton = new StopButton();
        stopButton.addActionListener(this);
        controlPanel.add(stopButton);
        previousButton = new PreviousButton();
        previousButton.addActionListener(this);
        controlPanel.add(previousButton);
        playPauseButton = new PlayPauseButton();
        playPauseButton.addActionListener(this);
        controlPanel.add(playPauseButton, "height pref!");
        nextButton = new NextButton();
        nextButton.addActionListener(this);
        controlPanel.add(nextButton);
        volumeButton = new VolumeButton(Settings.isMuted());
        JPopupMenu volumePopupMenu = volumeButton.getPopupMenu();
        volumeSlider = new JSlider(JSlider.VERTICAL, 0, 100, Settings.getGain());
        volumeSlider.setMajorTickSpacing(25);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                Object source = e.getSource();
                if (source == volumeSlider) {
                    if (volumeSlider.getValueIsAdjusting()) {
                        try {
                            int volumeValue = volumeSlider.getValue();
                            volumeButton.setVolumeIcon(volumeValue);
                            audioPlayer.setGain(volumeValue / 100.0F);
                            Settings.setGain(volumeValue);
                        } catch (PlayerException ex) {
                            logger.debug(ex.getMessage(), ex);
                        }
                    }
                }
            }
        });
        volumeSlider.setEnabled(!Settings.isMuted());
        JPanel volumePanel = new JPanel(new MigLayout("fill"));
        JLabel volumeLabel = new JLabel(tr("MainFrame.Menu.Player.Volume"), JLabel.CENTER);
        volumeLabel.setFont(volumeLabel.getFont().deriveFont(Font.BOLD));
        volumePanel.add(volumeLabel, "north");
        volumePanel.add(volumeSlider);
        JCheckBox muteCheckBox = new JCheckBox(tr("MainFrame.Menu.Player.Mute"));
        muteCheckBox.setSelected(Settings.isMuted());
        muteCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                try {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        volumeSlider.setEnabled(false);
                        volumeButton.setVolumeMutedIcon();
                        audioPlayer.setMuted(true);
                        Settings.setMuted(true);
                    } else {
                        volumeSlider.setEnabled(true);
                        volumeButton.setVolumeIcon(Settings.getGain());
                        audioPlayer.setMuted(false);
                        Settings.setMuted(false);
                    }
                } catch (PlayerException ex) {
                    logger.debug(ex.getMessage(), ex);
                }
            }
        });
        volumePanel.add(muteCheckBox, "south");
        volumePopupMenu.add(volumePanel);
        controlPanel.add(volumeButton);
        southPanel.add(controlPanel, "gap 0 0 2 5");

        JPanel statusBar = new JPanel(new MigLayout("ins 2 0 2 0"));
        SubstanceLookAndFeel.setDecorationType(statusBar, DecorationAreaType.FOOTER);
        timeLabel = new JLabel(Utilities.ZERO_TIMER);
        timeLabel.setFont(timeLabel.getFont().deriveFont(Font.BOLD));
        statusBar.add(timeLabel, "gap 8 4 0 0");
        statusLabel = new JLabel();
        statusBar.add(statusLabel, "gap 4 8 0 0, wmin 0");
        southPanel.add(statusBar, "south");
        mainFrame.getContentPane().add(southPanel, "south");
    }

    protected void createTrayIcon() {
        if (SystemTray.isSupported()) {
            logger.info("SystemTray supported, initializing TrayIcon...");
            try {
                sysTray = SystemTray.getSystemTray();
                trayIcon = new JXTrayIcon(mainFrame.getIconImage(), tr("Application.title"));

                trayMenu = new JPopupMenu();

                hideShowTrayMenuItem = new JMenuItem(tr("MainFrame.Tray.HideMainFrame"));
                hideShowTrayMenuItem.setIcon(Utilities.APP_16_ICON);
                hideShowTrayMenuItem.addActionListener(this);
                trayMenu.add(hideShowTrayMenuItem);

                trayMenu.addSeparator();

                playPauseTrayMenuItem = new JMenuItem(tr("MainFrame.Menu.Player.Play"));
                playPauseTrayMenuItem.addActionListener(this);
                trayMenu.add(playPauseTrayMenuItem);

                stopTrayMenuItem = new JMenuItem(tr("MainFrame.Menu.Player.Stop"));
                stopTrayMenuItem.addActionListener(this);
                trayMenu.add(stopTrayMenuItem);

                previousTrayMenuItem = new JMenuItem(tr("MainFrame.Menu.Player.Previous"));
                previousTrayMenuItem.addActionListener(this);
                trayMenu.add(previousTrayMenuItem);

                nextTrayMenuItem = new JMenuItem(tr("MainFrame.Menu.Player.Next"));
                nextTrayMenuItem.addActionListener(this);
                trayMenu.add(nextTrayMenuItem);

                trayMenu.addSeparator();

                exitTrayMenuItem = new JMenuItem(tr("MainFrame.Menu.File.Exit"));
                exitTrayMenuItem.addActionListener(this);
                trayMenu.add(exitTrayMenuItem);

                trayIcon.setJPopupMenu(trayMenu);
                trayIcon.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        if (SwingUtilities.isLeftMouseButton(e)) {
                            if (mainFrame.getExtendedState() == JFrame.ICONIFIED) {
                                if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.NORMAL)) {
                                    mainFrame.setExtendedState(JFrame.NORMAL);
                                }
                                mainFrame.setVisible(true);
                            } else {
                                if (Toolkit.getDefaultToolkit().isFrameStateSupported(JFrame.ICONIFIED)) {
                                    mainFrame.setExtendedState(JFrame.ICONIFIED);
                                }
                                mainFrame.dispose();
                            }
                        }
                    }
                });
                sysTray.add(trayIcon);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        } else {
            logger.info("This environment doesn't allow you to controll XtremeMP throught a TrayIcon...");
        }
    }

    protected void setTime(final String timeText, final int seekSliderValue) {
        if (EventQueue.isDispatchThread()) {
            seekSlider.setValue(seekSliderValue);
            timeLabel.setText(timeText);
        } else {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    seekSlider.setValue(seekSliderValue);
                    timeLabel.setText(timeText);
                }
            });
        }
    }

    protected void setStatus(String text) {
        final String status = (text != null) ? text : "";
        if (EventQueue.isDispatchThread()) {
            statusLabel.setText(status);
        } else {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    statusLabel.setText(status);
                }
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == openMenuItem) {
            JFileChooser fileChooser = new JFileChooser(Settings.getLastDir());
            fileChooser.addChoosableFileFilter(playlistFileFilter);
            fileChooser.addChoosableFileFilter(audioFileFilter);
            fileChooser.setMultiSelectionEnabled(false);
            if (fileChooser.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                FileFilter fileFilter = fileChooser.getFileFilter();
                if (fileFilter == playlistFileFilter) {
                    playlistManager.clearPlaylist();
                    acStop();
                    playlistManager.loadPlaylist(file.getPath());
                } else if (fileFilter == audioFileFilter) {
                    String fileName = file.getName().substring(0, file.getName().lastIndexOf(".")).trim();
                    PlaylistItem newPli = new PlaylistItem(fileName, file.getAbsolutePath(), -1, true);
                    playlistManager.add(newPli);
                    playlist.setCursor(playlist.indexOf(newPli));
                }
                acOpenAndPlay();
                Settings.setLastDir(file.getParent());
            }
        } else if (source == openURLMenuItem) {
            String url = JOptionPane.showInputDialog(mainFrame,
                    tr("Dialog.OpenURL"),
                    tr("Dialog.OpenURL.Message"),
                    JOptionPane.INFORMATION_MESSAGE);
            if (url != null && Utilities.startWithProtocol(url)) {
                boolean isPlaylistFile = false;
                for (String ext : PlaylistFileFilter.playlistExt) {
                    if (url.endsWith(ext)) {
                        isPlaylistFile = true;
                    }
                }
                if (isPlaylistFile) {
                    playlistManager.clearPlaylist();
                    playlistManager.loadPlaylist(url);
                    playlist.begin();
                } else {
                    PlaylistItem newPli = new PlaylistItem(url, url, -1, false);
                    playlistManager.add(newPli);
                    playlist.setCursor(playlist.indexOf(newPli));
                }
                acOpenAndPlay();
            }
        } else if (source == openPlaylistMenuItem) {
            playlistManager.openPlaylist();
        } else if (source == savePlaylistMenuItem) {
            playlistManager.savePlaylistDialog();
        } else if (source == preferencesMenuItem) {
            preferencesDialog = new PreferencesDialog(mainFrame, audioPlayer);
        } else if (source == exitMenuItem || source == exitTrayMenuItem) {
            exit();
        } else if (source == playPauseMenuItem || source == playPauseButton || source == playPauseTrayMenuItem) {
            acPlayPause();
        } else if (source == stopMenuItem || source == stopButton || source == stopTrayMenuItem) {
            acStop();
        } else if (source == previousMenuItem || source == previousButton || source == previousTrayMenuItem) {
            acPrevious();
        } else if (source == nextMenuItem || source == nextButton || source == nextTrayMenuItem) {
            acNext();
        } else if (source == randomizePlaylistMenuItem) {
            playlistManager.randomizePlaylist();
        } else if (source == playlistManagerMenuItem) {
            if (visualizationPanel.isVisible()) {
                CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
                audioPlayer.getDspAudioDataConsumer().remove(visualizationPanel);
                cardLayout.show(mainPanel, Utilities.PLAYLIST_MANAGER);
                playlistManagerMenuItem.setSelected(true);
                Settings.setLastView(Utilities.PLAYLIST_MANAGER);
            }
        } else if (source == visualizationMenuItem) {
            if (playlistManager.isVisible()) {
                CardLayout cardLayout = (CardLayout) (mainPanel.getLayout());
                audioPlayer.getDspAudioDataConsumer().add(visualizationPanel);
                cardLayout.show(mainPanel, Utilities.VISUALIZATION_PANEL);
                visualizationMenuItem.setSelected(true);
                Settings.setLastView(Utilities.VISUALIZATION_PANEL);
            }
        } else if (source == updateMenuItem) {
            SoftwareUpdate.checkForUpdates(true);
            SoftwareUpdate.showCheckForUpdatesDialog();
        } else if (source == aboutMenuItem) {
            Object[] options = {tr("Button.Close")};
            Desktop desktop = null;
            if (Desktop.isDesktopSupported()) {
                desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                    options = new Object[]{tr("Button.Close"), tr("Button.Website")};
                }
            }
            Version currentVersion = Version.getCurrentVersion();
            StringBuffer message = new StringBuffer();
            message.append("<html><b><font color='red' size='5'>" + tr("Application.title"));
            message.append("</font></b><br>" + tr("Application.description"));
            message.append("<br>Copyright © 2005-2009 The Xtreme Media Player Project");
            message.append("<br><br><b>" + tr("Dialog.About.Author") + ": </b>" + tr("Application.author"));
            message.append("<br><b>" + tr("Dialog.About.Version") + ": </b>" + currentVersion);
            message.append("<br><b>" + tr("Dialog.About.ReleaseDate") + ": </b>" + currentVersion.getReleaseDate());
            message.append("<br><b>" + tr("Dialog.About.Homepage") + ": </b>" + tr("Application.homepage"));
            message.append("<br><br><b>" + tr("Dialog.About.JavaVersion") + ": </b>" + System.getProperty("java.version"));
            message.append("<br><b>" + tr("Dialog.About.JavaVendor") + ": </b>" + System.getProperty("java.vendor"));
            message.append("<br><b>" + tr("Dialog.About.JavaHome") + ": </b>" + System.getProperty("java.home"));
            message.append("<br><b>" + tr("Dialog.About.OSName") + ": </b>" + System.getProperty("os.name"));
            message.append("<br><b>" + tr("Dialog.About.OSArch") + ": </b>" + System.getProperty("os.arch"));
            message.append("<br><b>" + tr("Dialog.About.UserName") + ": </b>" + System.getProperty("user.name"));
            message.append("<br><b>" + tr("Dialog.About.UserHome") + ": </b>" + System.getProperty("user.home"));
            message.append("<br><b>" + tr("Dialog.About.UserDir") + ": </b>" + System.getProperty("user.dir"));
            message.append("</html>");
            int n = JOptionPane.showOptionDialog(mainFrame, message, tr("Dialog.About"),
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                    Utilities.APP_256_ICON, options, options[0]);
            if (n == 1 && desktop != null) {
                try {
                    URL url = new URL(tr("Application.homepage"));
                    desktop.browse(url.toURI());
                } catch (Exception ex) {
                    logger.error(ex.getMessage(), ex);
                }
            }
        }
    }

    @Override
    public void playbackBuffering(PlaybackEvent pe) {
        setStatus(tr("MainFrame.StatusBar.Buffering"));
    }

    @Override
    public void playbackOpened(PlaybackEvent pe) {
        try {
            audioPlayer.setGain(Settings.getGain() / 100.0F);
            audioPlayer.setMuted(Settings.isMuted());
        } catch (PlayerException ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            if (currentPli != null && !currentPli.isFile()) {
                currentPli.loadTagInfo();
            }
            setStatus(currentPli.getFormattedDisplayName());
        }
    }

    @Override
    public void playbackEndOfMedia(PlaybackEvent pe) {
        acNext();
    }

    @Override
    public void playbackPlaying(PlaybackEvent pe) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                playPauseButton.setPauseIcon();
            }
        });
    }

    @Override
    public void playbackProgress(PlaybackEvent pe) {
        if (currentPli != null && !seekSlider.getValueIsAdjusting() && !seekSlider.isPressed() && mainFrame.isVisible()) {
            acUpdateTime(seekSlider.getOldValue() + Math.round(pe.getPosition() / 1000F));

            // Shoutcast stream title.
            Map properties = pe.getProperties();
            String streamTitleKey = "mp3.shoutcast.metadata.StreamTitle";
            if (!currentPli.isFile() && properties.containsKey(streamTitleKey)) {
                String streamTitle = ((String) properties.get(streamTitleKey)).trim();
                TagInfo tagInfo = currentPli.getTagInfo();
                if (!streamTitle.isEmpty() && (tagInfo != null)) {
                    String sTitle = " (" + tagInfo.getTitle() + ")";
                    if (!currentPli.getFormattedDisplayName().equals(streamTitle + sTitle)) {
                        currentPli.setFormattedDisplayName(streamTitle + sTitle);
                        EventQueue.invokeLater(new Runnable() {

                            @Override
                            public void run() {
                                playlistManager.refreshRow(playlist.indexOf(currentPli));
                                setStatus(currentPli.getFormattedDisplayName());
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public void playbackPaused(PlaybackEvent pe) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                playPauseButton.setPlayIcon();
            }
        });
    }

    @Override
    public void playbackStopped(PlaybackEvent pe) {
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                playPauseButton.setPlayIcon();
                acUpdateTime(0);
            }
        });
    }

    @Override
    public void acOpen() {
        PlayerLauncher playerLauncher = new PlayerLauncher(false);
        playerLauncher.execute();
    }

    @Override
    public void acOpenAndPlay() {
        PlayerLauncher playerLauncher = new PlayerLauncher(true);
        playerLauncher.execute();
    }

    @Override
    public void acPrevious() {
        if (!playlist.isEmpty()) {
            playlist.previousCursor();
            acOpenAndPlay();
        }
    }

    @Override
    public void acNext() {
        if (!playlist.isEmpty()) {
            playlist.nextCursor();
            acOpenAndPlay();
        }
    }

    @Override
    public void acPlayPause() {
        try {
            if (playlist.isEmpty()) {
                if ((audioPlayer.getState() == AudioSystem.NOT_SPECIFIED) || (audioPlayer.getState() == AudioPlayer.STOP)) {
                    playlistManager.addFilesDialog();
                }
            } else {
                if ((audioPlayer.getState() != AudioPlayer.PLAY) && (audioPlayer.getState() != AudioPlayer.PAUSE) && (playlist.getCursorPosition() == -1)) {
                    playlist.begin();
                }
            }
            switch (audioPlayer.getState()) {
                case AudioPlayer.INIT:
                    audioPlayer.play();
                    break;
                case AudioPlayer.PLAY:
                    audioPlayer.pause();
                    break;
                case AudioPlayer.PAUSE:
                    audioPlayer.play();
                    break;
                default:
                    acOpenAndPlay();
                    break;
            }
        } catch (PlayerException ex) {
            logger.error(ex.getMessage());
//                String msg = "<html><b>An exeption was generated:</b><br><br>" + ex.getMessage() + "<html>";
//                JOptionPane.showMessageDialog(mainFrame, msg, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void acStop() {
        audioPlayer.stop();
    }

    @Override
    public void acUpdateTime(int value) {
        String timeText = Utilities.ZERO_TIMER;
        if (currentPli != null) {
            String formattedLength = currentPli.getFormattedLength();
            if (Utilities.isNullOrEmpty(formattedLength)) {
                timeText = currentPli.getFormattedLength(Math.round(value / 1000f));
            } else {
                timeText = currentPli.getFormattedLength(Math.round(value / 1000f)) + " / " + formattedLength.trim();
            }
        }
        setTime(timeText, (currentPli == null) ? 0 : value);
    }

    @Override
    public void acSeek() {
        try {
            if (audioPlayer != null && seekSlider.isEnabled()) {
                audioPlayer.seek(Math.round(audioPlayer.getByteLength() * (double) seekSlider.getValue() / seekSlider.getMaximum()));
            }
        } catch (PlayerException ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    @Override
    public void acDisable() {
        if (EventQueue.isDispatchThread()) {
            seekSlider.setEnabled(false);
            statusLabel.setText("");
        } else {
            EventQueue.invokeLater(new Runnable() {

                @Override
                public void run() {
                    seekSlider.setEnabled(false);
                    statusLabel.setText("");
                }
            });
        }
    }

    @Override
    public void onIntellitype(int command) {
        switch (command) {
            case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
                acPlayPause();
                break;
            case JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK:
                acPrevious();
                break;
            case JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK:
                acNext();
                break;
            case JIntellitype.APPCOMMAND_MEDIA_STOP:
                acStop();
                break;
        }
    }

    private class PlayerLauncher extends AbstractSwingWorker<Boolean, Void> {

        private boolean play = false;
        private boolean isFile = false;
        private int duration = -1;

        public PlayerLauncher(boolean play) {
            this.play = play;
        }

        @Override
        protected Boolean doInBackground() throws PlayerException, MalformedURLException {
            PlaylistItem pli = playlist.getCursor();
            if (pli != null) {
                currentPli = pli;
                isFile = pli.isFile();
                if (isFile) {
                    audioPlayer.open(new File(pli.getLocation()));
                    duration = Math.round(audioPlayer.getDuration() / 1000);
                } else {
                    audioPlayer.open(new URL(pli.getLocation()));
                }
                if (play) {
                    audioPlayer.play();
                }
                return Boolean.TRUE;
            }
            currentPli = null;
            return Boolean.FALSE;
        }

        @Override
        protected void done() {
            if (currentPli != null) {
                try {
                    if (get()) {
                        seekSlider.reset();
                        playlistManager.colorizeRow();
                        if (isFile) {
                            if (duration > 0) {
                                seekSlider.setMaximum(duration);
                                seekSlider.setEnabled(true);
                            } else {
                                seekSlider.setMaximum((int) (currentPli.getDuration() * 1000));
                                seekSlider.setEnabled(false);
                            }
                        } else {
                            seekSlider.setEnabled(false);
                        }
                    }
                } catch (Exception ex) {
                    if (ex.getCause() instanceof PlayerException) {
                        acStop();
                        setStatus("An error occurred...");
                        logger.error(ex.getMessage(), ex);
                        if (ex.getCause().getCause() instanceof FileNotFoundException) {
                            String msg = "<html><b>" + currentPli.getFormattedDisplayName() + "</b> could not be used<br>because the original file could not be found.<html>";
                            JOptionPane.showMessageDialog(mainFrame, msg, "Message", JOptionPane.ERROR_MESSAGE);
                        }
//                    String msg = "<html><b>An exeption was generated:</b><br><br>" + ex.getMessage() + "<html>";
//                    JOptionPane.showMessageDialog(mainFrame, msg, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        }
    }
}
