/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.entities;

import plunder.java.main.ActionState;
import plunder.java.main.Direction;
import plunder.java.resources.PImageManager;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.main.ScreenLimitProviderIntf;
import images.Animator;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import timer.DurationTimer;

/**
 *
 * @author Kyle van Wiltenburg
 */
public class Player extends Entity {
    
    private static final int PLAYER_WIDTH = 16;
    private static final int PLAYER_HEIGHT = 16;
    private int health, maxHealth;
    
    private final DurationTimer invulTimer;
    
    {
        actionState = ActionState.IDLE;
        facing = Direction.DOWN;
//        drawObjectBoundary(true);
    }
    
    private final ScreenLimitProviderIntf screenLimiter;
    
    private final ArrayList<Direction> directions;
    private Direction facingDebug;
    private Direction facing;
    private ActionState actionState;
    private ActionState actionStateDebug;
    private static final int ANIMATION_SPEED = 160;
    
    private final Point environmentPosition;
    private final Point displacementPosition;
    
    private final Animator animator;
    
    /**
     * Constructor, returns an instance of the Player class
     *
     * @param position the current position of the entity on screen
     * @param screenLimiter inputs the minimum and maximum positions for the camera
     * @param ip the PImageManager for the entity
     * 
     */
    
    public Player(Point position, ScreenLimitProviderIntf screenLimiter, ImageProviderIntf ip) {

        super(ip.getImage(PImageManager.PLAYER_IDLE_DOWN_00), position, new Dimension(PLAYER_WIDTH, PLAYER_HEIGHT), ip);
        this.directions = new ArrayList<>();
        maxHealth = 6;
        health = maxHealth;
        this.environmentPosition = new Point(position);
        this.displacementPosition = new Point(0, 0);
        this.screenLimiter = screenLimiter;
        screenLimiter.setMaxY(screenLimiter.getMaxY() + (getSize().height / 2));
        
        invulTimer = new DurationTimer(1200);
        
        PImageManager im = new PImageManager();
        this.animator = new Animator(im, getImageProvider().getImageList(PImageManager.PLAYER_WALK_DOWN_LIST), ANIMATION_SPEED);
    }
    
    @Override
    public void draw(Graphics2D graphics) {
        if (invulTimer.getRemainingDurationMillis() / 80 % 2 == 0) super.draw(graphics);
    }
    
    @Override
    public void timerTaskHandler() {
        
        if (maxHealth % 2 > 0) maxHealth++;
        if (health > maxHealth) health = maxHealth;
        
        updateVelocity();
        move();
        
        updateActionState();
        updateFacingDirection();
        
        if (actionStateDebug != actionState || facingDebug != facing) {
            updateAnimator();
        }
        
        actionStateDebug = actionState;
        facingDebug = facing;
        
        updateImage();
        
        // Updates the player's position in the world
        setPosition(environmentPosition.x + displacementPosition.x, environmentPosition.y + displacementPosition.y);
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
        if (!onGround()) actionState = ActionState.JUMPING;
        else if (getVelocity().x != 0 || getVelocity().y != 0) actionState = ActionState.WALKING;
        else actionState = ActionState.IDLE;
    }
    
    private void updateAnimator() {
        
        switch (actionState) {
            
            case IDLE:
                switch (facing) {
                    case UP: 
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_IDLE_UP_LIST));
                        animator.setDelayDurationMillis(Integer.MAX_VALUE);
                        break;
                    case DOWN:
                     animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_IDLE_DOWN_LIST));
                        animator.setDelayDurationMillis(Integer.MAX_VALUE);
                        break;
                        case LEFT:
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_IDLE_LEFT_LIST));
                        animator.setDelayDurationMillis(Integer.MAX_VALUE);
                        break;
                    case RIGHT:
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_IDLE_RIGHT_LIST));
                        animator.setDelayDurationMillis(Integer.MAX_VALUE);
                        break;
                }
                break;
                
            case WALKING:
                switch (facing) {
                    case UP: 
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_WALK_UP_LIST));
                        animator.setDelayDurationMillis(ANIMATION_SPEED);
                        break;
                    case DOWN: 
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_WALK_DOWN_LIST));
                        animator.setDelayDurationMillis(ANIMATION_SPEED);
                        break;
                    case LEFT: 
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_WALK_LEFT_LIST));
                        animator.setDelayDurationMillis(ANIMATION_SPEED);
                        break;
                    case RIGHT: 
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_WALK_RIGHT_LIST));
                        animator.setDelayDurationMillis(ANIMATION_SPEED);
                        break;
                }
                break;
            
            case JUMPING:
                switch (facing) {
                    case UP: 
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_JUMP_UP_LIST));
                        animator.setDelayDurationMillis(Integer.MAX_VALUE);
                        break;
                    case DOWN: 
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_JUMP_DOWN_LIST));
                        animator.setDelayDurationMillis(Integer.MAX_VALUE);
                        break;
                    case LEFT: 
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_JUMP_LEFT_LIST));
                        animator.setDelayDurationMillis(Integer.MAX_VALUE);
                        break;
                    case RIGHT: 
                        animator.setImageNames(getImageProvider().getImageList(PImageManager.PLAYER_JUMP_RIGHT_LIST));
                        animator.setDelayDurationMillis(Integer.MAX_VALUE);
                        break;
                }
                break;
            
        }
        
    }
    
    @Override
    public Rectangle getObjectGroundBoundary() {
        return new Rectangle(getPosition().x - (getSize().width / 2) + 3,
        getPosition().y - (getSize().height) + 2,
        getSize().width - 6, getSize().height - 4);
    }
    
    private void updateFacingDirection() {
        
        if (getVelocity().y < 0) facing = Direction.UP;
        else if (getVelocity().y > 0) facing = Direction.DOWN;
        else {
            if (getVelocity().x < 0) facing = Direction.LEFT;
            else if (getVelocity().x > 0) facing = Direction.RIGHT;
        }
        
    }
    
    private void updateImage() {
        if (animator != null) {
            setImage(animator.getCurrentImage());
        }
    }
    
    @Override
    public void move() {
        
        if (getPosition().x + getVelocity().x <= screenLimiter.getMinX() || getPosition().x + getVelocity().x >= screenLimiter.getMaxX()) displacementPosition.x += getVelocity().x;
        else if (displacementPosition.x == 0) environmentPosition.x += getVelocity().x;
        else displacementPosition.x = 0;
        
        if (getPosition().y + getVelocity().y <= screenLimiter.getMinY() || getPosition().y + getVelocity().y >= screenLimiter.getMaxY()) displacementPosition.y += getVelocity().y;
        else if (displacementPosition.y == 0) environmentPosition.y += getVelocity().y;
        else displacementPosition.y = 0;
        
        applyZVelocity();
        
    }
    
    public void damage(int damage) {
        if (invulTimer.isComplete()) {
            health -= damage;
            invulTimer.start();
        }
    }
    
    public int getHealth() {
        return health;
    }
    
    public int getMaxHealth() {
        return maxHealth;
    }
    
    public ArrayList<Direction> getDirections() {
        return directions;
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
    
    public int getScreenMaxY() {
        return screenLimiter.getMaxY() - (getSize().height / 2);
    }
    
}
