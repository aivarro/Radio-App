package com.akaver.tabbedradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static java.util.Arrays.asList;

public class MainActivity extends AppCompatActivity implements FragmentThree.RadioStationNamesTransferer {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private int mMusicPlayerStatus = C.STREAM_STATUS_STOPPED;

    private IntentFilter mIntentFilter;
    private BroadcastReceiver mBroadcastReceiver;

    private HashMap<String, HashMap<String, String>> radiostations = new HashMap<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "OnCreate");
        setContentView(R.layout.activity_main);

        // Application wide broadcast receiver
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(C.INTENT_STREAM_STATUS_BUFFERING);
        mIntentFilter.addAction(C.INTENT_STREAM_STATUS_PLAYING);
        mIntentFilter.addAction(C.INTENT_STREAM_STATUS_STOPPED);
        mBroadcastReceiver = new BroadcastReceiverInMainActivity();



        // initialize ui components
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(mViewPager);

        mTabLayout = (TabLayout) findViewById(R.id.tablayout);
        mTabLayout.setupWithViewPager(mViewPager);

    }

    public int getmMusicPlayerStatus(){
        return mMusicPlayerStatus;
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();

        try {
            getStationNames();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // start listening for local broadcasts
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(mBroadcastReceiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        // stop listening for local broadcasts
        LocalBroadcastManager
                .getInstance(this)
                .unregisterReceiver(mBroadcastReceiver);

    }

    @Override
    public HashMap getStationNames() throws JSONException {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Set<String> keys = sharedPref.getStringSet(getString(R.string.saved_station_keys), null);

        if (keys == null) {
            setDefaultStations();
        } else {
            for (String stationKey : keys) {
                JSONArray jsonArray = new JSONArray(sharedPref.getString(stationKey, "[]"));
                if (jsonArray.length() > 0){
//                    Object[] stationValuesArray = stationValues.toArray();
                    HashMap<String, String> station = new HashMap<>();
                    station.put("name", jsonArray.get(0).toString());
                    station.put("url", jsonArray.get(1).toString());
                    radiostations.put(stationKey, station);
                }
            }
        }

        return radiostations;
    }

    @Override
    public void setStationNames(HashMap names) {
        try{
            radiostations = names;

            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putStringSet(getString(R.string.saved_station_keys), radiostations.keySet());
            for (String stationKey : radiostations.keySet()) {
//                Set<String> set = new LinkedHashSet<String>(radiostations.get(stationKey).values());
                JSONArray jsonArray = new JSONArray(radiostations.get(stationKey).values());
                editor.putString(stationKey, jsonArray.toString());
            }
            editor.commit();

        }catch (Exception e) {
        }
    }

    private class BroadcastReceiverInMainActivity extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "BroadcastReceiverInMainActivity.onReceive: " + intent.getAction());
            switch (intent.getAction()){
                case C.INTENT_STREAM_STATUS_BUFFERING:
                    mMusicPlayerStatus = C.STREAM_STATUS_BUFFERING;
                    break;
                case C.INTENT_STREAM_STATUS_PLAYING:
                    mMusicPlayerStatus = C.STREAM_STATUS_PLAYING;
                    break;
                case C.INTENT_STREAM_STATUS_STOPPED:
                    mMusicPlayerStatus = C.STREAM_STATUS_STOPPED;
                    break;
            }
        }
    }

    public void setDefaultStations() {

        HashMap<String, String> stationMap = new HashMap<>();
        stationMap.put("name","skyplus");
        stationMap.put("url", "http://skyplus.m3u8");

        HashMap<String, String> stationMap2 = new HashMap<>();
        stationMap2.put("name","some random radio");
        stationMap2.put("url", "http://random.m3u8");

        radiostations.clear();
        radiostations.put("skyplus", stationMap);
        radiostations.put("some random radio", stationMap2);
    }





    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFragment(new FragmentOneMusic(), "Music");
        adapter.addFragment(new FragmentTwo(), "Two");
        adapter.addFragment(new FragmentThree(), "Three");


        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPageChangeListener(adapter));
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mTitleList = new ArrayList<>();


        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mTitleList.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitleList.get(position);
        }
    }

    class ViewPageChangeListener extends ViewPager.SimpleOnPageChangeListener{
        ViewPagerAdapter mAdapter;
        public ViewPageChangeListener(ViewPagerAdapter adapter){
            mAdapter = adapter;
        }

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "Tab changed: " + mAdapter.getPageTitle(position));
        }
    }
}
