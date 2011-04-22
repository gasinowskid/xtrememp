/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xtrememp;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.JFrame;
import org.jdesktop.swingx.JXBusyLabel;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import xtrememp.player.audio.AudioPlayer;
import xtrememp.player.audio.PlaybackEvent;
import xtrememp.playlist.PlaylistEvent;

/**
 *
 * @author Deano
 */
public class XtremeMPTest {

    public XtremeMPTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getInstance method, of class XtremeMP.
     */
    @Test
    public void testGetInstance() {
        System.out.println("getInstance");
        XtremeMP expResult = null;
        XtremeMP result = XtremeMP.getInstance();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getMainFrame method, of class XtremeMP.
     */
    @Test
    public void testGetMainFrame() {
        System.out.println("getMainFrame");
        XtremeMP instance = null;
        JFrame expResult = null;
        JFrame result = instance.getMainFrame();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getBusyLabel method, of class XtremeMP.
     */
    @Test
    public void testGetBusyLabel() {
        System.out.println("getBusyLabel");
        XtremeMP instance = null;
        JXBusyLabel expResult = null;
        JXBusyLabel result = instance.getBusyLabel();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getAudioPlayer method, of class XtremeMP.
     */
    @Test
    public void testGetAudioPlayer() {
        System.out.println("getAudioPlayer");
        XtremeMP instance = null;
        AudioPlayer expResult = null;
        AudioPlayer result = instance.getAudioPlayer();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of init method, of class XtremeMP.
     */
    @Test
    public void testInit() {
        System.out.println("init");
        List<String> arguments = null;
        XtremeMP instance = null;
        instance.init(arguments);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of main method, of class XtremeMP.
     */
    @Test
    public void testMain() throws Exception {
        System.out.println("main");
        String[] args = null;
        XtremeMP.main(args);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of exit method, of class XtremeMP.
     */
    @Test
    public void testExit() {
        System.out.println("exit");
        XtremeMP instance = null;
        instance.exit();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createMenuBar method, of class XtremeMP.
     */
    @Test
    public void testCreateMenuBar() {
        System.out.println("createMenuBar");
        XtremeMP instance = null;
        instance.createMenuBar();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of createMainPanels method, of class XtremeMP.
     */
    @Test
    public void testCreateMainPanels() {
        System.out.println("createMainPanels");
        XtremeMP instance = null;
        instance.createMainPanels();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setTime method, of class XtremeMP.
     */
    @Test
    public void testSetTime() {
        System.out.println("setTime");
        String timeText = "";
        int seekSliderValue = 0;
        XtremeMP instance = null;
        instance.setTime(timeText, seekSliderValue);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setStatus method, of class XtremeMP.
     */
    @Test
    public void testSetStatus() {
        System.out.println("setStatus");
        String text = "";
        XtremeMP instance = null;
        instance.setStatus(text);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of enableControlButtons method, of class XtremeMP.
     */
    @Test
    public void testEnableControlButtons() {
        System.out.println("enableControlButtons");
        boolean flag = false;
        XtremeMP instance = null;
        instance.enableControlButtons(flag);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of actionPerformed method, of class XtremeMP.
     */
    @Test
    public void testActionPerformed() {
        System.out.println("actionPerformed");
        ActionEvent e = null;
        XtremeMP instance = null;
        instance.actionPerformed(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playbackBuffering method, of class XtremeMP.
     */
    @Test
    public void testPlaybackBuffering() {
        System.out.println("playbackBuffering");
        PlaybackEvent pe = null;
        XtremeMP instance = null;
        instance.playbackBuffering(pe);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playbackOpened method, of class XtremeMP.
     */
    @Test
    public void testPlaybackOpened() {
        System.out.println("playbackOpened");
        PlaybackEvent pe = null;
        XtremeMP instance = null;
        instance.playbackOpened(pe);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playbackEndOfMedia method, of class XtremeMP.
     */
    @Test
    public void testPlaybackEndOfMedia() {
        System.out.println("playbackEndOfMedia");
        PlaybackEvent pe = null;
        XtremeMP instance = null;
        instance.playbackEndOfMedia(pe);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playbackPlaying method, of class XtremeMP.
     */
    @Test
    public void testPlaybackPlaying() {
        System.out.println("playbackPlaying");
        PlaybackEvent pe = null;
        XtremeMP instance = null;
        instance.playbackPlaying(pe);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playbackProgress method, of class XtremeMP.
     */
    @Test
    public void testPlaybackProgress() {
        System.out.println("playbackProgress");
        PlaybackEvent pe = null;
        XtremeMP instance = null;
        instance.playbackProgress(pe);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playbackPaused method, of class XtremeMP.
     */
    @Test
    public void testPlaybackPaused() {
        System.out.println("playbackPaused");
        PlaybackEvent pe = null;
        XtremeMP instance = null;
        instance.playbackPaused(pe);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playbackStopped method, of class XtremeMP.
     */
    @Test
    public void testPlaybackStopped() {
        System.out.println("playbackStopped");
        PlaybackEvent pe = null;
        XtremeMP instance = null;
        instance.playbackStopped(pe);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acOpen method, of class XtremeMP.
     */
    @Test
    public void testAcOpen() {
        System.out.println("acOpen");
        XtremeMP instance = null;
        instance.acOpen();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acOpenAndPlay method, of class XtremeMP.
     */
    @Test
    public void testAcOpenAndPlay() {
        System.out.println("acOpenAndPlay");
        XtremeMP instance = null;
        instance.acOpenAndPlay();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acPrevious method, of class XtremeMP.
     */
    @Test
    public void testAcPrevious() {
        System.out.println("acPrevious");
        XtremeMP instance = null;
        instance.acPrevious();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acNext method, of class XtremeMP.
     */
    @Test
    public void testAcNext() {
        System.out.println("acNext");
        XtremeMP instance = null;
        instance.acNext();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acPlayPause method, of class XtremeMP.
     */
    @Test
    public void testAcPlayPause() {
        System.out.println("acPlayPause");
        XtremeMP instance = null;
        instance.acPlayPause();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acStop method, of class XtremeMP.
     */
    @Test
    public void testAcStop() {
        System.out.println("acStop");
        XtremeMP instance = null;
        instance.acStop();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acUpdateTime method, of class XtremeMP.
     */
    @Test
    public void testAcUpdateTime() {
        System.out.println("acUpdateTime");
        int value = 0;
        XtremeMP instance = null;
        instance.acUpdateTime(value);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of acSeek method, of class XtremeMP.
     */
    @Test
    public void testAcSeek() {
        System.out.println("acSeek");
        XtremeMP instance = null;
        instance.acSeek();
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playlistItemAdded method, of class XtremeMP.
     */
    @Test
    public void testPlaylistItemAdded() {
        System.out.println("playlistItemAdded");
        PlaylistEvent e = null;
        XtremeMP instance = null;
        instance.playlistItemAdded(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playlistItemRemoved method, of class XtremeMP.
     */
    @Test
    public void testPlaylistItemRemoved() {
        System.out.println("playlistItemRemoved");
        PlaylistEvent e = null;
        XtremeMP instance = null;
        instance.playlistItemRemoved(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of playModeChanged method, of class XtremeMP.
     */
    @Test
    public void testPlayModeChanged() {
        System.out.println("playModeChanged");
        PlaylistEvent e = null;
        XtremeMP instance = null;
        instance.playModeChanged(e);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of onIntellitype method, of class XtremeMP.
     */
    @Test
    public void testOnIntellitype() {
        System.out.println("onIntellitype");
        int command = 0;
        XtremeMP instance = null;
        instance.onIntellitype(command);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of guiEffectsStateChanged method, of class XtremeMP.
     */
    @Test
    public void testGuiEffectsStateChanged() {
        System.out.println("guiEffectsStateChanged");
        boolean flag = false;
        XtremeMP instance = null;
        instance.guiEffectsStateChanged(flag);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}