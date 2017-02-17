package com.example.dscs;

import android.app.ActivityManager;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.example.dscs.game.GameActivity;
import com.example.dscs.utility.UiUtils;

/**
 * Basic fragment for starting/stopping crawling service.
 */
public class StartFragment extends Fragment implements View.OnClickListener,
        CrawlingService.JobListener {

    private static final String TAG = StartFragment.class.getSimpleName();

    private TextView mStartButton;
    private TextView mJobInfoTextView;
    private TextView mTaskInfoTextView;
    private CrawlingService.CrawlingServiceBinder mBinder;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service is connected.");
            mBinder = (CrawlingService.CrawlingServiceBinder) service;
            mBinder.setOnJobFinishedListener(StartFragment.this);
            refreshButtonState();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "Service is disconnected.");
            mBinder = null;
            refreshButtonState();
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
        mStartButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                startActivity(intent);
                return true;
            }
        });

        mJobInfoTextView = (TextView) view.findViewById(R.id.job_info_text_view);
        mTaskInfoTextView = (TextView) view.findViewById(R.id.task_info_text_view);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setupWiFiReceiver();
        startAndBindService();
    }

    /**
     * When the enabled WiFi event is received, start a service if it's not started.
     */
    private void setupWiFiReceiver() {
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!isCrawlingServiceRunning() && intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_DISABLED) == WifiManager.WIFI_STATE_ENABLED) {
                    mStartButton.setTextColor(Color.DKGRAY);
                    mStartButton.setEnabled(false);
                    if (!isCrawlingServiceRunning()) {
                        startAndBindService();
                    }
                }
            }
        };

        final IntentFilter filters = new IntentFilter();
        filters.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        filters.addAction("android.net.wifi.STATE_CHANGE");
        getActivity().registerReceiver(receiver, filters);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshButtonState();
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
        Log.i(TAG, "onJobFinished: " + mBinder);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                refreshButtonState();
            }
        });
    }

    @Override
    public void dispatchTaskStatusChange(final String statusMessage) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mTaskInfoTextView.setText(statusMessage);
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

    private void refreshButtonState() {
        final int colorId;
        final Animation animation;
        if (isCrawlingServiceRunning()) {
            if (mBinder != null && mBinder.isFinished()) {
                colorId = android.R.color.holo_green_dark;
                animation = null;
                mStartButton.setText(getString(R.string.if_sentiment_very_satisfied));
                mJobInfoTextView.setText(getString(R.string.notification_job_finished));
                mTaskInfoTextView.setText("");
            } else {
                mStartButton.setText(getString(R.string.if_pause_circle_outline));
                mJobInfoTextView.setText(getString(R.string.info_job_in_progress));
                colorId = android.R.color.holo_red_light;
                animation = UiUtils.getRotatingAnimation();
            }
        } else {
            mStartButton.setText(getString(R.string.if_play_circle_outline));
            mJobInfoTextView.setText(getString(R.string.info_click_to_start_crawling));
            mTaskInfoTextView.setText("");
            colorId = android.R.color.holo_blue_light;
            animation = UiUtils.getTiltingAnimation();
        }

        if (animation != null) {
            mStartButton.startAnimation(animation);
        } else {
            mStartButton.setAnimation(null);
        }

        mStartButton.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getActivity() != null) {
                    mStartButton.setTextColor(ContextCompat.getColor(getActivity(), colorId));
                    mStartButton.setEnabled(true);
                }
            }
        }, 3000);
    }

    private Intent getServiceIntent() {
        return new Intent(getActivity(), CrawlingService.class);
    }
}
