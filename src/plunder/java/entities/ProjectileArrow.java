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

/**
 *
 * @author Kyle
 */
public class ProjectileArrow extends Projectile{
    
    {
        drawObjectBoundary(true);
    }
    
    public static final int WIDTH = 10;
    public static final int HEIGHT = 3;
    public static final double WEIGHT = .2;
    public static final int ANIMATION_SPEED = Integer.MAX_VALUE;
    
    public ProjectileArrow(Point position, int zDisplacement, Velocity velocity, double zVelocity, int rotation, boolean friendly, int damage, ImageProviderIntf ip, AudioPlayerIntf ap) {
        super(ip.getImage(PImageManager.ARROW_00), position, new Dimension(WIDTH, HEIGHT), WEIGHT, rotation, friendly, damage, ip, ap, PImageManager.ARROW_LIST, ANIMATION_SPEED);
        setVelocity(velocity);
        setZDisplacement(zDisplacement);
        setZVelocity(zVelocity);
    }
    
    
    
    @Override
    public void timerTaskHandler() {
        move();
        applyZVelocity();
        super.timerTaskHandler();
    }
    
}
