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

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;
import xtrememp.player.audio.AudioPlayer;
import xtrememp.util.LanguageBundle;
import xtrememp.util.Utilities;

/**
 *
 * @author Besmir Beqiri
 */
public class PreferencesDialog extends JDialog implements ActionListener {

    private final String GENERAL_PANEL = "GENERAL_PANEL";
    private final String AUDIO_PANEL = "AUDIO_PANEL";
    private final String HOTKEYS_PANEL = "HOTKEYS_PANEL";
    private AudioPlayer audioPlayer;
    private CardLayout cardLayout;
    private JPanel centerPanel;
    private JToggleButton generalButton;
    private JToggleButton audioButton;
    private JToggleButton hotkeysButton;
    private JComboBox languageComboBox;
    private JComboBox skinComboBox;
//    private JCheckBox instanceCheckBox;
//    private JCheckBox enqueueCheckBox;
    private JCheckBox updatesCheckBox;
    private JTextField cacheDirTextField;
    private JComboBox mixerComboBox;
    private JButton changeCacheDirButton;
    private JButton resetButton;
    private JButton closeButton;

    public PreferencesDialog(JFrame mainFrame, AudioPlayer audioPlayer) {
        super(mainFrame, true);
        this.audioPlayer = audioPlayer;
        setLayout(new MigLayout("fill"));
        setTitle(LanguageBundle.getString("Dialog.Preferences"));
        initComponents();
        Utilities.closeOnEscape(this);

//        setSize(500, 500);
        pack();
        setResizable(false);
        setLocationRelativeTo(mainFrame);
        getRootPane().setDefaultButton(closeButton);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == generalButton) {
            cardLayout.show(centerPanel, GENERAL_PANEL);
        } else if (source == audioButton) {
            cardLayout.show(centerPanel, AUDIO_PANEL);
        } else if (source == hotkeysButton) {
            cardLayout.show(centerPanel, HOTKEYS_PANEL);
        } else if (source == updatesCheckBox) {
            Settings.setAutomaticCheckForUpdatesEnabled(updatesCheckBox.isSelected());
        } else if (source == changeCacheDirButton) {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
            fileChooser.setDialogTitle(LanguageBundle.getString("Dialog.Preferences.General.CacheDirectory.SelectCacheDir"));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            while (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File cacheDir = fileChooser.getSelectedFile();
                if (cacheDir.canRead() && cacheDir.canWrite()) {
                    Settings.setCacheDir(cacheDir);
                    cacheDirTextField.setText(Settings.getCacheDir().getPath());
                    break;
                } else {
                    Object[] options = {LanguageBundle.getString("Button.Close")};
                    JOptionPane.showOptionDialog(XtremeMP.getInstance().getMainFrame(),
                            LanguageBundle.getString("Dialog.Preferences.General.CacheDirectory.NoReadWritePermissions"),
                            LanguageBundle.getString("Dialog.OptionDialog.Warning"),
                            JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                }
            }
        } else if (source == mixerComboBox) {
            String mixerName = (String) mixerComboBox.getSelectedItem();
            audioPlayer.setMixerName(mixerName);
            Settings.setMixerName(mixerName);
        } else if (source == resetButton) {
        } else if (source == closeButton) {
            dispose();
        }
    }

    private void initComponents() {
        Container container = getContentPane();

        // north panel
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(Box.createHorizontalGlue());

        generalButton = new JToggleButton(LanguageBundle.getString("Dialog.Preferences.General"), Utilities.getIcon("package_settings.png"));
        generalButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        generalButton.setHorizontalTextPosition(AbstractButton.CENTER);
        generalButton.setMnemonic(KeyEvent.VK_G);
        generalButton.setSelected(true);
        generalButton.addActionListener(this);
        toolBar.add(generalButton);

        audioButton = new JToggleButton(LanguageBundle.getString("Dialog.Preferences.Audio"), Utilities.getIcon("audio_control.png"));
        audioButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        audioButton.setHorizontalTextPosition(AbstractButton.CENTER);
        audioButton.setMnemonic(KeyEvent.VK_A);
        audioButton.addActionListener(this);
        toolBar.add(audioButton);

//        hotkeysButton = new JToggleButton(LanguageBundle.getString("Dialog.Preferences.Hotkeys"), Utilities.getIcon("key_bindings.png"));
//        hotkeysButton.setVerticalTextPosition(AbstractButton.BOTTOM);
//        hotkeysButton.setHorizontalTextPosition(AbstractButton.CENTER);
//        hotkeysButton.setMnemonic(KeyEvent.VK_H);
//        hotkeysButton.addActionListener(this);
//        toolBar.add(hotkeysButton);

        ButtonGroup bg = new ButtonGroup();
        bg.add(generalButton);
        bg.add(audioButton);
        bg.add(hotkeysButton);

        toolBar.add(Box.createHorizontalGlue());
        container.add(toolBar, "north");

        // center panel
        cardLayout = new CardLayout();
        centerPanel = new JPanel(cardLayout);
        // general panel
        JPanel generalPanel = new JPanel(new MigLayout("ins 8,fillx"));
        addTextSeparator(generalPanel, LanguageBundle.getString("Dialog.Preferences.General.Interface"));
        generalPanel.add(new JLabel("Language", SwingConstants.LEADING));
        languageComboBox = new JComboBox(Utilities.getSupportedLanguages());
        generalPanel.add(languageComboBox, "span,growx");
        generalPanel.add(new JLabel("Skin", SwingConstants.LEADING));
        skinComboBox = new JComboBox(Utilities.getSkins());
        generalPanel.add(skinComboBox, "span,growx");
//        addTextSeparator(generalPanel, LanguageBundle.getString("Dialog.Preferences.General.Instances"));
//        instanceCheckBox = new JCheckBox("Allow only one instance");
//        generalPanel.add(instanceCheckBox, "span,growx");
//        enqueueCheckBox = new JCheckBox("Enqueue files in playlist when in one instance mode");
//        generalPanel.add(enqueueCheckBox, "span,growx");
        addTextSeparator(generalPanel, LanguageBundle.getString("Dialog.Preferences.General.Updates"));
        updatesCheckBox = new JCheckBox("Check for updates at startup");
        updatesCheckBox.setSelected(Settings.isAutomaticCheckForUpdatesEnabled());
        updatesCheckBox.addActionListener(this);
        generalPanel.add(updatesCheckBox, "span,growx");
        addTextSeparator(generalPanel, LanguageBundle.getString("Dialog.Preferences.General.CacheDirectory"));
        cacheDirTextField = new JTextField(Settings.getCacheDir().getPath());
        generalPanel.add(cacheDirTextField, "span,growx,split");
        changeCacheDirButton = new JButton(LanguageBundle.getString("Button.Change"));
        changeCacheDirButton.addActionListener(this);
        generalPanel.add(changeCacheDirButton, "wrap");
        centerPanel.add(generalPanel, GENERAL_PANEL);
        // audio panel
        JPanel audioPanel = new JPanel(new MigLayout("ins 8,fillx"));
        addTextSeparator(audioPanel, "Playback Devices");
        audioPanel.add(new JLabel("Mixer", SwingConstants.LEADING));
        mixerComboBox = new JComboBox(audioPlayer.getMixers().toArray());
        mixerComboBox.setSelectedItem(Settings.getMixerName());
        mixerComboBox.addActionListener(this);
        audioPanel.add(mixerComboBox, "span,growx");
        centerPanel.add(audioPanel, AUDIO_PANEL);
        // hotkeys panel
        JPanel hotkeysPanel = new JPanel(new MigLayout("ins 8,fillx"));
        centerPanel.add(hotkeysPanel, HOTKEYS_PANEL);
        container.add(centerPanel, "center");

        // south panel
        JPanel southPanel = new JPanel(new MigLayout("nogrid, fillx, aligny 100%, gapy unrel"));
        closeButton = new JButton(LanguageBundle.getString("Button.Close"));
        closeButton.addActionListener(this);
        southPanel.add(closeButton, "tag cancel");
//        resetButton = new JButton(LanguageBundle.getString("Button.Reset"));
//        resetButton.addActionListener(this);
//        southPanel.add(resetButton, "tag help2");
        container.add(southPanel, "south");
    }

    private void addTextSeparator(JPanel panel, String text) {
        JLabel label = new JLabel(text, SwingConstants.LEADING);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        panel.add(label, "gapbottom 1, span, split 2, aligny center");
        panel.add(new JSeparator(), "gapleft rel, growx");
    }
}
