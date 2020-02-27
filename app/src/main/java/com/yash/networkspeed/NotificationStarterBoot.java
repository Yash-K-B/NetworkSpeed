package com.yash.networkspeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.TrafficStats;

public class NotificationStarterBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences preferences=context.getSharedPreferences("Data",Context.MODE_PRIVATE);
        //long received_data=preferences.getLong("ref_recv_data",0);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putLong("ref_recv_data",TrafficStats.getMobileRxBytes());
        editor.apply();


        }

}
