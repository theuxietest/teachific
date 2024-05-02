package com.so.luotk.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.output.ReportTestData;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

public class TestAdapter extends RecyclerView.Adapter<TestAdapter.ViewHolder> implements Filterable {
    private final Context context;
    private ArrayList<ReportTestData> reportTestList;
    private List<ReportTestData> dataListFull;
    private List<ReportTestData> filteredList;
    private TestAdapter.OnItemClickListener mItemClickListener;


    public TestAdapter(Context context, ArrayList<ReportTestData> reportTestList) {
        this.context = context;
        this.reportTestList = reportTestList;
        dataListFull = new ArrayList<>(reportTestList);
        filteredList = new ArrayList<>(reportTestList);
    }


    @Override
    public TestAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_list, parent, false);
        return new TestAdapter.ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final TestAdapter.ViewHolder holder, final int position) {

        holder.linearLayoutCalanderView.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_purple_outline));
        holder.tvMonth.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_purple_semi_colored));
        holder.imgRightArrow.setVisibility(View.VISIBLE);

        if (reportTestList.get(position).isNewItem()) {
            holder.new_item_dot.setVisibility(View.VISIBLE);
        } else {
            holder.new_item_dot.setVisibility(View.GONE);
        }

        holder.tvTestType.setVisibility(View.VISIBLE);
        if (reportTestList.get(position).getTest_type() == 2)
            holder.tvTestType.setText(context.getString(R.string.objective));
        else holder.tvTestType.setText(context.getString(R.string.subjective));
        holder.tvTopicName.setText(reportTestList.get(position).getTopic());
        if (reportTestList.get(position).getBatch() != null) {
            holder.tvBatchName.setVisibility(View.VISIBLE);
            holder.tvBatchName.setText(reportTestList.get(position).getBatch().getBatchName());
        } else {
            holder.tvBatchName.setVisibility(View.GONE);
        }
        holder.tvDate.setText(Utilities.getOnlyDate(reportTestList.get(position).getSubmitDate()));
        holder.tvMonth.setText(Utilities.getOnlyMonth(reportTestList.get(position).getSubmitDate()));
        holder.tvAssignTime.setText(reportTestList.get(position).getSubmitTime());
        holder.tvAssignDate.setText(Utilities.getFormattedDate(reportTestList.get(position).getSubmitDate(), false) + ",");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return reportTestList.size();


    }


    public void setUpdatedList(ArrayList<ReportTestData> folderList) {
        this.reportTestList = new ArrayList<>(folderList);
        dataListFull = new ArrayList<>(folderList);
        filteredList = new ArrayList<>(folderList);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName, tvMonth, tvDate, tvTestDuration, tvBatchName, tvAssignDate, tvAssignTime, tvTestType;
        LinearLayout linearLayoutCalanderView;
        ImageView imgRightArrow, new_item_dot;

        ViewHolder(View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tv_topic_name);
            tvMonth = itemView.findViewById(R.id.tv_month);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvTestType = itemView.findViewById(R.id.tv_test_type);
            linearLayoutCalanderView = itemView.findViewById(R.id.custom_calender_icon);
            imgRightArrow = itemView.findViewById(R.id.img_right_arrow_icon);
            // tvTestDuration = itemView.findViewById(R.id.tv_duration);
            tvBatchName = itemView.findViewById(R.id.tv_batch_name);
            new_item_dot = itemView.findViewById(R.id.new_item_dot);
            tvAssignDate = itemView.findViewById(R.id.tv_assign_date);
            tvAssignTime = itemView.findViewById(R.id.tv_assign_time);
        }
    }


    /**
     * Method to intialize click listener for items from activity
     *
     * @param mItemClickListener listener
     */
    public void SetOnItemClickListener(final TestAdapter.OnItemClickListener mItemClickListener) {
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
            FilterResults results = null;
            List<ReportTestData> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ReportTestData item : dataListFull) {
                    if (item.getTopic().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            results = new FilterResults();
            results.values = filteredList;


            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            reportTestList.clear();
            filteredList.clear();
            reportTestList.addAll((List) results.values);
            notifyDataSetChanged();
            filteredList = (List) results.values;

        }
    };

}
