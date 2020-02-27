package com.yash.networkspeed;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.util.List;


public class Dashboard extends AppCompatActivity {
    public static final String CHANNEL_ID="ch1";
    boolean mStopHandler = false;
    NotificationCompat.Builder notification;
    Handler mHandler;
    double t_old_data,r_old_data,old_r_data=0.0;
    RemoteViews mycontentView;
    StartNotificationService startNotificationService;
    boolean isBound=false;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        mHandler=new Handler();


    }



    public void showNotification(View v){
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        int UID = 0;
        //loop through the list of installed packages and see if the selected
        //app is in the list
        for (ApplicationInfo packageInfo : packages)
        {
            if(packageInfo.packageName.equals("com.android.chrome"))
            {
                //get the UID for the selected app
                UID = packageInfo.uid;
            }
            //Do whatever with the UID
            //Log.i("Check UID", "UID is: " + UID);
            UID = packageInfo.uid;
            long rx = TrafficStats.getUidRxPackets(UID);
            long tx = TrafficStats.getUidTxPackets(UID);
            //Log.v(UserProfile.class.getName(), "Rx : "+rx+" Tx : "+tx);

            Log.v("TAG", packageInfo.packageName+": Rec. Bytes : "+rx+" B");
            Log.v("TAG", packageInfo.packageName+": Sent Bytes : "+tx+" B");
        }



    }

    public void startCountingTotalFromNow(View view){
        startNotificationService.startCountingTotalFromNow();
    }
    public void cancelNotification(View v){
        startNotificationService.insertDataUsage();

    }

    //on activity starts
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("msg","starting service");
        Intent intent=new Intent(this,StartNotificationService.class);
        startService(intent);
        bindService(intent,NotificationServiceConnection,BIND_AUTO_CREATE);
    }




    //Getting Connection Object
    private ServiceConnection NotificationServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            StartNotificationService.MyBinder binder=(StartNotificationService.MyBinder)service;
            startNotificationService=binder.getService();
            isBound=true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound=false;
            startNotificationService=null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_exit:
                if(isBound){
                    startNotificationService.stopNotification();
                }
                break;

                default:
        }


        return super.onOptionsItemSelected(item);
    }


}



