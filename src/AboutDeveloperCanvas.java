/*
 * AboutDeveloperCanvas.java
 *
 * Created on September 4, 2007, 11:42 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author ahsan
 */
import javax.microedition.lcdui.*;

public class AboutDeveloperCanvas extends Canvas {
    private AboutScreen ab;
    private int selected; //1,2 for AboutScreen and 3 for Demo Version message
    
    //below codes is needed when demo version is running
    private t midlet;
    private int score;
    private TFCanvas tf;
    public AboutDeveloperCanvas(t midlet, TFCanvas tf, int selected, int score){
        setFullScreenMode(true);
        this.midlet = midlet;
        this.tf = tf;
        this.score = score;
        this.selected = selected;
        midlet.display.setCurrent(this);
    }
    /** Creates a new instance of AboutDeveloperCanvas */
    public AboutDeveloperCanvas(AboutScreen ab, int selected) {
        this.ab = ab;
        this.selected = selected;
        setFullScreenMode(true);
    }
    public void paint(Graphics g){
        g.setColor(0x000000);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(0x7cc576);
        g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.drawString("MENU", getWidth()-50, getHeight()-25, Graphics.TOP|Graphics.LEFT);
        
        Font f1 = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        Font f2 = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        
        if (selected == 3){
            //for demo version messsage
            g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.setColor(0xfec5c5);
            g.drawString("This is Unregistered Version", getWidth()/2, 10, Graphics.TOP|Graphics.HCENTER);
            
            g.setFont(f1);
            g.setColor(0x7accc8);
            g.drawString("Buy License Key", getWidth()/2, 40, Graphics.TOP|Graphics.HCENTER);
            
            int yy = 40 + f1.getHeight();
            
            yy += f2.getHeight()+3;
            g.setColor(0xbc8cbf);
            g.drawString("to register this game.", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f2.getHeight()+3;
            g.setFont(f2);
            g.setColor(0xbc8cbf);
            g.drawString("Play 60 levels &", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER );
            
            yy += f2.getHeight()+3;
            g.drawString("enjoy more features.", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f2.getHeight()+3;
            g.drawString("www.ftechdb.com", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f2.getHeight()+3;
            g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("Copyright© 2007 FTechdb", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            return;
        }
        
        
        if (selected == 1){
            //developer screen
            g.setFont(f1);
            g.setColor(0x6dcff6);
            g.drawString("Developed By: ", getWidth()/2, 10, Graphics.TOP|Graphics.HCENTER);
            
            int yy = 10 + f1.getHeight()+2;
            
            g.setFont(f2);
            g.setColor(0xc69c6d);
            g.drawString("Md. Mahmud Ahsan", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER );
            
            yy += f2.getHeight()+5;
            g.setColor(0x6dcff6);
            g.setFont(f1);
            g.drawString("Special Thanks:", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f1.getHeight()+2;
            g.setColor(0xc69c6d);
            g.setFont(f2);
            g.drawString("Munny", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f2.getHeight()+5;
            g.setColor(0x6dcff6);
            g.setFont(f1);
            g.drawString("Dedicated To:", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f1.getHeight()+5;
            g.setColor(0xc69c6d);
            g.setFont(f2);
            g.drawString("My Father", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f2.getHeight()+5;
            g.setColor(0xc4df9b);
            g.setFont(f1);
            g.drawString("Presents By:", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f1.getHeight()+2;
            g.setColor(0xc4df9b);
            g.setFont(f2);
            g.drawString("FTechdb.", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f1.getHeight()+2;
            g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.setColor(0xbc8cbf);
            g.drawString("Copyright© 2007 FTechdb", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
        } else if (selected == 2){
            //contact screen
            g.setFont(f1);
            g.setColor(0x7accc8);
            g.drawString("Contact Us: ", getWidth()/2, 40, Graphics.TOP|Graphics.HCENTER);
            
            int yy = 45 + f1.getHeight()+2;
            
            g.setFont(f2);
            g.setColor(0xbc8cbf);
            g.drawString("Company: FTechdb", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER );
            
            yy += f2.getHeight()+5;
            g.setColor(0xbc8cbf);
            g.drawString("www.ftechdb.com", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f2.getHeight()+5;
            g.drawString("info@ftechdb.com", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
            
            yy += f2.getHeight()+5;
            g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("Copyright© 2007 FTechdb", getWidth()/2, yy, Graphics.TOP|Graphics.HCENTER);
        }
    }
    public void keyPressed(int key){
        if (key != -7) return;
        
        if (selected == 3){
            InputForm form = new InputForm(tf, midlet, score);
            midlet.display.setCurrent(form);
            return;
        }
         ab.aboutScreen();
    }
}
