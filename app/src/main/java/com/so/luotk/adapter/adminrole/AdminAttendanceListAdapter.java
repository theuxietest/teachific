package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.databinding.ItemStudentListForAttendanceBinding;
import com.so.luotk.models.output.GetAttendenceResult;

import java.util.ArrayList;
import java.util.List;

public class AdminAttendanceListAdapter extends RecyclerView.Adapter<AdminAttendanceListAdapter.ViewHolder> {
    private List<GetAttendenceResult> attendenceResultList;
    private Context context;

    public AdminAttendanceListAdapter() {
        this.attendenceResultList = new ArrayList<>();
    }

    @NonNull
    @Override
    public AdminAttendanceListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new AdminAttendanceListAdapter.ViewHolder(ItemStudentListForAttendanceBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull AdminAttendanceListAdapter.ViewHolder holder, int position) {

        if (attendenceResultList.size() > 0) {
            GetAttendenceResult result = attendenceResultList.get(position);
            holder.binding.switchSelectItem.setChecked(result.getStatus().equalsIgnoreCase("Present"));
            holder.binding.tvStudentName.setText(result.getStudent_name());
        }
    }

    public void updateList(List<GetAttendenceResult> attendanceList) {
        this.attendenceResultList = new ArrayList<>(attendanceList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return attendenceResultList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemStudentListForAttendanceBinding binding;

        public ViewHolder(@NonNull ItemStudentListForAttendanceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
