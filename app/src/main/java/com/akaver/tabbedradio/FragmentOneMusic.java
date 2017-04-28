package com.akaver.tabbedradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

/**
 * Created by akaver on 07/04/2017.
 */

public class FragmentOneMusic extends Fragment implements SeekBar.OnSeekBarChangeListener {

    private static final String TAG = FragmentOneMusic.class.getSimpleName();

    private static final String[] streamSources = {
            "http://sky.babahhcdn.com/SKYPLUS",
            "http://sky.babahhcdn.com/NRJ"
    };



    private Spinner mSpinnerStreamSource;
    private Button mButtonPlayStop;
    private String mSelectedStreamSource;


    private AudioManager audioManager;

    private SeekBar mSeekBarVolume;

    private IntentFilter mIntentFilter;
    private BroadcastReceiver mBroadcastReceiver;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG,"onCreateView");
        View view = inflater.inflate(R.layout.fragment_one_music, container, false);


        // set up the spinner and its data source (dropdown)
        mSpinnerStreamSource = (Spinner) view.findViewById(R.id.spinnerStreamSource);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(
                        getContext(),
                        android.R.layout.simple_spinner_item,
                        streamSources);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        mSpinnerStreamSource.setAdapter(adapter);

        mSpinnerStreamSource.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener(){

                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Log.d(TAG, "mSpinnerStreamSource.OnItemSelectedListener " + (String) parent.getItemAtPosition(position));
                        mSelectedStreamSource = (String) parent.getItemAtPosition(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // nothing to do
                    }
                }
        );

        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        // set up the volume seekbar
        mSeekBarVolume = (SeekBar) view.findViewById(R.id.seekBarVolume);
        mSeekBarVolume.setOnSeekBarChangeListener(this);
        mSeekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        mSeekBarVolume.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));


        // set up the PlayStop button
        mButtonPlayStop = (Button) view.findViewById(R.id.buttonPlayStop);
        mButtonPlayStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // lets play some stream
                Log.d(TAG, "button.onclick STOP/PLAY");

                //TODO check network connectivity


                Intent serviceIntent = new Intent(getActivity(), MediaPlayerService.class);
                switch (((MainActivity) getActivity()).getmMusicPlayerStatus()){
                    case C.STREAM_STATUS_STOPPED:
                        Log.d(TAG, "Starting music player: " + mSelectedStreamSource);
                        // start the mediaplayer music service
                        serviceIntent.putExtra(C.INTENT_STREAM_SOURCE, mSelectedStreamSource);
                        getActivity().startService(serviceIntent);
                        break;
                    case C.STREAM_STATUS_BUFFERING:
                    case C.STREAM_STATUS_PLAYING:
                        Log.d(TAG, "Stopping music player");
                        getActivity().stopService(serviceIntent);
                        break;
                }


            }
        });

        return view;
    }





    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"onCreate");

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(C.INTENT_STREAM_STATUS_BUFFERING);
        mIntentFilter.addAction(C.INTENT_STREAM_STATUS_PLAYING);
        mIntentFilter.addAction(C.INTENT_STREAM_STATUS_STOPPED);
        mIntentFilter.addAction(C.INTENT_STREAM_VOLUME_CHANGED);

        mBroadcastReceiver = new BroadcastReceiverInFragmentOne();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.v(TAG,"onStop");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(TAG,"onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG,"onResume");

        switch (((MainActivity) getActivity()).getmMusicPlayerStatus()){
            case C.STREAM_STATUS_STOPPED:
                mButtonPlayStop.setText(getText(R.string.player_stopped));
                break;
            case C.STREAM_STATUS_PLAYING:
                mButtonPlayStop.setText(getText(R.string.player_playing));
                break;
            case C.STREAM_STATUS_BUFFERING:
                mButtonPlayStop.setText(getText(R.string.player_buffering));
                break;
        }


        // start listening for local broadcasts
        LocalBroadcastManager
                .getInstance(getContext())
                .registerReceiver(mBroadcastReceiver, mIntentFilter);
    }


    @Override
    public void onPause() {
        super.onPause();
        Log.v(TAG,"onPause");

        // stop listening for local broadcasts
        LocalBroadcastManager
                .getInstance(getContext())
                .unregisterReceiver(mBroadcastReceiver);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG,"onDestroy");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.v(TAG,"onAttach");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v(TAG,"onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.v(TAG,"onDetach");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        Log.d(TAG, "SeekBar.onProgressChanged: " + progress + " fromUser: " + fromUser);

        if (fromUser){
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


    private class BroadcastReceiverInFragmentOne extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case C.INTENT_STREAM_STATUS_BUFFERING:
                    mButtonPlayStop.setText("BUFFERING...");
                    break;
                case C.INTENT_STREAM_STATUS_PLAYING:
                    mButtonPlayStop.setText("STOP");
                    break;
                case C.INTENT_STREAM_STATUS_STOPPED:
                    mButtonPlayStop.setText("PLAY");
                    break;
                case C.INTENT_STREAM_VOLUME_CHANGED:
                    Log.d(TAG, "Stream volume: " + audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

                    mSeekBarVolume.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

                    break;
            }
        }
    }


}
