package com.metistd.akal;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

/**
 * Created by carlosolmos on 6/24/17.
 */

public class SoundPlayer {
    public static final int CHIME1 = R.raw.chime1;
    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;


    public static void initSounds(Context context) {
        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .build();
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put( CHIME1, soundPool.load(context, R.raw.chime1, 1) );

    }

    public static void playSound(Context context, int soundID) {
        if(soundPool == null || soundPoolMap == null){
            initSounds(context);
        }
        float volume = new Float(0.5).floatValue(); // whatever in the range = 0.0 to 1.0
        // play sound with same right and left volume, with a priority of 1,
        // zero repeats (i.e play once), and a playback rate of 1f
        soundPool.play(soundPoolMap.get(soundID), volume, volume, 1, 0, 1f);
    }

}
