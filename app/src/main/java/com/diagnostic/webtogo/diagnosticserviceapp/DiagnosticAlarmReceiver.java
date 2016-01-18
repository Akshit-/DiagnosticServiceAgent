package com.diagnostic.webtogo.diagnosticserviceapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class DiagnosticAlarmReceiver extends WakefulBroadcastReceiver {

    public static final int REQUEST_CODE = 123456;

    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;

    private long pollInterval;

    // Triggered by the Alarm periodically (starts the service to run task)
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("DService", "DiagnosticAlarmReceiver onReceive()");
        Intent i = new Intent(context, DiagnosticService.class);

        // Start the service
        startWakefulService(context, i);

    }

    public void setAlarm(Context context) {

        Toast.makeText(context, "Starting Monitoring", Toast.LENGTH_SHORT).show();

        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(context, DiagnosticAlarmReceiver.class);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(context);
        pollInterval = Integer.valueOf(SP.getString(DiagnosticPreferenceActivity.KEY_PREF_CPU_POLLING_Interval, "1"));

        // Create a PendingIntent to be triggered when the alarm goes off
        alarmIntent = PendingIntent.getBroadcast(context, 0,
                intent, 0);

        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
                TimeUnit.MINUTES.toMillis(pollInterval), alarmIntent);

        // Enable {@code SampleBootReceiver} to automatically restart the alarm when the
        // device is rebooted.
        ComponentName receiver = new ComponentName(context, DiagnosticBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr != null) {
            alarmMgr.cancel(alarmIntent);
            Log.d("DService", "onClick Alarm Cancelled");
        } else {
            Intent intent = new Intent(context, DiagnosticAlarmReceiver.class);
            final PendingIntent pIntent = PendingIntent.getBroadcast(context, 0,
                    intent, 0);

            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarm.cancel(pIntent);
            Log.d("DService", "onClick Alarm Cancelled");
        }

        // Disable {@code SampleBootReceiver} so that it doesn't automatically restart the
        // alarm when the device is rebooted.
        ComponentName receiver = new ComponentName(context, DiagnosticBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void restartAlarm(Context context) {
        cancelAlarm(context);
        setAlarm(context);
    }


}