/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.main;

import grid.Grid;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.ArrayList;
import static plunder.java.main.EntityManager.player;
import static plunder.java.main.Environment.DEFAULT_WINDOW_HEIGHT;
import static plunder.java.main.Environment.DEFAULT_WINDOW_WIDTH;

/**
 *
 * @author Kyle
 */
public class MapManager {
    
    ArrayList<String>[][] list = new ArrayList[10][10];
    
    public static Grid environmentGrid;
    
    public static void updateGrid(double xScreens, double yScreens) {
        if (xScreens < 1) xScreens = 1;
        if (yScreens < 1) yScreens = 1;
        int x = (int) (xScreens * DEFAULT_WINDOW_WIDTH);
        int y = (int) (yScreens * DEFAULT_WINDOW_HEIGHT);
        environmentGrid.setPosition(new Point(-(x / 2), -(y / 2)));
        environmentGrid.setColumns(x / environmentGrid.getCellWidth());
        environmentGrid.setRows(y / environmentGrid.getCellHeight());
        
        if (player != null) player.setScreenLimiter(
                environmentGrid.getGridSize().width - DEFAULT_WINDOW_WIDTH,
                environmentGrid.getGridSize().height - DEFAULT_WINDOW_HEIGHT);
    }
    
    public void addTile(TileID tileID, int x, int y) {
        
    }
    
    public void timerTaskHandler() {
        
    }
    
    public void drawTiles(Graphics2D graphics) {
        
    }
    
}
