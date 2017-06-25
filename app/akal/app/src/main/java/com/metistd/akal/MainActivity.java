package com.metistd.akal;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class MainActivity extends FragmentActivity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {
    //spotify:track:5JtPGzRgrWxkXX9LoROq3d
    public static final String defaultTrack = "spotify:track:62EnfJmuCuCTV1shACs9TN";

    private static final String CLIENT_ID = "8bc6fe3887fe404c906d28968aef9e94";
    private static final String REDIRECT_URI = "akalapp://callback";
    private static final int REQUEST_CODE = 8976;
    private Player mPlayer;
    private boolean playerReady = false;
    private boolean playingList = false;
    private int currentTrack = 0;
    //moby playlist.
    /*private static final ArrayList<String> playlist = new ArrayList<String>(Arrays.asList(
            "spotify:track:62EnfJmuCuCTV1shACs9TN",
            "spotify:track:2vKcvrE2YGciz9OyqAt14A",
            "spotify:track:5SXJh7e7UpnnOWPdC10gzM",
            "spotify:track:7IHgw0kWTNsdG1psdg3vie",
            "spotify:track:6sjthtPf4drsgCuggPbpPf",
            "spotify:track:0bFklu0eqe9Vw8n4yb4AEv",
            "spotify:track:78NU38JY9cJ622KQL578Xh",
            "spotify:track:0rbXbrLDkG01W23q8R3Ycl",
            "spotify:track:1ITY5rFFlvfIOJeRXuuWed",
            "spotify:track:7uZ7dY7xXRcwwdwBjxp7qG",
            "spotify:track:3ML1C8vKQzoSi1dqEma8Z9"));

    //sadhana
    */
    private static final ArrayList<String> playlist = new ArrayList<String>(Arrays.asList(
            "spotify:track:1zo8fCd1ZcW5EAL05JlVia",
            "spotify:track:65x8PLeBCisLJVSibyXemB",
            "spotify:track:77v7HC5yqQScXnu6v2VXw6",
            "spotify:track:0FtFnlRv4kocZoQKMwFHlF",
            "spotify:track:5OpxEeDuAI9QyQ2zVlM66K",
            "spotify:track:73exvKwaQL5StrRVJzGZVd",
            "spotify:track:5yduCrtXafg6OtQ2OkR7de"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AudioManager manager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        manager.setStreamVolume(AudioManager.STREAM_ALARM, 0, 0);
        manager.setStreamVolume(AudioManager.STREAM_RING, 0, 0);
        manager.setStreamVolume(AudioManager.STREAM_SYSTEM, 0, 0);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
        playerReady = true;
    }

    public void playOneTrack(String track){
        if(playerReady) {
            playingList = false;
            mPlayer.playUri(null, track, 0, 0);
        }
    }

    public void playAllTracks(){
        if(playerReady) {
            playingList = true;
            Log.d("MainActivity", "Now playing track " + currentTrack);
            mPlayer.playUri(null, playlist.get(currentTrack), 0, 0);
        }
    }

    public void resume(){
        if(playerReady) {
            mPlayer.resume(null);
        }
    }

    public void pause(){
        if(playerReady) {
            mPlayer.pause(null);
        }
    }

    public void stop(){
        currentTrack = 0;
        if(playerReady) {
            mPlayer.pause(null);
        }
    }

    public void forward(){
        if(playerReady) {
            mPlayer.pause(null);
        }
        if(currentTrack < (playlist.size()-1)){
            currentTrack ++;
        }
        playAllTracks();
    }

    public void rewind(){
        if(playerReady) {
            mPlayer.pause(null);
        }
        if(currentTrack > 0){
            currentTrack --;
        }
        playAllTracks();
    }

    public String getTrackName(){
        String name = "Track " + (currentTrack +1 );
        return name;
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d("MainActivity", "Received connection message: " + s);
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        switch (playerEvent) {
            case kSpPlaybackNotifyAudioDeliveryDone:
                if(playingList) {
                    //next song.
                    currentTrack ++;
                    if(currentTrack == playlist.size()){
                        currentTrack = 0;
                    }
                    Log.d("MainActivity", "Now playing track " + currentTrack);
                    mPlayer.playUri(null, playlist.get(currentTrack), 0, 0);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            default:
                break;
        }
    }
}
