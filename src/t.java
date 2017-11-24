/*  Game Name: Super FX-BALL
    Developer: Md. Mahmud Ahsan
    Starting Date: 26-08-07
 */
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
/**
 *
 * @author  ahsan
 * @version 1.0
 */
public class t extends MIDlet implements Runnable {
    Display display;
    private SplashScreen splash;
    private TitleScreen screen;
    private GameMenu menuScreen;
    String [] sMenu = {"New Game", "Load Game", "Scores", "About", "Register", "Exit"};
    boolean startGame;
    
    TFCanvas canvas;
    
    public void startApp() {
        //after pause state
        if (canvas != null){
            menuScreen.start(true);
            display.setCurrent(menuScreen);
            return;
        }else if (menuScreen != null){
            menuScreen.start();
            display.setCurrent(menuScreen);
            return;
        }
        
        //start a new game
        startGame = false;
        display = Display.getDisplay(this);
        splash = new SplashScreen();
        screen = new TitleScreen(this);
        
        menuScreen = new GameMenu(null, this, sMenu);
        callMe();
        Thread t = new Thread(this);
        t.start();
    }
    
    public void run(){
        display.setCurrent(splash);
        try{
            Thread.sleep(3000);
        } catch(Exception o){}
        while (!startGame){
            display.setCurrent(screen);
            try{
                Thread.sleep(500);
            } catch(Exception o){}
        }
        
        menuScreen.start();
        display.setCurrent(menuScreen);
    }
    
    public void pauseApp() {
        if (canvas != null)
            canvas.stop();
        else if (menuScreen != null)
            menuScreen.stop();
        
    }
    public void callMe(){
        //RecordStore.deleteRecordStore("vxf");
        //v = means register rms
        try{
            RecordStore recordStore = RecordStore.openRecordStore("vxf", true);
            RecordEnumeration enums = recordStore.enumerateRecords(null, null, false);
                        
            if (enums.hasNextElement()){
                int id = enums.nextRecordId();
                byte [] data = recordStore.getRecord(id);
                
                String s = new String(data);
                if (s.equals("xxx019875654351a3s2d1fa32sd1f3as12d3f2a1s3df21a3sd21f3a265w4e6r54we65r4")){
                    Global.fack = true;
                }
                recordStore.closeRecordStore();
            }
        } catch(Exception o){
            o.printStackTrace();
        }
    }
    
    public void destroyApp(boolean unconditional) {
    }
}
