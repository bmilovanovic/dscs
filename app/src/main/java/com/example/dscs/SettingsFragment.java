package com.example.dscs;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;

import com.example.dscs.job.Task;
import com.example.dscs.utility.PreferenceUtility;
import com.example.dscs.utility.UiUtils;
import com.microsoft.windowsazure.mobileservices.MobileServiceList;
import com.microsoft.windowsazure.mobileservices.table.MobileServiceTable;

import java.util.concurrent.ExecutionException;

/**
 * Fragment holding various preferences.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final String TAG = SettingsFragment.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        setupChooseJobPref();
        setupClearTablesPref();
        setupNumberOfTasksPref();
        setupCrawlingDelayPref();
    }

    /**
     * Sets up job choosing preference.
     */
    private void setupChooseJobPref() {
        final ListPreference jobPref =
                (ListPreference) findPreference(getString(R.string.pref_title_jobs));
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String summary = getString(R.string.pref_summary_jobs) + " " + newValue;
                jobPref.setSummary(summary);
                return true;
            }
        };
        jobPref.setOnPreferenceChangeListener(listener);
        initSummary(jobPref, listener);
    }

    /**
     * Sets up clear tables preference.
     */
    private void setupClearTablesPref() {
        final ListPreference clearTablesPref =
                (ListPreference) findPreference(getString(R.string.pref_title_clear_tables));
        clearTablesPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object o) {
                final int newValue = Integer.valueOf((String) o);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        switch (newValue) {
                            case 1:
                                for (Class clazz : PreferenceUtility.getCurrentJob()
                                        .getAllDomainClasses()) {
                                    clearTable(clazz);
                                }
                            case 0:
                                clearTable(Task.class);
                        }
                        UiUtils.hideClearingTablesNotification(getActivity());
                        UiUtils.showClearedTablesToast(getActivity());
                    }
                }).start();
                return true;
            }
        });
    }

    /**
     * Sets up number of tasks preference.
     */
    private void setupNumberOfTasksPref() {
        final EditTextPreference numberOfTasksPref =
                (EditTextPreference) findPreference(getString(R.string.pref_title_number_of_tasks));
        numberOfTasksPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String summary = getString(R.string.pref_summary_number_of_tasks) + " " + newValue;
                numberOfTasksPref.setSummary(summary);
                return true;
            }
        };
        numberOfTasksPref.setOnPreferenceChangeListener(listener);
        initSummary(numberOfTasksPref, listener);
    }

    /**
     * Sets up crawling delay preference.
     */
    private void setupCrawlingDelayPref() {
        final EditTextPreference crawlingDelayPref =
                (EditTextPreference) findPreference(getString(R.string.pref_title_crawling_delay));
        crawlingDelayPref.getEditText().setInputType(InputType.TYPE_CLASS_NUMBER);
        Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String summary = getString(R.string.pref_summary_crawling_delay) + " " + newValue;
                crawlingDelayPref.setSummary(summary);
                return true;
            }
        };
        crawlingDelayPref.setOnPreferenceChangeListener(listener);
        initSummary(crawlingDelayPref, listener);
    }

    /**
     * Deletes the data of a table on Azure.
     *
     * @param clazz Table.
     * @param <E>   Table template.
     */
    private <E> void clearTable(final Class<E> clazz) {
        UiUtils.showClearingTablesNotification(getActivity(), clazz.getSimpleName());
        MobileServiceTable<E> table = Network.getTable(getActivity(), clazz);
        try {
            MobileServiceList<E> list = table.where().execute().get();
            while (!list.isEmpty()) {
                Log.e(TAG, "clearTable: " + list.size());
                for (E task : list) {
                    table.delete(task).get();
                    list = table.where().execute().get();
                }
            }
            Log.d(TAG, "Table cleared: " + clazz.getSimpleName());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets initial summary value.
     *
     * @param preference Preference which summary is set.
     * @param listener   To set the value, listener is called with an empty data.
     */
    private void initSummary(Preference preference, Preference.OnPreferenceChangeListener listener) {
        listener.onPreferenceChange(preference,
                PreferenceManager.getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

}
