package com.so.luotk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.output.GetAttendenceResult;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;

public class MonthlyAttendenceListAdpter extends RecyclerView.Adapter<MonthlyAttendenceListAdpter.ViewHolder> {
    private final ArrayList<GetAttendenceResult> dateList;
    private final Context context;


    public MonthlyAttendenceListAdpter(Context context, ArrayList<GetAttendenceResult> dateList) {
        this.context = context;
        this.dateList = dateList;

    }

    @Override
    public MonthlyAttendenceListAdpter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_monthly_attendence, parent, false);
        return new MonthlyAttendenceListAdpter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final MonthlyAttendenceListAdpter.ViewHolder holder, final int position) {
        holder.tvDate.setText(Utilities.formatAttendanceDate(dateList.get(position).getLecture_date()));
        holder.tvAbsentPresent.setText(dateList.get(position).getStatus());

        if ((dateList.get(position).getLecture_name() != null)){
            if (!dateList.get(position).getLecture_name().isEmpty()) {
                holder.tvLectureName.setVisibility(View.VISIBLE);
                holder.tvLectureName.setText(dateList.get(position).getLecture_name());
            }
            else {
                holder.tvLectureName.setVisibility(View.GONE);
            }
        }
        else {
            holder.tvLectureName.setVisibility(View.GONE);
        }


        holder.tvLectureTime.setVisibility(View.GONE);
        //holder.tvLectureTime.setText(dateList.get(position).getLecture_timing());
        if (dateList.get(position).getStatus().equalsIgnoreCase("Absent")) {
            holder.tvAbsentPresent.setBackgroundResource(R.drawable.bg_red_round_corner);
            holder.tvAbsentPresent.setTextColor(ContextCompat.getColor(context, R.color.textColorWhite));
        } else {
            holder.tvAbsentPresent.setBackgroundResource(R.drawable.green_round_corner_bg);
        }

    }

    @Override
    public int getItemCount() {
        return dateList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvDate;
        private final TextView tvAbsentPresent;
        private final TextView tvLectureName;
        private final TextView tvLectureTime;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvAbsentPresent = itemView.findViewById(R.id.tv_absent_present);
            tvLectureName = itemView.findViewById(R.id.tv_lecture_name);
            tvLectureTime = itemView.findViewById(R.id.tv_lecture_time);

        }
    }
}