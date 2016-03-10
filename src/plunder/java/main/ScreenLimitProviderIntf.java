/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.main;

/**
 *
 * @author Kyle van Wiltenburg
 */
public interface ScreenLimitProviderIntf {
    
    public int getMinX();
    public int getMaxX();
    public int getMinY();
    public int getMaxY();
    
    public void setMinX(int minX);
    public void setMaxX(int maxX);
    public void setMinY(int minY);
    public void setMaxY(int maxY);
    
}
