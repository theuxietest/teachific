package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.newmodels.adminBatchModel.Result;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementBatchListAdapter extends RecyclerView.Adapter<AnnouncementBatchListAdapter.ViewHolder> {
    private List<Result> batchList;
    private onBatchClickListener onBatchClickListener;
    private final String isFrom;
    private int count = 0;
    private List<Result> selectedList;

    public AnnouncementBatchListAdapter(List<Result> batchList, String isFrom) {

        this.isFrom = isFrom;
        this.batchList = batchList;

    }

    @NonNull
    @Override
    public AnnouncementBatchListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = null;
        if (isFrom.equalsIgnoreCase("activity")) {
            view = LayoutInflater.from(context).inflate(R.layout.item_batch_list_for_announcement, parent, false);
        } else if (isFrom.equalsIgnoreCase("fragment")) {
            view = LayoutInflater.from(context).inflate(R.layout.item_batch_for_announcement, parent, false);
        }
        return new AnnouncementBatchListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementBatchListAdapter.ViewHolder holder, int position) {
        Result batch = batchList.get(position);


        if (isFrom.equalsIgnoreCase("activity")) {
            holder.batch_name.setText(batch.getBatchName());
            holder.checkBox_batch.setChecked(batch.isSelected());
            holder.itemView.setOnClickListener(v -> {

                if (holder.checkBox_batch.isChecked()) {
                    holder.checkBox_batch.setChecked(false);
                    batch.setSelected(false);
                } else {
                    holder.checkBox_batch.setChecked(true);
                    batch.setSelected(true);
                }
                onBatchClickListener.onClick(position);
            });
        } else {
            if (batchList.get(position).isSelected()) {
                holder.batch_name.setText(batchList.get(position).getBatchName());
            } else {
                holder.item_layout.setVisibility(View.GONE);
                holder.item_layout.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }
            holder.itemView.setOnClickListener(v -> {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                    removeAt(holder.getAdapterPosition());
            });

        }


    }

    public ArrayList<Result> getSelected() {
        ArrayList<Result> selected = new ArrayList<>();
        for (int i = 0; i < batchList.size(); i++) {
            if (batchList.get(i).isSelected()) {
                selected.add(batchList.get(i));
            }
        }
        return selected;
    }

    public List<Result> getCurrentList() {
        return this.batchList;
    }

    public void removeAt(int position) {
        batchList.get(position).setSelected(false);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, batchList.size());
    }

    public void setOnclickListener(onBatchClickListener clickListener) {
        this.onBatchClickListener = clickListener;
    }

    public interface onBatchClickListener {
        void onClick(int position);
    }

    public void updateList(List<Result> batchListResults) {
        this.batchList = batchListResults;
        notifyDataSetChanged();

    }

    public void updateCount(int count) {
        this.count = count;
    }

    @Override
    public int getItemCount() {

        return batchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView batch_name;
        private final CheckBox checkBox_batch;
        private final View item_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            batch_name = itemView.findViewById(R.id.tv_batch_name);
            checkBox_batch = itemView.findViewById(R.id.checkbox_student);
            item_layout = itemView.findViewById(R.id.item_layout);

        }
    }
}
