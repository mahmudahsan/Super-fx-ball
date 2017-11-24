/*
 * AboutScreen.java
 *
 * Created on September 4, 2007, 11:33 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author ahsan
 */
import javax.microedition.lcdui.*;

public class AboutScreen extends Canvas implements Runnable{
    private GameMenu menu;
    private Image img;
    private String [] menuList = {"About Developer", "About License", "Contact Info"};
    private int selected;
    
    private Command cBack;
    private t midlet;
    private boolean sleeping;//thread status true means thread is not active
    
    private boolean bContact;
    
    public AboutScreen(t midlet, GameMenu menu){
        setFullScreenMode(true);
        this.midlet = midlet;
        this.menu = menu;
        cBack = new Command("Back", Command.BACK, 1);
        try{
            img = Image.createImage("/res/menu-arrow.png");
        } catch(Exception o){}
        
        selected = 0;
        start();
        midlet.display.setCurrent(this);
    }
    public void start(){
        sleeping = false;
        Thread t = new Thread(this);
        t.start();
    }
    public void stop(){
        sleeping = true;
    }
    public void run(){
        while (!sleeping){
            try{
                repaint();
                Thread.sleep(50);
            } catch(Exception o){
                o.printStackTrace();
            }
        }
    }
    public void aboutScreen(){
        start();
        midlet.display.setCurrent(this);
    }
    public void paint(Graphics g){
        g.setColor(0x000000);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(0x7cc576);
        g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.drawString("MENU", getWidth()-50, getHeight()-25, Graphics.TOP|Graphics.LEFT);
        
        Font f1 = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_LARGE);
        
        g.setFont(f1);
        int yy = 70;
        for (int i = 0; i < menuList.length; ++i){
            g.setColor(0x97C4DD);
            g.drawString(menuList[i], 30, yy, Graphics.TOP|Graphics.LEFT);
            
            if (selected == i){
                g.setColor(0xE4BC96);
                g.drawImage(img, 3, yy+4, Graphics.TOP|Graphics.LEFT);
                g.drawString(menuList[i], 30, yy, Graphics.TOP|Graphics.LEFT);
            }
            yy += f1.getHeight();
        }
        
    }
    public void keyPressed(int key){
        if (key == getKeyCode(Canvas.DOWN) || key == -2){
            selected = (selected + 1) % menuList.length;
        } else if (key == getKeyCode(Canvas.UP) || key == -1){
            selected = selected - 1;
            if (selected < 0) selected = menuList.length-1;
        } else if (key == -6 || key == getKeyCode(Canvas.FIRE) || key == Canvas.KEY_NUM5 || key == -5){
            //selecte menu
            if (selected == 0){
                aboutScreenDeveloper();
            }else if (selected == 1){
                aboutScreenLicense();
            } else if (selected == 2){
                aboutScreenContact();
            }
        } else if (key == -7){
            stop();//stop current thread
            menu.start(); // start menu thread
            midlet.display.setCurrent(menu);
        }
    }
    public void aboutScreenDeveloper(){
        AboutDeveloperCanvas win = new AboutDeveloperCanvas(this, 1);
        stop();
        midlet.display.setCurrent(win);
    }
    /** Display the aggrement and license information. */
    public void aboutScreenLicense(){
        Form form = new Form("License and agreement");
        
        StringItem item = new StringItem("The Product is licensed to you on an As-Is basis, subject to the terms and conditions of this Agreement, for your personal use only.", "");
        item.setLayout(Item.LAYOUT_2 | Item.LAYOUT_LEFT| Item.LAYOUT_NEWLINE_AFTER);
        form.append(item);
        
        item = new StringItem("", "1. You may not modify or create any derivative works of the Product or documentation.");
        item.setLayout(Item.LAYOUT_2 | Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER);
        form.append(item);
        
        item = new StringItem("", "2. You may not decompile, disassemble, reverse engineer, or otherwise attempt to derive the source code for the Product.");
        item.setLayout(Item.LAYOUT_2 | Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER);
        form.append(item);
        
        item = new StringItem("", "3. You may not redistribute, encumber, sell, rent, lease, sublicense, or otherwise transfer rights to the Product without a prior written permission of FTechdb");
        item.setLayout(Item.LAYOUT_2 | Item.LAYOUT_LEFT | Item.LAYOUT_NEWLINE_BEFORE | Item.LAYOUT_NEWLINE_AFTER);
        form.append(item);
        
        form.addCommand(cBack);
        form.setCommandListener(
                new CommandListener(){
            public void commandAction(Command com, Displayable d){
                if (com == cBack)
                    aboutScreen();
            }
        }
        );
        midlet.display.setCurrent(form);
    }
    public void aboutScreenContact(){
       AboutDeveloperCanvas win = new AboutDeveloperCanvas(this, 2);
       stop();
       midlet.display.setCurrent(win);
    }
}
