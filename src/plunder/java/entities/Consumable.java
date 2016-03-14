/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import environment.Velocity;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import static plunder.java.main.EntityManager.player;
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;

/**
 *
 * @author Kyle
 */
public class Consumable extends Entity{

    public Consumable(BufferedImage image, Point position, int zDisplacement, Velocity velocity, double zVelocity, Dimension size, int weight, ImageProviderIntf ip, AudioPlayerIntf ap, String imageNameList, int animationSpeed) {
        super(image, position, size, weight, ip, ap, imageNameList, animationSpeed);
        setZDisplacement(zDisplacement);
        setVelocity(velocity);
        setZVelocity(zVelocity);
    }
    
    @Override
    public void timerTaskHandler() {
        
        if (player != null && !despawn() && getObjectBoundary().intersects(player.getObjectBoundary()) && getObjectGroundBoundary().intersects(player.getObjectGroundBoundary())) {
            playerPickUp();
        }
        
        if ((int) (getZDisplacement() + getZVelocity()) <= 0) {
            if ((int) getZVelocity() < 0) setZVelocity(-getZVelocity() * 2 / 3);
            else if (getZDisplacement() == 0){
                setVelocity(0, 0);
            }
        }
        
        super.timerTaskHandler();
        
    }
    
    public void playerPickUp() {
        setDespawn(true);
        player.displayItem((BufferedImage) getAnimator().getCurrentImage());
        pickUpEvent();
    }
    
    public void pickUpEvent() {
        
    }
    
}
