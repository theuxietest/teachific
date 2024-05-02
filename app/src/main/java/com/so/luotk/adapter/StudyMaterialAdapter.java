package com.so.luotk.adapter;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.so.luotk.R;
import com.so.luotk.models.newmodels.study.Datum;
import com.so.luotk.utils.PreferenceHandler;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class StudyMaterialAdapter extends RecyclerView.Adapter<StudyMaterialAdapter.ViewHolder> {
    private final Context context;
    private List<Datum> studyMaterialList;
    private StudyMaterialAdapter.OnItemClickListener mItemClickListener;
    private StudyMaterialAdapter.OnEditClickListener mEditClickListener;
    private final String isFrom;

    public StudyMaterialAdapter(Context context, List<Datum> dataList, String isFrom) {
        this.context = context;
        this.studyMaterialList = dataList;
        this.isFrom = isFrom;
    }

    @Override
    public StudyMaterialAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_study_material_list, parent, false);
        return new StudyMaterialAdapter.ViewHolder(v);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(final StudyMaterialAdapter.ViewHolder holder, final int position) {
        holder.tvTopicName.setText(studyMaterialList.get(position).getName());
        if (isFrom.equals("course")) {
            if (studyMaterialList.get(position).getIs_locked() == 1) {
                holder.imgIcon.setVisibility(View.INVISIBLE);
                holder.layout_locked_item.setVisibility(View.VISIBLE);
            } else {
                holder.imgIcon.setVisibility(View.VISIBLE);
                holder.layout_locked_item.setVisibility(View.GONE);
            }
        }
        if (studyMaterialList.get(position).isNewItem()) {
            holder.new_item_dot.setVisibility(View.VISIBLE);
        } else {
            holder.new_item_dot.setVisibility(View.GONE);
        }
        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
        Log.d("TAG", "setupUI: " + isAdmn + " : ");
        if (isAdmn) {
            holder.update_folder.setVisibility(GONE);
            holder.threeDotsLay.setVisibility(View.VISIBLE);
        } else {
            holder.update_folder.setVisibility(GONE);
            holder.threeDotsLay.setVisibility(GONE);
        }
        try {
            Log.d("TAG", "onBindViewHolder: " + studyMaterialList.get(position).getType());
            if (studyMaterialList.get(position).getType().equalsIgnoreCase("folder")) {
                holder.imgIcon.setImageResource(R.drawable.folder_icon);
            } else if (studyMaterialList.get(position).getType().equalsIgnoreCase("link")) {
                holder.imgIcon.setImageResource(R.drawable.ic_link);
            } else if (studyMaterialList.get(position).getType().equalsIgnoreCase("file")
                    && studyMaterialList.get(position).getContent().size() > 0 && studyMaterialList.get(position).getContent().get(0).endsWith("doc") ||
                    studyMaterialList.get(position).getContent().get(0).endsWith("docx")) {
                holder.imgIcon.setImageResource(R.drawable.ic_ppt);
            } else if (studyMaterialList.get(position).getType().equalsIgnoreCase("file") && studyMaterialList.get(position).getContent().get(0).endsWith("ppt") || studyMaterialList.get(position).getContent().get(0).endsWith("pptx")) {
                holder.imgIcon.setImageResource(R.drawable.ic_real_ppt);
            } else if (studyMaterialList.get(position).getType().equalsIgnoreCase("file") && studyMaterialList.get(position).getContent().get(0).contains("pdf")) {
                holder.imgIcon.setImageResource(R.drawable.ic_pdf);
            } else/* (studyMaterialList.get(position).getType().equalsIgnoreCase("file") && studyMaterialList.get(position).getContent().get(0).endsWith("jpg") || studyMaterialList.get(position).getContent().get(0).endsWith("jpeg")
                    || studyMaterialList.get(position).getContent().get(0).endsWith("png")) */ {
                holder.imgIcon.setImageResource(R.drawable.ic_img);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position);
            }
        });
        try {
            /*holder.update_folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mEditClickListener.onEditClick(position, holder.threeDotsLay);
                }
            });*/
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

    public void updateList(List<Datum> studyMaterialList) {
       this.studyMaterialList = new ArrayList<>(studyMaterialList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return studyMaterialList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName;
        ImageView new_item_dot, imgIcon, update_folder;
        View layout_locked_item;
        RelativeLayout threeDotsLay;

        ViewHolder(View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tv_material_name);
            new_item_dot = itemView.findViewById(R.id.new_item_dot);
            imgIcon = itemView.findViewById(R.id.img_icon);
            layout_locked_item = itemView.findViewById(R.id.layout_locked_item);
            update_folder = itemView.findViewById(R.id.update_folder);
            threeDotsLay = itemView.findViewById(R.id.threeDotsLay);
        }
    }


    /**
     * Method to intialize click listener for items from activity
     *
     * @param mItemClickListener listener
     */
    public void SetOnItemClickListener(final StudyMaterialAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    public void SetOnEditClickListener(final StudyMaterialAdapter.OnEditClickListener mEditClickListener) {
        this.mEditClickListener = mEditClickListener;
    }
    /**
     * Click listener interface
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public interface OnEditClickListener {
        void onEditClick(int position, RelativeLayout imageView);
    }

    public List<Datum> removeAt(int position) {
        studyMaterialList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, studyMaterialList.size());
        return studyMaterialList;
    }
}
