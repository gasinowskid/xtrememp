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
package xtrememp.skin;

import java.awt.Color;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceColorSchemeBundle;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.colorscheme.BaseDarkColorScheme;
import org.jvnet.substance.colorscheme.DarkGrayColorScheme;
import org.jvnet.substance.colorscheme.OrangeColorScheme;
import org.jvnet.substance.painter.border.StandardBorderPainter;
import org.jvnet.substance.painter.border.GlassBorderPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.decoration.MatteDecorationPainter;
import org.jvnet.substance.painter.gradient.GlassGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.StandardButtonShaper;
import org.jvnet.substance.watermark.SubstanceNullWatermark;

/**
 * Thanks to Kirill Grouchnikov for the changes and documentation applied to this class.
 * @author Besmir Beqiri
 */
public class XtremeDarkSapphireSkin extends SubstanceSkin {

    /**
     * Display name for <code>this</code> skin.
     */
    public static final String NAME = "Xtreme Dark Sapphire";

    /**
     * Color scheme for active visual state.
     * 
     * Extend BaseDarkColorScheme to mark this color scheme as dark.
     * Alternatively, extend SubstanceColorScheme and override the isDark()
     * method.
     */
    public static class DefaultColorScheme extends BaseDarkColorScheme {

        /**
         * The main ultra-light color.
         */
        private static final Color ultraLightColor = new Color(160, 160, 160);
        /**
         * The main extra-light color.
         */
        private static final Color extraLightColor = new Color(60, 60, 60);
        /**
         * The main light color.
         */
        private static final Color lightColor = new Color(45, 45, 45);
        /**
         * The main medium color.
         */
        private static final Color midColor = new Color(30, 30, 30);
        /**
         * The main dark color.
         */
        private static final Color darkColor = new Color(10, 10, 10);
        /**
         * The main ultra-dark color.
         */
        private static final Color ultraDarkColor = new Color(5, 5, 5);
        /**
         * The foreground color.
         */
        private static final Color foregroundColor = Color.white;

        public DefaultColorScheme() {
            super("XtremeMP Default");
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.substance.color.ColorScheme#getForegroundColor()
         */
        @Override
        public Color getForegroundColor() {
            return foregroundColor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.substance.color.ColorScheme#getUltraLightColor()
         */
        @Override
        public Color getUltraLightColor() {
            return ultraLightColor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.substance.color.ColorScheme#getExtraLightColor()
         */
        @Override
        public Color getExtraLightColor() {
            return extraLightColor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.substance.color.ColorScheme#getLightColor()
         */
        @Override
        public Color getLightColor() {
            return lightColor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.substance.color.ColorScheme#getMidColor()
         */
        @Override
        public Color getMidColor() {
            return midColor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.substance.color.ColorScheme#getDarkColor()
         */
        @Override
        public Color getDarkColor() {
            return darkColor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jvnet.substance.color.ColorScheme#getUltraDarkColor()
         */
        @Override
        public Color getUltraDarkColor() {
            return ultraDarkColor;
        }
    }

    /**
     * Creates a new skin.
     */
    public XtremeDarkSapphireSkin() {
        SubstanceColorScheme activeScheme = new OrangeColorScheme().invert();
        SubstanceColorScheme defaultScheme = new DefaultColorScheme();
        SubstanceColorScheme disabledScheme = new DarkGrayColorScheme();

        // The default color scheme bundle
        SubstanceColorSchemeBundle defaultBundle = new SubstanceColorSchemeBundle(
                activeScheme, defaultScheme, disabledScheme);
        this.registerDecorationAreaSchemeBundle(defaultBundle,
                DecorationAreaType.NONE);

        // the color scheme bundle for header area. Uses the same
        // color scheme for default and active states.
        SubstanceColorSchemeBundle headerBundle = new SubstanceColorSchemeBundle(
                activeScheme, activeScheme, disabledScheme);

        // Matte decoration painter has been tweaked a little in
        // version 5.0 to use darker colors. The second parameter is the
        // background scheme to use. It tries to recreate a lighter and
        // more vibrant look to XtremeMP header area under version 4.3
        this.registerDecorationAreaSchemeBundle(headerBundle, activeScheme.tint(0.15).saturate(0.85),
                DecorationAreaType.PRIMARY_TITLE_PANE,
                DecorationAreaType.SECONDARY_TITLE_PANE,
                DecorationAreaType.HEADER, DecorationAreaType.FOOTER,
                DecorationAreaType.TOOLBAR);

        // Mark GENERAL as a registered decoration area. This will make
        // the status bar to be considered a decoration area. Note that now
        // the separator and resize grip (in the status bar) are painted
        // with correct blacker shades, since the default scheme is black
        // and not orange invert.
        this.registerAsDecorationArea(defaultScheme, DecorationAreaType.GENERAL);

        this.buttonShaper = new StandardButtonShaper();
        this.borderPainter = new StandardBorderPainter();
        this.watermark = new SubstanceNullWatermark();
        this.gradientPainter = new GlassGradientPainter();
        this.borderPainter = new GlassBorderPainter();
        this.decorationPainter = new MatteDecorationPainter();
//        this.decorationPainter = new ArcDecorationPainter();
        this.highlightPainter = new ClassicHighlightPainter();
//        this.highlightPainter = new GlassHighlightPainter();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jvnet.substance.skin.SubstanceSkin#getDisplayName()
     */
    @Override
    public String getDisplayName() {
        return NAME;
    }
}
