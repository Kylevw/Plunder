/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import images.Animator;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import path.TrigonometryCalculator;
import plunder.java.main.EntityManager;
import static plunder.java.main.EntityManager.consumables;
import static plunder.java.main.EntityManager.enemies;
import static plunder.java.main.EntityManager.player;
import plunder.java.resources.PImageManager;
import timer.DurationTimer;
import static plunder.java.main.EntityManager.explosions;

/**
 *
 * @author Kyle
 */
public class Explosion {
    
    private final Point position;
    private final Animator animator;
    private final int size;
    
    private DurationTimer despawnTimer;
    
    private BufferedImage currentImage;
    
    private static final int ANIMATION_SPEED = 80;
    private static final int RADIUS = 27;
    private static final int MAX_DAMAGE = 4;
    
    private boolean dealtDamage;
    
    public Explosion(Point position, int size) {
        this.position = position;
        PImageManager im = new PImageManager();
        this.animator = new Animator(im, im.getImageList(PImageManager.EXPLOSION_LIST), ANIMATION_SPEED);
        this.size = RADIUS * size;
        despawnTimer = new DurationTimer(640);
    }
    
    public void draw(Graphics2D graphics) {
        graphics.drawImage(currentImage, position.x - (size / 2), position.y - (size / 2), size, size, null);
    }
    
    public void timerTaskHandler() {
        currentImage = (BufferedImage) animator.getCurrentImage();
        if (!dealtDamage) {
            dealtDamage = true;
            if (player != null) {
                double playerDistance = TrigonometryCalculator.getSideLength(player.getZDisplacement(), TrigonometryCalculator.getHypotenuse(position.x, position.y, player.getPosition().x, player.getPosition().y));
                if (playerDistance <= size / 2) player.damage(size / RADIUS);
            }
            if (enemies != null) {
                EntityManager.getEnemies().stream().forEach((enemy) -> {
                    double enemyDistance = TrigonometryCalculator.getSideLength(enemy.getZDisplacement(), TrigonometryCalculator.getHypotenuse(position.x, position.y, enemy.getPosition().x, enemy.getPosition().y));
                    if (enemyDistance <= size / 2) {
                        enemy.damage((int) ((size - enemyDistance - 1) * MAX_DAMAGE / RADIUS) + 1);
                    }
                });
            }
            if (consumables != null) {
                EntityManager.getConsumables().stream().forEach((consumable) -> {
                    double consumableDistance = TrigonometryCalculator.getSideLength(consumable.getZDisplacement(), TrigonometryCalculator.getHypotenuse(position.x, position.y, consumable.getPosition().x, consumable.getPosition().y));
                    if (consumableDistance <= size / 2) {
                        if (consumable instanceof Bomb) consumable.explode(2);
                        else consumable.setDespawn(true);
                    }
                });
            }
        }
    }
    
    public boolean despawn() {
        return despawnTimer.isComplete();
    }
    
}
