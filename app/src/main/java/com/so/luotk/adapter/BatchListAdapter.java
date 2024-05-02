package com.so.luotk.adapter;

import android.content.Context;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.databinding.ItemBatchListBinding;
import com.so.luotk.models.output.BatchListResult;
import com.so.luotk.utils.PreferenceHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.view.View.VISIBLE;

public class BatchListAdapter extends RecyclerView.Adapter<BatchListAdapter.ViewHolder> implements Filterable {
    private final ArrayList<BatchListResult> dataListFull;
    private ArrayList<BatchListResult> batchList;
    private final Context context;
    private BatchListAdapter.OnItemClickListener mItemClickListener;
    private BatchListAdapter.OnEditItemClickListener mEditItemClickListener;
    private final String isFrom;
    private String dayNames = "";
    private final ArrayList<String> dayList;
    private final Boolean isDarkMode;
    private String userType;

    public BatchListAdapter(Context context, ArrayList<BatchListResult> batchList, String isFrom, boolean darkMode) {
        this.context = context;
        this.batchList = batchList;
        this.isFrom = isFrom;
        dataListFull = new ArrayList<>(batchList);
        dayList = new ArrayList<>();
        this.isDarkMode = darkMode;

    }

    @Override
    public BatchListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        userType = PreferenceHandler.readString(context, PreferenceHandler.USER_TYPE, null);
        return new BatchListAdapter.ViewHolder(ItemBatchListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(final BatchListAdapter.ViewHolder holder, final int position) {
        Log.d("Adapter", "onBindViewHolder: " + isFrom);
        if (isFrom.equalsIgnoreCase("admin")) {
            if (isDarkMode) {
                holder.binding.tvCreateMeeting.getBackground().setColorFilter(ResourcesCompat.getColor(context.getResources(), R.color.textColorBlack, null), PorterDuff.Mode.SRC_ATOP);
                holder.binding.tvCreateMeeting.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.blue_main, null));
            }
            holder.binding.tvCreateMeeting.setVisibility(View.VISIBLE);
        } else if (isFrom.equalsIgnoreCase("adminrole")) {
            holder.binding.tvCreateMeeting.setVisibility(View.GONE);
            if (isDarkMode) {
                holder.binding.tvCreateMeeting.getBackground().setColorFilter(ResourcesCompat.getColor(context.getResources(), R.color.textColorBlack, null), PorterDuff.Mode.SRC_ATOP);
                holder.binding.tvCreateMeeting.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.blue_main, null));
            }
            holder.binding.tvStudentsNum.setVisibility(View.GONE);
        } else {
            holder.binding.tvCreateMeeting.setVisibility(View.GONE);
            if (batchList.get(position).getNotificationType() != null && batchList.get(position).getNotificationType().equalsIgnoreCase("addStudent")) {
                if (batchList.get(position).isNewBatch()) {
                    holder.binding.layoutAnimationIcon.setVisibility(View.VISIBLE);
                    holder.binding.animationViewNew.setVisibility(View.VISIBLE);
                }
            } else
                holder.binding.layoutAnimationIcon.setVisibility(View.GONE);
        }
        holder.binding.tvClassTime.setVisibility(View.GONE);
        holder.binding.imgClockIcon.setVisibility(View.GONE);
        holder.binding.tvSubjectName.setText(batchList.get(position).getBatchName());
        holder.binding.tvClassName.setText(batchList.get(position).getCourseName());
        Log.d("TAG", "onBindViewHolder: " + batchList.get(position).getCourseName());
        if (!isDarkMode) {
//            holder.binding.mainView.setCardBackgroundColor(Color.parseColor(batchList.get(position).getHexColor()));
        }
        convertStringToJson(batchList.get(position).getDays_time());
        if (!dayNames.isEmpty()) {
            holder.binding.days.setText(dayNames);
        }
        if (userType.equals("organisation")) {
            holder.binding.threeDotsLay.setVisibility(VISIBLE);
            holder.binding.imageButton2.setVisibility(View.GONE);
        } else {
            holder.binding.threeDotsLay.setVisibility(View.GONE);
            holder.binding.imageButton2.setVisibility(VISIBLE);
        }
        holder.binding.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position);
                if (batchList.get(position).isBatchNotify()) {
                    holder.binding.mainView.clearAnimation();
                }
            }
        });

        holder.binding.threeDotsLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditItemClickListener.onEditClick(position, holder.binding.threeDotsLay);
              /*  if (batchList.get(position).isBatchNotify()) {
                    holder.binding.mainView.clearAnimation();
                }*/
            }
        });

    }

    @Override
    public int getItemCount() {
        return batchList.size();
    }

    public void updateList(ArrayList<BatchListResult> batchLists) {
        this.batchList = new ArrayList<>(batchLists);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemBatchListBinding binding;

        ViewHolder(ItemBatchListBinding itemBatchListBinding) {
            super(itemBatchListBinding.getRoot());
            this.binding = itemBatchListBinding;
        }
    }

    public void SetOnItemClickListener(final BatchListAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void SetOnEditItemClickListener(final BatchListAdapter.OnEditItemClickListener mEditItemClickListener) {
        this.mEditItemClickListener = mEditItemClickListener;
    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnEditItemClickListener {
        void onEditClick(int position, RelativeLayout threeDotsLayout);
    }

    @Override
    public Filter getFilter() {
        return listFilter;
    }

    private final Filter listFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<BatchListResult> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (BatchListResult item : dataListFull) {
                    if (item.getBatchName().toLowerCase().contains(filterPattern)) {
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
            batchList.addAll((List<BatchListResult>) results.values);
            notifyDataSetChanged();

        }
    };

    public List<BatchListResult> getBatchList() {
        return this.batchList;
    }

    private void convertStringToJson(String response) {
        //   String response = "{\"MON\":{\"day\":\"Monday\",\"startTime\":\"03:36\",\"endTime\":\"06:36\"},\"TUE\":{\"day\":\"Tuesday\",\"startTime\":\"01:43 PM\",\"endTime\":\"05:43 PM\"},\"WED\":{\"day\":\"Wednesday\",\"startTime\":\"02:19 PM\",\"endTime\":\"04:43 PM\"}}";
        try {
            dayList.clear();
            JSONObject jsonObject = (JSONObject) new JSONObject(response);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONObject value = (JSONObject) jsonObject.get(key);
                    String day = (String) value.get("day");
                    dayList.add(day);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            StringBuilder sbString = new StringBuilder();
            for (String days : dayList) {
                sbString.append(days.substring(0, 2)).append(",");
            }
            dayNames = sbString.toString();
            if (dayNames.length() > 0)
                dayNames = dayNames.substring(0, dayNames.length() - 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public ArrayList<BatchListResult> removeAt(int position) {
        batchList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, batchList.size());
        return batchList;
    }
}