package com.example.dscs;

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

    private static final long DISABLE_BUTTON_DURATION = 3000;

    private TextView mStartButton;
    private TextView mJobInfoTextView;
    private TextView mTaskInfoTextView;
    private CrawlingService.CrawlingServiceBinder mBinder;
    private boolean mIsServiceFinished;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "Service is connected.");
            mBinder = (CrawlingService.CrawlingServiceBinder) service;
            mBinder.mService.setOnJobFinishedListener(StartFragment.this);
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
        if (!CrawlingService.isRunning(getActivity())) {
            getActivity().startService(getServiceIntent());
            mIsServiceFinished = false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinder == null && CrawlingService.isRunning(getActivity())) {
            getActivity().bindService(getServiceIntent(), mServiceConnection,
                    Context.BIND_AUTO_CREATE);
        } else {
            refreshButtonState();
        }
    }

    @Override
    public void onPause() {
        if (mBinder != null && CrawlingService.isRunning(getActivity())) {
            getActivity().unbindService(mServiceConnection);
            mBinder = null;
        }
        super.onPause();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                mStartButton.setTextColor(Color.DKGRAY);
                mStartButton.setEnabled(false);
                if (CrawlingService.isRunning(getActivity())) {
                    if (mBinder != null) {
                        getActivity().unbindService(mServiceConnection);
                        mBinder = null;
                    }
                    getActivity().stopService(getServiceIntent());
                    mServiceConnection.onServiceDisconnected(getActivity().getComponentName());
                } else {
                    getActivity().startService(getServiceIntent());
                    getActivity().bindService(getServiceIntent(), mServiceConnection,
                            Context.BIND_AUTO_CREATE);
                    mIsServiceFinished = false;
                }
        }
    }

    @Override
    public void onJobFinished() {
        Log.i(TAG, "onJobFinished: " + mBinder);
        mIsServiceFinished = true;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                refreshButtonState();
            }
        });
    }

    @Override
    public void dispatchTaskStatusChange(final String statusMessage) {
        mTaskInfoTextView.setText(statusMessage);
    }

    /**
     * Depending on a job and service state, set the button icon, color and the animation.
     */
    private void refreshButtonState() {
        final int colorId;
        final Animation animation;
        if (CrawlingService.isRunning(getActivity())) {
            if (mBinder != null && mIsServiceFinished) {
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

        if (mStartButton.getCurrentTextColor() != Color.DKGRAY) {
            mStartButton.setTextColor(ContextCompat.getColor(getActivity(), colorId));
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
        }, DISABLE_BUTTON_DURATION);
    }

    private Intent getServiceIntent() {
        return new Intent(getActivity(), CrawlingService.class);
    }
}
