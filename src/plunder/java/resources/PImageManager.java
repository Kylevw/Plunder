/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.resources;

import images.ImageManager;
import images.ResourceTools;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Kyle van Wiltenburg
 */
public class PImageManager extends ImageManager implements ImageProviderIntf{
    
    private static final String MISSING_TEXTURE = "MISSING_TEXTURE";
    
    public static final String SAND_TILE = "SAND_TILE";
    public static final String BRICK_TILE = "BRICK_TILE";
    
    public static final String BRICK_TILE_UP = "BRICK_TILE_UP";
    public static final String BRICK_TILE_DOWN = "BRICK_TILE_DOWN";
    public static final String BRICK_TILE_LEFT = "BRICK_TILE_LEFT";
    public static final String BRICK_TILE_RIGHT = "BRICK_TILE_RIGHT";
    public static final String BRICK_TILE_CORNER_IN_UPLEFT = "BRICK_TILE_CORNER_IN_UPLEFT";
    public static final String BRICK_TILE_CORNER_IN_UPRIGHT = "BRICK_TILE_CORNER_IN_UPRIGHT";
    public static final String BRICK_TILE_CORNER_IN_DOWNLEFT = "BRICK_TILE_CORNER_IN_DOWNLEFT";
    public static final String BRICK_TILE_CORNER_IN_DOWNRIGHT = "BRICK_TILE_CORNER_IN_DOWNRIGHT";
    public static final String BRICK_TILE_CORNER_OUT_UPLEFT = "BRICK_TILE_CORNER_OUT_UPLEFT";
    public static final String BRICK_TILE_CORNER_OUT_UPRIGHT = "BRICK_TILE_CORNER_OUT_UPRIGHT";
    public static final String BRICK_TILE_CORNER_OUT_DOWNLEFT = "BRICK_TILE_CORNER_OUT_DOWNLEFT";
    public static final String BRICK_TILE_CORNER_OUT_DOWNRIGHT = "BRICK_TILE_CORNER_OUT_DOWNRIGHT";
    public static final String BRICK_TILE_DARK = "BRICK_TILE_DARK";
    
    public static final String ENTITY_SHADOW = "ENTITY_SHADOW";
    
    public static final String BAT_UP = "BAT_UP";
    public static final String BAT_DOWN = "BAT_DOWN";
    
    public static final String HEART_CONTAINER = "HEART_CONTAINER";
    public static final String HALF_HEART_00 = "HALF_HEART_00";
    public static final String HALF_HEART_01 = "HALF_HEART_01";
    
    public static final String HEALTH_METER_0 = "HEALTH_METER_0";
    public static final String HEALTH_METER_1 = "HEALTH_METER_1";
    public static final String HEALTH_METER_2 = "HEALTH_METER_2";
    public static final String HEALTH_METER_3 = "HEALTH_METER_3";
    public static final String HEALTH_METER_4 = "HEALTH_METER_4";
    public static final String HEALTH_METER_5 = "HEALTH_METER_5";
    public static final String HEALTH_METER_6 = "HEALTH_METER_6";
    public static final String HEALTH_METER_7 = "HEALTH_METER_7";
    
    public static final String PLAYER_IDLE_UP_00 = "PLAYER_IDLE_UP_00";
    public static final String PLAYER_IDLE_DOWN_00 = "PLAYER_IDLE_DOWN_00";
    public static final String PLAYER_IDLE_LEFT_00 = "PLAYER_IDLE_LEFT_00";
    public static final String PLAYER_IDLE_RIGHT_00 = "PLAYER_IDLE_RIGHT_00";
    
    public static final String PLAYER_JUMP_UP_00 = "PLAYER_JUMP_UP_00";
    public static final String PLAYER_JUMP_DOWN_00 = "PLAYER_JUMP_DOWN_00";
    public static final String PLAYER_JUMP_LEFT_00 = "PLAYER_JUMP_LEFT_00";
    public static final String PLAYER_JUMP_RIGHT_00 = "PLAYER_JUMP_RIGHT_00";
    
    
    public static final String PLAYER_WALK_DOWN_00 = "PLAYER_WALK_DOWN_00";
    public static final String PLAYER_WALK_DOWN_01 = "PLAYER_WALK_DOWN_01";
    public static final String PLAYER_WALK_DOWN_02 = "PLAYER_WALK_DOWN_02";
    
    public static final String PLAYER_WALK_UP_00 = "PLAYER_WALK_UP_00";
    public static final String PLAYER_WALK_UP_01 = "PLAYER_WALK_UP_01";
    public static final String PLAYER_WALK_UP_02 = "PLAYER_WALK_UP_02";
    
    public static final String PLAYER_WALK_LEFT_00 = "PLAYER_WALK_LEFT_00";
    public static final String PLAYER_WALK_LEFT_01 = "PLAYER_WALK_LEFT_01";
    public static final String PLAYER_WALK_LEFT_02 = "PLAYER_WALK_LEFT_02";
    
    public static final String PLAYER_WALK_RIGHT_00 = "PLAYER_WALK_RIGHT_00";
    public static final String PLAYER_WALK_RIGHT_01 = "PLAYER_WALK_RIGHT_01";
    public static final String PLAYER_WALK_RIGHT_02 = "PLAYER_WALK_RIGHT_02";
    
    public static final String BAT_LIST = "BAT_LIST";
    
    public static final String PLAYER_IDLE_UP_LIST = "PLAYER_IDLE_UP_LIST";
    public static final String PLAYER_IDLE_DOWN_LIST = "PLAYER_IDLE_DOWN_LIST";
    public static final String PLAYER_IDLE_LEFT_LIST = "PLAYER_IDLE_LEFT_LIST";
    public static final String PLAYER_IDLE_RIGHT_LIST = "PLAYER_IDLE_RIGHT_LIST";
    
    public static final String PLAYER_WALK_UP_LIST = "PLAYER_WALK_UP_LIST";
    public static final String PLAYER_WALK_DOWN_LIST = "PLAYER_WALK_DOWN_LIST";
    public static final String PLAYER_WALK_LEFT_LIST = "PLAYER_WALK_LEFT_LIST";
    public static final String PLAYER_WALK_RIGHT_LIST = "PLAYER_WALK_RIGHT_LIST";
    
    public static final String PLAYER_JUMP_UP_LIST = "PLAYER_JUMP_UP_LIST";
    public static final String PLAYER_JUMP_DOWN_LIST = "PLAYER_JUMP_DOWN_LIST";
    public static final String PLAYER_JUMP_LEFT_LIST = "PLAYER_JUMP_LEFT_LIST";
    public static final String PLAYER_JUMP_RIGHT_LIST = "PLAYER_JUMP_RIGHT_LIST";
    
    private final ImageManager im;
    
    
    private final ArrayList<String> BAT;
    
    private final ArrayList<String> PLAYER_IDLE_UP;
    private final ArrayList<String> PLAYER_IDLE_DOWN;
    private final ArrayList<String> PLAYER_IDLE_LEFT;
    private final ArrayList<String> PLAYER_IDLE_RIGHT;
    
    private final ArrayList<String> PLAYER_WALK_UP;
    private final ArrayList<String> PLAYER_WALK_DOWN;
    private final ArrayList<String> PLAYER_WALK_LEFT;
    private final ArrayList<String> PLAYER_WALK_RIGHT;
    
    private final ArrayList<String> PLAYER_JUMP_UP;
    private final ArrayList<String> PLAYER_JUMP_DOWN;
    private final ArrayList<String> PLAYER_JUMP_LEFT;
    private final ArrayList<String> PLAYER_JUMP_RIGHT;
    
    
    private final HashMap<String, Image> imageMap;
    private final HashMap<String, ArrayList> imageListMap;
    
    {
        imageListMap = new HashMap<>();
        imageMap = new HashMap<>();
        
        im = new ImageManager(imageMap);
        
        PLAYER_WALK_UP = new ArrayList<>();
        PLAYER_WALK_DOWN = new ArrayList<>();
        PLAYER_WALK_LEFT = new ArrayList<>();
        PLAYER_WALK_RIGHT = new ArrayList<>();
        
        BAT = new ArrayList();
        
        PLAYER_IDLE_UP = new ArrayList<>();
        PLAYER_IDLE_DOWN = new ArrayList<>();
        PLAYER_IDLE_LEFT = new ArrayList<>();
        PLAYER_IDLE_RIGHT = new ArrayList<>();
        
        PLAYER_JUMP_UP = new ArrayList<>();
        PLAYER_JUMP_DOWN = new ArrayList<>();
        PLAYER_JUMP_LEFT = new ArrayList<>();
        PLAYER_JUMP_RIGHT = new ArrayList<>();
        
        
        BAT.add(BAT_UP);
        BAT.add(BAT_DOWN);
        
        PLAYER_IDLE_UP.add(PLAYER_IDLE_UP_00);
        PLAYER_IDLE_DOWN.add(PLAYER_IDLE_DOWN_00);
        PLAYER_IDLE_LEFT.add(PLAYER_IDLE_LEFT_00);
        PLAYER_IDLE_RIGHT.add(PLAYER_IDLE_RIGHT_00);
        
        PLAYER_JUMP_UP.add(PLAYER_JUMP_UP_00);
        PLAYER_JUMP_DOWN.add(PLAYER_JUMP_DOWN_00);
        PLAYER_JUMP_LEFT.add(PLAYER_JUMP_LEFT_00);
        PLAYER_JUMP_RIGHT.add(PLAYER_JUMP_RIGHT_00);
        
        PLAYER_WALK_DOWN.add(PLAYER_WALK_DOWN_00);
        PLAYER_WALK_DOWN.add(PLAYER_WALK_DOWN_01);
        PLAYER_WALK_DOWN.add(PLAYER_WALK_DOWN_02);
        PLAYER_WALK_DOWN.add(PLAYER_WALK_DOWN_01);
        
        PLAYER_WALK_UP.add(PLAYER_WALK_UP_00);
        PLAYER_WALK_UP.add(PLAYER_WALK_UP_01);
        PLAYER_WALK_UP.add(PLAYER_WALK_UP_02);
        PLAYER_WALK_UP.add(PLAYER_WALK_UP_01);
        
        PLAYER_WALK_LEFT.add(PLAYER_WALK_LEFT_00);
        PLAYER_WALK_LEFT.add(PLAYER_WALK_LEFT_01);
        PLAYER_WALK_LEFT.add(PLAYER_WALK_LEFT_02);
        PLAYER_WALK_LEFT.add(PLAYER_WALK_LEFT_01);
        
        PLAYER_WALK_RIGHT.add(PLAYER_WALK_RIGHT_00);
        PLAYER_WALK_RIGHT.add(PLAYER_WALK_RIGHT_01);
        PLAYER_WALK_RIGHT.add(PLAYER_WALK_RIGHT_02);
        PLAYER_WALK_RIGHT.add(PLAYER_WALK_RIGHT_01);
        
        imageListMap.put(BAT_LIST, BAT);
        
        imageListMap.put(PLAYER_IDLE_UP_LIST, PLAYER_IDLE_UP);
        imageListMap.put(PLAYER_IDLE_DOWN_LIST, PLAYER_IDLE_DOWN);
        imageListMap.put(PLAYER_IDLE_LEFT_LIST, PLAYER_IDLE_LEFT);
        imageListMap.put(PLAYER_IDLE_RIGHT_LIST, PLAYER_IDLE_RIGHT);
        
        imageListMap.put(PLAYER_WALK_UP_LIST, PLAYER_WALK_UP);
        imageListMap.put(PLAYER_WALK_DOWN_LIST, PLAYER_WALK_DOWN);
        imageListMap.put(PLAYER_WALK_LEFT_LIST, PLAYER_WALK_LEFT);
        imageListMap.put(PLAYER_WALK_RIGHT_LIST, PLAYER_WALK_RIGHT);
        
        imageListMap.put(PLAYER_JUMP_UP_LIST, PLAYER_JUMP_UP);
        imageListMap.put(PLAYER_JUMP_DOWN_LIST, PLAYER_JUMP_DOWN);
        imageListMap.put(PLAYER_JUMP_LEFT_LIST, PLAYER_JUMP_LEFT);
        imageListMap.put(PLAYER_JUMP_RIGHT_LIST, PLAYER_JUMP_RIGHT);
        
        imageMap.put(MISSING_TEXTURE, ResourceTools.loadImageFromResource("plunder/resources/images/utility/missing_texture.png"));
        imageMap.put(ENTITY_SHADOW, ResourceTools.loadImageFromResource("plunder/resources/images/utility/entity_shadow.png"));
        
        imageMap.put(SAND_TILE, ResourceTools.loadImageFromResource("plunder/resources/images/utility/sand_tile.png"));
        imageMap.put(BRICK_TILE, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile.png"));
        
        
        imageMap.put(BRICK_TILE_UP, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_up.png"));
        imageMap.put(BRICK_TILE_DOWN, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_down.png"));
        imageMap.put(BRICK_TILE_LEFT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_left.png"));
        imageMap.put(BRICK_TILE_RIGHT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_right.png"));
        imageMap.put(BRICK_TILE_CORNER_IN_UPLEFT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_corner_in_upleft.png"));
        imageMap.put(BRICK_TILE_CORNER_IN_UPRIGHT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_corner_in_upright.png"));
        imageMap.put(BRICK_TILE_CORNER_IN_DOWNLEFT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_corner_in_downleft.png"));
        imageMap.put(BRICK_TILE_CORNER_IN_DOWNRIGHT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_corner_in_downright.png"));
        imageMap.put(BRICK_TILE_CORNER_OUT_UPLEFT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_corner_out_upleft.png"));
        imageMap.put(BRICK_TILE_CORNER_OUT_UPRIGHT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_corner_out_upright.png"));
        imageMap.put(BRICK_TILE_CORNER_OUT_DOWNLEFT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_corner_out_downleft.png"));
        imageMap.put(BRICK_TILE_CORNER_OUT_DOWNRIGHT, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_corner_out_downright.png"));
        imageMap.put(BRICK_TILE_DARK, ResourceTools.loadImageFromResource("plunder/resources/images/utility/brick_tile_dark.png"));
        
        BufferedImage healthMeter = (BufferedImage) ResourceTools.loadImageFromResource("plunder/resources/images/utility/health_meter.png");
        BufferedImage heartSprites = (BufferedImage) ResourceTools.loadImageFromResource("plunder/resources/images/utility/player_health.png");
        
        BufferedImage bat = (BufferedImage) ResourceTools.loadImageFromResource("plunder/resources/images/entity/bat.png");
        
        BufferedImage playerSprites = (BufferedImage) ResourceTools.loadImageFromResource("plunder/resources/images/entity/player.png");
        
        imageMap.put(HEART_CONTAINER, heartSprites.getSubimage(0, 0, 9, 8));
        imageMap.put(HALF_HEART_00, heartSprites.getSubimage(9, 0, 5, 8));
        imageMap.put(HALF_HEART_01, heartSprites.getSubimage(13, 0, 5, 8));
        
        imageMap.put(BAT_UP, bat.getSubimage(0, 0, 9, 8));
        imageMap.put(BAT_DOWN, bat.getSubimage(9, 0, 9, 8));
        
        imageMap.put(PLAYER_IDLE_UP_00, playerSprites.getSubimage(16, 16, 16, 16));
        imageMap.put(PLAYER_IDLE_DOWN_00, playerSprites.getSubimage(0, 16, 16, 16));
        imageMap.put(PLAYER_IDLE_LEFT_00, playerSprites.getSubimage(32, 16, 16, 16));
        imageMap.put(PLAYER_IDLE_RIGHT_00, playerSprites.getSubimage(48, 16, 16, 16));
        
        imageMap.put(PLAYER_JUMP_UP_00, playerSprites.getSubimage(16, 48, 16, 16));
        imageMap.put(PLAYER_JUMP_DOWN_00, playerSprites.getSubimage(0, 48, 16, 16));
        imageMap.put(PLAYER_JUMP_LEFT_00, playerSprites.getSubimage(32, 48, 16, 16));
        imageMap.put(PLAYER_JUMP_RIGHT_00, playerSprites.getSubimage(48, 48, 16, 16)); 
        
        imageMap.put(PLAYER_WALK_DOWN_00, playerSprites.getSubimage(0, 0, 16, 16));
        imageMap.put(PLAYER_WALK_DOWN_01, playerSprites.getSubimage(0, 17, 16, 16));
        imageMap.put(PLAYER_WALK_DOWN_02, playerSprites.getSubimage(0, 32, 16, 16));
        
        imageMap.put(PLAYER_WALK_UP_00, playerSprites.getSubimage(16, 0, 16, 16));
        imageMap.put(PLAYER_WALK_UP_01, playerSprites.getSubimage(16, 17, 16, 16));
        imageMap.put(PLAYER_WALK_UP_02, playerSprites.getSubimage(16, 32, 16, 16));
        
        imageMap.put(PLAYER_WALK_LEFT_00, playerSprites.getSubimage(32, 0, 16, 16));
        imageMap.put(PLAYER_WALK_LEFT_01, playerSprites.getSubimage(32, 17, 16, 16));
        imageMap.put(PLAYER_WALK_LEFT_02, playerSprites.getSubimage(32, 32, 16, 16));
        
        imageMap.put(PLAYER_WALK_RIGHT_00, playerSprites.getSubimage(48, 0, 16, 16));
        imageMap.put(PLAYER_WALK_RIGHT_01, playerSprites.getSubimage(48, 17, 16, 16));
        imageMap.put(PLAYER_WALK_RIGHT_02, playerSprites.getSubimage(48, 32, 16, 16));
        
        imageMap.put(HEALTH_METER_7, healthMeter.getSubimage(0, 0, 9, 2));
        imageMap.put(HEALTH_METER_6, healthMeter.getSubimage(0, 2, 9, 2));
        imageMap.put(HEALTH_METER_5, healthMeter.getSubimage(0, 4, 9, 2));
        imageMap.put(HEALTH_METER_4, healthMeter.getSubimage(0, 6, 9, 2));
        imageMap.put(HEALTH_METER_3, healthMeter.getSubimage(0, 8, 9, 2));
        imageMap.put(HEALTH_METER_2, healthMeter.getSubimage(0, 10, 9, 2));
        imageMap.put(HEALTH_METER_1, healthMeter.getSubimage(0, 12, 9, 2));
        imageMap.put(HEALTH_METER_0, healthMeter.getSubimage(0, 14, 9, 2));
        
    }
    
    @Override
    public BufferedImage getImage(String name){
        BufferedImage image = (BufferedImage) im.getImage(name);
        if (image == null) image = (BufferedImage) im.getImage(MISSING_TEXTURE);
        return image;
    }
    
    @Override
    public ArrayList<String> getImageList(String listName){
        ArrayList<String> arrayList = imageListMap.get(listName);
        if (arrayList == null || arrayList.isEmpty()) {
            arrayList = new ArrayList<>();
            arrayList.add(MISSING_TEXTURE);
        }
        return arrayList;
    }
}

