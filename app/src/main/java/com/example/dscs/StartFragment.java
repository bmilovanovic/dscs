package com.example.dscs;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * Basic fragment for starting/stopping crawling service.
 */
public class StartFragment extends Fragment implements View.OnClickListener,
        CrawlingService.OnJobFinishedListener {

    private static final String TAG = StartFragment.class.getSimpleName();

    private TextView mStartButton;
    private CrawlingService.CrawlingServiceBinder mBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service is connected.");
            mBinder = (CrawlingService.CrawlingServiceBinder) service;
            mBinder.setOnJobFinishedListener(StartFragment.this);
            refreshButtonState(false);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service is disconnected.");
            mBinder = null;
            refreshButtonState(false);
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        mStartButton = (TextView) view.findViewById(R.id.start_button);
        mStartButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        startAndBindService();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshButtonState(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isCrawlingServiceRunning() && mServiceConnection != null) {
            getActivity().unbindService(mServiceConnection);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                mStartButton.setTextColor(Color.DKGRAY);
                mStartButton.setEnabled(false);
                if (isCrawlingServiceRunning()) {
                    getActivity().unbindService(mServiceConnection);
                    getActivity().stopService(getServiceIntent());
                    mServiceConnection.onServiceDisconnected(getActivity().getComponentName());
                } else {
                    startAndBindService();
                }
        }
    }

    @Override
    public void onJobFinished() {
        Log.e(TAG, "onJobFinished: " + (mBinder));
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                refreshButtonState(true);
            }
        });
    }

    private boolean startAndBindService() {
        if (!isCrawlingServiceRunning()) {
            getActivity().startService(getServiceIntent());
        }
        return getActivity().bindService(getServiceIntent(),
                mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private boolean isCrawlingServiceRunning() {
        final ActivityManager activityManager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo serviceInfo : activityManager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceInfo.service.getClassName().equals(CrawlingService.class.getName())) {
                return true;
            }
        }
        return false;
    }

    private void refreshButtonState(boolean animateChange) {
        final int color;
        final Animation animation;
        if (isCrawlingServiceRunning()) {
            if (mBinder != null && mBinder.isFinished()) {
                color = Color.BLUE;
                animation = null;
                mStartButton.setText(getString(R.string.if_sentiment_very_satisfied));
            } else {
                mStartButton.setText(getString(R.string.if_pause_circle_outline));
                color = Color.RED;
                animation = UiUtils.getRotatingAnimation();
            }
        } else {
            mStartButton.setText(getString(R.string.if_play_circle_outline));
            color = Color.GREEN;
            animation = UiUtils.getTiltingAnimation();
        }
        if (animateChange) {
            mStartButton.startAnimation(UiUtils.getChangingAnimation());
        }
        mStartButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                mStartButton.setEnabled(true);
                mStartButton.setTextColor(color);
                if (animation != null) {
                    mStartButton.startAnimation(animation);
                } else {
                    mStartButton.setAnimation(null);
                }
            }
        }, 3000);
    }

    private Intent getServiceIntent() {
        return new Intent(getActivity(), CrawlingService.class);
    }
}
