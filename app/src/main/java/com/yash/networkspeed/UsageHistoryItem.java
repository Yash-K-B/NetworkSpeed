package com.yash.networkspeed;

public class UsageHistoryItem {
    String date,mobile_data,wifi_data;
    UsageHistoryItem(String date,String mobile_data,String wifi_data){
        this.date=date;
        this.mobile_data=mobile_data;
        this.wifi_data=wifi_data;
    }

    public String getDate() {
        return date;
    }

    public String getMobileData() {
        return mobile_data;
    }

    public String getWifiData() {
        return wifi_data;
    }
}
