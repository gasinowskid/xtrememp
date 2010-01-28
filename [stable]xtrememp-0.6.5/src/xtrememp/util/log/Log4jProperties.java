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
package xtrememp.util.log;

import java.io.File;
import java.util.Properties;
import xtrememp.Settings;

/**
 *
 * @author Besmir Beqiri
 */
public class Log4jProperties extends Properties {

    public Log4jProperties() {
        super();
        setProperty("log4j.rootLogger", "DEBUG, CA, FA");
        setProperty("log4j.appender.CA", "org.apache.log4j.ConsoleAppender");
        setProperty("log4j.appender.CA.layout", "org.apache.log4j.TTCCLayout");
        setProperty("log4j.appender.FA", "org.apache.log4j.FileAppender");
        File logFile = new File(Settings.getCacheDir().getPath(), "xtrememp.log");
        setProperty("log4j.appender.FA.file", logFile.getPath());
        setProperty("log4j.appender.FA.append", Boolean.toString(false));
        setProperty("log4j.appender.FA.layout", "org.apache.log4j.PatternLayout");
        setProperty("log4j.appender.FA.layout.ConversionPattern", "%d %-5p %-17c{2} (%30F:%L) %3x - %m%n");
    }
}
