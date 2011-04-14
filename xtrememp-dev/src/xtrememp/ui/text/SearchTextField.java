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
package xtrememp.ui.text;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Map;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import static xtrememp.util.Utilities.tr;

/**
 *
 * @author Besmir Beqiri
 */
public class SearchTextField extends JPanel {

    private final char faChar = 'S';
    private final String searchText = tr("MainFrame.PlaylistManager.Filter") + "  (Alt+" + faChar + ")";
    private JTextField textField;

    public SearchTextField(int columns) {
        super(new BorderLayout());
        textField = new PromptTextField(searchText, columns);
        textField.setBackground(new Color(0, 0, 0, 64));
        textField.setFont(getFont().deriveFont(Font.BOLD));
        textField.setFocusAccelerator(faChar);
        this.add(textField, BorderLayout.CENTER);
    }

    public JTextField getTextField() {
        return textField;
    }

    private class PromptTextField extends JTextField {

        private String promptText;
        private Font promptFont;
        private Insets insets;
        private Map desktopHints;

        public PromptTextField(String promptText) {
            this(promptText, 0);
        }

        public PromptTextField(int columns) {
            this(null, columns);
        }

        public PromptTextField(String promptText, int columns) {
            super(columns);

            this.promptText = promptText;
            this.promptFont = getFont().deriveFont(Font.BOLD | Font.ITALIC);
            this.insets = new Insets(3, 5, 3, 5);
            Toolkit tk = Toolkit.getDefaultToolkit();
            this.desktopHints = (Map) (tk.getDesktopProperty("awt.font.desktophints"));

            addKeyListener(new KeyAdapter() {

                @Override
                public void keyPressed(KeyEvent evt) {
                    if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        setText("");
                    }
                }
            });
        }

        @Override
        public Insets getInsets() {
            return insets;
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (promptText != null && !isFocusOwner() && getText().isEmpty()) {
                Graphics2D g2d = (Graphics2D) g.create();

                if (desktopHints != null) {
                    g2d.addRenderingHints(desktopHints);
                }

                if (SubstanceLookAndFeel.isCurrentLookAndFeel()) {
                    SubstanceColorScheme enabledColorScheme = SubstanceLookAndFeel.getCurrentSkin().getEnabledColorScheme(DecorationAreaType.TOOLBAR);
                    SubstanceColorScheme activeColorScheme = SubstanceLookAndFeel.getCurrentSkin().getActiveColorScheme(DecorationAreaType.TOOLBAR);
                    SubstanceColorScheme colorScheme = enabledColorScheme.isDark() ? enabledColorScheme : activeColorScheme;
                    g2d.setColor(colorScheme.getForegroundColor());
                } else {
                    g2d.setColor(getDisabledTextColor());
                }

                g2d.setFont(promptFont);
                g2d.drawString(promptText, insets.left, getHeight() - (insets.top + insets.bottom));
                g2d.dispose();
            }
        }
    }
}
