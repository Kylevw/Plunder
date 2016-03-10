/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.main;

import environment.ApplicationStarter;
import java.awt.Dimension;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;

/**
 *
 * @author Kyle van Wiltenburg
 */
public class Plunder {

    public static JFrame frame;
    
        /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        startUp();
    }

    private static void startUp() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();
        frame = ApplicationStarter.run(new String[0], "Plunder", new Dimension(width, height), new Environment());
    }
    
    public static Dimension getWindowSize() {
        if (frame != null) return frame.getContentPane().getSize();
        else return new Dimension(Environment.DEFAULT_WINDOW_WIDTH, Environment.DEFAULT_WINDOW_HEIGHT);
    }
}
