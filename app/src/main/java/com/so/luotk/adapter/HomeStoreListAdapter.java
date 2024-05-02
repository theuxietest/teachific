package com.so.luotk.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.databinding.HomeItemStoreListBinding;
import com.so.luotk.models.newmodels.courseModel.Datum;
import com.so.luotk.utils.PreferenceHandler;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;



public class HomeStoreListAdapter extends RecyclerView.Adapter<HomeStoreListAdapter.MyViewHolder> {
    private String base;
    private final OnItemClickListener mItemClickListener;
    private final List<Datum> list;
    private boolean flag;
    private Context mContext;

    public HomeStoreListAdapter(OnItemClickListener mItemClickListener, Context context, boolean flag) {
        this.mItemClickListener = mItemClickListener;
        this.mContext = context;
        list = new ArrayList<>();
        this.flag = flag;
    }

    public void updateList(List<Datum> coursesList) {
        list.addAll(coursesList);
        notifyDataSetChanged();
    }

    public void removeList() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        HomeItemStoreListBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.home_item_store_list, parent, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        if (PreferenceHandler.readString(mContext, PreferenceHandler.SHARE_COURSE, "").equals("1")) {
            holder.binding.shareCourse.setVisibility(View.VISIBLE);
        } else {
            holder.binding.shareCourse.setVisibility(View.GONE);
        }
        holder.bind(list.get(position), position, mItemClickListener, base, flag);
        /*try {
            if (list.get(position).getDiscount() != null) {
                if (Float.parseFloat(list.get(position).getDiscount()) > 0) {
                    holder.binding.actualPrice.setVisibility(View.VISIBLE);
                } else {
                    holder.binding.actualPrice.setVisibility(View.GONE);
                }
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setBaseUrl(String imageBaseUrl) {
        base = imageBaseUrl;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        HomeItemStoreListBinding binding;

        MyViewHolder(HomeItemStoreListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Datum data, int position, OnItemClickListener onItemClickListener, String base, boolean flag) {
            binding.setBase(base);
            binding.setFlag(flag);
            binding.setClick(onItemClickListener);
            binding.setData(data);
            binding.setPosition(position);
            binding.executePendingBindings();
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Datum data, int position);
    }

}