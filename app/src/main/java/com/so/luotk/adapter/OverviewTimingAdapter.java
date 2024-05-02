package com.so.luotk.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.output.GetBatchOverviewResult;
import com.so.luotk.models.output.TimingList;

import java.util.ArrayList;

public class OverviewTimingAdapter extends RecyclerView.Adapter<OverviewTimingAdapter.ViewHolder> {
    private ArrayList<TimingList> timingList;
    private final Context context;
    private final ArrayList<GetBatchOverviewResult> daysList;
    private final boolean isDarkMode;


    public OverviewTimingAdapter(Context context,/* ArrayList<TimingList> timingList, */ArrayList<GetBatchOverviewResult> daysList,boolean isDarkMode) {
        this.context = context;
        // this.timingList = timingList;
        this.daysList = daysList;
        this.isDarkMode=isDarkMode;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_overview_timing_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvDay.setText(daysList.get(position).getDay().substring(0, 3));
        if(!isDarkMode) {
            if (daysList.get(position).getDay().substring(0, 3).equalsIgnoreCase("Mon")) {
                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#D0F5F7"));
            } else if (daysList.get(position).getDay().substring(0, 3).equalsIgnoreCase("Tue")) {
                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFDEE1"));
            } else if (daysList.get(position).getDay().substring(0, 3).equalsIgnoreCase("Wed")) {
                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#DDF6E8"));
            } else if (daysList.get(position).getDay().substring(0, 3).equalsIgnoreCase("Thu")) {
                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFEFD8"));
            } else if (daysList.get(position).getDay().substring(0, 3).equalsIgnoreCase("Fri")) {
                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#E3F2FC"));
            } else if (daysList.get(position).getDay().substring(0, 3).equalsIgnoreCase("Sat")) {
                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#E5E4FD"));
            } else if (daysList.get(position).getDay().substring(0, 3).equalsIgnoreCase("Sun")) {
                holder.itemLayout.setCardBackgroundColor(Color.parseColor("#FFFFE0"));
            }
        }
        holder.tvStartTime.setText(daysList.get(position).getStartTime());
        holder.tvEndTime.setText(daysList.get(position).getEndTime());

    }

    @Override
    public int getItemCount() {
        // return timingList.size();
        return daysList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final AppCompatTextView tvDay;
        private final AppCompatTextView tvStartTime;
        private final AppCompatTextView tvEndTime;
        private final CardView itemLayout;

        ViewHolder(View itemView) {
            super(itemView);
            tvDay = itemView.findViewById(R.id.tv_day);
            tvStartTime = itemView.findViewById(R.id.tv_start_time);
            tvEndTime = itemView.findViewById(R.id.tv_end_time);
            itemLayout = itemView.findViewById(R.id.item_layout);

        }
    }

}

