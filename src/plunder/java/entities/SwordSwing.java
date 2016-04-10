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
import java.awt.geom.AffineTransform;
import path.TrigonometryCalculator;
import plunder.java.main.EntityManager;
import static plunder.java.main.EntityManager.player;
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;
import timer.DurationTimer;

/**
 *
 * @author Kyle
 */
public class SwordSwing extends Projectile{
    
    {
//        drawObjectBoundary(true);
    }
    
    public static final int ANIMATION_SPEED = 40;
    public static final int SWING_TIME = ANIMATION_SPEED * 2;
    public static final int KNOCKBACK = 6;
    public static final int WIDTH = 4;
    public static final int HEIGHT = 12;
    
    private final double rotation;
    
    private final DurationTimer swingTimer;

    public SwordSwing(Point position, int zDisplacement, double rotation, boolean friendly, int damage, ImageProviderIntf ip, AudioPlayerIntf ap) {
        super(ip.getImage(PImageManager.SWORD_SWING_00), position, zDisplacement, new Velocity(0, 0), 0, new Dimension(WIDTH, HEIGHT), 0, friendly, damage, ip, ap, PImageManager.SWORD_SWING_LIST, ANIMATION_SPEED);
        swingTimer = new DurationTimer(SWING_TIME);
        this.rotation = rotation;
    }
    
    @Override
    public void draw(Graphics2D graphics) {
        setImage(getAnimator().getCurrentImage());
        if (drawBoundary()) {
            graphics.setColor(Color.RED);
            graphics.drawPolygon(getProjectileGroundBoundary());
        }
        
        AffineTransform at = graphics.getTransform();
        graphics.rotate(Math.toRadians(rotation), getPosition().x, getPosition().y - getZDisplacement() - 2);
        graphics.drawImage(getImage(), getPosition().x - (getSize().width / 2), getPosition().y - (getSize().height) - getZDisplacement() + 4, getSize().width, getSize().height, null);

        graphics.setTransform(at);
        
        if (drawBoundary()) {
            graphics.setColor(Color.BLUE);
            graphics.drawPolygon(getProjectileBoundary());
        }
    }
    
    @Override
    public void timerTaskHandler() {
        if (swingTimer.isComplete()) setDespawn(true);
        setRotation(rotation);
        
        if (getZDisplacement() <= 0) setDespawn(true);
        else if (isFriendly()) {
            EntityManager.getEnemies().stream().filter((enemy) -> (!despawn() &&
                    getProjectileGroundBoundary().intersects(enemy.getObjectGroundBoundary()) && 
                    getProjectileBoundary().intersects(enemy.getObjectBoundary()))).forEach((enemy) -> {
                        enemy.accelerateKnockbackVelocity(TrigonometryCalculator.calculateVelocity(rotation, KNOCKBACK));
                        enemy.damage(getDamage());
            });
        } else if (getProjectileGroundBoundary().intersects(player.getObjectGroundBoundary()) && 
                    getProjectileBoundary().intersects(player.getObjectBoundary())) {
            player.damage(getDamage());
        }
    }
}
