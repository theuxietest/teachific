package com.so.luotk.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.so.luotk.R;
import com.so.luotk.activities.EasyVideoPlayer;
import com.so.luotk.models.videoData.QualityModel;

import java.util.ArrayList;
import java.util.List;

public class VideoQualityAdapter extends RecyclerView.Adapter<VideoQualityAdapter.ViewHolder> {
    private List<QualityModel> qualityList;
    private final Context context;
    private String selectedQuality;
    private OnItemClickListener mItemClickListener;
    private List<QualityModel> dataListFull;
    private YouTubePlayer youTubePlayer;

    public VideoQualityAdapter(ArrayList<QualityModel> qualityList, EasyVideoPlayer easyVideoPlayer, YouTubePlayer youTubePlayerViewThis, String selectedQuality) {
        this.context = easyVideoPlayer;
        this.qualityList = qualityList;
        this.selectedQuality = selectedQuality;
        this.youTubePlayer = youTubePlayerViewThis;
        dataListFull = new ArrayList<>(qualityList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.id_select_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        QualityModel qualityModel = qualityList.get(position);
        holder.radioButton.setText(qualityModel.getName());
        if (qualityModel.isSelected()) {
            holder.radioButton.setChecked(true);
        } else {
            holder.radioButton.setChecked(false);
        }
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return qualityList.size();

    }


    public void setUpdatedList(List<QualityModel> videoList) {
        this.qualityList = new ArrayList<>(videoList);
        dataListFull = new ArrayList<>(videoList);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RadioButton radioButton;
        private RelativeLayout relativeLayout;

        ViewHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.radio);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);
        }
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /**
     * Click listener interface
     */
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public List<QualityModel> removeAt(int position) {
        qualityList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, qualityList.size());
        return qualityList;
    }
}
