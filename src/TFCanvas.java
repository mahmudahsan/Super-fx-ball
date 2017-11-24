/*
 * boardCanvas.java
 *
 * Created on August 26, 2007, 9:05 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author ahsan
 */
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;
import java.util.*;
import java.io.*;

public class TFCanvas extends GameCanvas implements Runnable{
    private t midlet;
    private Random rand = new Random(); // random object;
    //  private Vector vector; // is used in ballTouchMethod after powerBall
    //  private boolean vectorDo; //when true vector elements should process
    
    private boolean showStageName; //used when stage name have to shown
    private boolean gameOver;
    private boolean sleeping; // game thread (main looping thread) is active or not
    private int frameDelay; // fps of game normally 33 is 30 frame per second
    private int score;
    private int stagesScore; //this hold the total score before a stage begins
    private int lives;
    private int inputDelay;
    
    private GameMenu gameMenu;
    
    static int viewWidth; // the total area of the gameCanvas
    static int viewHeight;
    
    private LayerManager layers;
    private PaddleSprite sPaddle;
    private int paddleSpeed;
    private BallSprite [] sBallArray;// maximum 6 ball can be made
    private Sprite sStageNumber; // graphics that shows as stage number
    private Sprite [] sGift; // graphics that used for various purpose
    private int giftProbability;
    private Sprite [] sTileBurst; // shown when tiles are burst down
    private BallSprite sBullet;
    private BallSprite[][] sBulletArray;// Two dimensional array to hold sBullet and shoot parallal
    
    private String giftMessage;
    private boolean showGiftMessage;
    
    //giftoutput delay
    private int giftShowDelay;
    //below lines is for animation tiles
    private int animateTile[][]; //it contains animateTile x,y location
    private int animateTileLength;
    private int animationDelay;
    
    //Stage code
    private TiledLayer tileStage;
    //read stage
    private DataInputStream din;
    
    //music and sound
    private Player pShoot;
    private Player pLife;
    private Player pKill;
    private Player pBrick;
    private Player pBomb;
    private Player pGood;
    private Player pTriple;
    
    //when 1st stage is shown, must provide currentStage to 0 (zero)
    public TFCanvas(t midlet, GameMenu gameMenu, int currentStage) {
        super(true);
        this.midlet = midlet;
        this.gameMenu = gameMenu;
        
        try{
            Global.currentStage = currentStage;
            init();
        } catch(Exception o){
            showError(o);
        }
    }
    public void start(){
        sleeping = false;
        Thread mainThread = new Thread(this);
        mainThread.start();
    }
    //This initialize is needed when a new game starts or a new stage starts or a new life begins
    private void commonInit(){
        //commonInit() is called by nextStage so automatically called by init()
        Global.shooting = false;
        Global.breakThrough = false;
        Global.powerBall = false;
        Global.bombBurst = false;
        Global.ballSpeed = 1;
        Global.numberOfBall = 1;
        sPaddle.setFrame(1);
        
        boolean oneTime = true;
        for (int i = 0; i <3; ++i){
            sBallArray[i].setSpeed(0, 0);
            sBallArray[i].setVisible(false);
            sBallArray[i].setBallMoving(false);
            sPaddle.setPosition(5, viewHeight-30);
            
            if (oneTime){
                oneTime = false;
                sBallArray[0].setVisible(true);
                
                if (sPaddle.getFrame() == 0 || sPaddle.getFrame() == 3)
                    sBallArray[0].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                else if (sPaddle.getFrame() == 1 || sPaddle.getFrame() == 4)
                    sBallArray[0].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                else
                    sBallArray[0].setPosition(sPaddle.getX()+25+sPaddle.getWidth()/2, sPaddle.getY());
            }
        }
        
        //make visible part invisible for a new stage
        for (int i = 0; i < 5; ++i){
            if (sBulletArray[i][0].isVisible()) sBulletArray[i][0].setVisible(false);
            if (sBulletArray[i][1].isVisible()) sBulletArray[i][1].setVisible(false);
            
            if (i < 3){
                if (sGift[i].isVisible()) sGift[i].setVisible(false);
            }
        }
    }
    
    //this is one time initialization when game starts
    private void init() throws Exception{
        setFullScreenMode(true);
        viewWidth = getWidth();
        viewHeight = getHeight();
        
        gameOver = false;
        frameDelay = 18; //render fps (1/20)*1000
        sleeping = true; // true means main thread is gone sleep
        score = 0;
        lives = 3;
        inputDelay = 0;
        giftProbability = 5;
        
        animateTile = new int[60][2]; // maximum animate tile will be 40
        rand = new Random();
        
        layers = new LayerManager();
        sPaddle = new PaddleSprite(Image.createImage("/res/paddle.png"), 100, 15, viewWidth);
        paddleSpeed = 10;
        
        sBallArray = new BallSprite[3];
        for (int i = 0; i < 3; ++i)
            sBallArray[i] = new BallSprite(Image.createImage("/res/ball.png"), 8, 8, 0, 0, viewWidth, viewHeight);
        
        sStageNumber = new Sprite(Image.createImage("/res/stage-number.png"), 12, 15);
        sGift = new Sprite[3];
        sTileBurst = new Sprite[3];
        for (int i = 0; i < 3; ++i){
            sGift[i] = new Sprite(Image.createImage("/res/gift.png"), 22, 22); //multiple frames
            sTileBurst[i] = new Sprite(Image.createImage("/res/tile-burst.png"));
        }
        //tileStage design and mapping
        tileStage = new TiledLayer(Global.MAP_COL_TILES, Global.MAP_ROW_TILES, Image.createImage("/res/tiles.png"), Global.MAP_TILE_WIDTH, Global.MAP_TILE_HEIGHT);
        
        for (int i = 0; i < 3; ++i){
            layers.append(sTileBurst[i]);
            sTileBurst[i].setVisible(false);
            layers.append(sGift[i]);
            sGift[i].setVisible(false);
        }
        
        //only for bullet purpose
        sBullet = new BallSprite(Image.createImage("/res/bullet.png"));
        sBulletArray = new BallSprite[5][2];
        
        layers.append(tileStage);
        layers.append(sPaddle);
        for (int i = 0; i < 3; ++i)
            layers.append(sBallArray[i]);
        
        //create bulletArray and add in layers
        for (int i = 0; i < 5; ++i){
            sBulletArray[i] = new BallSprite[2];
            sBulletArray[i][0] = new BallSprite(sBullet);
            sBulletArray[i][1] = new BallSprite(sBullet);
            
            sBulletArray[i][0].setVisible(false);
            sBulletArray[i][1].setVisible(false);
            
            layers.append(sBulletArray[i][0]);
            layers.append(sBulletArray[i][1]);
        }
        
        //sound create and load
        try{            
            pTriple = Manager.createPlayer(getClass().getResourceAsStream("/res/sdk/pTriple.kkc"), "audio/X-wav");
            pTriple.prefetch();
            
            pBomb = Manager.createPlayer(getClass().getResourceAsStream("/res/sdk/pBomb.kkc"), "audio/X-wav");
            pBomb.prefetch();
            
            pGood = Manager.createPlayer(getClass().getResourceAsStream("/res/sdk/pGood.kkc"), "audio/X-wav");
            pGood.prefetch();
            
            pKill = Manager.createPlayer(getClass().getResourceAsStream("/res/sdk/pKill.kkc"), "audio/X-wav");
            pKill.prefetch();
            
            pLife = Manager.createPlayer(getClass().getResourceAsStream("/res/sdk/pLife.kkc"), "audio/X-wav");
            pLife.prefetch();
            
            pShoot = Manager.createPlayer(getClass().getResourceAsStream("/res/sdk/pShoot.kkc"), "audio/X-wav");
            pShoot.prefetch();
            
            pBrick = Manager.createPlayer(getClass().getResourceAsStream("res/sdk/pBrick.kkc"), "audio/X-wav");
            pBrick.prefetch();
        } catch(Exception o){
            showError(o);
        }
        
        nextStage();
        sleeping = false;
    }
    public int getLives(){
        return lives;
    }
    public void setLives(int lives){
        this.lives = lives;
    }
    public int getScore(){
        return stagesScore;
    }
    public void setScore(int sc){
        score = stagesScore = sc;
    }
    public void keyPressed(int key){
        if (gameOver)return;
        if (showStageName) return;
        if (key == -7){
            //key = -7 is the key above RED Cancel button
            stop(); //stop current thread
            
            gameMenu.start(true);
            midlet.display.setCurrent(gameMenu);
        }
    }
    public void run(){
        Graphics g = getGraphics();
        
        while (!sleeping){
            try{
                if (checkStageEnd()){
                    nextStage();
                }
                update();
                draw(g);
            } catch(Exception o){
                /*o.printStackTrace();*/
                sleeping = true;
            }
            if (gameOver){
                try{
                    InputForm in = new InputForm(this, midlet, score);
                    midlet.display.setCurrent(in);
                    return;
                }catch(Exception o){o.printStackTrace();}
                return;
            }
            
            if (showStageName){
                showStageName = false;
                try{
                    Thread.sleep(2000);
                }catch(Exception o){showError(o);}
            } else{
                try{
                    Thread.sleep(frameDelay);
                } catch(Exception o){
                    
                }
            }
            // set all visible burst tile and visible gift tile to invisible
            for (int i = 0; i < 3; ++i){
                if (sTileBurst[i].isVisible())
                    sTileBurst[i].setVisible(false);
                
                if (sGift[i].isVisible() && sGift[i].getY() >= viewHeight){
                    sGift[i].setVisible(false);
                }
            }
        }
    }
    private void update() throws Exception{
        if (showStageName) return;
        
        if (lives <= 0){
            gameOver = true;
            stop();
            return;
        }
        
        int keyStates = getKeyStates();
        
        if (++inputDelay > 2){
            if ( (keyStates & FIRE_PRESSED) != 0){
                if(!sBallArray[0].getBallMoving()){
                    sBallArray[0].setBallMoving(true);
                    sBallArray[0].setSpeed(5, 4);
                }
                if (Global.shooting){
                    for (int i = 0; i < 5; ++i){
                        if (!sBulletArray[i][0].isVisible() && !sBulletArray[i][1].isVisible()){
                             try{ pShoot.start();}catch(Exception o){};
                            sBulletArray[i][0].setVisible(true);
                            sBulletArray[i][1].setVisible(true);
                            
                            if (sPaddle.getFrame() == 3){
                                sBulletArray[i][0].setPosition(sPaddle.getX(), sPaddle.getY());
                                sBulletArray[i][1].setPosition(sPaddle.getX()+sPaddle.getWidth()-4, sPaddle.getY());
                            } else if (sPaddle.getFrame() == 4){
                                sBulletArray[i][0].setPosition(sPaddle.getX()+20, sPaddle.getY());
                                sBulletArray[i][1].setPosition(sPaddle.getX()+sPaddle.getWidth()-24, sPaddle.getY());
                            } else if (sPaddle.getFrame() == 5){
                                sBulletArray[i][0].setPosition(sPaddle.getX()+25, sPaddle.getY());
                                sBulletArray[i][1].setPosition(sPaddle.getX()+sPaddle.getWidth()-29, sPaddle.getY());
                            }
                            break;
                        }
                    }
                }
            }
            inputDelay = 0;
        }
        if ( (keyStates & LEFT_PRESSED)!= 0){
            sPaddle.move(-paddleSpeed, 0);
            
            if (!sBallArray[0].getBallMoving()){
                if (sPaddle.getFrame() == 0 || sPaddle.getFrame() == 3)
                    sBallArray[0].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                else if (sPaddle.getFrame() == 1 || sPaddle.getFrame() == 4)
                    sBallArray[0].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                else
                    sBallArray[0].setPosition(sPaddle.getX()+25+sPaddle.getWidth()/2, sPaddle.getY());
            }
            
        } else if ( (keyStates & RIGHT_PRESSED)!= 0){
            sPaddle.move(paddleSpeed, 0);
            
            if (!sBallArray[0].getBallMoving()){
                if (sPaddle.getFrame() == 0 || sPaddle.getFrame() == 3 )
                    sBallArray[0].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                else if (sPaddle.getFrame() == 1 || sPaddle.getFrame() == 4)
                    sBallArray[0].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                else
                    sBallArray[0].setPosition(sPaddle.getX()+25+sPaddle.getWidth()/2, sPaddle.getY());
            }
        }
        
        //check whether ball collides with sPaddle or tileStage
        for (int i = 0; i < 3; ++i){
            if (sBallArray[i].isVisible() && sPaddle.collidesWith(sBallArray[i], true) && sBallArray[i].getBallMoving()){
                sPaddle.changeBallMotion(sBallArray[i], false); // this update the ball motion
                sBallArray[i].move(0, -7);
            }
            
            if (sBallArray[i].isVisible() && sBallArray[i].collidesWith(tileStage, true) && sBallArray[i].getBallMoving()){
                for (int j = 0; j < 3; ++j){
                    if (!sTileBurst[j].isVisible()){
                        sTileBurst[j].setPosition(sBallArray[i].getX()-10, sBallArray[i].getY()-5);
                        sTileBurst[j].setVisible(true);
                    }
                    ballTouchMap(sBallArray[i], false); // to break a particular brick
                    placeGift(sBallArray[i]); // call this method to place gift if necessary
                    break;
                }
            }
        }
        
        //check whether paddle is touched with gift, there is atmost 3 gifts
        for (int i = 0; i < 3; ++i){
            if (sGift[i].collidesWith(sPaddle, true) && sGift[i].isVisible()){
                sGift[i].setVisible(false);
                
                score += 100;
                int frame = sGift[i].getFrame();
                //below code for paddle increase and decrease
                if (frame == 5){
                    int x = sPaddle.getFrame();
                    switch(x){
                        case 2: sPaddle.setFrame(1); break;
                        case 1: sPaddle.setFrame(0); break;
                        case 5: sPaddle.setFrame(4); break;
                        case 4: sPaddle.setFrame(3);
                    }
                }
                if (frame == 6){
                    int x = sPaddle.getFrame();
                    switch(x){
                        case 0: sPaddle.setFrame(1); break;
                        case 1: sPaddle.setFrame(2); break;
                        case 3: sPaddle.setFrame(4); break;
                        case 4: sPaddle.setFrame(5);
                    }
                }
                
                //increase life
                if (frame == 0){
                    try{ pLife.start();}catch(Exception o){};
                    ++lives;
                    giftMessage = "Bonus Life";
                    showGiftMessage = true;
                }
                //kill 1 life
                if (frame == 8){
                    try{ pKill.start();}catch(Exception o){};
                    Global.SET_BALL_IN_PADDLE = true; // this decrease the life by 1
                    giftMessage = "Life Lost";
                    showGiftMessage = true;
                }
                //speed change slower to speeder
                if (frame == 9){
                    for (int j = 0; j < 3; ++j){
                        if (sBallArray[j].isVisible() && Global.ballSpeed == 2){
                            Global.ballSpeed = 1;
                            sPaddle.changeBallMotion(sBallArray[j], true);
                        } else if (sBallArray[j].isVisible() && Global.ballSpeed == 1){
                            Global.ballSpeed = 0;
                            sPaddle.changeBallMotion(sBallArray[j], true);
                        }
                    }
                }
                //speed change
                if (frame == 3){
                    for (int j = 0; j < 3; ++j){
                        if (sBallArray[j].isVisible() && Global.ballSpeed == 0){
                            Global.ballSpeed = 1;
                            sPaddle.changeBallMotion(sBallArray[j], true);
                        } else if (sBallArray[j].isVisible() && Global.ballSpeed == 1){
                            Global.ballSpeed = 2;
                            sPaddle.changeBallMotion(sBallArray[j], true);
                        }
                    }
                }
                //breakThrough
                if (frame == 2){
                    try{ pGood.start();}catch(Exception o){};
                    if (!Global.breakThrough){
                        giftMessage = "Breaking Ball";
                        showGiftMessage = true;
                    }
                    Global.breakThrough = true;
                    
                }
                //power ball
                if (frame == 4){
                    try{ pGood.start();}catch(Exception o){};
                    if (!Global.powerBall){
                        giftMessage = "Power Ball";
                        showGiftMessage = true;
                    }
                    Global.powerBall = true;
                }
                //shooting
                if (frame == 1){
                    try{ pGood.start();}catch(Exception o){};
                    switch(sPaddle.getFrame()){
                        case 0: sPaddle.setFrame(3); break;
                        case 1: sPaddle.setFrame(4); break;
                        case 2: sPaddle.setFrame(5);
                    }
                    if (!Global.shooting){
                        giftMessage = "Paddle Fire";
                        showGiftMessage = true;
                    }
                    Global.shooting = true;
                }
                //triple ball
                if (frame == 7){
                    try{ pTriple.start();}catch(Exception o){};
                    Global.numberOfBall = 3;
                    for (int k = 0; k < 3; ++k){
                        if (sBallArray[k].isVisible()) continue;
                        
                        sBallArray[k].setVisible(true);
                        sBallArray[k].setBallMoving(true);
                        if (k == 0)
                            sBallArray[k].setSpeed(Global.BALL_XSPEED_NORMAL, -Global.BALL_YSPEED_NORMAL);
                        else if (k == 1)
                            sBallArray[k].setSpeed(-4, -Global.BALL_YSPEED_NORMAL);
                        else
                            sBallArray[k].setSpeed(3, -Global.BALL_YSPEED_NORMAL);
                        
                        if (sPaddle.getFrame() == 0 || sPaddle.getFrame() == 3)
                            sBallArray[k].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                        else if (sPaddle.getFrame() == 1 || sPaddle.getFrame() == 4)
                            sBallArray[k].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                        else
                            sBallArray[k].setPosition(sPaddle.getX()+25+sPaddle.getWidth()/2, sPaddle.getY());
                    }
                }
            }
        }
        
        //update gift sprite to down
        for (int i = 0; i < 3; ++i){
            if (sGift[i].isVisible()){
                sGift[i].move(0, 3);
            }
        }
        
        //update animateTile
        if (++animationDelay > 10){
            animationDelay = 0;
            for (int i = 0; i < animateTileLength; ++i){
                if (animateTile[i][1] == -1 && animateTile[i][0] == -1 )
                    continue; // if already burth the animate tile
                
                if (tileStage.getCell(animateTile[i][1], animateTile[i][0]) == 8)
                    tileStage.setCell(animateTile[i][1], animateTile[i][0], 9);
                else if (tileStage.getCell(animateTile[i][1], animateTile[i][0]) == 9)
                    tileStage.setCell(animateTile[i][1], animateTile[i][0], 8);
                
                if (tileStage.getCell(animateTile[i][1], animateTile[i][0]) == 0){
                     try{pBomb.start();}catch(Exception o){}
                    int x, y;
                    for (int k = 0; k < 8; ++k){
                        x = Global.dc[k]+animateTile[i][1]; //1 col
                        y = Global.dr[k]+animateTile[i][0]; //0 row
                        
                        try{
                            if (tileStage.getCell(x, y) != 0){
                                tileStage.setCell(x, y, 0);
                                score += 10;
                            }
                        } catch(Exception o){}
                    }
                    animateTile[i][1] = animateTile[i][0] = -1; //set not to revisit
                }
            }
        }
        // Check bullet touch map or not and update bullet moving
        if (Global.shooting){
            for (int i = 0; i < 5; ++i){
                if (sBulletArray[i][0].isVisible() && sBulletArray[i][0].collidesWith(tileStage, true)){
                    ballTouchMap(sBulletArray[i][0], true);
                    sBulletArray[i][0].setVisible(false);
                }
                if (sBulletArray[i][1].isVisible() && sBulletArray[i][1].collidesWith(tileStage, true)){
                    ballTouchMap(sBulletArray[i][1], true);
                    sBulletArray[i][1].setVisible(false);
                }
                
                if (sBulletArray[i][0].isVisible())
                    sBulletArray[i][0].move(0, -6);
                if (sBulletArray[i][1].isVisible())
                    sBulletArray[i][1].move(0, -6);
                
                if (sBulletArray[i][0].getY() <= 0)
                    sBulletArray[i][0].setVisible(false);
                if (sBulletArray[i][1].getY() <= 0)
                    sBulletArray[i][1].setVisible(false);
            }
        }
        //show some speech in giftMessage
        if (score >50000 && score < 50200){
            giftMessage = "Nice Score";
            showGiftMessage = true;
        }
        else if (score > 100000 && score < 100200){
            giftMessage = "Looking Smart!";
            showGiftMessage = true;
        }
        
        //update paddle
        sPaddle.update();
        
        //update ball
        for (int i = 0; i < 3; ++i){
            if (!sBallArray[i].isVisible()) continue;
            
            if (Global.powerBall)
                sBallArray[i].setFrame(2);
            else if (Global.breakThrough)
                sBallArray[i].setFrame(1);
            else if (Global.ballSpeed == 0)
                sBallArray[i].setFrame(4);
            else if (Global.ballSpeed == 2)
                sBallArray[i].setFrame(3);
            else
                sBallArray[i].setFrame(0);
            
            sBallArray[i].update();
        }
        
        if (Global.SET_BALL_IN_PADDLE){
            try{ pKill.start();}catch(Exception o){}
            Global.SET_BALL_IN_PADDLE = false;
            if (lives > 0){
                commonInit();
                sBallArray[0].setSpeed(0,0);
                sBallArray[0].setVisible(true);
                sPaddle.setFrame(1);
                if (sPaddle.getFrame() == 0 || sPaddle.getFrame() == 3)
                    sBallArray[0].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                else if (sPaddle.getFrame() == 1 || sPaddle.getFrame() == 4)
                    sBallArray[0].setPosition(sPaddle.getX()+sPaddle.getWidth()/2, sPaddle.getY());
                else
                    sBallArray[0].setPosition(sPaddle.getX()+25+sPaddle.getWidth()/2, sPaddle.getY());
                --lives;
            }
        }
        
    }
    private void draw(Graphics g) throws Exception{
        g.setColor(0x000000);
        g.fillRect(0, 0, viewWidth, viewHeight);
        
        if (showStageName){ // detect stage and paint the correct image
            int x = (viewWidth/2)-50;
            int y = viewHeight/3;
            g.drawImage(Image.createImage("/res/stage.png"), x, y, Graphics.TOP | Graphics.LEFT);
            
            int num = Global.currentStage;
            int arr[] = new int[3];
            int i=0;
            int d;
            
            while (num != 0){
                d = num % 10;
                num = num / 10;
                arr[i++]=d;
            }
            sStageNumber.setPosition(x + 84, y);
            for (x = --i; x >=0; --x){
                sStageNumber.setFrame(arr[x]);
                sStageNumber.paint(g);
                sStageNumber.move(12,0);
            }
            flushGraphics();
            return;
        }
        
        //show score
        g.setColor(0xdab73f);
        g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM));
        g.drawString(""+score, 0, 0, Graphics.TOP | Graphics.LEFT );
        
        g.setColor(255, 155, 0);
        if (showGiftMessage){
            g.drawString(giftMessage, viewWidth/2, sPaddle.getY()-70, Graphics.HCENTER|Graphics.TOP);
            if (++giftShowDelay > 20){
                showGiftMessage = false;
                giftShowDelay = 0;
            }
        }
        
        //show lives
        g.setColor(0x7da7d8);
        g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_MEDIUM));
        g.drawString("Lives: "+lives, viewWidth-90, 0, Graphics.TOP | Graphics.LEFT );
        
        if (gameOver){
            g.setColor(0x000000);
            g.fillRect(0, 0, viewWidth, viewHeight);
            g.setColor(0xfffffa);
            g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD, Font.SIZE_LARGE));
            g.drawString("GAME OVER", viewWidth/2, viewHeight/2, Graphics.TOP | Graphics.HCENTER);
            flushGraphics();
            
            try{
                Thread.sleep(2000);
            } catch(Exception o){}
            
            return;
        }
       /*
        g.setColor(0xffffff);
        g.setFont(Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_SMALL));
        g.drawString("view"+viewWidth+"x"+viewHeight, 0, 30, Graphics.BOTTOM|Graphics.LEFT);
        */
        layers.paint(g, 0, 0);
        flushGraphics();
    }
    
    public void stop(){
        sleeping = true;
    }
    //this method go to the next stage
    public void nextStage(){
        if (Global.currentStage >= 60){
            //all stages completed
            stop();
            InputForm form = new InputForm(this, midlet, score);
            midlet.display.setCurrent(form);
            return;
        }
        if (Global.currentStage >= 3 && !Global.fack){
            stop();
            AboutDeveloperCanvas showDemo = new AboutDeveloperCanvas(midlet, this, 3, score);
            return;
        }
        
        commonInit();
        ++Global.currentStage; // increment the stage by 1
        
        if (Global.currentStage > 10)
            giftProbability = 7; // change probabilty by time
        else if (Global.currentStage > 20)
            giftProbability = 10;
        else if (Global.currentStage > 30)
            giftProbability = 11;
        else if (Global.currentStage > 40)
            giftProbability = 15;
        else if (Global.currentStage > 50)
            giftProbability = 20;
        
        showGiftMessage = false;
        giftShowDelay = 0;
        
        showStageName = true;
        stagesScore = score;//save the last score
        
        tileStage.setPosition(Global.MAP_SET_X, Global.MAP_SET_Y);
        int col, row;
        
        animationDelay = 0;
        animateTileLength = 0;
        
        try{
            din = new DataInputStream(getClass().getResourceAsStream("/res/pam/st"+Global.currentStage+".pam"));
        } catch(Exception o){}
        
        int readInt;
        
        try{
            for (row = 0; row <16; ++row){
                for (col = 0; col < 12; ++col){
                    readInt = din.readInt();
                    if (col >= Global.MAP_COL_TILES) continue;
                    
                    tileStage.setCell(col, row, readInt);
                    //save location of animation tile
                    if (readInt == 8 || readInt == 9){
                        animateTile[animateTileLength][0] = row;
                        animateTile[animateTileLength][1] = col;
                        ++animateTileLength;
                    }
                }
            }
            if (din!=null)din.close();
        } catch(Exception o){}
    }
    
    //this code solve problems when ball and tiledLayer collides
    // if bullet is true then sBall is a bullet
    private void ballTouchMap(BallSprite sBall, boolean bullet){
        try{pBrick.start();}catch(Exception o){}
        score += 20;
        int ballX, ballY;
        int mapX, mapY;
        int tX, tY;
        
        int xSpeed = sBall.getXSpeed();
        int ySpeed = sBall.getYSpeed();
        
        int finalMapX = 0;
        int finalMapY = 0; // variables used to detect final tile that is broken
        int finalMapCell = 0;
        
        ballX = sBall.getX();
        ballY = sBall.getY();
        int ballW = sBall.getWidth();
        int ballH = sBall.getHeight();
        
        tX = (ballX - Global.MAP_SET_X) % Global.MAP_TILE_WIDTH;
        mapX = (ballX - Global.MAP_SET_X) / Global.MAP_TILE_WIDTH;
        if (tX != 0) ++mapX;
        
        mapY = (ballY - Global.MAP_SET_Y) / Global.MAP_TILE_HEIGHT;
        
        --mapX; --mapY;
        if (mapX < 0) mapX = 0;
        if (mapY < 0) mapY = 0;
        
        boolean flag = true; //if one cell is broken no need to break other cell
        
        // try is for if IndexOutOfBoundsException occurs just ignore it
        if (sBall.getYSpeed() <= 0){
            try{
                if (tileStage.getCell(mapX, mapY+1) != 0 && flag){
                    finalMapCell = tileStage.getCell(mapX, mapY+1);
                    tileStage.setCell(mapX, mapY+1, 0);
                    finalMapX = mapX;
                    finalMapY = mapY+1;
                    flag = false;
                }
            }catch(Exception o) {/*o.printStackTrace();*/}
            
            try{
                if (tileStage.getCell(mapX+1, mapY+1) !=0 && flag){
                    finalMapCell = tileStage.getCell(mapX+1, mapY+1);
                    tileStage.setCell(mapX+1, mapY+1, 0);
                    finalMapX = mapX+1;
                    finalMapY = mapY+1;
                    flag = false;
                }
            }catch(Exception o) {/*o.printStackTrace();*/}
            try{
                if (tileStage.getCell(mapX, mapY) != 0 && flag){
                    finalMapCell = tileStage.getCell(mapX, mapY);
                    tileStage.setCell(mapX, mapY, 0);
                    finalMapX = mapX;
                    finalMapY = mapY;
                    flag = false;
                }
            } catch(Exception o) {/*o.printStackTrace();*/}
            
            try{
                if (tileStage.getCell(mapX+1, mapY) != 0 && flag){
                    finalMapCell = tileStage.getCell(mapX+1, mapY);
                    tileStage.setCell(mapX+1, mapY, 0);
                    finalMapX = mapX+1;
                    finalMapY = mapY;
                    flag = false;
                }
            }catch(Exception o){/*o.printStackTrace();*/}
            
            //if Global.breakThrough == false
            if (!Global.breakThrough)
                sBall.move(0, 2);
        } else{
            try{
                if (tileStage.getCell(mapX, mapY) != 0 && flag){
                    finalMapCell = tileStage.getCell(mapX, mapY);
                    tileStage.setCell(mapX, mapY, 0);
                    finalMapX = mapX;
                    finalMapY = mapY;
                    flag = false;
                }
            }catch(Exception o){/*o.printStackTrace();*/}
            try{
                if (tileStage.getCell(mapX+1, mapY) != 0 && flag){
                    finalMapCell = tileStage.getCell(mapX+1, mapY);
                    tileStage.setCell(mapX+1, mapY, 0);
                    finalMapX = mapX+1;
                    finalMapY = mapY;
                    flag = false;
                }
            }catch(Exception o){/*o.printStackTrace();*/}
            try{
                if (tileStage.getCell(mapX, mapY+1) != 0&& flag){
                    finalMapCell = tileStage.getCell(mapX, mapY+1);
                    tileStage.setCell(mapX, mapY+1, 0);
                    finalMapX = mapX;
                    finalMapY = mapY+1;
                    flag = false;
                }
            } catch(Exception o){/*o.printStackTrace();*/}
            
            try{
                if (tileStage.getCell(mapX+1, mapY+1) !=0 && flag){
                    finalMapCell = tileStage.getCell(mapX+1, mapY+1);
                    tileStage.setCell(mapX+1, mapY+1, 0);
                    finalMapX = mapX+1;
                    finalMapY = mapY+1;
                    flag = false;
                }
            }catch(Exception o){/*o.printStackTrace();*/}
            
            if (!Global.breakThrough)
                sBall.move(0, -2);
        }
        try{
            if (tileStage.getCell(mapX, mapY+2) != 0 && flag){
                finalMapCell = tileStage.getCell(mapX, mapY+2);
                tileStage.setCell(mapX, mapY+2, 0);
                finalMapX = mapX;
                finalMapY = mapY+2;
                flag = false;
            }
        }catch(Exception o){/*o.printStackTrace();*/}
        try{
            if (tileStage.getCell(mapX+1, mapY+2) != 0 && flag){
                finalMapCell = tileStage.getCell(mapX+1, mapY+2);
                tileStage.setCell(mapX+1, mapY+2, 0);
                finalMapX = mapX+1;
                finalMapY = mapY+2;
                flag = false;
            }
        } catch(Exception o){/*o.printStackTrace();*/}
        
        //when frame 18 is touched it convert ot frame 17
        if (finalMapCell == 18)
            tileStage.setCell(finalMapX, finalMapY, 17);
        //when frame 25 is touched it convert ot frame 22
        if (finalMapCell == 25)
            tileStage.setCell(finalMapX, finalMapY, 23);
        
        //when frame 15 is touched it convert to frame 10
        if (finalMapCell == 15)
            tileStage.setCell(finalMapX, finalMapY, 10);
        //when frame 10 is touched it convert to frame 5
        if (finalMapCell == 10)
            tileStage.setCell(finalMapX, finalMapY, 5);
        
        //if sBall is bullet then return
        if (bullet) return;
        
        //if powerBall is active
        if (Global.powerBall){
            try{pBomb.start();}catch(Exception o){}
            score += 10;
            tileStage.setCell(finalMapX, finalMapY, 0);
            int r, c;
            for (int i = 0; i < 5; ++i){
                c = finalMapX + Global.dr[i];
                r = finalMapY + Global.dc[i];
                try{
                    if (tileStage.getCell(c, r) != 0){
                        tileStage.setCell(c, r, 0);
                        score += 10; // for each cell
                    }
                } catch(Exception o){}
            }
        }
        
        //when breakThrough is active
        if (Global.breakThrough){
            score += 10;
            tileStage.setCell(finalMapX, finalMapY, 0);
            return;
        }
        
        //check whether x reverse or y reverse
        int tCol = ((finalMapX+1) * Global.MAP_TILE_WIDTH) + Global.MAP_SET_X;
        int tRow = ((finalMapY+1) * Global.MAP_TILE_HEIGHT)+ Global.MAP_SET_Y;
        
        if (((ballX <= tCol) || (ballX >= tCol+7) ) && (ballY <= tRow+18) && (ballY >= tRow)){
            sBall.setSpeed(-sBall.getXSpeed(), sBall.getYSpeed());
            if (ballX <= tCol)
                sBall.move(-2, 0);
            else
                sBall.move(2, 0);
        } else{
            sBall.setSpeed(sBall.getXSpeed(), -sBall.getYSpeed());
        }
    }
    
    //This method check whether stage is completed or not if completed all cell must be zero
    private boolean checkStageEnd(){
        int row = Global.MAP_ROW_TILES;
        int col = Global.MAP_COL_TILES;
        int cell;
        
        try{
            for (int i = 0; i < row; ++i){
                for (int j = 0; j < col; ++j){
                    cell = tileStage.getCell(j, i); //getCell(column, row);
                    if (cell != 0) return false;
                }
            }
        }catch(Exception o){
            showBug("error occured");
        }
        return true;
    }
    
    //This method calcualate whether a gift file should place or not
    
    private void placeGift(BallSprite sBall){
        if (rand.nextInt() % giftProbability == 0){
            for (int i = 0; i < 3; ++i){
                if (!sGift[i].isVisible()){
                    int x = Math.abs(rand.nextInt()%10);
                    if (x == 0 && lives > 3)
                        x = 8;
                    sGift[i].setFrame(x);
                    sGift[i].setVisible(true);
                    sGift[i].setPosition(sBall.getX(), sBall.getY());
                    return;
                }
            }
        }
    }
    
    //Below code for debugging purpose and for showing error or information
    private void showBug(String message){
        //this method only used for debugging
        System.out.println(message);
    }
    private void showError(Exception o){
        Alert alert = new Alert("Error!", o.toString(), null, AlertType.ERROR);
        alert.setTimeout(Alert.FOREVER);
        midlet.display.setCurrent(alert);
    }
    private void showInfo(String message){
        Alert alert = new Alert("Info.", message, null, AlertType.INFO);
        alert.setTimeout(Alert.FOREVER);
        midlet.display.setCurrent(alert);
    }
}
