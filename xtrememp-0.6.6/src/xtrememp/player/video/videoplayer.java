


package xtrememp.player.video;


import java.awt.BorderLayout;
import java.awt.Component;
import java.io.IOException;
import java.net.URL;
import javax.media.CannotRealizeException;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.swing.JPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.media.Format;
import javax.media.PlugInManager;
import javax.media.format.VideoFormat;




public class videoplayer extends JPanel
{

   public videoplayer(URL mediaURL)
    {
    Format[] inFormats = {new VideoFormat("MPEG")};
    PlugInManager.addPlugIn("net.sourceforge.jffmpeg.VideoDecoder",inFormats,null,PlugInManager.CODEC);

    try {
           PlugInManager.commit();
        } catch (IOException ex) {
            Logger.getLogger(videoplayer.class.getName()).log(Level.SEVERE, null, ex);
        }


        setLayout(new BorderLayout());

        Manager.setHint(Manager.LIGHTWEIGHT_RENDERER,true);

        try
        {
            //create player
            Player mediaPlayer = Manager.createRealizedPlayer(mediaURL);

            //get components for video and playback control
            Component video = mediaPlayer.getVisualComponent();
            Component controls = mediaPlayer.getControlPanelComponent();

            if(video != null){
                add(video,BorderLayout.CENTER);//add video
            }
            if(controls != null){
                add(controls, BorderLayout.SOUTH); //add controls
            }
            mediaPlayer.start();
        }
        catch(NoPlayerException noPlayerException)
        {
            System.err.println("No media player!!");
        }
        catch(CannotRealizeException cannotRealizeException)
        {
         System.err.println("Media player not recognized!!");
        }
        catch(IOException iOException)
        {
            System.err.println("Error");
        }//end catch
    }//end videoplayer constructor
}//end videoplayer class
