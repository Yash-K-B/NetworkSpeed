package com.yash.networkspeed;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.ViewHolder> {
    List<UsageHistoryItem> items;
    HistoryItemAdapter(List<UsageHistoryItem> items){
        this.items=items;
        Log.d("msg1","Adapter Initiated/ list size:"+items.size());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View view=inflater.inflate(R.layout.history_item,parent,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final UsageHistoryItem item=items.get(position);
        holder.date.setText(item.getDate());
        holder.mobile_data.setText(item.getMobileData());
        holder.wifi_data.setText(item.getWifiData());
    }

    @Override
    public int getItemCount() {
        //Log.d("msg1","List size:"+items.size());
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView date,mobile_data,wifi_data;
        public ViewHolder(View itemView) {
            super(itemView);
            date=(TextView)itemView.findViewById(R.id.date);
            mobile_data=(TextView)itemView.findViewById(R.id.mobile_data);
            wifi_data=(TextView)itemView.findViewById(R.id.wifi_data);

        }
    }

}
