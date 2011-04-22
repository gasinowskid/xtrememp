/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package xtrememp.player.video;
import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * This class implements custom exception for the player.
 *
 *
 */
public class PlayerException extends Exception {

    private Throwable cause = null;

    public PlayerException() {
        super();
    }

    public PlayerException(String msg) {
        super(msg);
    }

    public PlayerException(Throwable cause) {
        super();
        this.cause = cause;
    }

    public PlayerException(String msg, Throwable cause) {
        super(msg);
        this.cause = cause;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }

    @Override
    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        } else if (cause != null) {
            return cause.toString();
        } else {
            return null;
        }
    }

    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }

    @Override
    public void printStackTrace(PrintStream out) {
        synchronized (out) {
            PrintWriter pw = new PrintWriter(out, false);
            printStackTrace(pw);
            pw.flush();
        }
    }

    @Override
    public void printStackTrace(PrintWriter out) {
        if (cause != null) cause.printStackTrace(out);
    }
}

