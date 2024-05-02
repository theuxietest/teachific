package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.so.luotk.R;
import com.so.luotk.databinding.ItemBatchListForAnnouncementBinding;
import com.so.luotk.models.output.StudentDetailsData;
import com.so.luotk.utils.PreferenceHandler;

import java.util.ArrayList;
import java.util.List;

public class SelectStudentsAdapter extends RecyclerView.Adapter<SelectStudentsAdapter.ViewHolder> {
    List<StudentDetailsData> studentDetailsData;
    Context context;
    private boolean isAllSelected;
    private SelectStudentsAdapter.onStudentClickListener clickListener;
    private final LinearLayout linearLayout;
    private final MaterialCheckBox checkboxSelectAll;
    private String userType;

    public SelectStudentsAdapter(List<StudentDetailsData> studentDetailsData, LinearLayout linearLayout, MaterialCheckBox materialCheckBox) {
        this.studentDetailsData = studentDetailsData;
        this.linearLayout = linearLayout;
        this.checkboxSelectAll = materialCheckBox;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        userType = PreferenceHandler.readString(context, PreferenceHandler.USER_TYPE, "");

        return new SelectStudentsAdapter.ViewHolder(ItemBatchListForAnnouncementBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentDetailsData student = studentDetailsData.get(position);
        holder.binding.tvStudentName.setText(student.getName());
        if (student.getPhoto() != null) {
            holder.binding.txtInImg.setVisibility(View.GONE);
            Glide.with(context).load("https://web.smartowls.in/" + student.getPhoto()).into(holder.binding.nameImg);
        } else {
            String first_letter = student.getName().substring(0, 1).toUpperCase();
            holder.binding.txtInImg.setText(first_letter);
            holder.binding.txtInImg.setVisibility(View.VISIBLE);
            holder.binding.nameImg.setImageResource(R.color.gray);
        }
        holder.binding.checkboxStudent.setChecked(student.isSelected());

        if (!TextUtils.isEmpty(userType)) {
            if (student.getPhone() != null) {
                if (userType.equalsIgnoreCase("organisation"))
                    holder.binding.tvStudentNum.setText(student.getPhone());
                if (userType.equalsIgnoreCase("faculty")&& student.getPhone().length() > 3)
                    holder.binding.tvStudentNum.setText(student.getPhone().substring(0, 2) + "********");
            }
        }

        holder.itemView.setOnClickListener(v -> {

            if (holder.binding.checkboxStudent.isChecked()) {
                holder.binding.checkboxStudent.setChecked(false);
                student.setSelected(false);
            } else {
                holder.binding.checkboxStudent.setChecked(true);
                student.setSelected(true);
            }

            if (getSelected().size() > 0)
                linearLayout.setVisibility(View.VISIBLE);
            else linearLayout.setVisibility(View.GONE);
            checkboxSelectAll.setChecked(getSelected().size() == studentDetailsData.size());

            /*holder.binding.selectLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (holder.binding.checkboxBatch.isChecked()) {
                        holder.binding.checkboxBatch.setChecked(false);
                    } else {
                        holder.binding.checkboxBatch.setChecked(true);
                    }
                }
            });*/
            /*try {
                clickListener.onClick(position);
            } catch (Exception e) {
                e.printStackTrace();
            }*/
        });

    }

    @Override
    public int getItemCount() {
        return studentDetailsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBatchListForAnnouncementBinding binding;

        public ViewHolder(@NonNull ItemBatchListForAnnouncementBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public ArrayList<StudentDetailsData> getSelected() {
        ArrayList<StudentDetailsData> selected = new ArrayList<>();
        for (int i = 0; i < studentDetailsData.size(); i++) {
            if (studentDetailsData.get(i).isSelected()) {
                selected.add(studentDetailsData.get(i));
                isAllSelected = true;
            } else isAllSelected = false;
        }
        return selected;
    }

    public boolean isFullListSelected() {
        return this.isAllSelected;
    }

    public void SetOnItemClickListener(final SelectStudentsAdapter.onStudentClickListener mItemClickListener) {
        this.clickListener = mItemClickListener;
    }

    /*public void setOnclickListener(onStudentClickListener clickListener) {
        this.clickListener = clickListener;
    }*/

    public interface onStudentClickListener {
        void onClick(int position);
    }

    public void updateList(List<StudentDetailsData> studentDataList) {
        this.studentDetailsData = new ArrayList<>(studentDataList);
        notifyDataSetChanged();
    }
}
