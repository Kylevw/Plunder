/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.resources;

/**
 *
 * @author Kyle
 */
public interface AudioPlayerIntf {
    
    public void playAudio(String name, boolean loop);
    public void stopAudio(String name);
    
}