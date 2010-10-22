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

import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Locale;
import javax.swing.AbstractButton;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
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
import xtrememp.ui.combobox.IconComboBox;
import xtrememp.ui.combobox.SkinComboSelector;
import xtrememp.ui.skin.GFXUIListener;
import xtrememp.util.Utilities;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 */
public class PreferencesDialog extends JDialog implements ActionListener {

    private final String GENERAL_PANEL = "GENERAL_PANEL";
    private final String AUDIO_PANEL = "AUDIO_PANEL";
    private final String HOTKEYS_PANEL = "HOTKEYS_PANEL";
    private AudioPlayer audioPlayer;
    private GFXUIListener gfxUIListener;
    private CardLayout cardLayout;
    private JPanel centerPanel;
    private JToggleButton generalButton;
    private JToggleButton audioButton;
    private JToggleButton hotkeysButton;
    private JComboBox languageComboBox;
    private SkinComboSelector skinComboSelector;
    private JCheckBox uiEffectsCheckbox;
//    private JCheckBox instanceCheckBox;
//    private JCheckBox enqueueCheckBox;
    private JCheckBox updatesCheckBox;
    private JTextField cacheDirTextField;
    private JComboBox mixerComboBox;
    private JButton changeCacheDirButton;
    private JButton resetButton;
    private JButton closeButton;

    public PreferencesDialog(AudioPlayer audioPlayer, GFXUIListener gfxUIListener) {
        super(XtremeMP.getInstance().getMainFrame(), true);
        this.audioPlayer = audioPlayer;
        this.gfxUIListener = gfxUIListener;
        setLayout(new MigLayout("fill"));
        setTitle(tr("Dialog.Preferences"));
        initComponents();
        Utilities.closeOnEscape(this);

        setResizable(false);
        pack();
        setLocationRelativeTo(getParent());
        getRootPane().setDefaultButton(closeButton);
        closeButton.requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source.equals(generalButton)) {
            cardLayout.show(centerPanel, GENERAL_PANEL);
        } else if (source.equals(audioButton)) {
            cardLayout.show(centerPanel, AUDIO_PANEL);
        } else if (source.equals(hotkeysButton)) {
            cardLayout.show(centerPanel, HOTKEYS_PANEL);
        } else if (source.equals(languageComboBox)) {
            Settings.setLanguageIndex(languageComboBox.getSelectedIndex());
        } else if (source.equals(uiEffectsCheckbox)) {
            Settings.setUIEffectsEnabled(uiEffectsCheckbox.isSelected());
            gfxUIListener.guiEffectsStateChanged(uiEffectsCheckbox.isSelected());
        } else if (source.equals(updatesCheckBox)) {
            Settings.setAutomaticUpdatesEnabled(updatesCheckBox.isSelected());
        } else if (source.equals(changeCacheDirButton)) {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.home"));
            fileChooser.setDialogTitle(tr("Dialog.Preferences.General.CacheDirectory.SelectCacheDir"));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            while (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File cacheDir = fileChooser.getSelectedFile();
                if (cacheDir.canRead() && cacheDir.canWrite()) {
                    Settings.setCacheDir(cacheDir);
                    cacheDirTextField.setText(Settings.getCacheDir().getPath());
                    break;
                } else {
                    Object[] options = {tr("Button.Close")};
                    JOptionPane.showOptionDialog(this.getParent(),
                            tr("Dialog.Preferences.General.CacheDirectory.NoReadWritePermissions"),
                            tr("Dialog.OptionDialog.Warning"),
                            JOptionPane.OK_OPTION, JOptionPane.WARNING_MESSAGE, null, options, options[0]);
                }
            }
        } else if (source.equals(mixerComboBox)) {
            String mixerName = (String) mixerComboBox.getSelectedItem();
            audioPlayer.setMixerName(mixerName);
            Settings.setMixerName(mixerName);
        } else if (source.equals(resetButton)) {
        } else if (source.equals(closeButton)) {
            dispose();
        }
    }

    private void initComponents() {
        Container container = getContentPane();

        // north panel
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(Box.createHorizontalGlue());

        generalButton = new JToggleButton(tr("Dialog.Preferences.General"), Utilities.PREFERENCES_SYSTEM_ICON);
        generalButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        generalButton.setHorizontalTextPosition(AbstractButton.CENTER);
        generalButton.setMnemonic(KeyEvent.VK_G);
        generalButton.setSelected(true);
        generalButton.addActionListener(this);
        toolBar.add(generalButton);

        audioButton = new JToggleButton(tr("Dialog.Preferences.Audio"), Utilities.AUDIO_CARD_ICON);
        audioButton.setVerticalTextPosition(AbstractButton.BOTTOM);
        audioButton.setHorizontalTextPosition(AbstractButton.CENTER);
        audioButton.setMnemonic(KeyEvent.VK_A);
        audioButton.addActionListener(this);
        toolBar.add(audioButton);

//        hotkeysButton = new JToggleButton(tr("Dialog.Preferences.Hotkeys"), Utilities.getIcon("key_bindings.png"));
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
        addTextSeparator(generalPanel, tr("Dialog.Preferences.General.Interface"));
        generalPanel.add(new JLabel(tr("Dialog.Preferences.General.Interface.Language"), SwingConstants.LEADING));
        languageComboBox = new IconComboBox<Locale>(Utilities.getLanguages()) {

            @Override
            public String getCaption(Locale item) {
                if (item.equals(Utilities.getSystemLocale())) {
                    return "Auto";
                } else {
                    String dl = item.getDisplayLanguage(item);
                    String dl2Prefix = dl.substring(0, 1).toUpperCase();
                    String dl2Suffix = dl.substring(1, dl.length()).toLowerCase();
                    String dl2 = dl2Prefix.concat(dl2Suffix);
                    return dl2;
                }
            }

            @Override
            public Icon getItemIcon(Locale item) {
                if (!item.equals(Utilities.getSystemLocale())) {
                    return Utilities.getIcon("flags/" + item.getLanguage() + ".png");
                }
                return null;
            }
        };
        languageComboBox.setSelectedIndex(Settings.getLanguageIndex());
        languageComboBox.addActionListener(this);
        generalPanel.add(languageComboBox, "span,growx");
        generalPanel.add(new JLabel(tr("Dialog.Preferences.General.Interface.Skin"), SwingConstants.LEADING));
        skinComboSelector = new SkinComboSelector();
        generalPanel.add(skinComboSelector, "span,growx");
        generalPanel.add(new JLabel(tr("Dialog.Preferences.General.Interface.UIEffects"), SwingConstants.LEADING));
        uiEffectsCheckbox = new JCheckBox();
        uiEffectsCheckbox.setSelected(Settings.isUIEffectsEnabled());
        uiEffectsCheckbox.addActionListener(this);
        generalPanel.add(uiEffectsCheckbox, "span,growx");
//        addTextSeparator(generalPanel, tr("Dialog.Preferences.General.Instances"));
//        instanceCheckBox = new JCheckBox("Allow only one instance");
//        generalPanel.add(instanceCheckBox, "span,growx");
//        enqueueCheckBox = new JCheckBox("Enqueue files in playlist when in one instance mode");
//        generalPanel.add(enqueueCheckBox, "span,growx");
        addTextSeparator(generalPanel, tr("Dialog.Preferences.General.Updates"));
        updatesCheckBox = new JCheckBox(tr("Dialog.Preferences.General.Updates.CheckForUpdatesOnStartup"));
        updatesCheckBox.setSelected(Settings.isAutomaticUpdatesEnabled());
        updatesCheckBox.addActionListener(this);
        generalPanel.add(updatesCheckBox, "span,growx");
        addTextSeparator(generalPanel, tr("Dialog.Preferences.General.CacheDirectory"));
        cacheDirTextField = new JTextField(Settings.getCacheDir().getPath());
        generalPanel.add(cacheDirTextField, "span,growx,split");
        changeCacheDirButton = new JButton(tr("Button.Change"));
        changeCacheDirButton.addActionListener(this);
        generalPanel.add(changeCacheDirButton, "wrap");
        centerPanel.add(generalPanel, GENERAL_PANEL);
        // audio panel
        JPanel audioPanel = new JPanel(new MigLayout("ins 8,fillx"));
        addTextSeparator(audioPanel, tr("Dialog.Preferences.Audio.PlaybackDevice"));
        audioPanel.add(new JLabel(tr("Dialog.Preferences.Audio.PlaybackDevice.Mixer"), SwingConstants.LEADING));
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
        closeButton = new JButton(tr("Button.Close"));
        closeButton.addActionListener(this);
        southPanel.add(closeButton, "tag cancel");
//        resetButton = new JButton(tr("Button.Reset"));
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
