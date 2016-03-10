/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.main;

import java.awt.Graphics2D;
import java.awt.Point;

/**
 *
 * @author Kyle
 */
public class Tile {
    
    private final Point position;
    private final TileID id;
    
    public Tile(Point position, TileID id) {
        this.position = position;
        this.id = id;
    }
    
    public void draw (Graphics2D graphics) {
        
    }
    
    public Point getGridPosition() {
        return position;
    }
    
    public Point getEnvironmentPosition() {
        Point point = new Point(position.x * Environment.GRID_CELL_SIZE, position.y * Environment.GRID_CELL_SIZE);
        return point;
    }
}
