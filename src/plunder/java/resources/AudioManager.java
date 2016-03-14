/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package plunder.java.resources;

import audio.Playlist;
import audio.SoundManager;
import audio.Source;
import audio.Track;
import java.util.ArrayList;

/**
 *
 * @author Kyle
 */
public class AudioManager implements AudioPlayerIntf {
    
    private final SoundManager am;
    private final ArrayList<Track> tracks = new ArrayList<>();
    
    public static String EXPLOSION = "EXPLOSION";
    
    {
        tracks.add(new Track(EXPLOSION, Source.RESOURCE, "/plunder/resources/sounds/explosion.wav"));

        am = new SoundManager(new Playlist(tracks));
    }
    
    @Override
    public void playAudio(String name, boolean loop) {
        if (loop) am.play(name, -1);
        else am.play(name);
    }

    @Override
    public void stopAudio(String name) {
        am.stop(name);
    }
    
}
