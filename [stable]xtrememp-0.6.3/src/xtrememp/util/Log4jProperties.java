/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xtrememp.util;

import java.io.File;
import java.util.Properties;
import xtrememp.Settings;

/**
 *
 * @author Besi
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
