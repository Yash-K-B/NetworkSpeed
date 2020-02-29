package com.yash.networkspeed;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.TrafficStats;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.ArrayList;
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
    TextView uploadData,downloadData,disp_date;
    SharedPreferences preferences;
    DecimalFormat df;
    RecyclerView history;
    List<UsageHistoryItem> items=null;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        mHandler=new Handler();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        updateUI();


    }

    void updateUI(){
        DateManipulate dateManipulate=new DateManipulate();
        uploadData=findViewById(R.id.upload_data);
        downloadData=findViewById(R.id.download_data);
        disp_date=findViewById(R.id.today_date);
        preferences=this.getSharedPreferences("Data",MODE_PRIVATE);
        double download_data=preferences.getFloat("todays_usage_mob",0.0f);
        double upload_data=preferences.getFloat("todays_usage_wifi",0.0f);
        DataItem dataItem=new DataItem(upload_data);
        df=new DecimalFormat("0.00");
        dataItem.convertToHighestSuffix();
        String s=" "+df.format(dataItem.getData())+" "+dataItem.getType();
        uploadData.setText(s);
        dataItem=new DataItem(download_data);
        dataItem.convertToHighestSuffix();
        s=df.format(dataItem.getData())+" "+dataItem.getType();
        downloadData.setText(s);
        disp_date.setText(dateManipulate.getDisplayDate(0));
//        disp=findViewById(R.id.display);
//        String ss="";
//        ss+="D05022020_wifi :"+df.format(new DataItem(preferences.getFloat("D05022020_wifi",0.0f)).convert().getData())+new DataItem(preferences.getFloat("D05022020_wifi",0.0f)).convert().getType();
//        ss+="\nD06022020_wifi: "+df.format(new DataItem(preferences.getFloat("D06022020_wifi",0.0f)).convert().getData())+new DataItem(preferences.getFloat("D06022020_wifi",0.0f)).convert().getType();
//        ss+="\nD07022020_wifi: "+df.format(new DataItem(preferences.getFloat("D07022020_wifi",0.0f)).convert().getData())+new DataItem(preferences.getFloat("D07022020_wifi",0.0f)).convert().getType();
//        disp.setText(ss);
        items=new ArrayList<UsageHistoryItem>();
        for(int i=1;i<=20;i++){
            String mob_date,wifi_date;
            mob_date=dateManipulate.getDate(i)+"_mob";
            wifi_date=dateManipulate.getDate(i)+"_wifi";
            DataItem d=new DataItem(preferences.getFloat(mob_date,0.0f));
            String mobile="Mobile:"+df.format(d.convert().getData())+d.getType();
            d=new DataItem(preferences.getFloat(wifi_date,0.0f));
            String wifi="WiFi:"+df.format(d.convert().getData())+d.getType();
            UsageHistoryItem item=new UsageHistoryItem(dateManipulate.getDisplayDate(i),mobile,wifi);
            items.add(item);
        }
        Log.d("msg1","List size at dashboard:"+items.size());
        history=findViewById(R.id.history_screen);
        HistoryItemAdapter adapter=new HistoryItemAdapter(items);
        history.setLayoutManager(new LinearLayoutManager(this));
        history.setAdapter(adapter);
        history.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
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
        items=new ArrayList<UsageHistoryItem>();
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
        updateUI();
        Log.d("msg","onresume----------------------------------------------->");
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
                this.finish();
                break;

                default:
        }


        return super.onOptionsItemSelected(item);
    }


}



