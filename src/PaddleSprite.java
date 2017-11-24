/*
 * PaddleSprite.java
 *
 * Created on August 27, 2007, 3:16 AM
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

public class PaddleSprite extends Sprite {
    private int viewWidth;
    
    public PaddleSprite(Image img, int viewWidth) {
        super(img);
        this.viewWidth = viewWidth;
        setFrame(1); //set normal paddle
    }
    //Fot animated sprite
    public PaddleSprite(Image img, int frameWidth, int frameHeight, int viewWidth) {
        super(img, frameWidth, frameHeight);
        this.viewWidth = viewWidth;
        setFrame(1); //set normal paddle
    }
    
    public void changeBallMotion(BallSprite ball, boolean Return){
        int x = getX();
        int width = getWidth();
        int frameNumber = getFrame(); // 0=big, 1=normal, 2=small, 3=fireBig, 4=fireNormal,5=fireSmall
        int ballX = ball.getX();
        
        
        int ballXSpeed = 0; 
        int ballYSpeed = 0; 
        
        switch(Global.ballSpeed){
            case 0: ballXSpeed = Global.BALL_XSPEED_FAST; ballYSpeed = Global.BALL_YSPEED_FAST; break;
            case 1: ballXSpeed = Global.BALL_XSPEED_NORMAL; ballYSpeed= Global.BALL_YSPEED_NORMAL; break;
            case 2: ballXSpeed = Global.BALL_XSPEED_SLOW; ballYSpeed = Global.BALL_YSPEED_SLOW;
            
            if (ball.getXSpeed() < 0 && ball.getYSpeed() >= 0)
                ball.setSpeed(-ballXSpeed, ballYSpeed);
            else if (ball.getXSpeed() < 0 && ball.getYSpeed() < 0)
                ball.setSpeed(-ballXSpeed, -ballYSpeed);
            else if (ball.getXSpeed() >= 0 && ball.getYSpeed() < 0)
                ball.setSpeed(ballXSpeed, -ballYSpeed);
            else if (ball.getXSpeed() >= 0 && ball.getYSpeed() >= 0)
                ball.setSpeed(ballXSpeed, ballYSpeed);
      
            if (Return) return;
        }
        //a paddle is divided into 6 parts
        if (frameNumber == 0 || frameNumber == 3){
            //when big frame then width is 100px
            if (ballX >= x+width-10){
               ballXSpeed = Math.abs(ballXSpeed) + 1;
                ballYSpeed -= 1;
            }
            else if (ballX >= x+width-30){
                ballXSpeed = Math.abs(ballXSpeed) + 1;
                ballYSpeed -= 1;
            }
            else if (ballX >= x+width-40){
                ballXSpeed = Math.abs(ballXSpeed-2);
                ;//normal situation remains same  
            }
            else if (ballX >= x+width-50){
                ballXSpeed = Math.abs(ballXSpeed-3);
                ;//normal situation remains same  
            }
            else if (ballX >= x+40){
                ballXSpeed = -Math.abs(ballXSpeed-3);
                //no need th change ySpeed
            }
            else if (ballX >= x+30){
                ballXSpeed = -Math.abs(ballXSpeed-2);
                //no need th change ySpeed
            }
            else if (ballX >= x + 10){
                ballXSpeed = -Math.abs(ballXSpeed-2);
                ballYSpeed -= 1;
            }
            else if (ballX >= x){
                ballXSpeed = -Math.abs(ballXSpeed-1);
                ballYSpeed -= 1;
            }            
        } 
        else if (frameNumber == 1 || frameNumber == 4){
            //when normal frame then width is 60px (20px is minus from both side)
            if (ballX >= x+width-30){
                ballXSpeed = Math.abs(ballXSpeed) + 1;
                ballYSpeed -= 1;
            }
            else if (ballX >= x+width-40){
                ballXSpeed = Math.abs(ballXSpeed-2);
                ballYSpeed -= 1;
            }
            else if (ballX >= x+width-50){
                ballXSpeed = Math.abs(ballXSpeed-3);
                //normal situation remains same  
            }
            else if (ballX >= x+width-60){
                ballXSpeed = -Math.abs(ballXSpeed-3);
                //no need th change ySpeed
            }
            else if (ballX >= x+width-70){
                ballXSpeed = -Math.abs(ballXSpeed-2);
                ballYSpeed -= 1;
            }
            else if (ballX >= x+width-85){
                ballXSpeed = -Math.abs(ballXSpeed-1);
                ballYSpeed -= 1;
            }              
        } 
        else if (frameNumber == 2 || frameNumber == 5){
            //when normal frame then width is 50px (25px is minus from both side)
            if (ballX >= x+width-35){
                ballXSpeed = Math.abs(ballXSpeed) + 1;
                ballYSpeed -= 1;
            }
            else if (ballX >= x+width-45){
                ballXSpeed = Math.abs(ballXSpeed-2);
                ballYSpeed -= 1;
            }
            else if (ballX >= x+width-55){
                ballXSpeed = Math.abs(ballXSpeed-3);
                ;//normal situation remains same  
            }
            else if (ballX >= x+width-65){
                ballXSpeed = -Math.abs(ballXSpeed-3);
                //no need th change ySpeed
            }
            else if (ballX >= x+width-75){
                ballXSpeed = -Math.abs(ballXSpeed-2);
                ballYSpeed -= 1;
            }
            else if (ballX >= x+width-85){
                ballXSpeed = -Math.abs(ballXSpeed-1);
                ballYSpeed -= 1;
            }              
        }
        ball.setSpeed(ballXSpeed, -ballYSpeed); // when ball touches paddle y must be reverse
    }
    
    public void update(){
        checkBounds();
    }
    private void checkBounds(){
        //this code stop the paddle
        int x = getX();
        int y = getY();
        int frameNumber = getFrame();
        
        if (frameNumber == 0 || frameNumber == 3){
            if (x < 0)
                setPosition(0, y);
            else if (x > (viewWidth - getWidth()))
                setPosition(viewWidth - getWidth(), y);
        }
        else if (frameNumber == 1 || frameNumber == 4){
            if (x < -20)
                setPosition(-20, y);
            else if (x > (viewWidth - getWidth()+20))
                setPosition(viewWidth - getWidth()+20, y);
        }
        else if (frameNumber == 2 || frameNumber == 5){
            if (x < -25)
                setPosition(-25, y);
            else if (x > (viewWidth - getWidth() + 25))
                setPosition(viewWidth - getWidth() + 25, y);
        }
    }
    
}
