/*
 * TitleScreen.java
 *
 * Created on September 1, 2007, 8:21 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author ahsan
 */
import javax.microedition.lcdui.*;

public class TitleScreen extends Canvas{
    private Image img;
    t tt;
    public TitleScreen(t tt) {
        this.tt = tt;
        setFullScreenMode(true);
        try{
            img = Image.createImage("/res/title-screen.png");
        }catch(Exception o){}
    }
    public void paint(Graphics g){
        g.setColor(0x000000);
        g.fillRect(0, 0, getWidth(), getHeight());
        g.drawImage(img, getWidth()/2, getHeight()/2, Graphics.VCENTER|Graphics.HCENTER);
    }    
    public void keyPressed(int key){
        tt.startGame = true;
    }
}
