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
package xtrememp.update;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xtrememp.Settings;
import xtrememp.XtremeMP;
import xtrememp.util.LanguageBundle;

/**
 *
 * @author Besmir Beqiri
 */
public final class SoftwareUpdate {

    private static final Logger logger = LoggerFactory.getLogger(SoftwareUpdate.class);
    private static final String updatesURL = LanguageBundle.getString("Application.updatesURL");
    private static final Version currentVersion = Version.getCurrentVersion();
    private static SwingWorker checkForUpdatesWorker;
    private static JDialog checkForUpdatesDialog;
    private static Version newerVersion;

    public static void checkForUpdates(final boolean showDialogs) {
        checkForUpdatesWorker = new SwingWorker<Version, Void>() {

            @Override
            protected Version doInBackground() throws Exception {
                logger.debug("checkForUpdates: started...");
                if (showDialogs) {
                    // Simulate a delay of 2 sec to let the user cancel the task.
                    Thread.sleep(2000);
                }
                return getLastVersion(new URL(updatesURL));
            }

            @Override
            protected void done() {
                logger.debug("checkForUpdates: done");
                if (checkForUpdatesDialog != null && checkForUpdatesDialog.isVisible()) {
                    checkForUpdatesDialog.dispose();
                }
                if (!isCancelled()) {
                    try {
                        newerVersion = get();
                        if (newerVersion != null && newerVersion.compareTo(currentVersion) == 1) {
                            logger.debug("checkForUpdates: currentVersion = {}", currentVersion);
                            logger.debug("checkForUpdates: newerVersion = {}", newerVersion);
                            logger.debug("SoftwareUpdate::checkForUpdates: updates found");
                            Object[] options = {LanguageBundle.getString("Button.Cancel")};
                            Desktop desktop = null;
                            if (Desktop.isDesktopSupported()) {
                                desktop = Desktop.getDesktop();
                                if (desktop.isSupported(Desktop.Action.BROWSE)) {
                                    options = new Object[]{LanguageBundle.getString("Button.Download"), LanguageBundle.getString("Button.Cancel")};
                                }
                            }
                            JPanel panel = new JPanel(new BorderLayout(0, 10));
                            panel.add(new JLabel("<html>" + LanguageBundle.getString("Dialog.SoftwareUpdate.UpdatesFound") +
                                    " (" + newerVersion + ")</html>"), BorderLayout.CENTER);
                            JCheckBox hideCheckBox = null;
                            if (Settings.isAutomaticCheckForUpdatesEnabled()) {
                                hideCheckBox = new JCheckBox(LanguageBundle.getString("Dialog.SoftwareUpdate.DisableAutomaticCheckForUpdates"));
                                panel.add(hideCheckBox, BorderLayout.SOUTH);
                            }
                            int n = JOptionPane.showOptionDialog(XtremeMP.getInstance().getMainFrame(), panel, LanguageBundle.getString("Dialog.SoftwareUpdate"),
                                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                            if (hideCheckBox != null) {
                                Settings.setAutomaticCheckForUpdatesEnabled(!hideCheckBox.isSelected());
                            }
                            if ((options.length == 2) && (n == 0)) {
                                try {
                                    URL url = new URL(newerVersion.getDownloadURL());
                                    desktop.browse(url.toURI());
                                } catch (Exception ex) {
                                    logger.error(ex.getMessage(), ex);
                                }
                            }
                        } else {
                            logger.debug("checkForUpdates: no updates found");
                            if (showDialogs) {
                                JOptionPane.showMessageDialog(XtremeMP.getInstance().getMainFrame(),
                                        LanguageBundle.getString("Dialog.SoftwareUpdate.NoUpdatesFound"),
                                        LanguageBundle.getString("Dialog.SoftwareUpdate"), JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    } catch (Exception ex) {
                        logger.error(ex.getMessage(), ex);
                        if (showDialogs) {
                            JOptionPane.showMessageDialog(XtremeMP.getInstance().getMainFrame(),
                                    LanguageBundle.getString("Dialog.SoftwareUpdate.ConnectionFailure"),
                                    LanguageBundle.getString("Dialog.SoftwareUpdate"), JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                }
            }
        };
        checkForUpdatesWorker.execute();
    }

    public static void showCheckForUpdatesDialog() {
        checkForUpdatesDialog = new JDialog(XtremeMP.getInstance().getMainFrame(),
                LanguageBundle.getString("Dialog.SoftwareUpdate"), true);
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.add(new JLabel(LanguageBundle.getString("Dialog.SoftwareUpdate.CheckingForUpdates")), BorderLayout.CENTER);
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(250, 20));
        panel.add(progressBar, BorderLayout.SOUTH);
        JButton cancelButton = new JButton(LanguageBundle.getString("Button.Cancel"));
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkForUpdatesWorker != null && !checkForUpdatesWorker.isDone()) {
                    checkForUpdatesWorker.cancel(true);
                }
                checkForUpdatesDialog.dispose();
            }
        });
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
                JOptionPane.DEFAULT_OPTION, null, new Object[]{cancelButton}, cancelButton);
        checkForUpdatesDialog.setContentPane(optionPane);
        checkForUpdatesDialog.pack();
        checkForUpdatesDialog.setResizable(false);
        checkForUpdatesDialog.setLocationRelativeTo(checkForUpdatesDialog.getParent());
        checkForUpdatesDialog.getRootPane().setDefaultButton(cancelButton);
        checkForUpdatesDialog.setVisible(true);
    }

    public static Version getLastVersion(URL url) throws Exception {
        Version result = null;
        InputStream urlStream = null;
        try {
            URLConnection urlConnection = url.openConnection();
            urlConnection.setAllowUserInteraction(false);
            urlConnection.setConnectTimeout(30000);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(false);
            urlConnection.setReadTimeout(10000);
            urlConnection.setUseCaches(true);
            urlStream = urlConnection.getInputStream();
            Properties properties = new Properties();
            properties.load(urlStream);

            result = new Version();
            result.setMajorNumber(Integer.parseInt(properties.getProperty("xtrememp.lastVersion.majorNumber")));
            result.setMinorNumber(Integer.parseInt(properties.getProperty("xtrememp.lastVersion.minorNumber")));
            result.setMicroNumber(Integer.parseInt(properties.getProperty("xtrememp.lastVersion.microNumber")));
            result.setVersionType(Version.VersionType.valueOf(properties.getProperty("xtrememp.lastVersion.versionType")));
            result.setReleaseDate(properties.getProperty("xtrememp.lastVersion.releaseDate"));
            result.setDownloadURL(properties.getProperty("xtrememp.lastVersion.dounloadURL"));
        } finally {
            IOUtils.closeQuietly(urlStream);
        }
        return result;
    }

    public static void scheduleCheckForUpdates(long delay) {
        Timer timer = new Timer();
        timer.schedule(new CheckForUpdatesTask(), delay);
    }

    private static class CheckForUpdatesTask extends TimerTask {

        @Override
        public void run() {
            checkForUpdates(false);
        }
    }
}
