package com.example.dscs.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.dscs.R;
import com.example.dscs.job.Job;
import com.example.dscs.job.MovieJob;

/**
 * Getting values from shared preferences is done here.
 */
public class PreferenceUtility {

    private static Job sApplicationJob;

    /**
     * Gets current job application is working on.
     *
     * @return Job.
     */
    public static Job getCurrentJob() {
        if (sApplicationJob == null) {
            sApplicationJob = new MovieJob();
        }
        return sApplicationJob;
    }

    /**
     * Decides whether to skip tasks initialization.
     *
     * @param context Context for getting shared preferences.
     * @return Whether this device should start working on a job with tasks initialization.
     */
    public static boolean shouldInitTasks(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(R.string.pref_title_init_tasks), true);
    }

    /**
     * Gets total number of tasks the device should process.
     *
     * @param context Context for getting shared preferences.
     * @return Tasks number.
     */
    public static int getNumberOfTasks(Context context) {
        return getNumberPref(context, R.string.pref_title_number_of_tasks,
                R.string.pref_default_number_of_tasks);
    }

    /**
     * Gets how much time should process wait before moving on to a next task.
     *
     * @param context Context for getting shared preferences.
     * @return Delay between subsequent task processions.
     */
    public static int getCrawlingDelay(Context context) {
        return getNumberPref(context, R.string.pref_title_crawling_delay,
                R.string.pref_default_crawling_delay);
    }

    /**
     * Gets a value of integer shared preference.
     *
     * @param context        Context for getting shared preferences.
     * @param prefKeyId      String ID of the preference key.
     * @param defaultValueId String ID of the default prefernce value.
     * @return Integer value of preference.
     */
    private static int getNumberPref(Context context, int prefKeyId, int defaultValueId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String stringVal = preferences.getString(
                context.getString(prefKeyId), context.getString(defaultValueId));
        return Integer.valueOf(stringVal);
    }
}
