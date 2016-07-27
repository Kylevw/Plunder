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
import java.awt.Rectangle;
import static plunder.java.main.EntityManager.consumables;
import plunder.java.main.TileMap;
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;

/**
 *
 * @author Kyle
 */
public class ProjectileArrow extends Projectile{
    
    {
//        drawObjectBoundary(true);
    }
    
    public static final int WIDTH = 10;
    public static final int HEIGHT = 3;
    public static final double WEIGHT = .2;
    public static final int ANIMATION_SPEED = Integer.MAX_VALUE;
    
    public ProjectileArrow(Point position, int zDisplacement, Velocity velocity, double zVelocity, boolean friendly, int damage, ImageProviderIntf ip, AudioPlayerIntf ap) {
        super(ip.getImage(PImageManager.ARROW_00), position, zDisplacement, velocity, zVelocity, new Dimension(WIDTH, HEIGHT), WEIGHT, friendly, damage, ip, ap, PImageManager.ARROW_LIST, ANIMATION_SPEED);
    }
    
    
    
    @Override
    public void timerTaskHandler() {
        move();
        applyZVelocity();
        if (TileMap.collision(new Rectangle(getObjectGroundBoundary().x + getVelocity().x,
                getObjectGroundBoundary().y + getVelocity().y, getObjectGroundBoundary().width,
                getObjectGroundBoundary().height))) {
            int respawn = random(2);
            if (respawn > 0) consumables.add(new Arrow(getPosition(), 0, new Velocity(getVelocity().x / 2, getVelocity().y / 2), 2, getImageProvider(), getAudioPlayer()));
        }
        super.timerTaskHandler();
        if (isFriendly() && getZDisplacement() <= 0) {
            int respawn = random(2);
            if (respawn > 0) consumables.add(new Arrow(getPosition(), 0, new Velocity(getVelocity().x / 2, getVelocity().y / 2), 2, getImageProvider(), getAudioPlayer()));
        }
    }
    
}
