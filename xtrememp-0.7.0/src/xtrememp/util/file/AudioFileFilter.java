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
package xtrememp.util.file;

import java.io.File;
import java.io.FilenameFilter;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Besmir Beqiri
 */
public class AudioFileFilter extends FileFilter implements FilenameFilter {

    public static final String[] audioFormatExt = {".spx", ".snd", ".aifc", ".aif", ".wav", ".au", ".flac", ".mp1", ".mp2", ".mp3", ".ogg"};

    @Override
    public boolean accept(File dir, String name) {
        File file = new File(dir, name);
        if (file.exists() && file.isDirectory()) {
            return true;
        }
        
        String s = name.toLowerCase();
        for (String ext : audioFormatExt) {
            if (s.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }
        
        String s = file.getName().toLowerCase();
        for (String ext : audioFormatExt) {
            if (s.endsWith(ext)) {
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String getDescription() {
        String description = "Supported Audio Files";
        if (audioFormatExt.length > 0) {
            description += " (";
            for (String ext : audioFormatExt) {
                description += " *" + ext;
            }
            description += " )";
        }
        return description;
    }
}