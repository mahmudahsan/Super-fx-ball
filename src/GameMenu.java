/*
 * GameMenu.java
 *
 * Created on September 2, 2007, 2:05 PM
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
import javax.microedition.media.*;
import javax.microedition.media.control.*;

public class GameMenu extends Canvas implements Runnable{
    private String [] menuList;
    private int selected;
    private Image img;
    private int centerWidth;
    private int centerHeight;
    private Font fMenu;
    private Random rand;
    boolean menuRun; // true means menu thread is running when go to another canvas set it to false
    
    private TFCanvas canvas; // a new game
    boolean continueScreen; // true means this class works as continue screen, false means this class is GameMenu Screen
    private t midlet;
    
    //music and sound
    private Player musicPlayer;
    
    public GameMenu(Object setNull, t midlet, String [] list) {
        setFullScreenMode(true);
        this.midlet = midlet;
        
        setNull = null;
        System.gc();
        
        menuList = list;
        selected = 0;
        rand = new Random();
        menuRun = true;
        
        try{
            img = Image.createImage("/res/menu-arrow.png");
        } catch(Exception o){}
        
        try{
            if (musicPlayer != null) musicPlayer.close();
            musicPlayer = Manager.createPlayer(getClass().getResourceAsStream("/res/sdk/tm.kkc"), "audio/midi");
            musicPlayer.setLoopCount(1000);
            musicPlayer.prefetch();
        } catch(Exception o){}
        centerWidth = getWidth()/2;
        centerHeight = getHeight()/2-90;
        fMenu = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_LARGE);
    }
    public void start(){
        startMusic();
        selected = 0;
        continueScreen = false;
        canvas = null; //when menu screen is open it should be null
        menuRun = true;
        Thread t = new Thread(this);
        t.start();
    }
    public void start(boolean value){
        //should call from continue screen
        this.continueScreen = value;
        selected = 0;
        menuRun = true;
        Thread t = new Thread(this);
        t.start();
    }
    
    public void stop(){
        menuRun = false; // stop this menuThread
        stopMusic();
    }
    public void startMusic(){
        try{
            if (musicPlayer != null){
                musicPlayer.setMediaTime(0);
                musicPlayer.start();
            }
        } catch(Exception o){}
    }
    public void stopMusic(){
        try{
            if (musicPlayer != null){
               musicPlayer.stop();
            }
        } catch(Exception o){}
    }
    public void run(){
        while (menuRun){
            repaint();
            try{
                Thread.sleep(100);
            } catch(Exception o){}
        }
    }
    public void keyPressed(int key){
        if (continueScreen){
            stopMusic();
            if (key == getKeyCode(Canvas.DOWN) || key == -2){
                selected = (selected + 1) % 3;
            } else if (key == getKeyCode(Canvas.UP) || key == -1){
                selected = selected - 1;
                if (selected < 0) selected = 2;
            } else if (key == -6 || key == getKeyCode(Canvas.FIRE) || key == Canvas.KEY_NUM5 || key == -5){
                //selecte menu
                if (selected == 0){
                    //continue the game as before
                    stop();
                    canvas.start();
                    midlet.display.setCurrent(canvas);
                } else if (selected == 1){
                    stop();
                    canvas = null;
                    System.gc();
                    start();
                }
                else if (selected == 2){
                    //save game YES
                    SaveLoad sv = new SaveLoad();
                    sv.saveGame(Global.currentStage, canvas.getScore(), canvas.getLives());
                    stop();
                    canvas = null;
                    System.gc();
                    start();
                }
            }
        } else{
            //game menu screen
            if (key == getKeyCode(Canvas.DOWN) || key == -2){
                selected = (selected + 1) % menuList.length;
            } else if (key == getKeyCode(Canvas.UP) || key == -1){
                selected = selected - 1;
                if (selected < 0) selected = menuList.length-1;
            } else if (key == -6 || key == getKeyCode(Canvas.FIRE) || key == Canvas.KEY_NUM5 || key == -5){
                //selecte menu
                if (selected == 0){
                    stop(); // stop current thread
                    
                    canvas = new TFCanvas(midlet, this, 0);
                    canvas.start();
                    midlet.canvas = canvas; // set a reference of canvas to midlet canvas
                    midlet.display.setCurrent(canvas);
                }else if (selected == 1){
                    //load game
                    SaveLoad ld = new SaveLoad();
                    ld.loadGame();
                    if (ld.status == false) return;
                    
                    stop(); // stop current thread
                    
                    canvas = new TFCanvas(midlet, this, ld.stage);
                    canvas.setLives(ld.lives);
                    canvas.setScore(ld.score);
                    canvas.start();
                    midlet.canvas = canvas; // set a reference of canvas to midlet canvas
                    midlet.display.setCurrent(canvas);
                }                 
                else if (selected == 2){
                    //score screen
                    stop();
                    HiScoreScreen sc = new HiScoreScreen(midlet);
                }
                else if (selected == 3){
                    stop();
                    AboutScreen ab = new AboutScreen(midlet, this);                    
                }
                else if (selected == 4){
                    stop();
                    CheckLicense chk = new CheckLicense(midlet, this);                    
                }
                else if (selected == 5){
                    stop();
                    midlet.destroyApp(true);
                    midlet.notifyDestroyed();
                }
            }
        }
    }
    
    public void paint(Graphics g){
        g.setColor(0x000000);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(0xffffff);
        for (int i = 0; i < 50; ++i){
            int x = rand.nextInt() % getWidth();
            int y = rand.nextInt() % getHeight();
            
            g.drawLine(x, y, x+1, y);
        }
        
        if (!continueScreen){
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
            g.setColor(0xD8E8B3);
            g.drawString("www.ftechdb.com", getWidth()/2, 0, Graphics.TOP|Graphics.HCENTER);
            
            g.setFont(fMenu);
            g.setColor(0x97C4DD);
            int yy = 0;
            for (int i = 0; i < menuList.length; ++i){
                g.setColor(0x97C4DD);
                g.drawString(menuList[i], centerWidth-50, centerHeight+yy, Graphics.TOP|Graphics.LEFT);
                
                if (selected == i){
                    g.setColor(0xE4BC96);
                    g.drawImage(img, centerWidth-70, centerHeight+yy+4, Graphics.TOP|Graphics.LEFT);
                    g.drawString(menuList[i], centerWidth-50, centerHeight+yy, Graphics.TOP|Graphics.LEFT);
                }
                yy += fMenu.getHeight();
            }
        } else{
            //continueScreen
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
            g.setColor(0xD8E8B3);
            g.drawString("Continue?", centerWidth-70, centerHeight, Graphics.TOP|Graphics.LEFT);
            
            int yy = fMenu.getHeight();
            g.setFont(fMenu);
            
            g.setColor(0x97C4DD);
            g.drawString("YES", centerWidth-50, centerHeight+yy, Graphics.TOP | Graphics.LEFT);
            if (selected == 0){
                g.setColor(0xE4BC96);
                g.drawString("YES", centerWidth-50, centerHeight+yy, Graphics.TOP | Graphics.LEFT);
                g.drawImage(img, centerWidth-70, centerHeight+yy+4, Graphics.TOP|Graphics.LEFT);
            }
            yy += fMenu.getHeight();
            g.setColor(0x97C4DD);
            g.drawString("NO", centerWidth-50, centerHeight+yy, Graphics.TOP | Graphics.LEFT);
            if (selected == 1){
                g.setColor(0xE4BC96);
                g.drawString("NO", centerWidth-50, centerHeight+yy, Graphics.TOP | Graphics.LEFT);
                g.drawImage(img, centerWidth-70, centerHeight+yy+4, Graphics.TOP|Graphics.LEFT);
            }
            yy += fMenu.getHeight();
            
            //Save Game
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_LARGE));
            g.setColor(0xD8E8B3);
            g.drawString("Save Game?", centerWidth-70, centerHeight+yy, Graphics.TOP|Graphics.LEFT);
            yy += fMenu.getHeight();
            
            g.setFont(fMenu);
            g.setColor(0x97C4DD);
            g.drawString("YES", centerWidth-50, centerHeight+yy, Graphics.TOP | Graphics.LEFT);
            if (selected == 2){
                g.setColor(0xE4BC96);
                g.drawString("YES", centerWidth-50, centerHeight+yy, Graphics.TOP | Graphics.LEFT);
                g.drawImage(img, centerWidth-70, centerHeight+yy+4, Graphics.TOP|Graphics.LEFT);
            }
        }
    }
}
