package com.so.luotk.adapter.adminrole;

import android.app.TimePickerDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.so.luotk.R;
import com.so.luotk.models.output.BatchTimingList;


import java.util.Calendar;
import java.util.List;

public class SetBatchTimingsAdapter extends RecyclerView.Adapter<SetBatchTimingsAdapter.ViewHolder> {
    private final List<BatchTimingList> batchTimingLists;
    private final Context context;
    private timeOnCLick timeOnCLickListener;
    private final MaterialButton submitButton;

    public SetBatchTimingsAdapter(Context context, List<BatchTimingList> batchTimingLists, MaterialButton submmitButton) {
        this.context = context;
        this.batchTimingLists = batchTimingLists;
        this.submitButton = submmitButton;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_set_batch_timing, parent, false);
        return new SetBatchTimingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BatchTimingList timingList = batchTimingLists.get(position);
        holder.day.setText(timingList.getDay());
        if (timingList.isOn()) {
            holder.isDayOn.setChecked(true);
            holder.tv_time_to.setText(timingList.getTimeTo());
            holder.tv_time_from.setText(timingList.getTimeFrom());
            holder.layout_timing.setVisibility(View.VISIBLE);
//            holder.tv_noClass.setVisibility(View.GONE);
        } else {
            holder.isDayOn.setChecked(false);
            holder.tv_time_to.setText("00:00");
            holder.tv_time_from.setText("00:00");
            holder.layout_timing.setVisibility(View.GONE);
//            holder.tv_noClass.setVisibility(View.VISIBLE);
        }
        holder.isDayOn.setOnClickListener(view -> {
            boolean buttomTrue = false;
            if (holder.isDayOn.isChecked()) {
                timingList.setOn(true);
                holder.layout_timing.setVisibility(View.VISIBLE);
                for (int i = 0; i < batchTimingLists.size(); i++) {
                    if (!buttomTrue) {
                        if (batchTimingLists.get(i).isOn()) {
                            buttomTrue = true;
                            submitButton.setEnabled(true);
                            submitButton.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_main));
                            submitButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                        } else {
                            submitButton.setEnabled(false);
                            submitButton.setBackgroundColor(ContextCompat.getColor(context, R.color.disabledButtonColor));
                            submitButton.setTextColor(ContextCompat.getColor(context, R.color.disabled_btn));
                        }
                    }

                }
//                holder.tv_noClass.setVisibility(View.GONE);
//                notifyItemChanged(position);
            } else {
                timingList.setOn(false);
                holder.layout_timing.setVisibility(View.GONE);
                for (int i = 0; i < batchTimingLists.size(); i++) {
                    if (!buttomTrue) {
                        if (batchTimingLists.get(i).isOn()) {
                            buttomTrue = true;
                            submitButton.setEnabled(true);
                            submitButton.setBackgroundColor(ContextCompat.getColor(context, R.color.blue_main));
                            submitButton.setTextColor(ContextCompat.getColor(context, R.color.white));
                        } else {
                            submitButton.setEnabled(false);
                            submitButton.setBackgroundColor(ContextCompat.getColor(context, R.color.disabledButtonColor));
                            submitButton.setTextColor(ContextCompat.getColor(context, R.color.disabled_btn));
                        }
                    }

                }
//                holder.tv_noClass.setVisibility(View.VISIBLE);
//                notifyItemChanged(position);
            }
        });
//        if (timingList.isOn()) {
         /*   holder.tv_time_to.setText(timingList.getTimeTo());
            holder.tv_time_from.setText(timingList.getTimeFrom());*/
            /*holder.timeFrom.setOnClickListener(view -> {
                openTimePicker24(holder.tv_time_from);
                timingList.setTimeFrom(holder.tv_time_from.getText().toString());
//                notifyDataSetChanged();
                // timeOnCLickListener.onClick(batchTimingLists.get(position), "from", position);

            });*/
           /* holder.timeTo.setOnClickListener(view -> {
                openTimePicker24(holder.tv_time_to);
                timingList.setTimeTo(holder.tv_time_to.getText().toString());
//                notifyDataSetChanged();
                // timeOnCLickListener.onClick(batchTimingLists.get(position), "to", position);
            });*/
        /*    if (!TextUtils.isEmpty(timingList.getTimeFrom())) {
                holder.tv_time_from.setText(timingList.getTimeFrom());
            }
            if (!TextUtils.isEmpty(timingList.getTimeTo())) {
                holder.tv_time_to.setText(timingList.getTimeTo());
            }*/
//        }

        /*if (timingList.isOn()) {
            holder.timeFrom.setOnClickListener(view -> {
                openTimePicker24(holder.tv_time_from);
                String getTimeFrom = holder.tv_time_from.getText().toString();
                timingList.setTimeFrom(getTimeFrom);
//                notifyItemChanged(position);
                // timeOnCLickListener.onClick(batchTimingLists.get(position), "from", position);

            });
            holder.timeTo.setOnClickListener(view -> {
                openTimePicker24(holder.tv_time_to);
                String getTimeTo = holder.tv_time_to.getText().toString();
                timingList.setTimeTo(getTimeTo);
//                notifyItemChanged(position);
                // timeOnCLickListener.onClick(batchTimingLists.get(position), "to", position);
            });
        }*/
    }

    public void setTimeClickListener(timeOnCLick timeClickListener) {
        this.timeOnCLickListener = timeClickListener;

    }

    public interface timeOnCLick {
        void onClick(BatchTimingList timingList, String toFrom, int position, TextView tv_time_to);

    }

    @Override
    public int getItemCount() {
        return batchTimingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView day, /*tv_noClass,*/ tv_time_from, tv_time_to;
        View timeFrom, timeTo, layout_timing;
        Switch isDayOn;
        LinearLayout layout_time_from, layout_time_to;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.tv_day);
            timeFrom = itemView.findViewById(R.id.layout_time_from);
            timeTo = itemView.findViewById(R.id.layout_time_to);
            isDayOn = itemView.findViewById(R.id.switch_select_day);
//            tv_noClass = itemView.findViewById(R.id.tv_noClass);
            layout_timing = itemView.findViewById(R.id.layout_timing);
            tv_time_from = itemView.findViewById(R.id.tv_time_from);
            tv_time_to = itemView.findViewById(R.id.tv_time_to);
            layout_time_from = itemView.findViewById(R.id.layout_time_from);
            layout_time_to = itemView.findViewById(R.id.layout_time_to);

            layout_time_from.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getPosition();
                    BatchTimingList timingList = batchTimingLists.get(position);
                    openTimePicker24(tv_time_from, "from", timingList);
//                    timeOnCLickListener.onClick(timingList, "from", position, tv_time_from);
                }
            });
            layout_time_to.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getPosition();
                    BatchTimingList timingList = batchTimingLists.get(position);
                    openTimePicker24(tv_time_to, "to", timingList);
//                    timeOnCLickListener.onClick(timingList, "to", position, tv_time_to);
                }
            });
            /*timeFrom.setOnClickListener(view -> {
                int position = getPosition();
                BatchTimingList timingList = batchTimingLists.get(position);
                if (timingList.isOn()) {
                    openTimePicker24(tv_time_from);
                    String getTimeFrom = tv_time_from.getText().toString();
                    timingList.setTimeFrom(getTimeFrom);
                }
                // timeOnCLickListener.onClick(batchTimingLists.get(position), "from", position);

            });
            timeTo.setOnClickListener(view -> {
                int position = getPosition();
                BatchTimingList timingList = batchTimingLists.get(position);
                if (timingList.isOn()) {
                    openTimePicker24(tv_time_to);
                    String getTimeTo = tv_time_to.getText().toString();
                    timingList.setTimeTo(getTimeTo);
                }
                // timeOnCLickListener.onClick(batchTimingLists.get(position), "to", position);
            });*/


        }
    }

    private void openTimePicker(TextView textView) {
        final String[] selected_time = new String[1];
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                String AM_PM;
                int mHour = hourOfDay;
                if (hourOfDay < 12) {
                    AM_PM = "AM";
                } else {
                    AM_PM = "PM";
                    mHour = mHour - 12;
                }
                String selected_time = mHour + ":" + minute + " " + AM_PM;
                textView.setText(selected_time);
            }
        }, mHour, mMinute, false);
        timePickerDialog.show();

    }

    private void openTimePicker24(TextView textView, String fromWhere, BatchTimingList timingList) {
        final String[] selected_time = new String[1];
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hourOfDay,
                                  int minute) {
                Log.d("TAG", "onTimeSetBefore: " + hourOfDay + " : "+ minute);
                String hourSt = "";
                String minutesSt = "";
                int mHour = hourOfDay;
                int mMinutes = minute;
                if (hourOfDay < 10) {
                    hourSt = "0"+mHour;
                } else {
                    hourSt = ""+mHour;
                }

                if (mMinutes < 10) {
                    minutesSt = "0"+mMinutes;
                } else {
                    minutesSt = ""+mMinutes;
                }

                String selected_time = hourSt + ":" + minutesSt;
                if (fromWhere.equals("from")) {
                    timingList.setTimeFrom(selected_time);
                } else {
                    timingList.setTimeTo(selected_time);
                }
                Log.d("TAG", "onTimeSet: " + selected_time + " : "+ minute);
                textView.setText(selected_time);
            }
        }, mHour, mMinute, true);
        timePickerDialog.show();

    }
}
