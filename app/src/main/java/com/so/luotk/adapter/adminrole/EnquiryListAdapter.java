package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.databinding.ItemEnquiryListBinding;
import com.so.luotk.models.output.StudentDetailsData;

import java.util.List;

public class EnquiryListAdapter extends RecyclerView.Adapter<EnquiryListAdapter.ViewHolder> {
    private final List<StudentDetailsData> studentDetailsData;

    private Context context;

    public EnquiryListAdapter(List<StudentDetailsData> studentDetailsData) {
        this.studentDetailsData = studentDetailsData;
    }

    @NonNull
    @Override
    public EnquiryListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new EnquiryListAdapter.ViewHolder(ItemEnquiryListBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull EnquiryListAdapter.ViewHolder holder, int position) {
        holder.binding.tvStudentName.setText(studentDetailsData.get(position).getName());

    }

    @Override
    public int getItemCount() {
        return studentDetailsData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemEnquiryListBinding binding;

        public ViewHolder(@NonNull ItemEnquiryListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
