package com.so.luotk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.output.AssignmentList;

import androidx.cardview.widget.CardView;

import java.util.ArrayList;
import java.util.List;

public class AssignmentAdapter extends RecyclerView.Adapter<AssignmentAdapter.ViewHolder> implements Filterable {
    private final ArrayList<AssignmentList> assignmentList;
    private final Context context;
    private AssignmentAdapter.OnItemClickListener mItemClickListener;
    private final List<AssignmentList> dataListFull;
    private List<AssignmentList> filteredList;
    private final boolean isFromReportTab;

    public AssignmentAdapter(Context context, ArrayList<AssignmentList> assignmentList, boolean isFromReportTab) {
        this.context = context;
        this.assignmentList = assignmentList;
        this.isFromReportTab = isFromReportTab;
        dataListFull = new ArrayList<>(assignmentList);
        filteredList = new ArrayList<>();

    }

    @Override
    public AssignmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment_list, parent, false);
        return new AssignmentAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final AssignmentAdapter.ViewHolder holder, final int position) {
        holder.tvTopicName.setText(assignmentList.get(position).getTopicName());
        holder.tvAssignTime.setText(assignmentList.get(position).getAssignTime());
        holder.tvAssignDate.setText(assignmentList.get(position).getAssignDate());
        if (isFromReportTab) {
            holder.tvSubTopicName.setText("Introduction to Real Numbers");
            holder.tvSubTopicName.setVisibility(View.VISIBLE);
        } else {
            holder.tvSubTopicName.setVisibility(View.GONE);
        }
        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return assignmentList.size();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView tvTopicName, tvSubTopicName, tvAssignTime, tvAssignDate;
        private final CardView itemLayout;

        ViewHolder(View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tv_topic_name);
            tvSubTopicName = itemView.findViewById(R.id.tv_sub_topic_name);
            tvAssignTime = itemView.findViewById(R.id.tv_assign_time);
            tvAssignDate = itemView.findViewById(R.id.tv_assign_date);
            itemLayout = itemView.findViewById(R.id.item_layout);


        }
    }


    /**
     * Method to intialize click listener for items from activity
     *
     * @param mItemClickListener listener
     */
    public void SetOnItemClickListener(final AssignmentAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * Click listener interface
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }


    //implement fileration code
    @Override
    public Filter getFilter() {
        return listFilter;
    }

    private final Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<AssignmentList> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (AssignmentList item : dataListFull) {
                    if (item.getTopicName().toLowerCase().contains(filterPattern)) {
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
            assignmentList.clear();
            filteredList.clear();
            assignmentList.addAll((List) results.values);
            notifyDataSetChanged();
            filteredList = (List) results.values;
        }
    };

    public int getFilteredListSize() {
        return filteredList.size();
    }

}
