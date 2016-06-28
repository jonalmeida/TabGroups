package com.jonalmeida.tabgroups;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.TabGroupViewHolder> {
    private static final String LOGTAG = RecyclerAdapter.class.getSimpleName();
    public final Context context;

    GroupData data = GroupData.getInstance();

    public RecyclerAdapter(Context aContext) {
        context = aContext;
        data.adapter = this;
    }

    @Override
    public TabGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_group, parent, false);
        return new TabGroupViewHolder(v);
    }

    @Override
    public void onBindViewHolder(TabGroupViewHolder holder, int position) {
        String key = new ArrayList<>(data.keySet()).get(position);
        Log.d(LOGTAG, "Binding key: " + key);
        holder.bindItem(data.get(key));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class TabGroupViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTv;
        private LinearLayout innerLayout;

        public TabGroupViewHolder(View itemView) {
            super(itemView);
            titleTv = (TextView) itemView.findViewById(R.id.group_title);
//            innerLayout = (LinearLayout) itemView.findViewById(R.id.group_tab_inner_layout);
        }

        public void bindItem(Group group) {
            String output = group.name().substring(0, 1).toUpperCase() + group.name().substring(1);
            titleTv.setText(output);
            //innerLayout
        }
    }
}
