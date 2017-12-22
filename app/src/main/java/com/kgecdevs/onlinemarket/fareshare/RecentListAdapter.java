package com.kgecdevs.onlinemarket.fareshare;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Aditya on 21-12-2017.
 */

public class RecentListAdapter extends RecyclerView.Adapter<RecentListAdapter.MyViewHolder> {

    private ArrayList<String> recentupdates;
    private LayoutInflater inflater;

    public RecentListAdapter(Context context, ArrayList<String> ups)
    {
        recentupdates=ups;
        inflater=LayoutInflater.from(context);
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(inflater.inflate(R.layout.recent_task_card, parent,false));
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String title=recentupdates.get(position);
        holder.setData(title, position);
    }

    @Override
    public int getItemCount() {
        return recentupdates.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder
    {
        private TextView memoholder;
        public MyViewHolder(View itemView) {
            super(itemView);
            memoholder=itemView.findViewById(R.id.recenttext);
        }

        public void setData(String title, int position) {
            memoholder.setText(title);
        }
    }
}

