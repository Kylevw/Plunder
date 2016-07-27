/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.main;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import static plunder.java.main.Environment.DEFAULT_WINDOW_HEIGHT;
import static plunder.java.main.Environment.DEFAULT_WINDOW_WIDTH;
import static plunder.java.main.Environment.GRID_CELL_SIZE;
import static plunder.java.main.MapManager.environmentGrid;
import plunder.java.resources.ImageProviderIntf;
import plunder.java.resources.PImageManager;

/**
 *
 * @author Kyle
 */
public class TileMap {
    private static int xSize = environmentGrid.getColumns();
    private static int ySize = environmentGrid.getRows();
    private static int[][] mapData = new int[xSize][ySize];
    
    public static void setMap(int[][] mapData) {
        TileMap.mapData = mapData;
    }
    
    public static void drawMap(Graphics2D graphics, int xTranslation, int yTranslation, ImageProviderIntf im) {
        for (int x = 0; x < xSize; x++) {
            for (int y = 0; y < ySize; y++) {
                Point point = MapManager.environmentGrid.getCellSystemCoordinate(x, y);
                if (point.x + environmentGrid.getCellWidth() >= -xTranslation &&
                        point.y + environmentGrid.getCellHeight() >= -yTranslation &&
                        point.x - DEFAULT_WINDOW_WIDTH <= -xTranslation &&
                        point.y - DEFAULT_WINDOW_HEIGHT <= -yTranslation)
                    {
                        BufferedImage image = im.getImage(PImageManager.SAND_TILE);;
                        
                        if (mapData != null) {
                            if (mapData[x][y] == 1) image = im.getImage(PImageManager.BRICK_TILE_DARK);
                            else if (mapData[x][y] == 2) image = im.getImage(PImageManager.BRICK_TILE_UP);
                            else if (mapData[x][y] == 3) image = im.getImage(PImageManager.BRICK_TILE_DOWN);
                            else if (mapData[x][y] == 4) image = im.getImage(PImageManager.BRICK_TILE_LEFT);
                            else if (mapData[x][y] == 5) image = im.getImage(PImageManager.BRICK_TILE_RIGHT);
                            else if (mapData[x][y] == 6) image = im.getImage(PImageManager.BRICK_TILE_CORNER_IN_UPLEFT);
                            else if (mapData[x][y] == 8) image = im.getImage(PImageManager.BRICK_TILE_CORNER_IN_UPRIGHT);
                            else if (mapData[x][y] == 7) image = im.getImage(PImageManager.BRICK_TILE_CORNER_IN_DOWNLEFT);
                            else if (mapData[x][y] == 9) image = im.getImage(PImageManager.BRICK_TILE_CORNER_IN_DOWNRIGHT);
                        }
                        
                        if (image != null) graphics.drawImage(image,
                        point.x, point.y,
                        environmentGrid.getCellWidth(),
                        environmentGrid.getCellHeight(), null);
                    }
            }
        }
        
    }
    
    public static boolean collision(Rectangle objectBoundary) {
        Point gridCell = new Point(((objectBoundary.x + 1) / MapManager.environmentGrid.getCellWidth()) + (environmentGrid.getColumns() / 2) - 1,
                ((objectBoundary.y + 1) / MapManager.environmentGrid.getCellHeight()) + (environmentGrid.getRows() / 2) - 1);
        Point gridCell2 = new Point(((objectBoundary.x + objectBoundary.width - 2) / MapManager.environmentGrid.getCellWidth()) + (environmentGrid.getColumns() / 2),
                ((objectBoundary.y + objectBoundary.height - 2) / MapManager.environmentGrid.getCellHeight()) + (environmentGrid.getRows() / 2));
        return mapData[gridCell.x][gridCell.y] > 0 ||
               mapData[gridCell2.x][gridCell.y] > 0 || 
               mapData[gridCell.x][gridCell2.y] > 0 ||
               mapData[gridCell2.x][gridCell2.y] > 0;
    }
    
}
