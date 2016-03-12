/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import static environment.Utility.random;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import path.TrigonometryCalculator;
import plunder.java.main.EntityManager;
import static plunder.java.main.EntityManager.player;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;
import timer.DurationTimer;

/**
 *
 * @author Kyle
 */
public class Bat extends Enemy {
    
    {
//        drawObjectBoundary(true);
    }

    private int xSpeed = 0;
    private int ySpeed = 0;
    
    private final DurationTimer moveTimer;
    
    private static final int IDLE_MOVEMENT_DELAY = 40;
    public static final int ATTACK_MOVEMENT_DELAY = 20;
    
    private static final int MAX_HEALTH = 6;
    private static final int STRENGTH = 1;
    public static final int DEFENSE = 0;
    public static final int SIGHT_DISTANCE = 100;
    public static final int ATTACK_DISTANCE = 20;
    public static final int ATTACK_DELAY = 900;
    
    private static final int DEFAULT_Z_DISPLACEMENT = 11;
    
    private static final int ANIMATION_SPEED = 80;
    public static final Dimension SIZE = new Dimension(9, 8);
    
    public Bat(Point position, ImageProviderIntf ip) {
        super(ip.getImage(PImageManager.BAT_UP), position, SIZE, 0, ip, PImageManager.BAT_LIST, ANIMATION_SPEED, MAX_HEALTH, STRENGTH, DEFENSE, SIGHT_DISTANCE, ATTACK_DISTANCE, ATTACK_DELAY);
        damage(1);
        moveTimer = new DurationTimer(IDLE_MOVEMENT_DELAY);
        setZDisplacement(DEFAULT_Z_DISPLACEMENT);
    }
    
    @Override
    public void timerTaskHandler() {
        if (player != null && player.getObjectGroundBoundary().intersects(getObjectGroundBoundary()) && player.getObjectBoundary().intersects(getObjectBoundary())) {
            player.damage(1);
        }
        xSpeed += (random(3) - 1);
        ySpeed += (random(3) - 1);
        //TODO Min-Max Method
        if (xSpeed > 1) xSpeed = 1;
        else if (xSpeed < -1) xSpeed = -1;
        if (ySpeed > 1) ySpeed = 1;
        else if (ySpeed < -1) ySpeed = -1;
        
        if (getVelocity().x > 1) setVelocity(1, getVelocity().y);
        if (getVelocity().x < -1) setVelocity(-1, getVelocity().y);
        if (getVelocity().y > 1) setVelocity(getVelocity().x, 1);
        if (getVelocity().y < -1) setVelocity(getVelocity().x, -1);
        
        if (moveTimer.isComplete()) {
            move();
            moveTimer.start();
            if (getZDisplacement() > DEFAULT_Z_DISPLACEMENT) {
                setZDisplacement(DEFAULT_Z_DISPLACEMENT);
                setZVelocity(0);
            }
        }
        super.timerTaskHandler();
        
    }
    
    @Override
    public void standardAI() {
        setVelocity(xSpeed, ySpeed);
        moveTimer.setDurationMillis(IDLE_MOVEMENT_DELAY);
    }
    
    @Override
    public void targetAI() {
        setVelocity(TrigonometryCalculator.calculateVelocity(getPosition(), EntityManager.player.getPosition(), 2));
        moveTimer.setDurationMillis(ATTACK_MOVEMENT_DELAY);
    }
    
    @Override
    public void attackAI() {
        accelerateZVelocity(0.11);
    }
    
    @Override
    public void startAttackAI() {
        setZVelocity(-1);
        setVelocity(TrigonometryCalculator.calculateVelocity(getPosition(), EntityManager.player.getPosition(), 3));
    }
    
    
    @Override
    public Rectangle getObjectGroundBoundary() {
        return new Rectangle(getPosition().x - (getSize().width / 2) + 1,
        getPosition().y - (getSize().height),
        getSize().width - 2, getSize().height);
    }
}
