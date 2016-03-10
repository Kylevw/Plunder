/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import static environment.Utility.random;
import images.Animator;
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
    private final DurationTimer attackTimer;
    
    private boolean attacking;
    
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
    
    private final Animator animator;

    public Bat(Point position, ImageProviderIntf ip) {
        super(ip.getImage(PImageManager.BAT_UP), position, SIZE, ip, MAX_HEALTH, STRENGTH, DEFENSE, SIGHT_DISTANCE, ATTACK_DISTANCE);
        damage(1);
        PImageManager im = new PImageManager();
        this.animator = new Animator(im, getImageProvider().getImageList(PImageManager.BAT_LIST), ANIMATION_SPEED);
        moveTimer = new DurationTimer(IDLE_MOVEMENT_DELAY);
        applyGravity(false);
        setZDisplacement(DEFAULT_Z_DISPLACEMENT);
        attackTimer = new DurationTimer(ATTACK_DELAY);
    }
    
    @Override
    public void timerTaskHandler() {
        if (player != null && player.getObjectGroundBoundary().intersects(getObjectGroundBoundary()) && player.getObjectBoundary().intersects(getObjectBoundary())) {
            player.damage(1);
        }
        xSpeed += (random(3) - 1);
        ySpeed += (random(3) - 1);
        if (xSpeed > 1) xSpeed = 1;
        else if (xSpeed < -1) xSpeed = -1;
        if (ySpeed > 1) ySpeed = 1;
        else if (ySpeed < -1) ySpeed = -1;
        
        if (player != null) {
            int testPoint = (int) TrigonometryCalculator.getHypotenuse(getPosition().x - EntityManager.player.getPosition().x, getPosition().y - EntityManager.player.getPosition().y);
            if (!attackTimer.isComplete()) accelerateZVelocity(0.11);
            if (testPoint <= getAttackDistance() && attackTimer.isComplete()) {
                attackTimer.start();
                setZVelocity(-1);
                setVelocity(TrigonometryCalculator.calculateVelocity(getPosition(), EntityManager.player.getPosition(), 3));
            } else if (testPoint <= getSightDistance() && attackTimer.isComplete()) {
                setVelocity(TrigonometryCalculator.calculateVelocity(getPosition(), EntityManager.player.getPosition(), 2));
                moveTimer.setDurationMillis(ATTACK_MOVEMENT_DELAY);
            } else if (attackTimer.isComplete()) {
                setVelocity(xSpeed, ySpeed);
                moveTimer.setDurationMillis(IDLE_MOVEMENT_DELAY);
            }
        } else if (attackTimer.isComplete()) {
            setVelocity(xSpeed, ySpeed);
            moveTimer.setDurationMillis(IDLE_MOVEMENT_DELAY);
        }
        
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
        updateImage();
        super.timerTaskHandler();
    }
    
    @Override
    public Rectangle getObjectGroundBoundary() {
        return new Rectangle(getPosition().x - (getSize().width / 2) + 1,
        getPosition().y - (getSize().height),
        getSize().width - 2, getSize().height);
    }
    
    private void updateImage() {
        if (animator != null) {
            setImage(animator.getCurrentImage());
        }
    }

}
