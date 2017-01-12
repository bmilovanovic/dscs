package com.example.dscs;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.example.aninterface.Storable;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;
import com.microsoft.windowsazure.mobileservices.table.query.Query;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOperations;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutionException;

import static com.microsoft.windowsazure.mobileservices.table.query.QueryOperations.val;

/**
 * Generator of all the work in the app, this service is used for crawling the web.
 */
public class CrawlingService extends Service {
    private static final String TAG = CrawlingService.class.getSimpleName();
    private Job mCurrentJob;

    private final CrawlingServiceBinder mBinder = new CrawlingServiceBinder();

    private MobileServiceTable<Task> mTaskTable;

    private Thread mWorkingThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (mCurrentJob == null) {
                SystemClock.sleep(1000);
            }
            mBinder.mIsFinished = false;
            Log.d(TAG, "Working thread just started working on a " +
                    mCurrentJob.getClass().getSimpleName());
            mCurrentJob.init(getApplicationContext());

            mTaskTable = Network.getTable(getApplicationContext(), Task.class);

            int delay = PreferenceUtility.getCrawlingDelay(getApplicationContext());
            while (processNextTask()) {
                SystemClock.sleep(delay);
            }

            if (mBinder.mJobListener.get() != null) {
                UiUtils.showAllTasksDoneToast(getApplicationContext());
                mBinder.mIsFinished = true;
                mBinder.mJobListener.get().onJobFinished();
            } else {
                UiUtils.showJobFinishedNotification(getBaseContext(),
                        mCurrentJob.getClass().getSimpleName());
            }

            logLife();
        }
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: ");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "onUnbind: ");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        if (mWorkingThread != null) {
            Log.d(TAG, "Working thread is interrupted!");
            mWorkingThread.interrupt();
            mWorkingThread = null;
        }
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mCurrentJob = PreferenceUtility.getCurrentJob(getBaseContext());
        mWorkingThread.start();
    }

    private void logLife() {
        long howLong = 0;
        while (!Thread.currentThread().isInterrupted()) {
            Log.d(TAG, "run: " + (howLong++) + "0s");
            SystemClock.sleep(10000);
        }
    }

    private boolean processNextTask() {
        try {
            // If there are no more submitted tasks, go for those stuck in progress
            return processNextTaskAmong(Task.SUBMITTED) || processNextTaskAmong(Task.IN_PROGRESS);
        } catch (InterruptedException | ExecutionException e) {
            Log.e(TAG, "Could not process task. " + e);
            e.printStackTrace();
        }
        return false;
    }

    private boolean processNextTaskAmong(int taskStatus) throws ExecutionException, InterruptedException {
        Query query = QueryOperations.field("status").eq(val(taskStatus));
        MobileServiceList<Task> submittedTasksList = mTaskTable.where(query).execute().get();
        Log.d(TAG, "Unprocessed tasks: " + submittedTasksList.size());
        if (submittedTasksList.size() > 0) {
            Task nextTask = submittedTasksList.get(0);
            updateTaskStatus(nextTask, Task.IN_PROGRESS);
            Storable parsedItem = mCurrentJob.parseTask(getApplicationContext(), nextTask.getKey());
            if (parsedItem != null) {
                parsedItem.store();
                updateTaskStatus(nextTask, Task.DONE);
            } else {
                updateTaskStatus(nextTask, Task.INVALID);
            }
            return true;
        }
        return false;
    }

    private void updateTaskStatus(Task task, int status) throws ExecutionException, InterruptedException {
        task.setStatus(status);
        mTaskTable.update(task).get();
    }

    public class CrawlingServiceBinder extends Binder {
        WeakReference<OnJobFinishedListener> mJobListener = new WeakReference<>(null);
        private boolean mIsFinished = false;

        void setOnJobFinishedListener(StartFragment onJobFinishedListener) {
            mJobListener = new WeakReference<OnJobFinishedListener>(onJobFinishedListener);
        }

        boolean isFinished() {
            return mIsFinished;
        }
    }

    public interface OnJobFinishedListener {
        void onJobFinished();
    }
}