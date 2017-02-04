package com.example.dscs.utility;

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

import com.example.dscs.MainActivity;
import com.example.dscs.R;

/**
 * A place for various screen interaction: toasts, notifications, animations.
 */
public class UiUtils {

    private static int NOTIFICATION_ID_CLEARING_TABLES = 1;
    @SuppressWarnings("FieldCanBeLocal")
    private static int NOTIFICATION_ID_ALL_TASKS_DONE = 2;

    // Toasts
    public static void showClearedTablesToast(final Context context) {
        showToast(context, context.getString(R.string.toast_all_tables_cleared));
    }

    public static void showEmptyTableToast(Context context, Class clazz) {
        showToast(context, context.getString(R.string.toast_empty_table, clazz.getSimpleName()));
    }

    public static void showAllTasksDoneToast(Context context) {
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
    public static void hideClearingTablesNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID_CLEARING_TABLES);
    }

    public static void showClearingTablesNotification(Context context, String className) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(context.getString(R.string.notification_clearing_tables))
                .setContentText(context.getString(R.string.notification_deleting_items_from_table, className));
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_CLEARING_TABLES, builder.build());
    }

    public static void showJobFinishedNotification(Context context, String jobName) {
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
    public static Animation getRotatingAnimation() {
        return getCircularAnimation(360.f, 2000, -1);
    }

    public static Animation getTiltingAnimation() {
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

    private static RotateAnimation getCircularAnimation(float end, int duration, int repeatCount) {
        RotateAnimation animation = new RotateAnimation(0.0f, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setRepeatCount(repeatCount);

        return animation;
    }

}
