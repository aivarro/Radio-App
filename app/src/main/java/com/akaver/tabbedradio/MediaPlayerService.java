package com.akaver.tabbedradio;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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


    private PhoneStateListener mPhoneStateListener;
    private TelephonyManager telephonyManager;

    private ScheduledExecutorService mScheduledExecutorService;

    private boolean mMusicIsPausedInCall;

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


        telephonyManager = (TelephonyManager) getSystemService(getBaseContext().TELEPHONY_SERVICE);

        mPhoneStateListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                //super.onCallStateChanged(state, incomingNumber);
                Log.d(TAG, "onCallStateChanged: " + state);

                switch (state){

                    // call is incoming
                    case TelephonyManager.CALL_STATE_RINGING:
                        Log.d(TAG, "onCallStateChanged: RINGING");
                        if (mMediaPlayer != null){
                            mMediaPlayer.stop();
                            mMusicIsPausedInCall = true;
                            LocalBroadcastManager
                                    .getInstance(getApplicationContext())
                                    .sendBroadcast(new Intent(C.INTENT_STREAM_STATUS_STOPPED));
                        }
                        break;

                    // ongoing phonecall
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d(TAG, "onCallStateChanged: OFFHOOK");
                        break;

                    // waiting for next call
                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d(TAG, "onCallStateChanged: IDLE");
                        if (mMediaPlayer!=null && mMusicIsPausedInCall){
                            try {
                                mMediaPlayer.reset();
                                mMediaPlayer.setDataSource(mMediaSource);
                                mMediaPlayer.prepareAsync();
                                LocalBroadcastManager
                                        .getInstance(getApplicationContext())
                                        .sendBroadcast(new Intent(C.INTENT_STREAM_STATUS_BUFFERING));

                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        break;

                    default:
                        Log.d(TAG, "onCallStateChanged: unknown!" );
                        break;
                }

            }
        };


        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);




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


    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();

        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }

        if (mPhoneStateListener != null){
            telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE);
        }

        LocalBroadcastManager
                .getInstance(getApplicationContext())
                .sendBroadcast(new Intent(C.INTENT_STREAM_STATUS_STOPPED));

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

        startMediaInfoService();

    }

    private void startMediaInfoService(){
        mScheduledExecutorService = Executors.newScheduledThreadPool(5);

        mScheduledExecutorService.scheduleAtFixedRate(
                new Runnable() {
                    @Override
                    public void run() {
                        // this will be executed at fixed rate
                        Intent infoIntent = new Intent(C.INTENT_STREAM_INFO);
                        infoIntent.putExtra(C.INTENT_STREAM_INFO_ARTIST,"Artist " + new Date().toString());
                        infoIntent.putExtra(C.INTENT_STREAM_INFO_TITLE,"Title " + Calendar.getInstance().get(Calendar.SECOND));

                        LocalBroadcastManager
                                .getInstance(getApplicationContext())
                                .sendBroadcast(infoIntent);
                    }
                },
                0, // initial delay, 0 - execute immediately after initialization
                15, // delay between starts
                TimeUnit.SECONDS
        );

    }
}
