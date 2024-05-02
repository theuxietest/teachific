package com.so.luotk.adapter.adminrole;


import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RadioButton;

import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.newmodels.adminBatchModel.GetBatchCreateId;

import java.util.ArrayList;
import java.util.List;

public class IdListAdapter extends RecyclerView.Adapter<IdListAdapter.ViewHolder> implements Filterable {
    private final List<GetBatchCreateId.IdResult> dataListFull;
    private List<GetBatchCreateId.IdResult> batchList;
    private final Context context;
    private OnItemClickListener mItemClickListener;
    private int lastCheckedPosition = -1;

    public IdListAdapter(Context context, List<GetBatchCreateId.IdResult> batchList, String isFrom) {
        this.context = context;
        this.batchList = batchList;
        this.dataListFull = new ArrayList<>(batchList);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.id_select_layout, parent, false);
        return new IdListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        GetBatchCreateId.IdResult data = dataListFull.get(position);
        Log.d("Adapter", "onBindViewHolder: " +data.getCategoryName());
        holder.radioButton.setText(data.getCategoryName());
        if (position == lastCheckedPosition) {
            data.setSlelected(true);
            holder.radioButton.setChecked(true);
        } else {
            data.setSlelected(false);
            holder.radioButton.setChecked(false);
        }
//        holder.radioButton.setChecked(position == lastCheckedPosition);
    }

    @Override
    public int getItemCount() {
        return batchList.size();
    }

    public void updateList(ArrayList<GetBatchCreateId.IdResult> batchLists) {
        this.batchList = new ArrayList<>(batchLists);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final RadioButton radioButton;

        ViewHolder(View itemView) {
            super(itemView);
            this.radioButton = itemView.findViewById(R.id.radio);
            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int copyOfLastCheckedPosition = lastCheckedPosition;
                    lastCheckedPosition = getAdapterPosition();
                    notifyItemChanged(copyOfLastCheckedPosition);
                    notifyItemChanged(lastCheckedPosition);

                }
            });
        }
    }

    public void SetOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    private final Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<GetBatchCreateId.IdResult> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (GetBatchCreateId.IdResult item : dataListFull) {
                    Log.d("TAG", "performFiltering: " + item.getCategoryName().toLowerCase() +" : "+ filterPattern);
                    if (item.getCategoryName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            batchList.clear();
            batchList.addAll((List<GetBatchCreateId.IdResult>) results.values);
            notifyDataSetChanged();

        }
    };

    public List<GetBatchCreateId.IdResult> getIdList() {
        return this.batchList;
    }



}