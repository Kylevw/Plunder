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
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;

/**
 *
 * @author Kyle
 */
public class Arrow extends Consumable{
    
    public static final int WIDTH = 3;
    public static final int HEIGHT = 8;
    public static final int WEIGHT = 3;
    public static final int ANIMATION_SPEED = 80;

    public Arrow(Point position, int zDisplacement, Velocity velocity, double zVelocity, ImageProviderIntf ip, AudioPlayerIntf ap) {
        super(ip.getImage(PImageManager.CONSUMABLE_ARROW_00), position, zDisplacement, velocity, zVelocity, new Dimension(WIDTH, HEIGHT), WEIGHT, ip, ap, PImageManager.CONSUMABLE_ARROW_LIST, ANIMATION_SPEED);
    }
    
    @Override
    public void timerTaskHandler() {
        move();
        super.timerTaskHandler();
    }
    
    @Override
    public void pickUpEvent() {
        if (player != null) player.addArrows(1);
    }
    
}
