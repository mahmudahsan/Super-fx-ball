/*
 * SaveLoad.java
 *
 * Created on September 3, 2007, 4:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author ahsan
 */
import javax.microedition.rms.*;

public class SaveLoad {
    int stage;
    int score;
    int lives;
    boolean status; //false means no record is saved
    
    private RecordStore rs;
    /** Creates a new instance of SaveLoad */
    public SaveLoad() {
        
    }
    
    public void saveGame(int st, int sc, int liv){
        this.stage = st-1;
        this.score = sc;
        this.lives = liv;
        
        try{
            RecordStore.deleteRecordStore("llabxf00709");
        }
        catch(Exception o){}
        
        try{
            rs = RecordStore.openRecordStore("llabxf00709", true);
            
            byte[] rStage = Integer.toString(stage).getBytes();
            byte[] rScore = Integer.toString(score).getBytes();
            byte[] rLives = Integer.toString(lives).getBytes();
            
            rs.addRecord(rStage, 0, rStage.length);
            rs.addRecord(rScore, 0, rScore.length);
            rs.addRecord(rLives, 0, rLives.length);
            
            rs.closeRecordStore();
        }
        catch(Exception o){
            status =false;
        }
    }
    public void loadGame(){
        try{
            rs = RecordStore.openRecordStore("llabxf00709", false);
            status = true;
            byte[] rStage;
            byte[] rScore;
            byte[] rLives;
            
            rStage = new byte[rs.getRecordSize(1)];
            rScore = new byte[rs.getRecordSize(2)];
            rLives = new byte[rs.getRecordSize(3)];
            
            int lenStage = rs.getRecord(1, rStage, 0);
            int lenScore = rs.getRecord(2, rScore, 0);
            int lenLives = rs.getRecord(3, rLives, 0);
                
            stage = Integer.parseInt(new String(rStage, 0, lenStage));
            score = Integer.parseInt(new String(rScore, 0, lenScore));
            lives = Integer.parseInt(new String(rLives, 0, lenLives));
            
            rs.closeRecordStore();
        }
        catch(Exception o){
            status =false;
        }
    }
    
}
