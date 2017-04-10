package com.akaver.tabbedradio;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.IOException;

/**
 * Created by akaver on 10/04/2017.
 */

public class MediaPlayerService extends Service implements
    MediaPlayer.OnCompletionListener,
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener,
        MediaPlayer.OnInfoListener
{
    private static final String TAG = MediaPlayerService.class.getSimpleName();


    //TODO use exoplayer

    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private String mMediaSource = "";


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");

        //set up the mediaplayer

        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnInfoListener(this);
        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");
        if (intent == null){
            return START_NOT_STICKY;
        }

        mMediaPlayer.reset();
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaSource = intent.getExtras().getString(C.INTENT_STREAM_SOURCE);
        Log.d(TAG, "Media source: " + mMediaSource);


        try {
            mMediaPlayer.setDataSource(mMediaSource);
            mMediaPlayer.prepareAsync();

            LocalBroadcastManager
                    .getInstance(getApplicationContext())
                    .sendBroadcast(new Intent(C.INTENT_STREAM_STATUS_BUFFERING));

        } catch (IOException e) {
            e.printStackTrace();
        }


        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // ================== MEDIAPLAYER callbacks =================
    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG, "MediaPlayer onCompletion");

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "MediaPlayer onError");
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        Log.d(TAG, "MediaPlayer onInfo");
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.d(TAG, "MediaPlayer onPrepared");

        mMediaPlayer.start();
        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .sendBroadcast(new Intent(C.INTENT_STREAM_STATUS_PLAYING));

    }
}
