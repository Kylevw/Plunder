/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import static environment.Utility.random;
import environment.Velocity;
import java.awt.Dimension;
import java.awt.Point;
import static plunder.java.main.EntityManager.consumables;
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;
import timer.DurationTimer;

/**
 *
 * @author Kyle
 */
public class ProjectileLaser extends Projectile{
    
    {
//        drawObjectBoundary(true);
    }
    
    private final DurationTimer despawnTimer;
    
    public static final int WIDTH = 10;
    public static final int HEIGHT = 3;
    public static final double WEIGHT = 0;
    public static final int DESPAWN_TIME = 900;
    public static final int ANIMATION_SPEED = Integer.MAX_VALUE;
    
    public ProjectileLaser(Point position, int zDisplacement, Velocity velocity, boolean friendly, int damage, ImageProviderIntf ip, AudioPlayerIntf ap) {
        super(ip.getImage(PImageManager.LASER_BLUE_00), position, zDisplacement, velocity, 0, new Dimension(WIDTH, HEIGHT), WEIGHT, friendly, damage, ip, ap, PImageManager.LASER_BLUE_LIST, ANIMATION_SPEED);
        despawnTimer = new DurationTimer(DESPAWN_TIME);
    }
    
    
    
    @Override
    public void timerTaskHandler() {
        
        if (despawnTimer.isComplete()) setDespawn(true);
        
        move();
        applyZVelocity();
        super.timerTaskHandler();
        if (isFriendly() && getZDisplacement() <= 0) {
            int respawn = random(2);
            if (respawn > 0) consumables.add(new Arrow(getPosition(), 0, new Velocity(getVelocity().x / 2, getVelocity().y / 2), 2, getImageProvider(), getAudioPlayer()));
        }
    }
    
}
