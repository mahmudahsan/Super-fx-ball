/*
 * Constant.java
 *
 * Created on August 27, 2007, 10:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author ahsan
 */
public class Global {
    public static boolean fack = false; // true means full version
    //initialize these b4 new game or beginning of each stage
    public static boolean shooting = false;
    public static boolean breakThrough = false; // this variable effects in ballTouchMap() method in TFCanvas
    public static boolean powerBall = false;
    public static boolean bombBurst = false;
    public static int ballSpeed = 1; // 0 -> fast speed, 1-> normal speed, 2-> slow ball
    public static int numberOfBall = 1;
    
    //soundOn = true, false means sound off
    public static boolean soundOn=true;
    //this variables are globally used but modified
    public static int currentStage; //when a new game starts set this variable = 0;
    //this variable for lives related
    public static boolean SET_BALL_IN_PADDLE = false;
    
    //constant
    public final static int MAP_SET_Y = 30; 
    public final static int MAP_SET_X = (TFCanvas.viewWidth/2);// -120; // 12tiles*20px = 240 so, move center - 120
    
    public final static int BALL_XSPEED_SLOW = 4;
    public final static int BALL_YSPEED_SLOW = 4;
    
    public final static int BALL_XSPEED_NORMAL = 6;
    public final static int BALL_YSPEED_NORMAL = 6;
    
    public final static int BALL_XSPEED_FAST = 9;
    public final static int BALL_YSPEED_FAST = 9;
    
    public final static int MAP_COL_TILES = 10;
    public final static int MAP_ROW_TILES = 16;
    public final static int MAP_TILE_WIDTH = 20;
    public final static int MAP_TILE_HEIGHT = 10;
    
    public final static int dr[] = {0, -1, -1, -1, 0, 1, 1, 1};
    public final static int dc[] = {-1, -1, 0, 1, 1, 1, 0, -1};
    
}
