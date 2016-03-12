/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import plunder.java.main.EntityManager;
import static plunder.java.main.EntityManager.player;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;

/**
 *
 * @author Kyle
 */
public class Consumable extends Entity{

    public Consumable(BufferedImage image, Point position, Dimension size, int weight, ImageProviderIntf ip, int animationSpeed) {
        super(image, position, size, weight, ip, PImageManager.PLAYER_WALK_DOWN_LIST, animationSpeed);
    }
    
    @Override
    public void timerTaskHandler() {
        if (EntityManager.player != null && getObjectBoundary().intersects(EntityManager.player.getObjectBoundary()) && getObjectGroundBoundary().intersects(EntityManager.player.getObjectGroundBoundary())) {
            playerPickUp();
        }
    }
    
    public void playerPickUp() {
        setDespawn(true);
    }
    
}
