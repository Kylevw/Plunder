/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import environment.Velocity;
import java.awt.Dimension;
import java.awt.Point;
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;
import timer.DurationTimer;

/**
 *
 * @author Kyle
 */
public class PrimedBomb extends Entity{
    
    private final DurationTimer bombTimer;
    
    private static final int ANIMATION_SPEED = 80;
    private static final int WIDTH = 7;
    private static final int HEIGHT = 8;
    private static final double WEIGHT = 3;
    private static final int FUSE_DELAY = 1000;

    public PrimedBomb(Point position, int zDisplacement, Velocity velocity, int zVelocity, ImageProviderIntf ip, AudioPlayerIntf ap) {
        super(ip.getImage(PImageManager.PRIMED_BOMB_00), position, new Dimension(WIDTH, HEIGHT), WEIGHT, ip, ap, PImageManager.PRIMED_BOMB_LIST, ANIMATION_SPEED);
        setZDisplacement(zDisplacement);
        setVelocity(velocity);
        setZVelocity(zVelocity);
        bombTimer = new DurationTimer(FUSE_DELAY);
    }
    
    @Override
    public void timerTaskHandler() {
        move();
        
        super.timerTaskHandler();
        
        if ((int) (getZDisplacement() + getZVelocity()) <= 0) {
            if ((int) getZVelocity() < 0) setZVelocity(-getZVelocity() * 2 / 3);
            else if (getZDisplacement() == 0){
                setVelocity(0, 0);
            }
        }
        
        if (bombTimer.isComplete()) {
            explode(2);
        }
        
    }
}
