/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.main;

import environment.Velocity;
import plunder.java.resources.GameState;
import plunder.java.resources.PImageManager;
import plunder.java.entities.Player;
import grid.Grid;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import plunder.java.entities.Arrow;
import plunder.java.entities.Bat;
import plunder.java.entities.Bomb;
import plunder.java.entities.Consumable;
import plunder.java.entities.Enemy;
import plunder.java.entities.Entity;
import plunder.java.entities.Explosion;
import plunder.java.entities.Heart;
import plunder.java.entities.PrimedBomb;
import plunder.java.entities.Projectile;
import plunder.java.entities.SamsaraEye;
import static plunder.java.main.EntityManager.bombs;
import static plunder.java.main.EntityManager.consumables;
import static plunder.java.main.EntityManager.enemies;
import static plunder.java.main.EntityManager.entities;
import static plunder.java.main.EntityManager.explosions;
import static plunder.java.main.EntityManager.player;
import static plunder.java.main.EntityManager.projectiles;
import static plunder.java.main.MapManager.environmentGrid;
import plunder.java.resources.AudioManager;

/**
 *
 * @author Kyle van Wiltenburg
 */
class Environment extends environment.Environment {
    
    private boolean paused;
    
    public GameState gameState;
    
    public static final int DEFAULT_WINDOW_WIDTH = 336;
    public static final int DEFAULT_WINDOW_HEIGHT = 192;
    public static final int DEFAULT_WINDOW_X = DEFAULT_WINDOW_WIDTH / 2;
    public static final int DEFAULT_WINDOW_Y = DEFAULT_WINDOW_HEIGHT / 2;
    
    public static final int GRID_CELL_SIZE = 12;
    
    public static final int HEARTS_PER_ROW = 6;
    
    private int xTranslation;
    private int yTranslation;
    
    PImageManager im;
    AudioManager am;

    public Environment() {
        
        gameState = GameState.ENVIRONMENT;
        im = new PImageManager();
        am = new AudioManager();
        
        environmentGrid = new Grid
        (DEFAULT_WINDOW_WIDTH / GRID_CELL_SIZE, DEFAULT_WINDOW_HEIGHT / GRID_CELL_SIZE, GRID_CELL_SIZE, GRID_CELL_SIZE, new Point(-DEFAULT_WINDOW_X, -DEFAULT_WINDOW_Y), Color.BLACK);
        
        MapManager.updateGrid(2, 2);
        
        player = new Player(new Point(0, 0), new PlayerScreenLimitProvider(environmentGrid.getGridSize().width - DEFAULT_WINDOW_WIDTH, environmentGrid.getGridSize().height - DEFAULT_WINDOW_HEIGHT), im, am);
    }
    
    

    Font gamefont, gamefont_7, gamefont_14;

    @Override
    public void initializeEnvironment() {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            InputStream input = classLoader.getResourceAsStream("plunder/resources/fonts/gamefont.ttf");

            gamefont = Font.createFont(Font.TRUETYPE_FONT, input);
            gamefont_7 = gamefont.deriveFont((float)7.0);
            gamefont_14 = gamefont.deriveFont((float)14.0);

        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void timerTaskHandler() {
        
        if (player != null && player.despawn()) player = null;
        
        ArrayList<Enemy> removeEnemies = new ArrayList<>();
        EntityManager.getEnemies().stream().forEach((enemy) -> {
            if (enemy.despawn()) removeEnemies.add(enemy);
        });
        enemies.removeAll(removeEnemies);
        
        ArrayList<Consumable> removeConsumables = new ArrayList<>();
        EntityManager.getConsumables().stream().forEach((consumable) -> {
            if (consumable.despawn()) removeConsumables.add(consumable);
        });
        consumables.removeAll(removeConsumables);
        
        ArrayList<PrimedBomb> removeBombs = new ArrayList<>();
        EntityManager.getBombs().stream().forEach((bomb) -> {
            if (bomb.despawn()) removeBombs.add(bomb);
        });
        bombs.removeAll(removeBombs);
        
        ArrayList<Explosion> removeExplosions = new ArrayList<>();
        EntityManager.getExplosions().stream().forEach((explosion) -> {
            if (explosion.despawn()) removeExplosions.add(explosion);
        });
        explosions.removeAll(removeExplosions);
        
        ArrayList<Projectile> removeProjectiles = new ArrayList<>();
        EntityManager.getProjectiles().stream().forEach((projectile) -> {
            if (projectile.despawn()) removeProjectiles.add(projectile);
        });
        projectiles.removeAll(removeProjectiles);
        
        entities = new ArrayList<>();
        if (player != null) entities.add(player);
        if (enemies != null) entities.addAll(EntityManager.getEnemies());
        if (consumables != null) entities.addAll(EntityManager.getConsumables());
        if (bombs != null) entities.addAll(EntityManager.getBombs());
        if (projectiles != null) entities.addAll(EntityManager.getProjectiles());
        
        entities.sort((Entity e1, Entity e2) -> {
            final int y1 = e1.getPosition().y;
            final int y2 = e2.getPosition().y;
            return y1 < y2 ? -1 : y1 > y2 ? 1 : 0;
        });
        
        if (!paused) {
            if (entities != null) {
                EntityManager.getEntities().stream().forEach((entity) -> {
                    entity.timerTaskHandler();
                });
            }
            if (explosions != null) {
                EntityManager.getExplosions().stream().forEach((explosion) -> {
                    explosion.timerTaskHandler();
                });
            }
        }
        
    }
    
    @Override
    public void keyPressedHandler(KeyEvent e) {
        // Uses the ProjectileArrow Keys to control the player
        if (player != null) {
            // Once pressing a key, it adds said key to the Directions list.
            if (e.getKeyCode() == KeyEvent.VK_W && !player.getDirections().contains(Direction.UP)) player.addDirection(Direction.UP);
            else if (e.getKeyCode() == KeyEvent.VK_S && !player.getDirections().contains(Direction.DOWN)) player.addDirection(Direction.DOWN);
            else if (e.getKeyCode() == KeyEvent.VK_A && !player.getDirections().contains(Direction.LEFT)) player.addDirection(Direction.LEFT);
            else if (e.getKeyCode() == KeyEvent.VK_D && !player.getDirections().contains(Direction.RIGHT)) player.addDirection(Direction.RIGHT);
            
            else if (e.getKeyCode() == KeyEvent.VK_UP && !player.getBowDirections().contains(Direction.UP)) player.addBowDirection(Direction.UP);
            else if (e.getKeyCode() == KeyEvent.VK_DOWN && !player.getBowDirections().contains(Direction.DOWN)) player.addBowDirection(Direction.DOWN);
            else if (e.getKeyCode() == KeyEvent.VK_LEFT && !player.getBowDirections().contains(Direction.LEFT)) player.addBowDirection(Direction.LEFT);
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT && !player.getBowDirections().contains(Direction.RIGHT)) player.addBowDirection(Direction.RIGHT);
            
            else if (e.getKeyCode() == KeyEvent.VK_SPACE) player.jump();
            
//            else if (e.getKeyCode() == KeyEvent.VK_1) MapManager.updateGrid(1, 1);
//            else if (e.getKeyCode() == KeyEvent.VK_2) MapManager.updateGrid(2, 2);
//            else if (e.getKeyCode() == KeyEvent.VK_3) MapManager.updateGrid(1, 2);
            
            else if (e.getKeyCode() == KeyEvent.VK_F && player != null) {
                consumables.add(new Heart(new Point(player.getPosition().x, player.getPosition().y - 30), 5, new Velocity(0, 0), 0, im, am));
            }
            else if (e.getKeyCode() == KeyEvent.VK_G && player != null) {
                consumables.add(new Bomb(new Point(player.getPosition().x, player.getPosition().y - 30), 5, new Velocity(0, 0), 0, im, am));
            }
            else if (e.getKeyCode() == KeyEvent.VK_H && player != null) {
                consumables.add(new Arrow(new Point(player.getPosition().x, player.getPosition().y - 30), 5, new Velocity(0, 0), 0, im, am));
            }
            else if (e.getKeyCode() == KeyEvent.VK_SLASH && player != null) {
                player.useBomb();
            }
            else if (e.getKeyCode() == KeyEvent.VK_PERIOD && player != null) {
                player.swingSword();
                player.setSwordSwingDebug(true);
            }

            else if (e.getKeyCode() == KeyEvent.VK_Z && player != null) {
                enemies.add(new Bat(new Point(player.getPosition().x, player.getPosition().y - 80), im, am));
            }
            
            else if (e.getKeyCode() == KeyEvent.VK_X && player != null) {
                enemies.add(new SamsaraEye(new Point(player.getPosition().x, player.getPosition().y - 80), im, am));
            }
            
            else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) paused = !paused;
            
        }
    }

    @Override
    public void keyReleasedHandler(KeyEvent e) {
        if (player != null) {
            // Once letting go of a key, it removes said key from the Directions list.
            if (e.getKeyCode() == KeyEvent.VK_W && player.getDirections().contains(Direction.UP)) player.removeDirection(Direction.UP);
            else if (e.getKeyCode() == KeyEvent.VK_S && player.getDirections().contains(Direction.DOWN)) player.removeDirection(Direction.DOWN);
            else if (e.getKeyCode() == KeyEvent.VK_A && player.getDirections().contains(Direction.LEFT)) player.removeDirection(Direction.LEFT);
            else if (e.getKeyCode() == KeyEvent.VK_D && player.getDirections().contains(Direction.RIGHT)) player.removeDirection(Direction.RIGHT);
            
            else if (e.getKeyCode() == KeyEvent.VK_UP && player.getBowDirections().contains(Direction.UP)) player.removeBowDirection(Direction.UP);
            else if (e.getKeyCode() == KeyEvent.VK_DOWN && player.getBowDirections().contains(Direction.DOWN)) player.removeBowDirection(Direction.DOWN);
            else if (e.getKeyCode() == KeyEvent.VK_LEFT && player.getBowDirections().contains(Direction.LEFT)) player.removeBowDirection(Direction.LEFT);
            else if (e.getKeyCode() == KeyEvent.VK_RIGHT && player.getBowDirections().contains(Direction.RIGHT)) player.removeBowDirection(Direction.RIGHT);
            else if (e.getKeyCode() == KeyEvent.VK_PERIOD) player.setSwordSwingDebug(false);
        }
    }
    
    @Override
    public void environmentMouseClicked(MouseEvent e) {
        Point ePoint = e.getPoint();
        ePoint.setLocation(
                ePoint.x * DEFAULT_WINDOW_WIDTH / Plunder.getWindowSize().width,
                ePoint.y * DEFAULT_WINDOW_HEIGHT / Plunder.getWindowSize().height
        );
    }
    
    @Override
    public void paintEnvironment(Graphics g) {
        
        // Resizes the default window size to the current size of the JFrame
        AffineTransform atWindow;
        Graphics2D graphics = (Graphics2D) g;
        atWindow = AffineTransform.getScaleInstance((double) Plunder.getWindowSize().width / DEFAULT_WINDOW_WIDTH, (double) Plunder.getWindowSize().height / DEFAULT_WINDOW_HEIGHT);
        if (atWindow != null) graphics.setTransform(atWindow);
        
        if (player != null) {
            xTranslation = player.getPosition().x;
            yTranslation = player.getPosition().y - player.getZDisplacement();
            if (xTranslation < player.getScreenMinX()) xTranslation = player.getScreenMinX();
            else if (xTranslation > player.getScreenMaxX()) xTranslation = player.getScreenMaxX();
            if (yTranslation < player.getScreenMinY()) yTranslation = player.getScreenMinY();
            else if (yTranslation > player.getScreenMaxY() + 8) yTranslation = player.getScreenMaxY() + 8;
            xTranslation = DEFAULT_WINDOW_X - xTranslation;
            yTranslation = DEFAULT_WINDOW_Y - yTranslation;
            if (MapManager.environmentGrid.getColumns()<= DEFAULT_WINDOW_WIDTH / GRID_CELL_SIZE) xTranslation = DEFAULT_WINDOW_WIDTH / 2;
            if (MapManager.environmentGrid.getRows() <= DEFAULT_WINDOW_HEIGHT / GRID_CELL_SIZE) yTranslation = DEFAULT_WINDOW_HEIGHT / 2;
        }
        
        // Translates all background images in reference to the player's current position
        graphics.translate(xTranslation, yTranslation);
        
        // Draws rectangles for debugging
        if (environmentGrid != null) {
            
//            drawGridBase(graphics);
            
//            buildWall(graphics, im.getImage(PImageManager.BRICK_TILE), -4, -2, 4, 2);
            
            TileMap.drawMap(graphics, xTranslation, yTranslation, im);
//            environmentGrid.paintComponent(graphics);
            
        }
        
        EntityManager.getExplosions().stream().forEach((explosion) -> {
            explosion.draw(graphics);
        });
        
        EntityManager.getEntities().stream().forEach((entity) -> {
            entity.draw(graphics);
        });
        
        graphics.translate(-xTranslation, -yTranslation);
        
        if (player != null) {
            for (int i = 0; i < player.getMaxHealth() / 2; i++) {
                graphics.drawImage(im.getImage(PImageManager.HEART_CONTAINER), 2 + (i % HEARTS_PER_ROW * 10), 2 + ((i / HEARTS_PER_ROW) * 9), 11, 10, null);
                if (player.healthBlip()) graphics.drawImage(im.getImage(PImageManager.HEART_BLIP), 2 + (i % HEARTS_PER_ROW * 10), 2 + ((i / HEARTS_PER_ROW) * 9), 11, 10, null);
            }
            
            for (int i = 0; i < player.getHealth(); i++) {
                if (i % 2 == 0) graphics.drawImage(im.getImage(PImageManager.HALF_HEART_LEFT), 2 + ((i % (HEARTS_PER_ROW * 2) / 2) * 10), 2 + ((i / (HEARTS_PER_ROW * 2)) * 9), 6, 10, null);
                else graphics.drawImage(im.getImage(PImageManager.HALF_HEART_RIGHT), 7 + ((i % (HEARTS_PER_ROW * 2) / 2) * 10), 2 + ((i / (HEARTS_PER_ROW * 2)) * 9), 6, 10, null);
            }
            graphics.drawImage(im.getImage(PImageManager.CONSUMABLE_BOMB_00), 5, 12 + ((((player.getMaxHealth() / 2) - 1) / HEARTS_PER_ROW) * 9), 5, 7, null);
            graphics.drawImage(im.getImage(PImageManager.CONSUMABLE_ARROW_00), 6, 20 + ((((player.getMaxHealth() / 2) - 1) / HEARTS_PER_ROW) * 9), 3, 7, null);
            graphics.setFont(gamefont_7);
            graphics.setColor(new Color(0, 0, 0, 80));
            graphics.drawString("x" + player.getBombCount(), 12, 19 + ((((player.getMaxHealth() / 2) - 1) / HEARTS_PER_ROW) * 9));
            graphics.drawString("x" + player.getArrowCount(), 12, 27 + ((((player.getMaxHealth() / 2) - 1) / HEARTS_PER_ROW) * 9));
            graphics.setColor(Color.WHITE);
            graphics.drawString("x" + player.getBombCount(), 11, 18 + ((((player.getMaxHealth() / 2) - 1) / HEARTS_PER_ROW) * 9));
            graphics.drawString("x" + player.getArrowCount(), 11, 26 + ((((player.getMaxHealth() / 2) - 1) / HEARTS_PER_ROW) * 9));
        }
        
        for (int i = 0; i < 7; i++) {
            graphics.drawImage(im.getImage(PImageManager.INVENTORY_SLOT), 222 + (i * 16), 2, 16, 16, null);
        }
        graphics.drawImage(im.getImage(PImageManager.ITEM_SWORD), 225, 5, 10, 10, null);
        graphics.drawImage(im.getImage(PImageManager.ITEM_BOW), 241, 5, 10, 10, null);
        
        if (paused) {
            graphics.setColor(new Color(0, 0, 0, 100));
            graphics.fillRect(0, 0, DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        }
        
    }
    
    public void drawGridBase(Graphics2D graphics) {
        graphics.setColor(Color.LIGHT_GRAY);
        graphics.fillRect(environmentGrid.getPosition().x + DEFAULT_WINDOW_X,
        environmentGrid.getPosition().y + DEFAULT_WINDOW_Y,
        environmentGrid.getGridSize().width - DEFAULT_WINDOW_WIDTH,
        environmentGrid.getGridSize().height - DEFAULT_WINDOW_HEIGHT);
        
        graphics.setColor(Color.GRAY);
        for (int i = 0; i < environmentGrid.getColumns(); i++) {
            int x = (int) environmentGrid.getCellSystemCoordinate(i, 0).x;
            graphics.fillRect(x, environmentGrid.getPosition().y, environmentGrid.getCellWidth(), environmentGrid.getCellHeight());
            graphics.fillRect(x, environmentGrid.getPosition().y + environmentGrid.getGridSize().height - environmentGrid.getCellHeight(), environmentGrid.getCellWidth(), environmentGrid.getCellHeight());
        }
        
        for (int i = 0; i < environmentGrid.getRows(); i++) {
            int y = (int) environmentGrid.getCellSystemCoordinate(0, i).y;
            graphics.fillRect(environmentGrid.getPosition().x, y, environmentGrid.getCellWidth(), environmentGrid.getCellHeight());
            graphics.fillRect(environmentGrid.getPosition().x + environmentGrid.getGridSize().width - environmentGrid.getCellWidth(), y, environmentGrid.getCellWidth(), environmentGrid.getCellHeight());
        }
        
        environmentGrid.paintComponent(graphics);
    }
    
    public void placeTile(Graphics2D graphics, BufferedImage image, int cellX, int cellY) {
        Point gridPoint = new Point(environmentGrid.getCellSystemCoordinate((environmentGrid.getColumns() / 2) + cellX, (environmentGrid.getRows() / 2) + cellY));
            if (gridPoint.x + environmentGrid.getCellWidth() >= -xTranslation &&
                gridPoint.x - DEFAULT_WINDOW_WIDTH <= -xTranslation &&
                gridPoint.y + environmentGrid.getCellHeight() >= -yTranslation &&
                gridPoint.y - DEFAULT_WINDOW_HEIGHT <= -yTranslation)
                
                graphics.drawImage(image,
                gridPoint.x, gridPoint.y,
                environmentGrid.getCellWidth(),
                environmentGrid.getCellHeight(), null);
                    
    }
    
}
