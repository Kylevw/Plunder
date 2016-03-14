/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.main;

import static environment.Utility.random;
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
import plunder.java.entities.Bat;
import plunder.java.entities.Bomb;
import plunder.java.entities.Consumable;
import plunder.java.entities.Enemy;
import plunder.java.entities.Entity;
import plunder.java.entities.Explosion;
import plunder.java.entities.Heart;
import plunder.java.entities.PrimedBomb;
import static plunder.java.main.EntityManager.bombs;
import static plunder.java.main.EntityManager.consumables;
import static plunder.java.main.EntityManager.enemies;
import static plunder.java.main.EntityManager.entities;
import static plunder.java.main.EntityManager.explosions;
import static plunder.java.main.EntityManager.player;
import plunder.java.resources.AudioManager;

/**
 *
 * @author Kyle van Wiltenburg
 */
class Environment extends environment.Environment {
    
    public final Grid environmentGrid;
    
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
        (DEFAULT_WINDOW_WIDTH / GRID_CELL_SIZE, DEFAULT_WINDOW_HEIGHT / GRID_CELL_SIZE * 2, GRID_CELL_SIZE, GRID_CELL_SIZE, new Point(-DEFAULT_WINDOW_X, -DEFAULT_WINDOW_Y), Color.BLACK);
        
        updateGrid(2, 2);
        
        enemies.add(new Bat(new Point(100, 0), im, am));
        enemies.add(new Bat(new Point(100, 40), im, am));
        enemies.add(new Bat(new Point(100, -40), im, am));
        
        player = new Player(new Point(0, 0), new PlayerScreenLimitProvider(environmentGrid.getGridSize().width - DEFAULT_WINDOW_WIDTH, environmentGrid.getGridSize().height - DEFAULT_WINDOW_HEIGHT), im, am);
    }
    
    private void updateGrid(double xScreens, double yScreens) {
        if (xScreens < 1) xScreens = 1;
        if (yScreens < 1) yScreens = 1;
        int x = (int) (xScreens * DEFAULT_WINDOW_WIDTH);
        int y = (int) (yScreens * DEFAULT_WINDOW_HEIGHT);
        environmentGrid.setPosition(new Point(-(x / 2), -(y / 2)));
        environmentGrid.setColumns(x / environmentGrid.getCellWidth());
        environmentGrid.setRows(y / environmentGrid.getCellHeight());
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

        } catch (FontFormatException ex) {
            Logger.getLogger(Environment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
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
        
        entities = new ArrayList<>();
        if (player != null) entities.add(player);
        if (enemies != null) entities.addAll(EntityManager.getEnemies());
        if (consumables != null) entities.addAll(EntityManager.getConsumables());
        if (bombs != null) entities.addAll(EntityManager.getBombs());
        
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
        // Uses the Arrow Keys to control the player
        if (player != null) {
            // Once pressing a key, it adds said key to the Directions list.
            if (e.getKeyCode() == KeyEvent.VK_W && !player.getDirections().contains(Direction.UP)) player.addDirection(Direction.UP);
            else if (e.getKeyCode() == KeyEvent.VK_S && !player.getDirections().contains(Direction.DOWN)) player.addDirection(Direction.DOWN);
            else if (e.getKeyCode() == KeyEvent.VK_A && !player.getDirections().contains(Direction.LEFT)) player.addDirection(Direction.LEFT);
            else if (e.getKeyCode() == KeyEvent.VK_D && !player.getDirections().contains(Direction.RIGHT)) player.addDirection(Direction.RIGHT);
            
            else if (e.getKeyCode() == KeyEvent.VK_SPACE) player.jump();
            
            else if (e.getKeyCode() == KeyEvent.VK_E && player != null) {
                consumables.add(new Heart(new Point(player.getPosition().x, player.getPosition().y - 30), 0, new Velocity(random(3) - 1, random(3) - 1), 1.5, im, am));
            }
            
            else if (e.getKeyCode() == KeyEvent.VK_Q && player != null) {
                consumables.add(new Bomb(new Point(player.getPosition().x, player.getPosition().y - 30), 0, new Velocity(random(3) - 1, random(3) - 1), 3, im, am));
            }
            
            else if (e.getKeyCode() == KeyEvent.VK_F && player != null) {
                consumables.add(new Heart(new Point(player.getPosition().x, player.getPosition().y - 30), 5, new Velocity(0, 0), 0, im, am));
            }
            
            else if (e.getKeyCode() == KeyEvent.VK_G && player != null) {
                consumables.add(new Bomb(new Point(player.getPosition().x, player.getPosition().y - 30), 5, new Velocity(0, 0), 0, im, am));
            }
            
            else if (e.getKeyCode() == KeyEvent.VK_J && player != null) {
                player.useBomb();
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
        }
    }
    
    @Override
    public void environmentMouseClicked(MouseEvent e) {
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
            yTranslation = player.getPosition().y - player.getZDisplacement() - (player.getSize().height / 2);
            if (xTranslation < player.getScreenMinX()) xTranslation = player.getScreenMinX();
            else if (xTranslation > player.getScreenMaxX()) xTranslation = player.getScreenMaxX();
            if (yTranslation < player.getScreenMinY()) yTranslation = player.getScreenMinY();
            else if (yTranslation > player.getScreenMaxY()) yTranslation = player.getScreenMaxY();
            xTranslation = DEFAULT_WINDOW_X - xTranslation;
            yTranslation = DEFAULT_WINDOW_Y - yTranslation;
        }
        
        // Translates all background images in reference to the player's current position
        graphics.translate(xTranslation, yTranslation);
        
        // Draws rectangles for debugging
        if (environmentGrid != null) {
            
//            drawGridBase(graphics);
            
            fillGrid(graphics, im.getImage(PImageManager.SAND_TILE));
//            buildWall(graphics, im.getImage(PImageManager.BRICK_TILE), -4, -2, 4, 2);
            
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_CORNER_IN_UPLEFT), -2, -2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_CORNER_IN_UPRIGHT), 0, -2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_CORNER_IN_DOWNLEFT), -2, 0);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_CORNER_IN_DOWNRIGHT), 0, 0);
            
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_UP), -1, -2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DOWN), -1, 0);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_LEFT), -2, -1);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_RIGHT), 0, -1);
            
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_CORNER_OUT_UPLEFT), 2, 2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_CORNER_OUT_UPRIGHT), -4, 2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_CORNER_OUT_DOWNLEFT), 2, -4);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_CORNER_OUT_DOWNRIGHT), -4, -4);
            
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DOWN), -3, -4);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DOWN), -2, -4);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DOWN), -1, -4);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DOWN), 0, -4);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DOWN), 1, -4);
            
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_UP), -3, 2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_UP), -2, 2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_UP), -1, 2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_UP), 0, 2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_UP), 1, 2);
            
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_LEFT), 2, -3);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_LEFT), 2, -2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_LEFT), 2, -1);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_LEFT), 2, 0);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_LEFT), 2, 1);
            
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_RIGHT), -4, -3);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_RIGHT), -4, -2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_RIGHT), -4, -1);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_RIGHT), -4, 0);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_RIGHT), -4, 1);
            
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), -3, -3);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), -2, -3);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), -1, -3);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), 0, -3);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), 1, -3);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), -3, -2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), 1, -2);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), -3, -1);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), 1, -1);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), -3, 0);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), 1, 0);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), -3, 1);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), -2, 1);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), -1, 1);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), 0, 1);
            placeTile(graphics, im.getImage(PImageManager.BRICK_TILE_DARK), 1, 1);
            
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
            graphics.setFont(gamefont_7);
            graphics.setColor(new Color(0, 0, 0, 80));
            graphics.drawString("x" + player.getBombCount(), 12, 19 + ((((player.getMaxHealth() / 2) - 1) / HEARTS_PER_ROW) * 9));
            graphics.setColor(Color.WHITE);
            graphics.drawString("x" + player.getBombCount(), 11, 18 + ((((player.getMaxHealth() / 2) - 1) / HEARTS_PER_ROW) * 9));
        }
        
        for (int i = 0; i < 7; i++) {
            graphics.drawImage(im.getImage(PImageManager.INVENTORY_SLOT), 222 + (i * 16), 2, 16, 16, null);
        }
        graphics.drawImage(im.getImage(PImageManager.ITEM_SWORD), 225, 5, 10, 10, null);
        graphics.drawImage(im.getImage(PImageManager.ITEM_BOW), 241, 5, 10, 10, null);
        
//        im.drawTintedImage(graphics, im.getImage(PImageManager.PLAYER_IDLE_DOWN_00), 10, 10, 16, 16, new Color(255, 0, 0, 50));
        
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
    
    public void buildWall(Graphics2D graphics, BufferedImage image, int cellX, int cellY, int columns, int rows, int xTranslation, int yTranslation) {
        for (int x = 0; x < columns; x++) {
            for (int y = 0; y < rows; y++) {
                
                Point gridPoint = new Point(environmentGrid.getCellSystemCoordinate((environmentGrid.getColumns() / 2) + cellX + x, (environmentGrid.getRows() / 2) + cellY + y));
                    if (player != null &&
                        gridPoint.x + environmentGrid.getCellWidth() >= -xTranslation &&
                        gridPoint.x - DEFAULT_WINDOW_WIDTH <= -xTranslation &&
                        gridPoint.y + environmentGrid.getCellHeight() >= -yTranslation &&
                        gridPoint.y - DEFAULT_WINDOW_HEIGHT <= -yTranslation)
                      
                        graphics.drawImage(image,
                        gridPoint.x, gridPoint.y,
                        environmentGrid.getCellWidth(),
                        environmentGrid.getCellHeight(), null);
                
            }
        }
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
    
    public void fillGrid(Graphics2D graphics, BufferedImage image) {
        for (int x = 0; x < environmentGrid.getColumns(); x++) {
                for (int y = 0; y < environmentGrid.getRows(); y++) {
                    
                    Point gridPoint = new Point(environmentGrid.getCellSystemCoordinate(x, y));
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
    }
    
}
