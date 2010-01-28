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
package xtrememp.ui.skin;

import org.pushingpixels.substance.api.ComponentState;
import org.pushingpixels.substance.api.DecorationAreaType;
import org.pushingpixels.substance.api.SubstanceColorScheme;
import org.pushingpixels.substance.api.SubstanceColorSchemeBundle;
import org.pushingpixels.substance.api.SubstanceSkin;
import org.pushingpixels.substance.api.painter.border.StandardBorderPainter;
import org.pushingpixels.substance.api.painter.decoration.MatteDecorationPainter;
import org.pushingpixels.substance.api.painter.fill.GlassFillPainter;
import org.pushingpixels.substance.api.painter.highlight.ClassicHighlightPainter;
import org.pushingpixels.substance.api.shaper.ClassicButtonShaper;
import org.pushingpixels.substance.api.watermark.SubstanceNullWatermark;

/**
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
        SubstanceSkin.ColorSchemes schemes = SubstanceSkin.getColorSchemes(
                DarkSapphireSkin.class.getClassLoader().getResource(
                "xtrememp/ui/skin/dark-sapphire.colorschemes"));
        SubstanceColorScheme activeScheme = schemes.get("Dark Sapphire Active");
        SubstanceColorScheme defaultScheme = schemes.get("Dark Sapphire Default");

        SubstanceColorSchemeBundle defaultSchemeBundle = new SubstanceColorSchemeBundle(
                activeScheme, defaultScheme, defaultScheme);
        defaultSchemeBundle.registerColorScheme(defaultScheme, 0.5f,
                ComponentState.DISABLED_UNSELECTED);
        
        this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
                DecorationAreaType.NONE);

        this.registerDecorationAreaSchemeBundle(defaultSchemeBundle,
                DecorationAreaType.PRIMARY_TITLE_PANE,
                DecorationAreaType.SECONDARY_TITLE_PANE,
                DecorationAreaType.HEADER,
                DecorationAreaType.FOOTER,
                DecorationAreaType.GENERAL,
                DecorationAreaType.TOOLBAR);

        this.buttonShaper = new ClassicButtonShaper();
        this.fillPainter = new GlassFillPainter();
        this.watermark = new SubstanceNullWatermark();
        this.decorationPainter = new MatteDecorationPainter();
//        this.decorationPainter = new ArcDecorationPainter();
        this.highlightPainter = new ClassicHighlightPainter();
//        this.highlightPainter = new GlassHighlightPainter();
        this.borderPainter = new StandardBorderPainter();
//        this.borderPainter = new GlassBorderPainter();
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
