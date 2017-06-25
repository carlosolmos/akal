package com.metistd.akal;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;

import java.util.HashMap;

import static android.media.AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE;

/**
 * Created by carlosolmos on 6/24/17.
 */

public class SoundPlayer {
    public static final int CHIME1 = R.raw.chime1;
    private static SoundPool soundPool;
    private static HashMap<Integer, Integer> soundPoolMap;


    public static void initSounds(Context context) {
        int sampleRate = getBestSampleRate(context);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build();

        soundPool = new SoundPool.Builder().setAudioAttributes(attributes)
                .setMaxStreams(2)
                .build();
        soundPoolMap = new HashMap<Integer, Integer>();
        soundPoolMap.put( CHIME1, soundPool.load(context, R.raw.chime1, 1) );

    }
    public static int getBestSampleRate(Context context)
    {
        if (Build.VERSION.SDK_INT >= 17) {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            String sampleRateString = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
            Log.d("SoundPlayer", "Sample Rate: " + sampleRateString);
            int sampleRate = sampleRateString == null ? 44100 : Integer.parseInt(sampleRateString);

            return sampleRate;
        } else {
            return 44100;
        }
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
