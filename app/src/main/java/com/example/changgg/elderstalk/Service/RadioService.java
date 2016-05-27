package com.example.changgg.elderstalk.Service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

// < ----- ----- ----- ----- ----- ----- Radio Stream ----- ----- ----- ----- ----- ----- >
// Android: Starting a service to play radio channels:
// http://stackoverflow.com/questions/14451432/android-starting-a-service-to-play-radio-channels

// Radio-Station-Stream-App-for-Android:
// https://github.com/tjnicolaides/Radio-Station-Stream-App-for-Android

// Online radio streaming app for Android:
// http://stackoverflow.com/questions/6283568/online-radio-streaming-app-for-android
// < ----- ----- ----- ----- ----- ----- Radio Stream ----- ----- ----- ----- ----- ----- >

public class RadioService extends Service{
    private static final String TAG = "RadioService";
    private MediaPlayer mMediaPlayer;

    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        mMediaPlayer = new MediaPlayer();
        try {
            mMediaPlayer.setDataSource("http://live.leanstream.co/ICRTFM-MP3?args=web");
            Log.i(TAG, "setDataSource");
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        mMediaPlayer.prepareAsync();
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
//                mMediaPlayer.start();
                Log.i(TAG, "mp & mMediaPlayer start()");
            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMediaPlayer.stop();
        Log.i(TAG, "onDestroy");
    }
}
