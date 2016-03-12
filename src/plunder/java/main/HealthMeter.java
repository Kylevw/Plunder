/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.main;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;

/**
 *
 * @author Kyle
 */
public class HealthMeter {
    
    private Point position;
    private final int maxHealth;
    private int health;
    private BufferedImage meterImage;
    private final ImageProviderIntf ip;
    
    public HealthMeter(Point position, int maxHealth, int health, ImageProviderIntf ip) {
        this.position = position;
        this.maxHealth = maxHealth;
        this.health = health;
        this.ip = ip;
    }
    
    public void setHealth(int health) {
        this.health = health;
    }
    
    public void setPosition(Point position) {
        this.position = position;
    }
    
    public void draw(Graphics2D graphics) {
        if (health < maxHealth) {
            int meterFactor = health * 8 / maxHealth;
                switch (meterFactor) {
                    case 0:
                        meterImage = ip.getImage(PImageManager.HEALTH_METER_0);
                        break;
                    case 1:
                        meterImage = ip.getImage(PImageManager.HEALTH_METER_1);
                        break;
                    case 2:
                        meterImage = ip.getImage(PImageManager.HEALTH_METER_2);
                        break;
                    case 3:
                        meterImage = ip.getImage(PImageManager.HEALTH_METER_3);
                        break;
                    case 4:
                        meterImage = ip.getImage(PImageManager.HEALTH_METER_4);
                        break;
                    case 5:
                        meterImage = ip.getImage(PImageManager.HEALTH_METER_5);
                        break;
                    case 6:
                        meterImage = ip.getImage(PImageManager.HEALTH_METER_6);
                        break;
                    case 7:
                        meterImage = ip.getImage(PImageManager.HEALTH_METER_7);
                        break;
            }
        }
        graphics.drawImage(meterImage, position.x, position.y, 9, 2, null);
    }
}
