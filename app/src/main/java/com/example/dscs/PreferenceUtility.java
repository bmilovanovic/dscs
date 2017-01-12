package com.example.dscs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Getting values from shared preferences is done here.
 */
class PreferenceUtility {

    static Job getCurrentJob(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String stringVal = preferences.getString(
                context.getString(R.string.pref_title_jobs), "");
        return new MovieJob();
    }

    static int getNumberOfTasks(Context context) {
        return getNumberPref(context, R.string.pref_title_number_of_tasks,
                R.string.pref_default_number_of_tasks);
    }

    static int getCrawlingDelay(Context context) {
        return getNumberPref(context, R.string.pref_title_crawling_delay,
                R.string.pref_default_crawling_delay);
    }

    private static int getNumberPref(Context context, int prefKeyId, int defaultValueId) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String stringVal = preferences.getString(
                context.getString(prefKeyId), context.getString(defaultValueId));
        return Integer.valueOf(stringVal);
    }

    static boolean shouldInitTasks(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(context.getString(R.string.pref_title_init_tasks), true);
    }
}
