/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;

/**
 *
 * @author Kyle
 */
public class Enemy extends Entity {
    
    private final int sightDistance, attackDistance;
    
    private final int maxHealth, strength, defense;
    private int health;
    private BufferedImage meterImage;

    public Enemy(BufferedImage image, Point position, Dimension size, ImageProviderIntf ip, int maxHealth, int strength, int defense, int sightDistance, int attackDistance) {
        super(image, position, size, ip);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.strength = strength;
        this.defense = defense;
        this.sightDistance = sightDistance;
        this.attackDistance = attackDistance;
    }
    
    @Override
    public void timerTaskHandler() {
        if (health < maxHealth) {
            if (health <= 0) {
                System.out.println("Bleh");
            } else {
                int meterFactor = health * 8 / maxHealth;
                switch (meterFactor) {
                    case 0:
                        meterImage = getImageProvider().getImage(PImageManager.HEALTH_METER_0);
                        break;
                    case 1:
                        meterImage = getImageProvider().getImage(PImageManager.HEALTH_METER_1);
                        break;
                    case 2:
                        meterImage = getImageProvider().getImage(PImageManager.HEALTH_METER_2);
                        break;
                    case 3:
                        meterImage = getImageProvider().getImage(PImageManager.HEALTH_METER_3);
                        break;
                    case 4:
                        meterImage = getImageProvider().getImage(PImageManager.HEALTH_METER_4);
                        break;
                    case 5:
                        meterImage = getImageProvider().getImage(PImageManager.HEALTH_METER_5);
                        break;
                    case 6:
                        meterImage = getImageProvider().getImage(PImageManager.HEALTH_METER_6);
                        break;
                    case 7:
                        meterImage = getImageProvider().getImage(PImageManager.HEALTH_METER_7);
                        break;
                }
            }
        }
    }
    
    @Override
    public void draw(Graphics2D graphics) {
        graphics.setColor(Color.BLUE);
        if (drawBoundary()) graphics.drawOval(getPosition().x - sightDistance, getPosition().y - sightDistance, sightDistance * 2, sightDistance * 2);
        graphics.setColor(Color.RED);
        if (drawBoundary()) graphics.drawOval(getPosition().x - attackDistance, getPosition().y - attackDistance, attackDistance * 2, attackDistance * 2);
        super.draw(graphics);
        if (meterImage != null) {
            graphics.drawImage(meterImage, getPosition().x - 4, getPosition().y - getSize().height - getZDisplacement() - 3, 9, 2, null);
        }
    }
    
    public void damage(int damage) {
        int damageFactor = damage - defense;
        if (damageFactor < 0 && damage > 0) damageFactor = 1;
        health -= damageFactor;
    }
    
    public int getAttackDistance() {
        return attackDistance;
    }
    
    public int getSightDistance() {
        return sightDistance;
    }
    
    public int getStrength() {
        return strength;
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
}
