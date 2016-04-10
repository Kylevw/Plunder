/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import static environment.Utility.random;
import environment.Velocity;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import path.TrigonometryCalculator;
import plunder.java.main.EntityManager;
import static plunder.java.main.EntityManager.projectiles;
import plunder.java.resources.AudioPlayerIntf;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;
import timer.DurationTimer;

/**
 *
 * @author Kyle
 */
public class SamsaraEye extends Enemy {
    
    {
//        drawObjectBoundary(true);
    }
    
    private final DurationTimer moveTimer;
    private final DurationTimer attackMoveTimer;
    private final DurationTimer standardMoveTimer;
    private final DurationTimer standardMoveDelayTimer;
    
    private static final int STANDARD_MOVEMENT_MIN_DELAY = 4000;
    private static final int STANDARD_MOVEMENT_MAX_DELAY = 8000;
    
    private static final int STANDARD_MOVEMENT_MIN_DURATION = 1200;
    private static final int STANDARD_MOVEMENT_MAX_DURATION = 2000;
    
    private static final int MOVE_DELAY = 40;
    private static final int ATTACK_DELAY = 2000;
    private static final int ATTACK_MOVE_DELAY = 900;
    
    private static final int MAX_HEALTH = 8;
    private static final int STRENGTH = 1;
    private static final int PROJECTILE_STRENGTH = 1;
    public static final int DEFENSE = 1;
    public static final int SIGHT_DISTANCE = 160;
    public static final int ATTACK_DISTANCE = 90;
    public static final int WEIGHT = 3;
    
    private static final int DEFAULT_Z_DISPLACEMENT = 2;
    
    private static final int ANIMATION_SPEED = 80;
    public static final Dimension SIZE = new Dimension(9, 8);
    
    public SamsaraEye(Point position, ImageProviderIntf ip, AudioPlayerIntf ap) {
        super(ip.getImage(PImageManager.BAT_00), position, SIZE, WEIGHT, ip, ap, PImageManager.BAT_LIST, ANIMATION_SPEED, MAX_HEALTH, STRENGTH, DEFENSE, SIGHT_DISTANCE, ATTACK_DISTANCE, ATTACK_DELAY);
        setZDisplacement(DEFAULT_Z_DISPLACEMENT);
        applyGravity(false);
        moveTimer = new DurationTimer(MOVE_DELAY);
        attackMoveTimer = new DurationTimer(ATTACK_MOVE_DELAY);
        standardMoveDelayTimer = new DurationTimer(STANDARD_MOVEMENT_MIN_DELAY);
        standardMoveTimer = new DurationTimer(STANDARD_MOVEMENT_MIN_DURATION);
    }
    
    @Override
    public void timerTaskHandler() {
        
        if (getVelocity().x > 1) setVelocity(1, getVelocity().y);
        if (getVelocity().x < -1) setVelocity(-1, getVelocity().y);
        if (getVelocity().y > 1) setVelocity(getVelocity().x, 1);
        if (getVelocity().y < -1) setVelocity(getVelocity().x, -1);
        
        if (moveTimer.isComplete()) {
            move();
            moveTimer.start();
        }
        if (getZDisplacement() > DEFAULT_Z_DISPLACEMENT) {
            setZDisplacement(DEFAULT_Z_DISPLACEMENT);
            setZVelocity(0);
        }
        super.timerTaskHandler();
        
    }
    
    @Override
    public void standardAI() {
        if (standardMoveDelayTimer.isComplete()) {
            setVelocity(random(3) - 1, random(3) - 1);
            standardMoveDelayTimer.start(STANDARD_MOVEMENT_MIN_DELAY + random(STANDARD_MOVEMENT_MAX_DELAY - STANDARD_MOVEMENT_MIN_DELAY));
            standardMoveTimer.start(STANDARD_MOVEMENT_MIN_DURATION + random(STANDARD_MOVEMENT_MAX_DURATION - STANDARD_MOVEMENT_MIN_DURATION));
        } else if (standardMoveTimer.isComplete()) {
            setVelocity(0, 0);
        }
    }
    
    @Override
    public void targetAI() {
        setVelocity(TrigonometryCalculator.calculateVelocity(getPosition(), EntityManager.player.getPosition(), 2));
    }
    
    @Override
    public void attackAI() {
        if (attackMoveTimer.isComplete() && getPlayerDistance() <= SIGHT_DISTANCE && getPlayerDistance() > ATTACK_DISTANCE)
        setVelocity(TrigonometryCalculator.calculateVelocity(getPosition(), EntityManager.player.getPosition(), 2));
        else setVelocity(0,0);
    }
    
    @Override
    public void startAttackAI() {
        setVelocity(0, 0);
        attackMoveTimer.start();
        Velocity projectileVelocity = TrigonometryCalculator.calculateVelocity(getPosition(), EntityManager.player.getPosition(), 8);
        projectiles.add(new ProjectileLaser(new Point(getCenterOfMass().x, getCenterOfMass().y + 1), getZDisplacement() - 1, projectileVelocity, false, PROJECTILE_STRENGTH, getImageProvider(), getAudioPlayer()));
    }
    
    
    @Override
    public Rectangle getObjectGroundBoundary() {
        return new Rectangle(getPosition().x - (getSize().width / 2) + 1,
        getPosition().y - (getSize().height),
        getSize().width - 2, getSize().height);
    }
}
