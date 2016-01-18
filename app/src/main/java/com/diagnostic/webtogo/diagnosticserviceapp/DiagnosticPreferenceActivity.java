package com.diagnostic.webtogo.diagnosticserviceapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by akshit on 10/9/15.
 */
public class DiagnosticPreferenceActivity extends PreferenceActivity {

    public static final String KEY_PREF_CPU_MONITORING = "pref_cpuMonitoring";
    public static final String KEY_PREF_CPU_THRESHOLD = "pref_cpuThreshold";
    public static final String KEY_PREF_CPU_POLLING_Interval = "pref_cpuPollingInterval";

    static DiagnosticAlarmReceiver diagnosticAlarmReceiver = new DiagnosticAlarmReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new DiagnosticPreferenceFragment()).commit();

    }

    public static class DiagnosticPreferenceFragment extends PreferenceFragment implements
            SharedPreferences.OnSharedPreferenceChangeListener {

        static Context context;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.diagnostic_preferences);

            context = getActivity().getApplicationContext();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                              String key) {

            if (key.equals(KEY_PREF_CPU_MONITORING)) {
                //Preference cpuMonitoring = findPreference(key);
                // Set summary to be the user-description for the selected value
                //cpuMonitoring.setSummary(sharedPreferences.getString(key, ""));
                boolean cpuMonitoring = sharedPreferences.getBoolean(KEY_PREF_CPU_MONITORING, false);
                Log.d("DService", "onSharedPreferenceChanged(cpu check) + cpuMonitoring:"+cpuMonitoring);

                if (cpuMonitoring)
                    diagnosticAlarmReceiver.setAlarm(context);
                else
                    diagnosticAlarmReceiver.cancelAlarm(context);

            }

            if (key.equals(KEY_PREF_CPU_THRESHOLD)
                    || key.equals(KEY_PREF_CPU_POLLING_Interval)) {

                Preference pref = findPreference(key);
                // Set summary to be the user-description for the selected value
                pref.setSummary(pref.getSummary());

                boolean cpuMonitoring = sharedPreferences.getBoolean(KEY_PREF_CPU_MONITORING, false);
                Log.d("DService", "onSharedPreferenceChanged(threshold & interval) + cpuMonitoring:" + cpuMonitoring);

                if (cpuMonitoring)
                    diagnosticAlarmReceiver.restartAlarm(context);

            }
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

    }


}
