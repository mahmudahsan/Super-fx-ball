/*
 * InputForm.java
 *
 * Created on September 4, 2007, 3:11 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author ahsan
 */
import javax.microedition.lcdui.*;

public class InputForm extends Form implements CommandListener{
    Command cOk;
    TextField tName;
    t midlet;
    private int score;
    
    public InputForm(TFCanvas setNull, t midlet, int score) {
        super("User Input");
        
        setNull = null;
        System.gc();
        this.midlet = midlet;
        this.score = score;
        
        cOk = new Command("OK", Command.OK, 1);
        tName = new TextField("Your Name", "", 10, TextField.ANY);
        append(tName);
        addCommand(cOk);
        setCommandListener(this);
    }
    public void commandAction(Command com, Displayable d){
        String name = tName.getString();
        if (name == null || name.equals("")){
            Alert al = new Alert("Name?", "Enter your name", null, AlertType.INFO);
            al.setTimeout(Alert.FOREVER);
            midlet.display.setCurrent(al);
            return;
        }
        try{
            HiScoreScreen sc = new HiScoreScreen(midlet, name, score);        
        }
        catch(Exception o){
            o.printStackTrace();
        }
    }    
}
