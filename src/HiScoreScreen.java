/*
 * HiScoreScreen.java
 *
 * Created on September 3, 2007, 8:45 PM
 */

import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import java.io.*;

//create custom type
class DataType {
    String name;
    int score;
}

//this hiScoreScreen saves record of top 5 base on their scores
public class HiScoreScreen extends Canvas{
    private RecordStore rs;
    private RecordEnumeration enums;
    private boolean hasData;
    private boolean currentScore; //when user data is need to save it is true
    
    private t midlet;
    
    private DataType dList[];
    private int dListLength;
    
    private String currName;
    private int currScore;
        
    public HiScoreScreen(t midlet) {
        setFullScreenMode(true);
        this.midlet = midlet;
        init();
        currentScore = false;
        try {
            rs = RecordStore.openRecordStore("hiscore", false);
            enums = rs.enumerateRecords(null, null, true);
            loadData();
        } catch(Exception e) {
            hasData = false;// no database created
            e.printStackTrace();
        }
        midlet.display.setCurrent(this);
    }
    //below constructor is called when need to save data
    public HiScoreScreen(t midlet, String name, int score){
        setFullScreenMode(true);
        this.midlet = midlet;
        currName = name;
        currScore =score;
        currentScore = true;
        
        try {
            init();
            rs = RecordStore.openRecordStore("hiscore", true);
            enums = rs.enumerateRecords(null, null, true);
            loadData();
            enums = rs.enumerateRecords(null, null, true);
            beforeSaveData(name, score);
            
            init();
            rs = RecordStore.openRecordStore("hiscore", false);
            enums = rs.enumerateRecords(null, null, true);
            loadData();//need to reload data after being saved
            rs.closeRecordStore();
        } catch(Exception e) {
            hasData = false;
            System.out.println("Here comes");
            e.printStackTrace();
        }
        midlet.display.setCurrent(this);
    }
    private void init(){
        dList = new DataType[6];
        dListLength = 0;
        for (int i = 0; i < 6; ++i)
            dList[i] = new DataType();
    }
    private void loadData() throws Exception{
        byte [] data;
        int len;
        int id;
        
        try{
            while (enums.hasNextElement()){
                id = enums.nextRecordId();
                
                hasData = true;
               
                data = new byte[rs.getRecordSize(id)];
                len = rs.getRecord(id, data, 0);
                
                try{
                    DataInputStream is = new DataInputStream(new ByteArrayInputStream(data));
                    
                    DataType record = new DataType();
                    record.name = is.readUTF();
                    record.score = is.readInt();
                    
                    dList[dListLength] = record;
                } catch(Exception o){
                    o.printStackTrace();
                }
                dListLength++;
            }
        } catch(Exception o){
            o.printStackTrace();
        }
        enums.reset();
        enums.destroy();
    }
    
    private void beforeSaveData(String name, int score) throws Exception{
        dList[dListLength].name = name;
        dList[dListLength].score = score;
        dListLength++;
        //do sort
        if (dListLength > 1)
            bubbleSort();
                
        int id;
        try{
            while (enums.hasNextElement()){
                id = enums.nextRecordId();
                rs.deleteRecord(id);
            }          
        } catch(Exception o){
            o.printStackTrace();
        }
        
        for (int i = 0; i < dListLength; ++i){
            if (i >= 5) break;
            saveData(dList[i].name, dList[i].score);
         }
        rs.closeRecordStore();
    }
    
    private void saveData(String name, int score)throws Exception{
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream os = new DataOutputStream(baos);
        
        try{
            // Type is a class defined below
            DataType x = new DataType();
            x.name = name;
            x.score = score;
            
            os.writeUTF(x.name);
            os.writeInt(x.score);
            os.flush();
            
            byte [] data = baos.toByteArray();
            
            rs.addRecord(data, 0, data.length);
            
            baos.reset();
            baos.close();
        } catch(Exception o){
            System.out.println("Error" + o.toString());
        }
    }
    private void bubbleSort(){
        int i, j;
        DataType temp = new DataType();
        
        for (i = 1; i < dListLength; ++i){
            for (j = 0; j < dListLength-i; ++j){
                if (dList[j].score < dList[j+1].score){
                    temp = dList[j];
                    dList[j] = dList[j+1];
                    dList[j+1] = temp;
                }
            }
        }
    }
    
    public void paint(Graphics g) {
        //print hi score
        g.setColor(0x000000);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(0x7cc576);
        g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.drawString("MENU", getWidth()-50, getHeight()-25, Graphics.TOP|Graphics.LEFT);
        
        if (hasData){
            //data have to show
            g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD|Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM));
            g.setColor(0xc7b299);
            g.drawString("BEST SCORES", getWidth()/2, 10, Graphics.TOP|Graphics.HCENTER);
            
            g.setColor(0x6dcff6);
            Font f = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
            g.setFont(f);
            int yy = 50;
            for (int i = dListLength-1; i >= 0; --i){
                g.drawString(dList[i].name, getWidth()/2-65, yy, Graphics.TOP|Graphics.HCENTER);
                g.drawString(""+dList[i].score, getWidth()/2+50, yy, Graphics.TOP|Graphics.HCENTER);
                yy+= f.getHeight();
            }
            
            g.setColor(0xc7b299);
            g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            if (currentScore)
                g.drawString(currName + "'s score: "+currScore, getWidth()/2-20, yy, Graphics.TOP|Graphics.HCENTER);
        } else{
            g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_LARGE));
            g.setColor(0x6dcff6);
            g.drawString("No Data", getWidth()/2-50, 50, Graphics.TOP|Graphics.HCENTER);
        }
    }
    
    /**
     * Called when a key is pressed.
     */
    protected  void keyPressed(int keyCode) {
        //for all key
        GameMenu menuScreen = new GameMenu(this, midlet, midlet.sMenu);
        menuScreen.start();
        System.gc();
        midlet.display.setCurrent(menuScreen);
    }
}
