package com.diagnostic.webtogo.diagnosticserviceapp;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.List;

public class DiagnosticActivity extends AppCompatActivity {

    private Context context;

    DiagnosticAlarmReceiver diagnosticAlarmReceiver = new DiagnosticAlarmReceiver();

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this.getApplicationContext();

        //runningservices();
    }


    //testing code
    public void runningservices(){
        ActivityManager activityManager = (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.AppTask>  l = activityManager.getAppTasks();

        Log.d("DService", "runningservices:");

        for(ActivityManager.AppTask r : l){

//            Log.d("DService", r.processName+" : "+r.importance +" : "+ r.importanceReasonComponent);
//            Log.d("DService", r.+" : "+r.baseActivity +" : "+ r.id);

//            Log.d("DService", r.process +" : "+ r.foreground +" : "+ r.uid + r.);

            Log.d("DService", r.toString());


        }

        PackageManager pm = this.getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(0);

        //for(PackageInfo pi : list) {
        ApplicationInfo ai = null;
        try {
            ai = pm.getApplicationInfo("com.zomut.watchdoglite", 0);
            System.out.println(">>>>>>packages is<<<<<<<<" + ai.processName);

            if ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                System.out.println(">>>>>>packages is system package");
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        //}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            Intent i = new Intent(this, DiagnosticPreferenceActivity.class);
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
