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
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.util.Utilities;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 */
public class EqualizerDialog extends JDialog implements ActionListener {

    private final Logger logger = LoggerFactory.getLogger(EqualizerDialog.class);
    private String[] sliderLabels = {"60", "170", "310", "600", "1K", "3K", "6K", "12K", "14K", "16K"};
    private String[] presetsLabels = {"Normal", "Classical", "Club", "Dance", "Full Bass", "Full Bass & Treble", "Full Treble", "Laptop", "Live", "Party", "Pop", "Reggae", "Rock", "Techno"};
    private int[] gainValue = {50, 50, 50, 50, 50, 50, 50, 50, 50, 50, 50};
    private int[] PRESET_NORMAL = {50, 50, 50, 50, 50, 50, 50, 50, 50, 50};
    private int[] PRESET_CLASSICAL = {50, 50, 50, 50, 50, 50, 70, 70, 70, 76};
    private int[] PRESET_CLUB = {50, 50, 42, 34, 34, 34, 42, 50, 50, 50};
    private int[] PRESET_DANCE = {26, 34, 46, 50, 50, 66, 70, 70, 50, 50};
    private int[] PRESET_FULLBASS = {26, 26, 26, 36, 46, 62, 76, 78, 78, 78};
    private int[] PRESET_FULLBASSTREBLE = {34, 34, 50, 68, 62, 46, 28, 22, 18, 18};
    private int[] PRESET_FULLTREBLE = {78, 78, 78, 62, 42, 24, 8, 8, 8, 8};
    private int[] PRESET_LAPTOP = {38, 22, 36, 60, 58, 46, 38, 24, 16, 14};
    private int[] PRESET_LIVE = {66, 50, 40, 36, 34, 34, 40, 42, 42, 42};
    private int[] PRESET_PARTY = {32, 32, 50, 50, 50, 50, 50, 50, 32, 32};
    private int[] PRESET_POP = {56, 38, 32, 30, 38, 54, 56, 56, 54, 54};
    private int[] PRESET_REGGAE = {48, 48, 50, 66, 48, 34, 34, 48, 48, 48};
    private int[] PRESET_ROCK = {32, 38, 64, 72, 56, 40, 28, 24, 24, 24};
    private int[] PRESET_TECHNO = {30, 34, 48, 66, 64, 48, 30, 24, 24, 28};
    private Map<String, int[]> presetMap;
    public static final int LINEARDIST = 1;
    public static final int OVERDIST = 2;
    private float[] bands = null;
    private int[] eqgains = null;
    private int eqdist = OVERDIST;
    private JCheckBox activateCheckBox;
    private JComboBox presetComboBox;
    private JSlider panSlider;
    private JSlider[] sliders;

    public EqualizerDialog(JFrame mainFrame) {
        super(mainFrame, false);
        setLayout(new BorderLayout());
        setTitle(tr("Equalizer"));
        initComponents();
        Utilities.closeOnEscape(this);

        pack();
        setResizable(false);
        setLocationRelativeTo(mainFrame);
        setVisible(true);
    }

    private void initComponents() {
        Container container = getContentPane();

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        activateCheckBox = new JCheckBox("Activate");
        toolBar.add(activateCheckBox);
        toolBar.addSeparator();
        JPanel panPanel = new JPanel(new BorderLayout());
        panSlider = new JSlider(-100, 100, 0);
        panPanel.add(new JLabel("L"), BorderLayout.WEST);
        panPanel.add(panSlider, BorderLayout.CENTER);
        panPanel.add(new JLabel("R"), BorderLayout.EAST);
        toolBar.add(panPanel);
        toolBar.addSeparator();
        presetMap = new HashMap<String, int[]>();
        presetMap.put(presetsLabels[0], PRESET_NORMAL);
        presetMap.put(presetsLabels[1], PRESET_CLASSICAL);
        presetMap.put(presetsLabels[2], PRESET_CLUB);
        presetMap.put(presetsLabels[3], PRESET_DANCE);
        presetMap.put(presetsLabels[4], PRESET_FULLBASS);
        presetMap.put(presetsLabels[5], PRESET_FULLBASSTREBLE);
        presetMap.put(presetsLabels[6], PRESET_FULLTREBLE);
        presetMap.put(presetsLabels[7], PRESET_LAPTOP);
        presetMap.put(presetsLabels[8], PRESET_LIVE);
        presetMap.put(presetsLabels[9], PRESET_PARTY);
        presetMap.put(presetsLabels[10], PRESET_POP);
        presetMap.put(presetsLabels[11], PRESET_REGGAE);
        presetMap.put(presetsLabels[12], PRESET_ROCK);
        presetMap.put(presetsLabels[13], PRESET_TECHNO);
        presetComboBox = new JComboBox(presetsLabels);
        presetComboBox.setSelectedIndex(Settings.getEqualizerPresetIndex());
        presetComboBox.addActionListener(this);
        toolBar.add(presetComboBox);
        container.add(toolBar, BorderLayout.NORTH);

        sliders = new JSlider[10];
        int sLength = sliders.length;
        JPanel centerPanel = new JPanel(new GridLayout(0, sLength));
        sliders = new JSlider[10];
        for (int i = 0; i < sLength; i++) {
            JPanel sliderPanel = new JPanel(new BorderLayout());
            sliders[i] = new JSlider(SwingConstants.VERTICAL, 0, 100, 50);
            sliders[i].setMajorTickSpacing(50);
            sliders[i].setMinorTickSpacing(5);
            sliders[i].setPaintTicks(true);
            sliderPanel.add(sliders[i], BorderLayout.CENTER);
            JLabel sliderLabel = new JLabel(sliderLabels[i]);
            sliderLabel.setHorizontalAlignment(JLabel.CENTER);
            sliderPanel.add(sliderLabel, BorderLayout.SOUTH);
            centerPanel.add(sliderPanel);
        }
        container.add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * Set bands array for equalizer.
     *
     * @param bands
     */
    public void setBands(float[] bands) {
        this.bands = bands;
    }

    /**
     * Update sliders from gains array.
     *
     * @param gains
     */
    public void updateSliders(int[] gains) {
        if (gains != null) {
            for (int i = 0, len = gains.length; i < len; i++) {
                gainValue[i + 1] = gains[i];
                sliders[i].setValue(gainValue[i + 1]);
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();

        if (source == activateCheckBox) {
        } else if (source == presetComboBox) {
            Settings.setEqualizerPresetIndex(presetComboBox.getSelectedIndex());
            String currentPreset = String.valueOf(presetComboBox.getSelectedItem());
            updateSliders(presetMap.get(currentPreset));
            logger.info("Equilazer: new preset selected: {}", currentPreset);
        }
    }
}
