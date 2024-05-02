package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.databinding.ItemJoinRequestListBinding;
import com.so.luotk.models.output.JoinRequestResult;

import java.util.ArrayList;
import java.util.List;


public class JoinRequestListAdapter extends RecyclerView.Adapter<JoinRequestListAdapter.ViewHolder> {
    private Context context;
    private List<JoinRequestResult> dataList;
    private final OnItemClickListener clickListener;

    public JoinRequestListAdapter(OnItemClickListener clickListener) {
        dataList = new ArrayList<>();
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public JoinRequestListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new JoinRequestListAdapter.ViewHolder(ItemJoinRequestListBinding.inflate(LayoutInflater.from(context)));
    }

    @Override
    public void onBindViewHolder(@NonNull JoinRequestListAdapter.ViewHolder holder, int position) {
        bind(dataList.get(position), holder.binding);

    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void updateList(List<JoinRequestResult> dataList) {
        this.dataList = new ArrayList<>(dataList);
        notifyDataSetChanged();
    }

    public void clearList() {
        this.dataList.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemJoinRequestListBinding binding;

        public ViewHolder(@NonNull ItemJoinRequestListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void bind(JoinRequestResult data, ItemJoinRequestListBinding binding) {
        binding.setClick(clickListener);
        binding.setData(data);
        //   binding.setPosition(position);
        binding.executePendingBindings();
    }

    public interface OnItemClickListener {
        void onItemClick(String id, boolean isAccept);
    }
}
