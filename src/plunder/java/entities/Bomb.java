/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import environment.Velocity;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import static plunder.java.main.EntityManager.player;
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;

/**
 *
 * @author Kyle
 */
public class Bomb extends Consumable {
    
    {
//        drawObjectBoundary(true);
    }
    
    private static final int WEIGHT = 3;
    private static final int ANIMATION_SPEED = 80;
    private static final int WIDTH = 5;
    private static final int HEIGHT = 7;

    
    public Bomb(Point position, int zDisplacement, Velocity velocity, double zVelocity, ImageProviderIntf ip, AudioPlayerIntf ap) {
        super(ip.getImage(PImageManager.CONSUMABLE_BOMB_00), position, zDisplacement, velocity, zVelocity, new Dimension(WIDTH, HEIGHT), WEIGHT, ip, ap, PImageManager.CONSUMABLE_BOMB_LIST, ANIMATION_SPEED);
    }
    
    @Override
    public void timerTaskHandler() {
        move();
        super.timerTaskHandler();
    }
    
    @Override
    public Rectangle getObjectGroundBoundary() {
        return new Rectangle(getPosition().x - (getSize().width / 2),
        getPosition().y - getSize().height + 2,
        getSize().width, getSize().height - 2);
    }
    
    @Override
    public void pickUpEvent() {
        if (player != null) player.addBombs(1);
    }
    
}
