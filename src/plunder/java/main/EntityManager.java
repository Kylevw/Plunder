/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.main;

import java.util.ArrayList;
import plunder.java.entities.Enemy;
import plunder.java.entities.Player;

/**
 *
 * @author Kyle
 */
public class EntityManager {
    
    public static Player player;
    public static ArrayList<Enemy> enemies = new ArrayList<>();
    
    public static ArrayList<Enemy> getEnemies() {
        return enemies;
    }
    
}
