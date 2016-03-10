/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import environment.Actor;
import environment.Velocity;
import plunder.java.resources.PImageManager;
import plunder.java.resources.ImageProviderIntf;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

/**
 *
 * @author Kyle
 */
public class Entity extends Actor{
    
    {
        zDisplacement = 0;
        zVelocity = 0;
        drawBoundary = false;
    }
    
    private boolean onGround;
    
    private boolean applyGravity = true;
    
    private final Dimension size;
    private boolean drawBoundary;
    private int zDisplacement;
    private double zVelocity;
    
    private final ImageProviderIntf ip;
    
    public Entity(BufferedImage image, Point position, Dimension size, ImageProviderIntf ip) {
        super(image, position, new Velocity(0, 0));
        this.size = size;
        this.ip = ip;
    }
    
    public Dimension getSize() {
        return size;
    }
    
    public void timerTaskHandler() {
        
    }
    
    @Override
    public void draw(Graphics2D graphics) {
        graphics.drawImage(ip.getImage(PImageManager.ENTITY_SHADOW), getShadowRectangle().x, getShadowRectangle().y, getShadowRectangle().width, getShadowRectangle().height, null);
        if (drawBoundary) {
            graphics.setColor(Color.RED);
            graphics.draw(getObjectGroundBoundary());
        }
        graphics.drawImage(getImage(), getPosition().x - (size.width / 2), getPosition().y - (size.height) - zDisplacement, size.width, size.height, null);
        if (drawBoundary) {
            graphics.setColor(Color.BLUE);
            graphics.draw(getObjectBoundary());
        }
    }
    
    @Override
    public Rectangle getObjectBoundary() {
        return new Rectangle(getObjectGroundBoundary().x, getObjectGroundBoundary().y - zDisplacement,
        getObjectGroundBoundary().width, getObjectGroundBoundary().height);
    }
    
    public Rectangle getObjectGroundBoundary() {
        return new Rectangle(getPosition().x - (size.width / 2),
        getPosition().y - (size.height),
        size.width, size.height);
    }
    
    public void drawObjectBoundary(boolean drawBoundary) {
        this.drawBoundary = drawBoundary;
    }
    
    public int getZDisplacement() {
        return zDisplacement;
    }
    
    public int getZVelocity() {
        return (int) zVelocity;
    }
    
    public void setZVelocity(int zVelocity) {
        this.zVelocity = zVelocity;
    }
    
    public void accelerateZVelocity(double zVelocity) {
        this.zVelocity += zVelocity;
    }
    
    public void setZDisplacement(int zDisplacement) {
        this.zDisplacement = zDisplacement;
    }
    
    
    @Override
    public void move() {
        applyZVelocity();
        super.move();
    }
    
    public void applyZVelocity() {
        zDisplacement += zVelocity;
        
        if (applyGravity) {
            if (zDisplacement <= 0) {
                zDisplacement = 0;
                zVelocity = 0;
            } else accelerateZVelocity(-.5);
        }
    }
    
    public Rectangle getShadowRectangle() {
        int shadowWidth = getObjectGroundBoundary().width - 4 - (zDisplacement / 4 * 2);
        if (shadowWidth < 0) shadowWidth = 0;
        return new Rectangle(getObjectGroundBoundary().x + 2 + (zDisplacement / 4), getObjectGroundBoundary().y + 2 + (getObjectGroundBoundary().height / 2) + (zDisplacement / 4), shadowWidth, shadowWidth);
    }
    
    public boolean drawBoundary() {
        return drawBoundary;
    }
    
    public boolean intersects(Entity entity) {
        return getObjectBoundary().intersects(entity.getObjectBoundary()) &&
        getObjectGroundBoundary().intersects(entity.getObjectGroundBoundary());
    }
    
    public void applyGravity(boolean applyGravity) {
        this.applyGravity = applyGravity;
    }
    
    public void setImage(String image) {
        super.setImage(ip.getImage(image));
    }
    
    public ImageProviderIntf getImageProvider() {
        return ip;
    }
    
    public boolean onGround() {
        return zDisplacement == 0;
    }
    
}