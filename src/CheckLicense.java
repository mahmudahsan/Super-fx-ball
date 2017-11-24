/*
 * CheckLicense.java
 *
 * Created on September 5, 2007, 1:57 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author ahsan
 */
import javax.microedition.rms.*;
import javax.microedition.lcdui.*;

public class CheckLicense {
    private t midlet;
    private GameMenu menu;
    private Form form;
    Command cAct;
    Command cBack;
    Command cHelp;
    TextField fAct;
    /** Creates a new instance of CheckLicense */
    public CheckLicense(t midlet, GameMenu menu) {
        this.midlet = midlet;
        this.menu = menu;
        cAct = new Command("Activate", Command.OK, 1);
        cBack = new Command("Back", Command.BACK, 0);
        cHelp = new Command("Help", Command.SCREEN, 0);
        
        chkReg();
    }
    private void chkReg(){
        form =new Form("Register Super FX-Ball");
        
        fAct = new TextField("Activation Key:", "", 30, TextField.ANY);
        
        //if not registered
        form.append(new StringItem("Enter code to activate this game.", null));
        form.append(fAct);
        form.addCommand(cAct);
        form.addCommand(cBack);
        form.addCommand(cHelp);
        form.setCommandListener(
                new CommandListener(){
            public void commandAction(Command com, Displayable d){
                if (com == cAct){
                    boolean result = regProcess(fAct.getString());
                    if (result){
                        for (int i = 0; i < form.size(); ++i)
                            form.delete(i);
                        form.removeCommand(cAct);
                        
                        for (int i = 0; i < form.size(); ++i)
                            form.delete(i);
                        form.append(new StringItem("Congratulation: ", "Enjoy Super FX-BALL. Thank you for registering our product."));
                    }
                } else if (com == cBack){
                    menu.start();
                    midlet.display.setCurrent(menu);
                } else if (com == cHelp){
                    sRH();
                }
            }
        }
        );
        
        //first check if register or not
        if (Global.fack){
            for (int i = 0; i < form.size(); ++i)
                form.delete(i);
            form.removeCommand(cAct);
            
            for (int i = 0; i < form.size(); ++i)
                form.delete(i);
            form.append(new StringItem("Congratulation: ", "Enjoy Super FX-BALL. Thank you for registering our product."));
            
            midlet.display.setCurrent(form);
            return;
        }
        midlet.display.setCurrent(form);
    }
    
    private boolean regProcess(String s){
        if (s == null || s.equals("")){
            Alert alert = new Alert("Error", "Please enter correct code.", null, AlertType.INFO);
            alert.setTimeout(Alert.FOREVER);
            midlet.display.setCurrent(alert);
            return false;
        }
        StringBuffer buffer = new StringBuffer();
        StringBuffer match = new StringBuffer();
        
        int i, j;
        for (i = 0; i < s.length(); ++i){
            if (s.charAt(i) == 'T' && s.charAt(i+1) == 'A'){
                i = i+1;
                break;
            }
            buffer.append(s.charAt(i));
        }
        
        for (j = i+1; j < s.length(); ++j){
            match.append(s.charAt(j));
        }
        
        bGn bb = new bGn("0590st1ahnsanatji7002", buffer.toString());
        String act = bb.decToOther();
        
        if (act.equals(match.toString().trim())){
            try{
                Global.fack = true;
                RecordStore rs = RecordStore.openRecordStore("vxf", true);
                String data = "xxx019875654351a3s2d1fa32sd1f3as12d3f2a1s3df21a3sd21f3a265w4e6r54we65r4";
                rs.addRecord(data.getBytes(), 0, data.length());
            } catch(Exception o){
                Alert alert = new Alert("Runtime Error", "Write Disk Error.", null, AlertType.INFO);
                alert.setTimeout(Alert.FOREVER);
                midlet.display.setCurrent(alert);
                return false;
            }
        } else{
            Alert alert = new Alert("Error", "Please enter correct code.", null, AlertType.INFO);
            alert.setTimeout(Alert.FOREVER);
            midlet.display.setCurrent(alert);
            return false;
        }
        
        return true;
    }
    private void sRH(){
        Form ff = new Form("How to register");
        
        ff.append(new StringItem("Visit our website to purchase activation code.", null));
        ff.append(new StringItem("Activation Code: ", "Enter activation code to this field.\n"));
        ff.append(new StringItem("URL", "http://www.ftechdb.com"));
        ff.append(new StringItem("Email", "info@ftechdb.com"));
        
        ff.addCommand(cBack);
        ff.setCommandListener(
                new CommandListener(){
            public void commandAction(Command com, Displayable d){
                if (com == cBack){
                    chkReg();
                }
            }
        }
        );
        midlet.display.setCurrent(ff);
    }
    
}
