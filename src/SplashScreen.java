/*
 * SplashScreen.java
 *
 * Created on September 1, 2007, 7:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author ahsan
 */
import javax.microedition.lcdui.*;
import java.util.*;

public class SplashScreen extends Canvas{
    /** Creates a new instance of SplashScreen */
    Image img;
    public SplashScreen() {
        setFullScreenMode(true);
        try{
             img = Image.createImage("/res/ftechdb-splash-screen.png");  
        }   
        catch(Exception o){}
    }
    public void paint(Graphics g){
        g.setColor(0xffffff);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(img, getWidth()/2, getHeight()/2, Graphics.VCENTER|Graphics.HCENTER);
    }
}
