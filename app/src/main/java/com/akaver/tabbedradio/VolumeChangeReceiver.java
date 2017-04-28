package com.akaver.tabbedradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

/**
 * Created by akaver on 28/04/2017.
 */

public class VolumeChangeReceiver extends BroadcastReceiver {
    private static final String TAG = VolumeChangeReceiver.class.getSimpleName();

    private static int prevMediaVolume = -1;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.getAction());

        switch (intent.getAction()){
            case "android.media.VOLUME_CHANGED_ACTION":
                int volume = intent.getIntExtra("android.media.EXTRA_VOLUME_STREAM_VALUE",-1);
                Log.d(TAG, "Volume: " + volume + " prev: "+prevMediaVolume);

                if (volume != prevMediaVolume){
                    prevMediaVolume = volume;
                    LocalBroadcastManager
                            .getInstance(context)
                            .sendBroadcast(new Intent(C.INTENT_STREAM_VOLUME_CHANGED));
                }

                break;
            default:
                break;
        }



    }
}
