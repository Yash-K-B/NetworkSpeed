package com.yash.networkspeed;


import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Dashboard extends AppCompatActivity {
    public static final String CHANNEL_ID="ch1";
    boolean mStopHandler = false;
    Handler mHandler;
    StartNotificationService startNotificationService;
    boolean isBound=false;
    TextView uploadData,downloadData,disp_date;
    SharedPreferences preferences;
    DecimalFormat df;
    RecyclerView history;
    List<UsageHistoryItem> items=null;
    AlertDialog dialog;
    Intent service;
    boolean flag_service_bound=false;
    List<ApplicationInfo> packages;
    Map<String,String> appname;
    boolean dialog_dismiss=true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("msg","onCreate---------------------------------------------->");
        //Creating Service And Binding
        service=new Intent(this,StartNotificationService.class);
        startService(service);
        bindService(service,NotificationServiceConnection,BIND_AUTO_CREATE);
        flag_service_bound=true;
        setContentView(R.layout.dashboard);
        mHandler=new Handler();
        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        updateUI();

    }


    void getAppNameFromPackage(){
        final PackageManager pm = getPackageManager();
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        appname=new HashMap<>();
        for (ApplicationInfo packageInfo : packages)
        {
            appname.put(packageInfo.packageName,packageInfo.loadLabel(pm).toString());
        }

    }

    //Update Activity Views
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




    //Thread
    Thread thread=new Thread(new Runnable() {
        @Override
        public void run() {
            String s="";
            DecimalFormat df=new DecimalFormat("0.0");
            int UID = 0;
            for (ApplicationInfo packageInfo : packages)
            {
                if(packageInfo.packageName.equals("com.kapp.youtube.final"))
                {
                    //get the UID for the selected app
                    UID = packageInfo.uid;
                    long rx = TrafficStats.getUidRxBytes(UID);
                    long tx = TrafficStats.getUidTxBytes(UID);
                    try {
                        DataItem di_rx=new DataItem(rx);
                        DataItem di_tx=new DataItem(tx);
                        s+=appname.get(packageInfo.packageName)+": \nReceived : "+(df.format(di_rx.convert().getData())+di_rx.getType())+"  Sent : "+(df.format(di_tx.convert().getData())+di_tx.getType())+"\n\n";
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //Do whatever with the UID
                //Log.i("Check UID", "UID is: " + UID);

//            Log.d("msg", packageInfo.packageName+": Rec. Bytes : "+rx+" B");
//            Log.d("msg", packageInfo.packageName+": Sent Bytes : "+tx+" B");
            }
            updateAlertDialog(s);
        }
    });





    //Application Usage
    public void showNotification(View v){

        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setTitle("App Data Usage")
                .setCancelable(false)
                .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        thread.interrupt();
                        dialog_dismiss=true;
                        dialog.dismiss();
                    }
                });

        String s="Loading...";
        builder.setMessage(s);
        dialog=builder.create();
        dialog.show();
        dialog_dismiss=false;

        if(!thread.isInterrupted())
            thread.run();
        else
           thread.start();
    }

     void updateAlertDialog(String s){
        Log.d("msg","update call");
        this.runOnUiThread(new RunnableWithParams(s) {
            @Override
            public void run() {
                dialog.setMessage(str);
            }
        });
        if (!dialog_dismiss)
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    thread.run();
                }
            },1000);

    }


    //On hold
    public void startCountingTotalFromNow(View view){
        startNotificationService.startCountingTotalFromNow();
    }
    public void cancelNotification(View v){
        startNotificationService.insertDataUsage();

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

    //on activity starts
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("msg","onSatrt----------------------------------------------->");
        bindService(service,NotificationServiceConnection,BIND_AUTO_CREATE);
        flag_service_bound=true;

        items=new ArrayList<UsageHistoryItem>();
        getAppNameFromPackage();
        Log.d("msg","starting service");
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindService(service,NotificationServiceConnection,BIND_AUTO_CREATE);
        flag_service_bound=true;

        dialog_dismiss=true;
        updateUI();
        Log.d("msg","onResume----------------------------------------------->");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("msg","onDestroy----------------------------------------------->");
        if(flag_service_bound){
            flag_service_bound=false;
            unbindService(NotificationServiceConnection);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("msg","onPause----------------------------------------------->");
        if (dialog!=null)
            dialog.dismiss();
        if(flag_service_bound){
            flag_service_bound=false;
            unbindService(NotificationServiceConnection);
        }
        dialog_dismiss=true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("msg","onStop----------------------------------------------->");
        if(flag_service_bound){
            flag_service_bound=false;
            unbindService(NotificationServiceConnection);
        }
        dialog_dismiss=true;
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



