/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.resources;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 *
 * @author Kyle van Wiltenburg
 */
public interface ImageProviderIntf {
    
    public BufferedImage getImage(String name);
    public void drawTintedImage(Graphics2D graphics, BufferedImage image, int x, int y, int width, int height, Color tintColor);
    public ArrayList<String> getImageList(String listName);
    
}
