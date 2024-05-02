package com.so.luotk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.output.AnnouncementData;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AnnouncementListAdapter extends RecyclerView.Adapter<AnnouncementListAdapter.ViewHolder> {
    private List<AnnouncementData> announcementList;
    private Context context;
    private OnItemClickListener mItemClickListener;


    public AnnouncementListAdapter(List<AnnouncementData> announcementList) {
        this.announcementList = new ArrayList<>();
    }

    @Override
    public AnnouncementListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_announcement_list, parent, false);
        return new AnnouncementListAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AnnouncementListAdapter.ViewHolder holder, final int position) {
        holder.tvAnnouncementName.setText(announcementList.get(position).getAnnouncement());
        holder.tvDate.setText(Utilities.getFormattedDate(announcementList.get(position).getCreated_at(), true) + ", " + Utilities.getFormattedTime(announcementList.get(position).getCreated_at()));

    }

    @Override
    public int getItemCount() {
        return announcementList.size();

    }

    public void updateList(List<AnnouncementData> announcementDataList) {
        Set set = new LinkedHashSet<>(announcementDataList);
        announcementDataList.clear();
        announcementDataList.addAll(set);
        this.announcementList = new ArrayList<>(announcementDataList);
        //this.announcementList = announcementDataList;
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAnnouncementName, tvDate;

        ViewHolder(View itemView) {
            super(itemView);
            tvAnnouncementName = itemView.findViewById(R.id.tv_announcement_name);
            tvDate = itemView.findViewById(R.id.tv_created_date);
        }
    }


    /**
     * Method to intialize click listener for items from activity
     *
     * @param mItemClickListener listener
     */
    public void SetOnItemClickListener(final AnnouncementListAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * Click listener interface
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
