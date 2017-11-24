/*
 * BallSprite.java
 *
 * Created on August 27, 2007, 2:32 AM
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
import java.util.*;

public class BallSprite extends Sprite {
    private int viewWidth;
    private int viewHeight;
    private int xSpeed;
    private int ySpeed;
    private boolean ballMoving;
    
    //this constructor only for bullet
    public BallSprite(Image img){
        super(img);
    }
    public BallSprite(Sprite sp){
        super(sp);
    }
    /* above two constructor only necessary for bullet */
    
    //this constructor for non animated sprite
    public BallSprite(Image img, int xMoveSpeed, int yMoveSpeed, int viewWidth, int viewHeight) {
        super(img);
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        xSpeed = xMoveSpeed;
        ySpeed = yMoveSpeed; 
    }
    //Fot animated sprite
    public BallSprite(Image img, int frameWidth, int frameHeight, int xMoveSpeed, int yMoveSpeed, int viewWidth, int viewHeight) {
        super(img, frameWidth, frameHeight);
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        xSpeed = xMoveSpeed;
        ySpeed = yMoveSpeed; 
    }
    
    public int getXSpeed(){
        return xSpeed;
    }
    public int getYSpeed(){
        return ySpeed;
    }
    
    public void setSpeed(int x, int y){
        this.xSpeed = x;
        this.ySpeed = y;
    }
    
    public void setBallMoving(boolean b){
        ballMoving = b;
    }
    public boolean getBallMoving(){
        return ballMoving;
    }
    
    public void update(){
        move(xSpeed, ySpeed);
        checkBounds();
    }
    private void checkBounds(){
        //this code bounce the ball
        int x = getX();
        int y = getY();
        
        if (x < 0){
            xSpeed = -xSpeed;
            move(3, 0);
        }
        else if (x > (viewWidth-getWidth())){
            xSpeed = -xSpeed;
            if (getYSpeed() >= 0)
                move(-(getWidth()/2), 3);
            else
                move(-(getWidth()/2), -3);
        }
        
        if (y <= 0){
            ySpeed = -ySpeed;
            move(0, 3);
        }
        if (y > viewHeight){
            setBallMoving(false);
            setVisible(false);
            --Global.numberOfBall;
            if (Global.numberOfBall == 0)
                Global.SET_BALL_IN_PADDLE = true;
            
        }
    }
}
