package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.databinding.ItemAdminAnnouncementListBinding;
import com.so.luotk.models.output.AnnouncementData;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AnnouncementListAdapter extends RecyclerView.Adapter<AnnouncementListAdapter.ViewHolder> {
    private Context context;
    private List<AnnouncementData> announcementDataList;
    private final onbuttonsClickListeners clickListeners;


    public AnnouncementListAdapter(onbuttonsClickListeners clickListeners) {
        this.clickListeners = clickListeners;
        this.announcementDataList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AnnouncementListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new AnnouncementListAdapter.ViewHolder(ItemAdminAnnouncementListBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementListAdapter.ViewHolder holder, int position) {
        AnnouncementData announcement = announcementDataList.get(position);
        holder.binding.tvAnnouncementName.setText(announcement.getAnnouncement());
        Utilities.formatDate(holder.binding.tvAnnouncementDate, announcement.getCreated_at(), 3);
        // holder.binding.tvAnnouncementDate.setText(String.format("%s, %s", Utilities.getFormattedDate(announcement.getCreated_at(), true), Utilities.getFormattedTime(announcement.getCreated_at())));
//        holder.binding.imgDelete.setOnClickListener(v -> clickListeners.onClick("delete", position));
        //holder.binding.imgOptions.setOnClickListener(v -> clickListeners.onClick("options", position));
        holder.itemView.setOnClickListener(v -> clickListeners.onClick("view", position));

    }

    public interface onbuttonsClickListeners {
        void onClick(String isFrom, int position);
    }

    @Override
    public int getItemCount() {
        return announcementDataList.size();
    }

    public void updateList(List<AnnouncementData> announcementDataList) {
        Set set = new LinkedHashSet(announcementDataList);
        announcementDataList.clear();
        announcementDataList.addAll(set);
        this.announcementDataList = new ArrayList<>(announcementDataList);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminAnnouncementListBinding binding;

        public ViewHolder(@NonNull ItemAdminAnnouncementListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
