package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.output.AdminSettings;

import java.util.List;

public class AdminSettingsAdapter extends RecyclerView.Adapter<AdminSettingsAdapter.ViewHolder> {
    private final List<AdminSettings> adminSettingsList;
    private Context context;

    public AdminSettingsAdapter(List<AdminSettings> adminSettingsList) {
        this.adminSettingsList = adminSettingsList;
    }

    @NonNull
    @Override
    public AdminSettingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_student_list_for_attendance, parent, false);
        return new AdminSettingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminSettingsAdapter.ViewHolder holder, int position) {
        AdminSettings adminSettings = adminSettingsList.get(position);
        holder.tv_setting_name.setText(adminSettings.getName());
        if (adminSettings.getName().equalsIgnoreCase("overview"))
            holder.switch_select_item.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return adminSettingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_setting_name;
        private final Switch switch_select_item;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_setting_name = itemView.findViewById(R.id.tv_student_name);
            switch_select_item = itemView.findViewById(R.id.switch_select_item);
        }
    }
}
