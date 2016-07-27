/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import environment.Actor;
import environment.Physics;
import environment.Velocity;
import images.Animator;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import static plunder.java.main.EntityManager.explosions;
import plunder.java.main.MapManager;
import static plunder.java.main.MapManager.environmentGrid;
import plunder.java.main.TileMap;
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;

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
    private double weight;
    
    private boolean explode;
    private int explosionSize;
    
    private boolean despawn;
    
    private final Animator animator;
    
    private final Dimension size;
    private boolean drawBoundary;
    private int zDisplacement;
    private double zVelocity;
    private double xKnockback, yKnockback;
    private boolean applyGravity;
    
    private final ImageProviderIntf ip;
    private final AudioPlayerIntf ap;
    
    public Entity(BufferedImage image, Point position, Dimension size, double weight, ImageProviderIntf ip, AudioPlayerIntf ap, String imageListName, int animationSpeed) {
        super(image, position, new Velocity(0, 0));
        this.ap = ap;
        this.size = size;
        this.ip = ip;
        this.weight = weight;
        if (this.weight < 0) this.weight = 0;
        PImageManager im = new PImageManager();
        this.animator = new Animator(im, ip.getImageList(imageListName), animationSpeed);
        applyGravity = true;
    }
    
    public void accelerateKnockbackVelocity(int x, int y) {
        xKnockback += x;
        yKnockback += y;
    }
    
    public void applyGravity(boolean applyGravity) {
        this.applyGravity = applyGravity;
    }
    
    public void accelerateKnockbackVelocity(Velocity velocity) {
        accelerateKnockbackVelocity(velocity.x, velocity.y);
    }
    
    public Dimension getSize() {
        return size;
    }
    
    public void timerTaskHandler() {
        updateImage();
        
        if (!TileMap.collision(new Rectangle(getObjectGroundBoundary().x + (int) xKnockback,
                getObjectGroundBoundary().y, getObjectGroundBoundary().width,
                getObjectGroundBoundary().height))) {
            setPosition(getPosition().x + (int) xKnockback, getPosition().y);
        }
        if (!TileMap.collision(new Rectangle(getObjectGroundBoundary().y,
                getObjectGroundBoundary().y + (int) yKnockback, getObjectGroundBoundary().width,
                getObjectGroundBoundary().height))) {
            setPosition(getPosition().x, getPosition().y + (int) yKnockback);
        }
        if (xKnockback != 0) xKnockback -= weight * xKnockback / Math.abs(xKnockback) / 8;
        if (yKnockback != 0) yKnockback -= weight * yKnockback / Math.abs(yKnockback) / 8;
        
        if (explode) {
            explosions.add(new Explosion(getPosition(), explosionSize, ap));
            setDespawn(true);
        }
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
        Rectangle groundBoundary = getObjectGroundBoundary();
        groundBoundary.translate(0, -zDisplacement);
        return groundBoundary;
//        return new Rectangle(groundBoundary.x, groundBoundary.y - zDisplacement,
//        groundBoundary.width, groundBoundary.height);
    }
    
    @Override
    public Point getCenterOfMass() {
        return Physics.getCenterOfMass(this.getObjectGroundBoundary());
    }
    
    public Rectangle getObjectGroundBoundary() {
        return new Rectangle(getPosition().x - (size.width / 2),
        getPosition().y - size.height,
        size.width, size.height);
    }
    
    public void drawObjectBoundary(boolean drawBoundary) {
        this.drawBoundary = drawBoundary;
    }
    
    public int getZDisplacement() {
        return zDisplacement;
    }
    
    public void explode(int explosionSize) {
        this.explosionSize = explosionSize;
        explode = true;
    }
    
    public double getZVelocity() {
        return zVelocity;
    }
    
    public void setZVelocity(double zVelocity) {
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
        if (!TileMap.collision(new Rectangle(getObjectGroundBoundary().x + getVelocity().x,
                getObjectGroundBoundary().y, getObjectGroundBoundary().width,
                getObjectGroundBoundary().height))) {
            setPosition(getPosition().x + getVelocity().x, getPosition().y);
        }
        if (!TileMap.collision(new Rectangle(getObjectGroundBoundary().x,
                getObjectGroundBoundary().y + getVelocity().y, getObjectGroundBoundary().width,
                getObjectGroundBoundary().height))) {
            setPosition(getPosition().x, getPosition().y + getVelocity().y);
        }
    }
    
    public void applyZVelocity() {
        zDisplacement += zVelocity;
        
        if (applyGravity) {
            if (zDisplacement <= 0) {
                zDisplacement = 0;
                zVelocity = 0;
            } else accelerateZVelocity(-.125 * weight);
        }
    }
    
    public Rectangle getShadowRectangle() {
        int shadowWidth = getObjectGroundBoundary().width - ((1 + (getObjectGroundBoundary().width / 6)) * 2) - (zDisplacement / 4 * 2);
        if (shadowWidth < 0) shadowWidth = 0;
        return new Rectangle(getObjectGroundBoundary().x + (1 + (getObjectGroundBoundary().width / 6)) + (zDisplacement / 4), getObjectGroundBoundary().y + (1 + (getObjectGroundBoundary().width / 6)) + (getObjectGroundBoundary().height / 2) + (zDisplacement / 4), shadowWidth, shadowWidth);
    }
    
    public boolean drawBoundary() {
        return drawBoundary;
    }
    
    public boolean intersects(Entity entity) {
        return getObjectBoundary().intersects(entity.getObjectBoundary()) &&
        getObjectGroundBoundary().intersects(entity.getObjectGroundBoundary());
    }
    
    public void setImage(String image) {
        super.setImage(ip.getImage(image));
    }
    
    public Animator getAnimator() {
        return animator;
    }
    
    public ImageProviderIntf getImageProvider() {
        return ip;
    }
    
    public AudioPlayerIntf getAudioPlayer() {
        return ap;
    }
    
    public boolean onGround() {
        return zDisplacement == 0;
    }
    
    public void setDespawn(boolean despawn) {
        this.despawn = despawn;
    }
    
    public boolean despawn() {
        return despawn;
    }
    
    private void updateImage() {
        if (animator != null) setImage(animator.getCurrentImage());
    }
    
    public void setImageList(String listName) {
        animator.setImageNames(getImageProvider().getImageList(listName));
    }
    
}
