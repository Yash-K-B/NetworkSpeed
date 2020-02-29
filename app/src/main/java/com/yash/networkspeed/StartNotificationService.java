package com.yash.networkspeed;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.net.TrafficStats;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.DecimalFormat;
import java.util.Calendar;

public class StartNotificationService extends Service {
    public static final String CHANNEL_ID="ch1";
    boolean mStopHandler = false;
    NotificationCompat.Builder notification;
    Handler mHandler;
    double t_old_mob_data,r_old_mob_data,t_old_total_data,r_old_total_data,t_old_wifi_data,r_old_wifi_data,old_r_data=0.0;
    RemoteViews mycontentView;
    private final IBinder iBinder=new MyBinder();
    DatabaseRoom databaseRoom;
    SharedPreferences preferences;
    boolean running=false;
    int mode=1;
    DateManipulate dateManipulate;
    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler=new Handler();
        databaseRoom=new DatabaseRoom(this);
        dateManipulate=new DateManipulate();

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ConnectivityManager cmg;
        Network []network=null;
        Network activeNetwork=null;
        NetworkCapabilities capabilities=null;
        Log.d("msg","Service started");

        cmg=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cmg!=null) {
//            network = cmg.getAllNetworks();
//            for (Network x : network) {
//                capabilities = cmg.getNetworkCapabilities(x);
//                if(capabilities!=null)
//                    Log.d("msg", "Transport Type: " + x + ":" + capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
//
//            }
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                activeNetwork=cmg.getActiveNetwork();
                capabilities=cmg.getNetworkCapabilities(activeNetwork);
            }
            else {
                network = cmg.getAllNetworks();
                for (Network x : network) {
                    capabilities = cmg.getNetworkCapabilities(x);
                    if (capabilities != null)
                        Log.d("msg", "Transport Type: " + x + ":" + capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI));
                }
            }



        }


       if(network!=null||activeNetwork!=null){
            if(capabilities!=null){
                    if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                        mode=1;
                        showNotification();
                    }
                    else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                        mode=2;
                        showNotification();
                    }
                    else
                    {
                        mode=0;
                        stopNotification();
                    }
                }
       }
       else {
                mode=0;
                stopNotification();
                }


        Log.d("msg","Running:"+running);
        return Service.START_NOT_STICKY;
    }

    //start Notification
    void showNotification(){

        mStopHandler=false;
        //Custom notification view (RemoteView)
//        mycontentView= new RemoteViews(getPackageName(), R.layout.notification);
//        mycontentView.setImageViewResource(R.id.image, R.drawable.ic_notifications_white_24dp);
//        mycontentView.setTextViewText(R.id.n_title, "Notification Title");
//        mycontentView.setTextViewText(R.id.n_body, "Notification Body");

        Intent intent=new Intent(this,Dashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT|Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent pendingIntent=PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //creating notification
        notification=new NotificationCompat.Builder(this,CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                //.setContent(mycontentView)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(Notification.PRIORITY_HIGH)
                .setColor(ContextCompat.getColor(StartNotificationService.this, android.R.color.holo_orange_light));


        preferences=getApplicationContext().getSharedPreferences("Data",MODE_PRIVATE);

        final DecimalFormat df=new DecimalFormat("0.00");



        //Old Values
        t_old_mob_data= TrafficStats.getMobileTxBytes();//Cellular data
        r_old_mob_data=TrafficStats.getMobileRxBytes();
        t_old_total_data=TrafficStats.getTotalTxBytes();//Total data
        r_old_total_data=TrafficStats.getTotalRxBytes();
        //t_old_wifi_data=t_old_total_data-t_old_mob_data;//wifi data
        //r_old_wifi_data=r_old_total_data-r_old_mob_data;


        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // complete calculations
                Log.d("msg","st----------------------------------------------------------------");
                //Current Values
                //if (mode != 0) {
                    double current_mob_t_data = TrafficStats.getMobileTxBytes();//Cellular data
                    double current_mob_r_data = TrafficStats.getMobileRxBytes();
                    double current_total_t_data = TrafficStats.getTotalTxBytes();//Total data
                    double current_total_r_data = TrafficStats.getTotalRxBytes();
                    //double current_wifi_t_data = current_total_t_data - current_mob_t_data;//wifi data
                    //double current_wifi_r_data = current_total_r_data - current_mob_r_data;

                    double t_data = 0.0, r_data = 0.0;

                   /* if(current_mob_t_data<t_old_mob_data)
                        t_old_mob_data = 0.0;

                    if(current_mob_r_data<r_old_mob_data)
                        r_old_mob_data=0.0;*/
                //Log.d("msg","  ii "+current_mob_r_data+"  /  "+r_old_mob_data+"change : "+(current_mob_r_data>r_old_mob_data));
                   if(((current_mob_r_data!=0.0&&current_mob_t_data!=0.0)&&(current_mob_r_data>=r_old_mob_data&&current_mob_t_data>=t_old_mob_data))||((current_mob_r_data==0.0&&current_mob_t_data==0.0)&&(current_total_t_data>=t_old_total_data&&current_total_r_data>=r_old_total_data)) ){
                       if (mode == 1) {

                           //Mobile Data Calculations
                           Log.d("msg", "mode 1");
                           if (current_mob_t_data != 0.0)
                               t_data = current_mob_t_data - t_old_mob_data;
                           if (current_mob_r_data != 0.0)
                               r_data = current_mob_r_data - r_old_mob_data;
                       } else if (mode == 2) {

                           //Wifi data Calculations
                           //Log.d("msg","mode 2");
                           if (current_mob_t_data != 0.0)
                               t_data = (current_total_t_data - t_old_total_data) - (current_mob_t_data - t_old_mob_data);
                           else t_data = (current_total_t_data - t_old_total_data);
                           if (current_mob_r_data != 0.0)
                               r_data = (current_total_r_data - r_old_total_data) - (current_mob_r_data - r_old_mob_data);
                           else r_data = (current_total_r_data - r_old_total_data);
                           //Log.d("msg","R current T:"+current_total_r_data+" old T"+r_old_total_data+" R current M:"+current_mob_r_data+" old M:"+r_old_mob_data+"///"+r_data);

                       }

                       String t_suffix = "B/s", r_suffix = "B/s";
                       old_r_data = preferences.getLong("ref_recv_data", 0);//getting from storage

                       DataItem recv = new DataItem(r_data, r_suffix);//Received Data
                       recv.convertToHighestSuffix();
                       DataItem trns = new DataItem(t_data, t_suffix);//Transmitted Data
                       trns.convertToHighestSuffix();


                       //storing usage data
                       double todays_usage = 0.0;
                       String dataUtype = "";
                       //Daily Data Usage
                       dateManipulate.refresh();
                       String todays_date = "D01011990";

                       if (mode == 1)
                           todays_date = dateManipulate.dateToString() + "_mob";
                       else if (mode == 2)
                           todays_date = dateManipulate.dateToString() + "_wifi";

                       double todays_use = preferences.getFloat(todays_date, -1.0f);

                       Log.d("msg", "todays use: " + todays_use);
                       if (todays_use == -1.0) {
//                        Log.d("msg","one time call todays usage");
                           SharedPreferences.Editor editor = preferences.edit();
                           if (mode == 1) {
                               todays_usage = preferences.getFloat("todays_usage_mob", 0.0f);
                               todays_usage -= todays_usage;
                               //Log.d("msg","todays usage mob : "+todays_usage);
                               editor.putFloat("todays_usage_mob", (float) todays_usage);
                           } else if (mode == 2) {
                               todays_usage = preferences.getFloat("todays_usage_wifi", 0.0f);
                               todays_usage -= todays_usage;
                               //Log.d("msg","todays usage wifi : "+todays_usage);
                               editor.putFloat("todays_usage_wifi", (float) todays_usage);
                           }

                           editor.apply();

                       } else {
                           if (mode == 1) {
                               todays_usage = preferences.getFloat(todays_date, 0.0f);
                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putFloat("todays_usage_mob", (float) todays_usage);
                               editor.apply();
                           } else if (mode == 2) {
                               todays_usage = preferences.getFloat(todays_date, 0.0f);
                               SharedPreferences.Editor editor = preferences.edit();
                               editor.putFloat("todays_usage_wifi", (float) todays_usage);
                               editor.apply();
                           }
                       }


                       //

                       if (mode == 1) {
                           todays_usage = preferences.getFloat("todays_usage_mob", 0.0f) + (r_data + t_data);
                           //Log.d("msg", "Mobile : " + todays_usage+" R: "+r_data+" S: "+t_data);
                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putFloat("todays_usage_mob", (float) todays_usage);
                           editor.apply();
                           dataUtype = "Mobile Data";
                       }
                       if (mode == 2) {
                           todays_usage = preferences.getFloat("todays_usage_wifi", 0.0f) + (r_data + t_data);
                           //Log.d("msg", "Wifi : " + todays_usage+" R: "+r_data+" S: "+t_data);
                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putFloat("todays_usage_wifi", (float) todays_usage);
                           editor.apply();
                           dataUtype = "Wifi";
                       }

                       if (mode != 0) {
                           SharedPreferences.Editor editor = preferences.edit();
                           editor.putFloat(todays_date, (float) todays_usage);
                           editor.apply();
                       }

                       //Log.d("msg","todays date: "+todays_date+" use : "+todays_usage);

                       DataItem trecv = new DataItem(todays_usage);//Total Received Data
                       trecv.convertToHighestSuffix();

//                TextDrawable drawable = TextDrawable.builder()
//                        .buildRect(df.format(recv.getData()), Color.TRANSPARENT);

                       //setting notification icon and data
                       int smallIcon = new StartNotificationService.ResourceMap(recv.getData(), recv.getSuffix()).getResourceId();//Extracting icon from resource
                       //Log.d("msg"," : "+smallIcon+" for data: "+recv.getData());
                       // mycontentView.setImageViewResource(R.id.image, smallIcon);
                       // mycontentView.setTextViewText(R.id.n_title, "Data Received: "+df.format(trecv.getData())+trecv.getType());
                       // mycontentView.setTextViewText(R.id.n_body, "Download: "+df.format(recv.getData())+""+recv.getSuffix()+"  Upload: "+df.format(trns.getData())+""+trns.getSuffix());

                       notification.setSmallIcon(smallIcon)
                               .setContentText(dataUtype + " Used: " + df.format(trecv.getData()) + trecv.getType() + " Total: " + df.format(new DataItem(current_mob_r_data).convert().getData()) + new DataItem(current_mob_r_data).convert().getType())
                               .setContentTitle("D: " + df.format(recv.getData()) + "" + recv.getSuffix() + "  U: " + df.format(trns.getData()) + "" + trns.getSuffix());

                       //Assigning current data to old data;
                       if (current_mob_t_data != 0.0)
                           t_old_mob_data = current_mob_t_data;//Cellular
                       if (current_mob_r_data != 0.0)
                           r_old_mob_data = current_mob_r_data;


                       t_old_total_data = current_total_t_data;//Total
                       r_old_total_data = current_total_r_data;
//                    t_old_wifi_data = current_wifi_t_data;//Wifi
//                    r_old_wifi_data = current_wifi_r_data;

                       if (mode != 0) {
                           //Display Notification With Manager
                           NotificationManagerCompat manager = NotificationManagerCompat.from(StartNotificationService.this);
                           manager.notify(1, notification.build());
                       }


                   }
                    if (!mStopHandler) {
                        mHandler.postDelayed(this, 1000); //runs every second
                    } else running = false;


                Log.d("msg","end----------------------------------------------------------------");

            }

        };

        // begin task
        if(!running) {
            running=true;
            mHandler.post(runnable);
        }

    }

    //Dismiss notification
    void stopNotification(){
        mStopHandler=true;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        NotificationManagerCompat nmc = NotificationManagerCompat.from(StartNotificationService.this);
                        nmc.cancel(1);
                    }
                },1000);
    }

    public void startCountingTotalFromNow(){
        SharedPreferences.Editor editor=preferences.edit();
        editor.putLong("ref_recv_data",TrafficStats.getMobileRxBytes());
        editor.putFloat("todays_usage_mob",0.0f);
        editor.putFloat("todays_usage_wifi",0.0f);
        editor.apply();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //insert data
    void insertDataUsage(){
        databaseRoom.insertData(10.0);
    }



    class ResourceMap {
        private double dataspeed;
        private String suffix;
        int resourceId;
        ResourceMap(double dataspeed,String suffix){
            this.dataspeed=dataspeed;
            this.suffix=suffix;
        }
        int getResourceId(){

            if(suffix.equals("KB/s")) {
                long l = (long) dataspeed;
                String bits = "";
                if (l < 100)
                    bits = "0";
                if (l < 10)
                    bits = "00";

                //Log.d("msg", "suffix:" + suffix+" value: "+l);
                if(l<1000)
                    return getResources().getIdentifier("wkb" + bits + l, "drawable", "com.yash.networkspeed");
                else return R.drawable.wmb010;
            }
            if(suffix.equals("MB/s")){
                DecimalFormat decimalFormat=new DecimalFormat("0.0");
                String d=decimalFormat.format(dataspeed);
                if(dataspeed<10.0){
                    return getResources().getIdentifier("wmb0" +d.charAt(0)+d.charAt(2), "drawable", "com.yash.networkspeed");
                }
            }
            return  R.drawable.wkb000;
        }

    }

    public class MyBinder extends Binder {

        public   StartNotificationService getService() {
            return StartNotificationService.this;

        }
    }

}

