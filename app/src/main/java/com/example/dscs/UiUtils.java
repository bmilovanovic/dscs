package com.example.dscs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

/**
 * A place for various screen interaction: toasts, notifications, animations.
 */
class UiUtils {

    private static int NOTIFICATION_ID_CLEARING_TABLES = 1;
    @SuppressWarnings("FieldCanBeLocal")
    private static int NOTIFICATION_ID_ALL_TASKS_DONE = 2;

    // Toasts
    static void showClearedTablesToast(final Context context) {
        showToast(context, context.getString(R.string.toast_all_tables_cleared));
    }

    static void showEmptyTableToast(Context context, Class clazz) {
        showToast(context, context.getString(R.string.toast_empty_table, clazz.getSimpleName()));
    }

    static void showAllTasksDoneToast(Context context) {
        showToast(context, context.getString(R.string.toast_all_tasks_done));
    }

    private static void showToast(final Context context, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Notifications
    static void hideClearingTablesNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID_CLEARING_TABLES);
    }

    static void showClearingTablesNotification(Context context, String className) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_clearing_tables))
                .setContentText(context.getString(R.string.notification_deleting_items_from_table, className));
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_CLEARING_TABLES, builder.build());
    }

    static void showJobFinishedNotification(Context context, String jobName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_job_finished))
                .setContentText(context.getString(R.string.notification_all_tasks_done, jobName))
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        new Intent(context, MainActivity.class), 0));
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_ALL_TASKS_DONE, builder.build());
    }

    // Animations
    static Animation getRotatingAnimation() {
        return getCircularAnimation(360.f, 2000, -1);
    }

    static Animation getTiltingAnimation() {
        Animation animationStart = getCircularAnimation(5.0f, 500, -1);
        Animation animationToLeft = getCircularAnimation(-10.0f, 500, -1);
        Animation animationToRight = getCircularAnimation(10.0f, 500, -1);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(animationStart);
        set.addAnimation(animationToLeft);
        set.addAnimation(animationToRight);
        set.setRepeatCount(-1);

        return set;
    }

    static Animation getChangingAnimation() {
        Animation animationStart = new ScaleAnimation(
                1f, 0.2f, // Start and end values for the X axis scaling
                1f, 0.2f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        animationStart.setFillAfter(true);
        animationStart.setDuration(500);

        Animation animationEnd = new ScaleAnimation(1f, 5f, 1f, 5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animationEnd.setFillAfter(true);
        animationEnd.setDuration(1000);
        animationEnd.setStartOffset(500);

        AnimationSet set = new AnimationSet(true);
        set.addAnimation(animationStart);
        for (int i = 0; i < 8; i++) {
            set.addAnimation(getCircularAnimation(360.0f, 1500, 1));
        }
        set.addAnimation(animationEnd);
        return set;
    }

    private static RotateAnimation getCircularAnimation(float end, int duration, int repeatCount) {
        RotateAnimation animation = new RotateAnimation(0.0f, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setRepeatCount(repeatCount);

        return animation;
    }

}
