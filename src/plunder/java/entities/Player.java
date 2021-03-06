/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import environment.Velocity;
import java.awt.Dimension;
import plunder.java.main.ActionState;
import plunder.java.main.Direction;
import plunder.java.resources.PImageManager;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.main.ScreenLimitProviderIntf;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import static plunder.java.main.EntityManager.bombs;
import static plunder.java.main.EntityManager.projectiles;
import plunder.java.main.MapManager;
import static plunder.java.main.MapManager.environmentGrid;
import plunder.java.main.TileMap;
import plunder.java.resources.AudioPlayerIntf;
import timer.DurationTimer;

/**
 *
 * @author Kyle van Wiltenburg
 */
public class Player extends Entity {
    
    private boolean swordSwingDebug;
    
    private static final int PLAYER_WIDTH = 16;
    private static final int PLAYER_HEIGHT = 16;
    private int health, maxHealth, bombCount, arrowCount;
    
    private static final int WEIGHT = 4;
    private final DurationTimer invulTimer;
    private final DurationTimer healthTimer;
    private final DurationTimer healthMeterBlinkTimer;
    private final DurationTimer itemDisplayTimer;
    private final DurationTimer swordAttackTimer;
    private int bowCharge;
    
    private BufferedImage displayItemImage;
    
    {
        actionState = ActionState.IDLE;
        facing = Direction.DOWN;
//        drawObjectBoundary(true);
    }
    
    private final ScreenLimitProviderIntf screenLimiter;
    
    private final ArrayList<Direction> directions;
    private final ArrayList<Direction> bowDirections;
    private Direction facingDebug;
    private Direction facing;
    private ActionState actionState;
    private ActionState actionStateDebug;
    
    private final Point environmentPosition;
    private final Point displacementPosition;
    
    private static final int ANIMATION_SPEED = 160;
    
    public static final int BOW_CHARGE_TIME = 30;
    
    
    /**
     * Constructor, returns an instance of the Player class
     *
     * @param position the current position of the entity on screen
     * @param screenLimiter inputs the minimum and maximum positions for the camera
     * @param ip the PImageManager for the entity
     * @param ap the AudioManager for the entity
     * 
     */
    
    public Player(Point position, ScreenLimitProviderIntf screenLimiter, ImageProviderIntf ip, AudioPlayerIntf ap) {

        super(ip.getImage(PImageManager.PLAYER_IDLE_DOWN_00), position, new Dimension(PLAYER_WIDTH, PLAYER_HEIGHT), WEIGHT, ip, ap, PImageManager.PLAYER_WALK_DOWN_LIST, ANIMATION_SPEED);
        this.directions = new ArrayList<>();
        this.bowDirections = new ArrayList<>();
        maxHealth = 6;
        health = maxHealth;
        this.environmentPosition = new Point(position);
        this.displacementPosition = new Point(0, 0);
        this.screenLimiter = screenLimiter;
        screenLimiter.setMaxY(screenLimiter.getMaxY());
        
        invulTimer = new DurationTimer(1200);
        healthTimer = new DurationTimer(1600);
        healthMeterBlinkTimer = new DurationTimer(200);
        itemDisplayTimer = new DurationTimer(600);
        swordAttackTimer = new DurationTimer(240);
    }
    
    @Override
    public void draw(Graphics2D graphics) {
        if (displayItemImage != null) graphics.drawImage(displayItemImage, null, getPosition().x - ((displayItemImage.getWidth() + 1) / 2), getPosition().y - PLAYER_WIDTH - displayItemImage.getHeight() - 1 - getZDisplacement());
        if (invulTimer.getRemainingDurationMillis() / 80 % 2 == 0) super.draw(graphics);
    }
    
    @Override
    public void timerTaskHandler() {
        
        correctDisplacementPosition();
        
//        System.out.println("Position: [" + getPosition().x + "," + getPosition().y + "]");
//        System.out.println("Environment Position: [" + environmentPosition.x + "," + environmentPosition.y + "]");
//        System.out.println("Displacement Position: [" + displacementPosition.x + "," + displacementPosition.y + "]");
        
        if (actionState == ActionState.BOW) {
            if (!bowDirections.isEmpty() && !bowDirections.contains(facing)) facing = bowDirections.get(bowDirections.size() - 1);
            if (bowDirections.isEmpty()) fireArrow();
        }
        
        if (actionState == ActionState.SWORD) {
            bowDirections.clear();
        }
        
        if (displayItemImage != null && itemDisplayTimer.isComplete()) displayItemImage = null;
        
        if (health <= 0) {
            setDespawn(true);
        }
        
        if (healthTimer.isComplete()) healthTimer.start();
        
        if (maxHealth % 2 > 0) maxHealth++;
        if (health > maxHealth) health = maxHealth;
        
        updateActionState();
        
        if (actionState != ActionState.BOW && actionState != ActionState.SWORD) {
            updateVelocity();
            updateFacingDirection();
        }
        else setVelocity(0, 0);

        move();
        
        if (actionStateDebug != actionState || facingDebug != facing) {
            updateAnimator();
        }
        
        actionStateDebug = actionState;
        facingDebug = facing;
        
        // Updates the player's position in the world
        setPosition(environmentPosition.x + displacementPosition.x, environmentPosition.y + displacementPosition.y);
        
        super.timerTaskHandler();
        
        if (actionState == ActionState.BOW) {
            bowCharge++;
            if (bowCharge > 40) bowCharge = 40;
            updateBowImage();
        }
    }
    
    private void correctDisplacementPosition() {
        if (environmentPosition.x < screenLimiter.getMinX()) {
            int xDifference = environmentPosition.x - screenLimiter.getMinX();
            environmentPosition.x -= xDifference;
            displacementPosition.x += xDifference;
        }
        else if (environmentPosition.x > screenLimiter.getMaxX()) {
            int xDifference = environmentPosition.x - screenLimiter.getMaxX();
            environmentPosition.x -= xDifference;
            displacementPosition.x += xDifference;
        }
        else if (displacementPosition.x != 0 && getPosition().x >= screenLimiter.getMinY() && getPosition().x <= screenLimiter.getMaxY()) {
            environmentPosition.x += displacementPosition.x;
            displacementPosition.x = 0;
        }
        
        if (environmentPosition.y < screenLimiter.getMinX()) {
            int yDifference = environmentPosition.y - screenLimiter.getMinY();
            environmentPosition.y -= yDifference;
            displacementPosition.y += yDifference;
        }
        else if (environmentPosition.y > screenLimiter.getMaxX()) {
            int yDifference = environmentPosition.y - screenLimiter.getMaxY();
            environmentPosition.y -= yDifference;
            displacementPosition.y += yDifference;
        }
        else if (displacementPosition.y != 0 && getPosition().y >= screenLimiter.getMinY() && getPosition().y <= screenLimiter.getMaxY()) {
            environmentPosition.y += displacementPosition.y;
            displacementPosition.y = 0;
        }
    }
    
    private void updateVelocity() {
        
        // If the player's Directions list contains a certain direction, apply that direction to the velocity
        setVelocity(0, 0);
        if (directions.contains(Direction.UP)) accelerate(0, -2);
        if (directions.contains(Direction.DOWN)) accelerate(0, 2);
        if (directions.contains(Direction.LEFT)) accelerate(-2, 0);
        if (directions.contains(Direction.RIGHT)) accelerate(2, 0);
        
    }
    
    private void updateActionState() {
        if (!swordAttackTimer.isComplete()) actionState = ActionState.SWORD;
        else if (!bowDirections.isEmpty()) actionState = ActionState.BOW;
        else if (!onGround()) actionState = ActionState.JUMPING;
        else if (getVelocity().x != 0 || getVelocity().y != 0) actionState = ActionState.WALKING;
        else actionState = ActionState.IDLE;
    }
    
    private void updateBowImage() {
        switch (facing) {
            case UP:
                if (bowCharge < BOW_CHARGE_TIME / 2) setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_UP_00));
                else if (bowCharge < BOW_CHARGE_TIME) setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_UP_01));
                else setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_UP_02));
                break;
            case DOWN:
                if (bowCharge < BOW_CHARGE_TIME / 2) setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_DOWN_00));
                else if (bowCharge < BOW_CHARGE_TIME) setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_DOWN_01));
                else setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_DOWN_02));
                break;
            case LEFT:
                if (bowCharge < BOW_CHARGE_TIME / 2) setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_LEFT_00));
                else if (bowCharge < BOW_CHARGE_TIME) setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_LEFT_01));
                else setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_LEFT_02));
                break;
            case RIGHT:
                if (bowCharge < BOW_CHARGE_TIME / 2) setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_RIGHT_00));
                else if (bowCharge < BOW_CHARGE_TIME) setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_RIGHT_01));
                else setImage(getImageProvider().getImage(PImageManager.PLAYER_BOW_RIGHT_02));
                break;
        }
    }
    
    private void updateAnimator() {
        
        switch (actionState) {
            
            case IDLE:
                switch (facing) {
                    case UP: 
                        setImageList(PImageManager.PLAYER_IDLE_UP_LIST);
                        break;
                    case DOWN:
                        setImageList(PImageManager.PLAYER_IDLE_DOWN_LIST);
                        break;
                        case LEFT:
                        setImageList(PImageManager.PLAYER_IDLE_LEFT_LIST);
                        break;
                    case RIGHT:
                        setImageList(PImageManager.PLAYER_IDLE_RIGHT_LIST);
                        break;
                }
            break;
                
            case WALKING:
                switch (facing) {
                    case UP: 
                        if (TileMap.collision(new Rectangle(getObjectGroundBoundary().x + getVelocity().x,
                getObjectGroundBoundary().y, getObjectGroundBoundary().width,
                getObjectGroundBoundary().height))) {
                            if (getVelocity().x < 0) setImageList(PImageManager.PLAYER_WALK_LEFT_LIST);
                            else if (getVelocity().x > 0) setImageList(PImageManager.PLAYER_WALK_RIGHT_LIST);
                            else setImageList(PImageManager.PLAYER_WALK_UP_LIST);
                        } else setImageList(PImageManager.PLAYER_WALK_UP_LIST);
                        
                        break;
                    case DOWN: 
                        if (TileMap.collision(new Rectangle(getObjectGroundBoundary().x + getVelocity().x,
                getObjectGroundBoundary().y, getObjectGroundBoundary().width,
                getObjectGroundBoundary().height))) {
                            if (getVelocity().x < 0) setImageList(PImageManager.PLAYER_WALK_LEFT_LIST);
                            else if (getVelocity().x > 0) setImageList(PImageManager.PLAYER_WALK_RIGHT_LIST);
                            else setImageList(PImageManager.PLAYER_WALK_DOWN_LIST);
                        } else setImageList(PImageManager.PLAYER_WALK_DOWN_LIST);
                        break;
                    case LEFT: 
                        setImageList(PImageManager.PLAYER_WALK_LEFT_LIST);
                        break;
                    case RIGHT: 
                        setImageList(PImageManager.PLAYER_WALK_RIGHT_LIST);
                        break;
                }
            break;
            
            case JUMPING:
                switch (facing) {
                    case UP: 
                        setImageList(PImageManager.PLAYER_JUMP_UP_LIST);
                        break;
                    case DOWN: 
                        setImageList(PImageManager.PLAYER_JUMP_DOWN_LIST);
                        break;
                    case LEFT: 
                        setImageList(PImageManager.PLAYER_JUMP_LEFT_LIST);
                        break;
                    case RIGHT: 
                        setImageList(PImageManager.PLAYER_JUMP_RIGHT_LIST);
                        break;
                }
            break;
        }
    }
    
    private void fireArrow() {
        arrowCount--;
        switch (facing) {
            case UP:
                projectiles.add(new ProjectileArrow(new Point(getCenterOfMass().x + 2, getCenterOfMass().y + 5), getZDisplacement() + 2, new Velocity(0, (int) (-6 * bowCharge / BOW_CHARGE_TIME) - 1), bowCharge / BOW_CHARGE_TIME, true, (int) (5 * bowCharge / BOW_CHARGE_TIME) + 1, getImageProvider(), getAudioPlayer()));
                break;
            case DOWN:
                projectiles.add(new ProjectileArrow(new Point(getCenterOfMass().x - 2, getCenterOfMass().y + 5), getZDisplacement() + 2, new Velocity(0, (int) (6 * bowCharge / BOW_CHARGE_TIME) + 1), bowCharge / BOW_CHARGE_TIME, true, (int) (5 * bowCharge / BOW_CHARGE_TIME) + 1, getImageProvider(), getAudioPlayer()));
                break;
            case LEFT:
                projectiles.add(new ProjectileArrow(new Point(getCenterOfMass().x, getCenterOfMass().y + 7), getZDisplacement() + 2, new Velocity((int) (-6 * bowCharge / BOW_CHARGE_TIME) - 1, 0), bowCharge / BOW_CHARGE_TIME, true, (int) (5 * bowCharge / BOW_CHARGE_TIME) + 1, getImageProvider(), getAudioPlayer()));
                break;
            case RIGHT:
                projectiles.add(new ProjectileArrow(new Point(getCenterOfMass().x, getCenterOfMass().y + 7), getZDisplacement() + 2, new Velocity((int) (6 * bowCharge / BOW_CHARGE_TIME) + 1, 0), bowCharge / BOW_CHARGE_TIME, true, (int) (5 * bowCharge / BOW_CHARGE_TIME) + 1, getImageProvider(), getAudioPlayer()));
                break;
        }
        bowCharge = 0;
    }
    
    public void swingSword() {
        if (!swordSwingDebug) {
            swordAttackTimer.start();
            switch (facing) {
                case UP:
                    projectiles.add(new SwordSwing(new Point(getCenterOfMass().x, getCenterOfMass().y - 6), getZDisplacement() + 1, 270, true, 3, getImageProvider(), getAudioPlayer()));
                    break;
                case DOWN:
                    projectiles.add(new SwordSwing(new Point(getCenterOfMass().x, getCenterOfMass().y + 11), getZDisplacement() + 1, 90, true, 3, getImageProvider(), getAudioPlayer()));
                    break;
                case LEFT:
                    projectiles.add(new SwordSwing(new Point(getCenterOfMass().x - 8, getCenterOfMass().y + 3), getZDisplacement() + 1, 180, true, 3, getImageProvider(), getAudioPlayer()));
                    break;
                case RIGHT:
                    projectiles.add(new SwordSwing(new Point(getCenterOfMass().x + 8, getCenterOfMass().y + 3), getZDisplacement() + 1, 0, true, 3, getImageProvider(), getAudioPlayer()));
                    break;
            }
            bowCharge = 0;
        }
    }
    
    public void setSwordSwingDebug(boolean swordSwingDebug) {
        this.swordSwingDebug = swordSwingDebug;
    }
    
    @Override
    public Rectangle getObjectGroundBoundary() {
        return new Rectangle(getPosition().x - (getSize().width / 2) + 3,
        getPosition().y - (getSize().height) + 2,
        getSize().width - 6, getSize().height - 4);
    }
    
    private void updateFacingDirection() {
        if (directions != null && !directions.isEmpty()) facing = directions.get(directions.size() - 1);
        switch (facing) {
            case UP:
                if (getVelocity().y >= 0) {
                    if (getVelocity().x < 0) facing = Direction.LEFT;
                    else if (getVelocity().x > 0) facing = Direction.RIGHT;
                }
                break;
            case DOWN:
                if (getVelocity().y <= 0) {
                    if (getVelocity().x < 0) facing = Direction.LEFT;
                    else if (getVelocity().x > 0) facing = Direction.RIGHT;
                }
                break;
            case LEFT:
                if (getVelocity().x >= 0) {
                    if (getVelocity().y < 0) facing = Direction.UP;
                    else if (getVelocity().y > 0) facing = Direction.DOWN;
                }
                break;
            case RIGHT:
                if (getVelocity().x <= 0) {
                    if (getVelocity().y < 0) facing = Direction.UP;
                    else if (getVelocity().y > 0) facing = Direction.DOWN;
                }
                break;
        }
    }
    
    @Override
    public void move() {
        
        if (!TileMap.collision(new Rectangle(getObjectGroundBoundary().x + getVelocity().x,
                getObjectGroundBoundary().y, getObjectGroundBoundary().width,
                getObjectGroundBoundary().height))) {
            environmentPosition.x += getVelocity().x;
        }
        if (!TileMap.collision(new Rectangle(getObjectGroundBoundary().x,
                getObjectGroundBoundary().y + getVelocity().y, getObjectGroundBoundary().width,
                getObjectGroundBoundary().height))) {
            environmentPosition.y += getVelocity().y;
        }
        
        applyZVelocity();
        
        
        
        
        
    }
    
    public void damage(int damage) {
        if (invulTimer.isComplete()) {
            health -= damage;
            invulTimer.start();
        }
    }
    
    public int getHealth() {
        if (health < maxHealth) return health;
        else return maxHealth;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public ArrayList<Direction> getDirections() {
        return directions;
    }
    
    public ArrayList<Direction> getBowDirections() {
        return bowDirections;
    }
    
    public ActionState getActionState() {
        return actionState;
    }
    
    public void addDirection(Direction direction) {
        directions.add(direction);
    }
    
    public void removeDirection(Direction direction) {
        directions.remove(direction);
    }
    
    public void addBowDirection(Direction direction) {
        if (arrowCount > 0) {
            facing = direction;
            bowDirections.add(direction);
        }
    }
    
    public void removeBowDirection(Direction direction) {
        bowDirections.remove(direction);
    }
    
    public Point getEnvironmentPosition() {
        return environmentPosition;
    }
    
    public Point getDisplacementPosition() {
        return displacementPosition;
    }
    
    public void jump() {
        if (onGround()) setZVelocity(4);
    }
    
    public int getScreenMinX() {
        return screenLimiter.getMinX();
    }
    
    public int getScreenMaxX() {
        return screenLimiter.getMaxX();
    }
    
    public int getScreenMinY() {
        return screenLimiter.getMinY();
    }
    
    public boolean healthBlip() {
        return health <= 2 && healthTimer.getRemainingDurationMillis() <= 100 || !healthMeterBlinkTimer.isComplete();
    }
    
    public int getScreenMaxY() {
        return screenLimiter.getMaxY() - (getSize().height / 2);
    }
    
    public void setScreenLimiter(int screenWidth, int screenHeight) {
        
        screenLimiter.setMinX(-screenWidth / 2);
        screenLimiter.setMinY(-screenHeight / 2);
        screenLimiter.setMaxX(screenWidth / 2);
        screenLimiter.setMaxY(screenHeight / 2);
    }
    
    public void heal(int health) {
        this.health += health;
        healthMeterBlinkTimer.start();
    }
    
    public void displayItem(BufferedImage displayItemImage) {
        this.displayItemImage = displayItemImage;
        itemDisplayTimer.start();
    }
    
    public void addBombs(int bombs) {
        bombCount += bombs;
    }
    
    public void addArrows(int arrows) {
        arrowCount += arrows;
    }
    
    public void useBomb() {
        if (bombCount > 0 && bowDirections.isEmpty()) {
            Velocity bombVelocity = new Velocity(0, 0);
            switch (facing) {
                case UP:
                    bombVelocity = new Velocity(0, -2);
                    break;
                case DOWN:
                    bombVelocity = new Velocity(0, 2);
                    break;
                case LEFT:
                    bombVelocity = new Velocity(-2, 0);
                    break;
                case RIGHT:
                    bombVelocity = new Velocity(2, 0);
                    break;
            }
            bombs.add(new PrimedBomb(new Point(getPosition().x, getPosition().y), 3, new Velocity(bombVelocity.x + (getVelocity().x / 2), bombVelocity.y + (getVelocity().y / 2)), 3, getImageProvider(), getAudioPlayer()));
            bombCount--;
        }
    }
    
    public int getBombCount() {
        return bombCount;
    }
    
    public int getArrowCount() {
        return arrowCount;
    }
}
