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
import android.widget.Toast;

import com.example.dscs.MainActivity;
import com.example.dscs.R;

/**
 * A place for various screen interactions: notifications, toasts, animations.
 */
public class UiUtils {

    // Notifications________________________________________________________________________________

    private static int NOTIFICATION_ID_CLEARING_TABLES = 1;
    @SuppressWarnings("FieldCanBeLocal")
    private static int NOTIFICATION_ID_ALL_TASKS_DONE = 2;

    /**
     * Informs user via notification that all tables have been cleared.
     *
     * @param context   Context for the UI operation.
     * @param className Table.
     */
    public static void showClearingTablesNotification(Context context, String className) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_launcher)
                .setContentTitle(context.getString(R.string.notification_clearing_tables))
                .setContentText(context.getString(R.string.notification_deleting_items_from_table, className));
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_CLEARING_TABLES, builder.build());
    }

    /**
     * Hides the clearing tables notification.
     *
     * @param context Context for the UI operation.
     */
    public static void hideClearingTablesNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID_CLEARING_TABLES);
    }

    /**
     * Informs user that the job has finished processing.
     *
     * @param context Context for the UI operation.
     * @param jobName Name of a job.
     */
    public static void showJobFinishedNotification(Context context, String jobName) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.icon_launcher)
                .setContentTitle(context.getString(R.string.notification_job_finished))
                .setContentText(context.getString(R.string.notification_all_tasks_done, jobName))
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, 0,
                        new Intent(context, MainActivity.class), 0));
        NotificationManager notificationManager = (NotificationManager) context.
                getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID_ALL_TASKS_DONE, builder.build());
    }

    // Toasts_______________________________________________________________________________________

    /**
     * Informs user via toast that all tables have been cleared.
     *
     * @param context Context for the UI operation.
     */
    public static void showClearedTablesToast(final Context context) {
        showToast(context, context.getString(R.string.toast_all_tables_cleared));
    }

    /**
     * Informs user via toast that the requested table is empty.
     *
     * @param context Context for the UI operation.
     * @param clazz   Table.
     */
    public static void showEmptyTableToast(Context context, Class clazz) {
        showToast(context, context.getString(R.string.toast_empty_table, clazz.getSimpleName()));
    }

    /**
     * Informs user via toast that all tasks have finished processing.
     *
     * @param context Context for the UI operation.
     */
    public static void showAllTasksDoneToast(Context context) {
        showToast(context, context.getString(R.string.toast_all_tasks_done));
    }

    /**
     * Informs user via toast that there is no internet.
     *
     * @param context Context for the UI operation.
     */
    public static void showNoConnectionToast(Context context) {
        showToast(context, context.getString(R.string.toast_no_internet));
    }

    /**
     * Informs user via toast about the event using a toast.
     *
     * @param context Context for the UI operation.
     * @param message Toast message.
     */
    private static void showToast(final Context context, final String message) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Animations___________________________________________________________________________________

    /**
     * Forms an animation that is rotating 360 degrees.
     *
     * @return Animation.
     */
    public static Animation getRotatingAnimation() {
        return getCircularAnimation(360.f, 2000, -1);
    }

    /**
     * Forms an animation that is rotating back and forth for a small angle.
     *
     * @return Animation.
     */
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

    /**
     * Forms custom rotate animation.
     *
     * @param end         End angle.
     * @param duration    Duration of the animation.
     * @param repeatCount How much time the animation is repeated.
     * @return Animation.
     */
    private static RotateAnimation getCircularAnimation(float end, int duration, int repeatCount) {
        RotateAnimation animation = new RotateAnimation(0.0f, end,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(duration);
        animation.setRepeatCount(repeatCount);

        return animation;
    }
}
