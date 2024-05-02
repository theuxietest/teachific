package com.so.luotk.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.output.Data;
import com.so.luotk.models.video.DatumVideo;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class AssignmentTestListAdapter extends RecyclerView.Adapter<AssignmentTestListAdapter.ViewHolder> implements Filterable {
    private final Context context;
    private ArrayList<Data> assignmentTestList;
    private List<Data> dataListFull;
    private List<Data> filteredList;
    private AssignmentTestListAdapter.OnItemClickListener mItemClickListener;
    private AssignmentTestListAdapter.OnEditClickListener mEditClickListener;
    private boolean isFromVideo, isFromReport, isFromAssignment;
    private ArrayList<DatumVideo> folderList;
    private List<DatumVideo> folderListFull;
    private List<DatumVideo> folderFilteredList;
    private String userType;


    public AssignmentTestListAdapter(Context context, ArrayList<Data> assignmentTestList, boolean isFromAssignment, boolean isFromReport) {
        this.context = context;
        this.assignmentTestList = assignmentTestList;
        this.isFromReport = isFromReport;
        this.isFromAssignment = isFromAssignment;
        dataListFull = new ArrayList<>(assignmentTestList);
        filteredList = new ArrayList<>(assignmentTestList);
    }

    public AssignmentTestListAdapter(Context context, ArrayList<DatumVideo> folderList, boolean isFromVideo) {
        this.context = context;
        this.folderList = folderList;
        this.isFromVideo = isFromVideo;
        folderListFull = new ArrayList<>(folderList);
        folderFilteredList = new ArrayList<>(folderList);
    }


    @Override
    public AssignmentTestListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        userType = PreferenceHandler.readString(context, PreferenceHandler.USER_TYPE, "");
        if (isFromVideo) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_folder_test_list, parent, false);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assignment_list, parent, false);
        }
        return new AssignmentTestListAdapter.ViewHolder(v);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final AssignmentTestListAdapter.ViewHolder holder, final int position) {
        if (isFromVideo) {
            holder.tvTopicName.setText(folderList.get(position).getFolderName());
            if (folderList.get(position).isNewItem()) {
                holder.new_item_dot.setVisibility(View.VISIBLE);
            } else {
                holder.new_item_dot.setVisibility(View.GONE);
            }
            boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
            Log.d("TAG", "setupUI: " + isAdmn + " : ");
            if (isAdmn) {
                holder.update_folder.setVisibility(GONE);
                holder.threeDotsLay.setVisibility(View.VISIBLE);
                /*if (userType.equalsIgnoreCase("organisation")) {
                    holder.threeDotsLay.setVisibility(View.VISIBLE);
                } else if (userType.equalsIgnoreCase("faculty")) {
                    holder.threeDotsLay.setVisibility(GONE);
                } else {
                    holder.threeDotsLay.setVisibility(GONE);
                }*/
            } else {
                holder.update_folder.setVisibility(GONE);
                holder.threeDotsLay.setVisibility(GONE);
            }
            try {
                holder.update_folder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        mEditClickListener.onEditClick(position);
                    }
                });
                holder.threeDotsLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditClickListener.onEditClick(position, holder.threeDotsLay);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            holder.tvTopicName.setText(assignmentTestList.get(position).getTopic());
            holder.tvAssignTime.setText(assignmentTestList.get(position).getSubmitTime());
            holder.tvAssignDate.setText(Utilities.getFormattedDate(assignmentTestList.get(position).getSubmitDate(), false) + ",");

            if (isFromReport) {
                if (assignmentTestList.get(position).getBatch() != null) {
                    holder.tvBatchName.setVisibility(View.VISIBLE);
                    holder.tvBatchName.setText(assignmentTestList.get(position).getBatch().getBatchName());
                }
                holder.threeDotsLay.setVisibility(GONE);
            } else {
                if (!isFromAssignment) {
                    holder.tv_objective_subjective.setVisibility(View.VISIBLE);
                    if (assignmentTestList.get(position).getTest_type() == 2)
                        holder.tv_objective_subjective.setText(context.getString(R.string.objective));
                    else holder.tv_objective_subjective.setText(context.getString(R.string.subjective));
                }
                if (!isFromAssignment && assignmentTestList.get(position).getIs_locked() == 1) {
                    holder.imgAssignmentIcon.setVisibility(View.INVISIBLE);
                    holder.layout_locked_item.setVisibility(View.VISIBLE);
                    holder.imgRightArrowIcon.setVisibility(View.VISIBLE);
                    holder.tvSubmitted.setVisibility(GONE);

                } else {
                    holder.imgAssignmentIcon.setVisibility(View.VISIBLE);
                    holder.layout_locked_item.setVisibility(View.GONE);
                    holder.tvBatchName.setVisibility(View.GONE);
                    holder.imgRightArrowIcon.setVisibility(View.GONE);

                    if (assignmentTestList.get(position).getStatus() == 1) {
                        if (userType.equalsIgnoreCase("organisation") || userType.equalsIgnoreCase("faculty")) {
                            holder.tvSubmitted.setVisibility(GONE);
                        } else {
                            holder.tvSubmitted.setVisibility(View.VISIBLE);
                            holder.tvSubmitted.setText(context.getString(R.string.txt_submitted));
                            holder.tvSubmitted.setTextColor(ContextCompat.getColor(context, R.color.green));

                        }

                    } else if (assignmentTestList.get(position).getStatus() == 0) {
                        if (userType.equalsIgnoreCase("organisation") || userType.equalsIgnoreCase("faculty")) {
                            holder.tvSubmitted.setVisibility(GONE);
                        } else {
                            holder.tvSubmitted.setVisibility(View.VISIBLE);
                            holder.tvSubmitted.setText(context.getString(R.string.pending));
                            holder.tvSubmitted.setTextColor(ContextCompat.getColor(context, R.color.red));
                        }

                    }
                    if (userType.equalsIgnoreCase("organisation") || userType.equalsIgnoreCase("faculty")) {
                        holder.threeDotsLay.setVisibility(View.VISIBLE);
                    } else {
                        holder.threeDotsLay.setVisibility(GONE);
                    }
                }

                if (assignmentTestList.get(position).isNewItem()) {
                    holder.new_item_dot.setVisibility(View.VISIBLE);
                } else {
                    holder.new_item_dot.setVisibility(View.GONE);
                }
                try {
                    if (userType.equalsIgnoreCase("organisation") || userType.equalsIgnoreCase("faculty")) {
                        holder.threeDotsLay.setVisibility(View.VISIBLE);
                    } else {
                        holder.threeDotsLay.setVisibility(GONE);
                    }
                    holder.threeDotsLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mEditClickListener.onEditClick(position, holder.threeDotsLay);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                mItemClickListener.onItemClick(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (isFromVideo) {
            return folderList.size();
        } else {
            return assignmentTestList.size();
        }

    }

    public void setUpdatedList(ArrayList<DatumVideo> folderList, boolean isFromVideo) {
        this.folderList = new ArrayList<>(folderList);
        folderListFull = new ArrayList<>(folderList);
        folderFilteredList = new ArrayList<>(folderList);
        this.isFromVideo = isFromVideo;
        notifyDataSetChanged();

    }

    public void setUpdatedDataList(ArrayList<Data> folderList, boolean isFromAssignment) {
        this.assignmentTestList = folderList;
        this.isFromAssignment = isFromAssignment;
        assignmentTestList = new ArrayList<>(folderList);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName, tvAssignTime, tvAssignDate, tvBatchName, tvSubmitted, tv_duration, tv_objective_subjective;
        ImageView imgAssignmentIcon, imgRightArrowIcon, new_item_dot, update_folder;
        View layout_locked_item;
        RelativeLayout threeDotsLay;

        ViewHolder(View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tv_topic_name);
            tvAssignTime = itemView.findViewById(R.id.tv_assign_time);
            tvAssignDate = itemView.findViewById(R.id.tv_assign_date);
            imgAssignmentIcon = itemView.findViewById(R.id.img_assignment_icon);
            imgRightArrowIcon = itemView.findViewById(R.id.img_right_arrow_icon);
            tvBatchName = itemView.findViewById(R.id.tv_batch_name);
            new_item_dot = itemView.findViewById(R.id.new_item_dot);
            tvSubmitted = itemView.findViewById(R.id.tv_submitted);
            tv_duration = itemView.findViewById(R.id.tv_duration);
            layout_locked_item = itemView.findViewById(R.id.layout_locked_item);
            tv_objective_subjective = itemView.findViewById(R.id.tv_objective_subjective);
            update_folder = itemView.findViewById(R.id.update_folder);
            threeDotsLay = itemView.findViewById(R.id.threeDotsLay);
        }
    }


    /**
     * Method to intialize click listener for items from activity
     *
     * @param mItemClickListener listener
     */
    public void SetOnItemClickListener(final AssignmentTestListAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void SetOnEditClickListener(final AssignmentTestListAdapter.OnEditClickListener mItemClickListener) {
        this.mEditClickListener = mItemClickListener;
    }

    /**
     * Click listener interface
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnEditClickListener {
        void onEditClick(int position, RelativeLayout threeDotsLay);
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
            if (isFromVideo) {
                List<DatumVideo> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(folderListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (DatumVideo item : folderListFull) {
                        if (item.getFolderName().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                results = new FilterResults();
                results.values = filteredList;
            } else {
                List<Data> filteredList = new ArrayList<>();
                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(dataListFull);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    for (Data item : dataListFull) {
                        if (item.getTopic().toLowerCase().contains(filterPattern)) {
                            filteredList.add(item);
                        }
                    }
                }
                results = new FilterResults();
                results.values = filteredList;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (isFromVideo) {
                folderList.clear();
                folderFilteredList.clear();
                folderList.addAll((List) results.values);
                notifyDataSetChanged();
                folderFilteredList = (List) results.values;
            } else {
                assignmentTestList.clear();
                filteredList.clear();
                assignmentTestList.addAll((List) results.values);
                notifyDataSetChanged();
                filteredList = (List) results.values;
            }

        }
    };


    public int getFilteredListSize() {
        return filteredList.size();
    }

    public ArrayList<DatumVideo> removeAt(int position) {
        folderList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, folderList.size());
        return folderList;
    }

    public ArrayList<Data> removeAssignmentAt(int position) {
        assignmentTestList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, assignmentTestList.size());
        return assignmentTestList;
    }
}
