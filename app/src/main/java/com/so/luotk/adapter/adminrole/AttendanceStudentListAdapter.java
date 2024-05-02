package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.so.luotk.R;

import com.so.luotk.models.output.StudentDetailsData;
import com.so.luotk.utils.PreferenceHandler;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class AttendanceStudentListAdapter extends RecyclerView.Adapter<AttendanceStudentListAdapter.ViewHolder> {
    List<StudentDetailsData> studentDataList;
    private String isFrom;
    private Context context;
    private OnDeleteClickListener clickListener;
    private String userType;

    public AttendanceStudentListAdapter(List<StudentDetailsData> studentDataList, String isFrom) {
        this.studentDataList = studentDataList;
        this.isFrom = isFrom;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        userType = PreferenceHandler.readString(context, PreferenceHandler.USER_TYPE, "");
        View view;
        if (isFrom.equalsIgnoreCase("attendance"))
            view = LayoutInflater.from(context).inflate(R.layout.item_student_list_for_attendance, parent, false);
        else
            view = LayoutInflater.from(context).inflate(R.layout.item_student_list, parent, false);
        return new AttendanceStudentListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StudentDetailsData student = studentDataList.get(position);
        holder.student_name.setText(student.getName());
        if (isFrom.equalsIgnoreCase("student") || isFrom.equalsIgnoreCase("faculty")) {
            if (student.getPhoto() != null) {
                holder.txt_in_img.setVisibility(View.GONE);
                Glide.with(context).load("https://web.smartowls.in/" + student.getPhoto()).into(holder.img_student);
            } else {
                String first_letter = student.getName().substring(0, 1).toUpperCase();
                holder.txt_in_img.setText(first_letter);
                holder.txt_in_img.setVisibility(View.VISIBLE);
                holder.img_student.setImageResource(R.color.gray);
            }
            if (!TextUtils.isEmpty(userType)) {
                if (student.getPhone() != null) {
                    if (userType.equalsIgnoreCase("organisation"))
                        holder.student_num.setText(student.getPhone());
                    if (userType.equalsIgnoreCase("faculty")&& student.getPhone().length() > 3)
                        holder.student_num.setText(student.getPhone().substring(0, 2) + "********");
                }
            }
            if (isFrom.equalsIgnoreCase("faculty")) {
                holder.btn_delete.setVisibility(View.VISIBLE);
            }
            holder.btn_delete.setOnClickListener(v -> {
                clickListener.onClick(position, true);
            });
        }

    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    public void updateList(List<StudentDetailsData> studentDataList, String isFrom) {
        this.studentDataList = new ArrayList<>(studentDataList);
        this.isFrom = isFrom;
        notifyDataSetChanged();
    }

    public List<StudentDetailsData> removeAt(int position) {
        studentDataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, studentDataList.size());
        return studentDataList;
    }

    public void setOnClickListener(OnDeleteClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public interface OnDeleteClickListener {
        void onClick(int position, boolean delete);
    }

    @Override
    public int getItemCount() {
        return studentDataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView student_name;
        private final TextView student_num;
        private TextView view_details;
        private final TextView txt_in_img;
        private final CircleImageView img_student;
        /*   private View layout_view_details;*/
        private final ImageView btn_delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            student_name = itemView.findViewById(R.id.tv_student_name);
            student_num = itemView.findViewById(R.id.tv_student_num);
            /*     view_details = itemView.findViewById(R.id.tv_view_details);*/
            img_student = itemView.findViewById(R.id.img_student);
            txt_in_img = itemView.findViewById(R.id.txt_in_img);
            /*   layout_view_details = itemView.findViewById(R.id.layout_view_details);*/
            btn_delete = itemView.findViewById(R.id.btn_delete);
        }
    }
}
