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
import com.so.luotk.models.videoData.SpeedModel;

import java.util.ArrayList;
import java.util.List;

public class VideoSpeedAdapter extends RecyclerView.Adapter<VideoSpeedAdapter.ViewHolder> {
    private List<SpeedModel> speedList;
    private final Context context;
    private String selectedSpeed;
    private OnItemClickListener mItemClickListener;
    private List<SpeedModel> dataListFull;
    private YouTubePlayer youTubePlayer;

    public VideoSpeedAdapter(ArrayList<SpeedModel> speedList, EasyVideoPlayer easyVideoPlayer, YouTubePlayer youTubePlayerViewThis, String selectedSpeed) {
        this.context = easyVideoPlayer;
        this.speedList = speedList;
        this.selectedSpeed = selectedSpeed;
        this.youTubePlayer = youTubePlayerViewThis;
        dataListFull = new ArrayList<>(speedList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.id_select_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        SpeedModel speedModel = speedList.get(position);
        holder.radioButton.setText(speedModel.getName());
        if (speedModel.isSelected()) {
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
        return speedList.size();

    }


    public void setUpdatedList(List<SpeedModel> videoList) {
        this.speedList = new ArrayList<>(videoList);
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

    public List<SpeedModel> removeAt(int position) {
        speedList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, speedList.size());
        return speedList;
    }
}
