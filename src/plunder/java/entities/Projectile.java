/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import environment.Velocity;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import path.TrigonometryCalculator;
import plunder.java.main.EntityManager;
import static plunder.java.main.EntityManager.player;
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;

/**
 *
 * @author Kyle
 */
public class Projectile extends Entity{

    private double rotation;
    private final boolean friendly;
    private final int damage;
    
    public Projectile(BufferedImage image, Point position, int zDisplacement, Velocity velocity, double zVelocity, Dimension size, double weight, boolean friendly, int damage, ImageProviderIntf ip, AudioPlayerIntf ap, String imageListName, int animationSpeed) {
        super(image, position, size, weight, ip, ap, imageListName, animationSpeed);
        setVelocity(velocity);
        setZDisplacement(zDisplacement);
        setZVelocity(zVelocity);
        this.friendly = friendly;
        this.damage = damage;
    }
    
    public boolean isFriendly() {
        return friendly;
    }
    
    @Override
    public void draw(Graphics2D graphics) {
        if (drawBoundary()) {
            graphics.setColor(Color.RED);
            graphics.drawPolygon(getProjectileGroundBoundary());
        }
        
        AffineTransform at = graphics.getTransform();
        graphics.rotate(Math.toRadians(rotation), getPosition().x, getPosition().y - getZDisplacement() - 2);
        graphics.drawImage(getImage(), getPosition().x - (getSize().width / 2), getPosition().y - (getSize().height) - getZDisplacement(), getSize().width, getSize().height, null);

        graphics.setTransform(at);
        
        if (drawBoundary()) {
            graphics.setColor(Color.BLUE);
            graphics.drawPolygon(getProjectileBoundary());
        }
    }
    
    @Override
    public void timerTaskHandler() {
        
        setRotation(TrigonometryCalculator.getVelocityAngle(getVelocity()));
        
        if (getZDisplacement() <= 0) setDespawn(true);
        else if (friendly) {
            EntityManager.getEnemies().stream().filter((enemy) -> (!despawn() &&
                    intersectsHitbox(enemy))).forEach((enemy) -> {
                        enemy.accelerateKnockbackVelocity(getVelocity());
                        enemy.damage(damage);
                        setDespawn(true);
            });
        } else if (intersectsHitbox(player)) {
            player.damage(damage);
            setDespawn(true);
        }
        
        super.timerTaskHandler();
        
    }
    
    public boolean intersectsHitbox(Entity entity) {
        return getProjectileGroundBoundary().intersects(entity.getObjectGroundBoundary()) && 
                getProjectileBoundary().intersects(entity.getObjectBoundary());
    }
    
    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
    
    public int getDamage() {
        return damage;
    }
    
    public Polygon getProjectileBoundary() {
        Polygon groundBoundary = getProjectileGroundBoundary();
        groundBoundary.translate(0, -getZDisplacement() - 2);
        return groundBoundary;
    }
    
    public Polygon getProjectileGroundBoundary() {
        Rectangle groundBoundary = new Rectangle(
                getPosition().x - (getSize().width / 2),
                getPosition().y - (getSize().height / 2),
                getSize().width, getSize().height);
        
        
        int[] xs = new int[]{
            groundBoundary.x,
            groundBoundary.x + groundBoundary.width,
            groundBoundary.x + groundBoundary.width,
            groundBoundary.x};
        
        int[] ys = new int[]{
            groundBoundary.y,
            groundBoundary.y,
            groundBoundary.y + groundBoundary.height,
            groundBoundary.y + groundBoundary.height};

        double[] src = new double[]{
        xs[0], ys[0],
        xs[1], ys[1],
        xs[2], ys[2],
        xs[3], ys[3]};
        
        double[] dst = new double[8];
        
        AffineTransform t = AffineTransform.getRotateInstance(Math.toRadians(rotation), getPosition().x, getPosition().y);
        t.transform(src, 0, dst, 0, 4);
        
        int[] xTrans = new int[]{(int)dst[0], (int)dst[2],(int)dst[4],(int)dst[6]};
        int[] yTrans = new int[]{(int)dst[1], (int)dst[3],(int)dst[5],(int)dst[7]};
        
        return new Polygon(xTrans, yTrans, 4); 
    }
    
}
