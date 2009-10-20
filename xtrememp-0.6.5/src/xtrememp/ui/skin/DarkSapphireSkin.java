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
package xtrememp.ui.skin;

import org.jvnet.substance.api.ComponentState;
import org.jvnet.substance.api.SubstanceColorScheme;
import org.jvnet.substance.api.SubstanceColorSchemeBundle;
import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.painter.border.GlassBorderPainter;
import org.jvnet.substance.painter.border.StandardBorderPainter;
import org.jvnet.substance.painter.decoration.DecorationAreaType;
import org.jvnet.substance.painter.decoration.MatteDecorationPainter;
import org.jvnet.substance.painter.gradient.GlassGradientPainter;
import org.jvnet.substance.painter.highlight.ClassicHighlightPainter;
import org.jvnet.substance.shaper.ClassicButtonShaper;
import org.jvnet.substance.watermark.SubstanceNullWatermark;

/**
 * Thanks to Kirill Grouchnikov (Substance author) for the changes and documentation applied to this class.
 * @author Besmir Beqiri
 */
public class DarkSapphireSkin extends SubstanceSkin {

    /**
     * Display name for <code>this</code> skin.
     */
    public static final String NAME = "Dark Sapphire";

    /**
     * Creates a new skin.
     */
    public DarkSapphireSkin() {
		SubstanceSkin.ColorSchemes schemes = SubstanceSkin
                .getColorSchemes(DarkSapphireSkin.class.getClassLoader()
                .getResource("xtrememp/ui/skin/dark-sapphire.colorschemes"));
		SubstanceColorScheme activeScheme = schemes.get("Dark Sapphire Active");
		SubstanceColorScheme defaultScheme = schemes.get("Dark Sapphire Default");

        // The default color scheme bundle
        SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
                activeScheme, defaultScheme, defaultScheme);
        defaultSchemeBundle.registerColorScheme(defaultScheme, 0.5f,
				ComponentState.DISABLED_UNSELECTED);
        this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
                DecorationAreaType.NONE);

        // the color scheme bundle for header area. Uses the same
        // color scheme for default and active states.
        SubstanceColorSchemeBundle headerBundle = new SubstanceColorSchemeBundle(
                activeScheme, activeScheme, defaultScheme);

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

        this.buttonShaper = new ClassicButtonShaper();
        this.borderPainter = new StandardBorderPainter();
        this.watermark = new SubstanceNullWatermark();
        this.gradientPainter = new GlassGradientPainter();
        this.decorationPainter = new MatteDecorationPainter();
//        this.decorationPainter = new ArcDecorationPainter();
        this.highlightPainter = new ClassicHighlightPainter();
//        this.highlightPainter = new GlassHighlightPainter();
        this.borderPainter = new GlassBorderPainter();
//        this.borderPainter = new CompositeBorderPainter("Twilight",
//				new ClassicBorderPainter(), new DelegateBorderPainter(
//						"Twilight Inner", new ClassicBorderPainter(),
//						0x40FFFFFF, 0x20FFFFFF, 0x00FFFFFF,
//						new ColorSchemeTransform() {
//							@Override
//							public SubstanceColorScheme transform(
//									SubstanceColorScheme scheme) {
//								return scheme.tint(0.8);
//							}
//						}));
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
