package com.diagnostic.webtogo.diagnosticserviceapp;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by akshit on 9/28/15.
 */
public class DiagnosticService extends IntentService {

    public String defaultCPUThreshold = "1";
    public String defaultPollingInterval = "1";

    public int cpuThreshold;
    protected long totalCpuUsage;

    public static final String TAG = "DiagnosticService";
    // An ID used to post the notification.
    public static final int NOTIFICATION_ID = 1;

    private NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;

    List<DiagnosticProcessModel> processModelList;

    public DiagnosticService() {

        super("diagnostic-service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        cpuThreshold = Integer.valueOf(SP.getString(DiagnosticPreferenceActivity.KEY_PREF_CPU_THRESHOLD, defaultCPUThreshold));
        int cpuPollingInterval = Integer.valueOf(SP.getString(DiagnosticPreferenceActivity.KEY_PREF_CPU_POLLING_Interval, defaultCPUThreshold));

        Log.d("DService", "onHandleIntent() + cpuThreshold:" + cpuThreshold);
        Log.d("DService", "onHandleIntent() + cpuPollingInterval:" + cpuPollingInterval);

        totalCpuUsage = getBackgroundAppsCPUStats();

        Log.d("DService", "onHandleIntent() + cpuUsage:" + totalCpuUsage);

        if (totalCpuUsage >= cpuThreshold) {

            Log.d("DService", "onHandleIntent() + sending Notification Alert!");

            sendNotification();

        }

        DiagnosticAlarmReceiver.completeWakefulIntent(intent);

    }

    private void sendNotification() {
        mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("DiagnosticService")
                        .setContentText("CPU Usage warning!");

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // notifyID allows you to update the notification later on.
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }


    //adb shell top -n 1 | awk '{if (match($9, "u0*") != 0 && $8 == "bg") print}'
    // adb shell top -n 1 | awk 'BEGIN{sum=0} {gsub("%","",$3); if($8 =="bg") sum += $3 } END{ print sum}'

    public long getBackgroundAppsCPUStats() {

        processModelList = new ArrayList<>();

        java.lang.Process p = null;
        BufferedReader in = null;

        long total = 0;
        try {
            p = Runtime.getRuntime().exec("top -n 1");//| awk '{if (match($9, \"u0*\") != 0 && $8 == \"bg\") print}'");
            in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            //Log.d("DService", "start cpu monitoring");

            String line;
            while ((line = in.readLine()) != null) {
                if (line.contains(" bg ")) {   // No need for grep
                    line = line.trim(); //important to remove whitespaces using \\s+
                    //Log.d("DS", "line:"+line);

                    String lineTokens[] = line.split("\\s+");
                    int pid = Integer.valueOf(lineTokens[0].trim());
                    long cpu = Integer.valueOf(lineTokens[2].trim().replace("%", " ").trim());
                    total += cpu;

                    String uid = lineTokens[8].trim();
                    String processName = lineTokens[9].trim();
                    processModelList.add(new DiagnosticProcessModel(pid,uid,cpu,processName));

                }
            }
            p.waitFor();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //Log.d("DService","return:"+total);


        return total;
    }

    public void killProcessByPID(int pid){
        android.os.Process.killProcess(pid);
    }

    public void getActivityFromPID(int pid){
//        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> pidsTask = activityManager.getRunningAppProcesses();
//
//        for(int i = 0; i < pidsTask.size(); i++) {
//            if(pid==pidsTask.get(i).pid)
////        }

    }

}
