/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import environment.Velocity;
import java.awt.Dimension;
import java.awt.Point;
import static plunder.java.main.EntityManager.player;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;
import timer.DurationTimer;

/**
 *
 * @author Kyle
 */
public class Heart extends Consumable {
    
    {
//        drawObjectBoundary(true);
    }
    
    private int yChangeTimer = 2;
    private boolean fallAnimation;
    
    private static final int WEIGHT = 1;
    private static final int ANIMATION_SPEED = 160;
    private static final int WIDTH = 5;
    private static final int HEIGHT = 5;

    private final DurationTimer moveTimer;
    private static final int MOVE_DELAY = 100;
    
    public Heart(Point position, int zDisplacement, Velocity velocity, double zVelocity, ImageProviderIntf ip) {
        super(ip.getImage(PImageManager.CONSUMABLE_HEART_00), position, zDisplacement, velocity, zVelocity, new Dimension(WIDTH, HEIGHT), WEIGHT, ip, PImageManager.CONSUMABLE_HEART_LIST, ANIMATION_SPEED);
        moveTimer = new DurationTimer(MOVE_DELAY);
    }
    
    @Override
    public void timerTaskHandler() {
        
        if (fallAnimation) {
            yChangeTimer++;
            if (getZDisplacement() > 0 && yChangeTimer > 2) {
                yChangeTimer = 0;
                setVelocity(-getVelocity().x, getVelocity().y);
            }
        } else if (getZVelocity() < 0) {
            fallAnimation = true;
            setVelocity(1, 0);
        }
        
        if (getZVelocity() < -1) setZVelocity(-1);
        
        super.timerTaskHandler();
        
        if (moveTimer.isComplete() || !fallAnimation) {
            move();
            moveTimer.start();
        }
    }
    
    @Override
    public void pickUpEvent() {
        if (player != null) player.heal(2);
    }
    
}
