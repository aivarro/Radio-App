package com.akaver.tabbedradio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPager mViewPager;

    private int mMusicPlayerStatus = C.STREAM_STATUS_STOPPED;

    private IntentFilter mIntentFilter;
    private BroadcastReceiver mBroadcastReceiver;


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
