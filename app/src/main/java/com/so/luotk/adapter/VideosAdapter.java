package com.so.luotk.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.so.luotk.R;
import com.so.luotk.models.video.DatumVideo;
import com.so.luotk.utils.PreferenceHandler;


import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.ViewHolder> implements Filterable {
    private List<DatumVideo> videoList;
    private final Context context;
    private VideosAdapter.OnItemClickListener mItemClickListener;
    private VideosAdapter.OnEditClickListener mEditClickListener;
    private List<DatumVideo> dataListFull;
    private final String isFrom;


    public VideosAdapter(Context context, List<DatumVideo> videoList, String isFrom) {
        this.context = context;
        this.videoList = videoList;
        this.isFrom = isFrom;
        dataListFull = new ArrayList<>(videoList);

    }

    @Override
    public VideosAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_list, parent, false);
        return new VideosAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final VideosAdapter.ViewHolder holder, final int position) {
        try {
            if (videoList.get(position).getType() != null) {
//                if (PreferenceHandler.readString(context, PreferenceHandler.FOLDER_IN_FOLDER, "0").equals("1")){
                    Log.d("TAG", "getFolder: ");
                    if (videoList.get(position).getType().equalsIgnoreCase("folder")) {
                        Log.d("TAG", "getFolderEqual: ");
                        holder.folder_lay.setVisibility(VISIBLE);
                        holder.file_lay.setVisibility(GONE);
                        holder.locked_item_layout.setVisibility(GONE);
                        if (videoList.get(position).isNewItem()) {
                            holder.new_item_dot.setVisibility(View.VISIBLE);
                        } else {
                            holder.new_item_dot.setVisibility(View.GONE);
                        }
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (isAdmn) {
                            holder.update_folder_.setVisibility(GONE);
                            holder.threeDotsLayFolder.setVisibility(View.VISIBLE);
                        } else {
                            holder.update_folder_.setVisibility(GONE);
                            holder.threeDotsLayFolder.setVisibility(GONE);
                        }
                        holder.tv_material_name.setText(videoList.get(position).getFolderName());
                        holder.tv_material_name.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mItemClickListener.onItemClick(position);
                            }
                        });
                        holder.threeDotsLayFolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEditClickListener.onEditClick(position, holder.threeDotsLayFolder);
                            }
                        });
                    } else {
                        Log.d("TAG", "getFolderNotEqual: ");
                        holder.folder_lay.setVisibility(GONE);
                        holder.file_lay.setVisibility(VISIBLE);
                        holder.tvTopicName.setText(videoList.get(position).getTitle());
                        holder.locked_item_layout.setVisibility(View.GONE);
                        Glide.with(context).load(videoList.get(position).getThumb()).into(holder.imgVideoIcon);
                        holder.layout_img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mItemClickListener.onItemClick(position);
                            }
                        });
                        holder.tvTopicName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mItemClickListener.onItemClick(position);
                            }
                        });
                        holder.threeDotsLay.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mEditClickListener.onEditClick(position, holder.threeDotsLay);
                            }
                        });

                        if (videoList.get(position).getIs_locked() == 1) {
                            holder.locked_item_layout.setVisibility(View.VISIBLE);
                        } else holder.locked_item_layout.setVisibility(View.GONE);
                        if (isFrom.equals("player")) {

                            if (videoList.get(position).isPlaying()) {
                                holder.playing_now.setVisibility(View.VISIBLE);
                                holder.itemLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorLightBlueGray));
                            } else {
                                holder.playing_now.setVisibility(View.GONE);
                                holder.itemLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_back));
                            }
                            holder.tvTopicName.setTextColor(ContextCompat.getColor(context, R.color.black));
                        }
                        boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                        if (isAdmn) {
                            holder.update_folder.setVisibility(GONE);
                            holder.threeDotsLay.setVisibility(VISIBLE);
                        } else {
                            holder.update_folder.setVisibility(GONE);
                            holder.threeDotsLay.setVisibility(GONE);
                        }
                    }
               /* } else {
                    Log.d("TAG", "getFolderElse: ");
                    holder.folder_lay.setVisibility(GONE);
                    holder.file_lay.setVisibility(VISIBLE);
                    holder.tvTopicName.setText(videoList.get(position).getTitle());
                    holder.locked_item_layout.setVisibility(View.GONE);
                    Glide.with(context).load(videoList.get(position).getThumb()).into(holder.imgVideoIcon);
                    holder.layout_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemClickListener.onItemClick(position);
                        }
                    });
                    holder.tvTopicName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mItemClickListener.onItemClick(position);
                        }
                    });
                    holder.threeDotsLay.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mEditClickListener.onEditClick(position, holder.threeDotsLay);
                        }
                    });

                    if (videoList.get(position).getIs_locked() == 1) {
                        holder.locked_item_layout.setVisibility(View.VISIBLE);
                    } else holder.locked_item_layout.setVisibility(View.GONE);
                    if (isFrom.equals("player")) {

                        if (videoList.get(position).isPlaying()) {
                            holder.playing_now.setVisibility(View.VISIBLE);
                            holder.itemLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorLightBlueGray));
                        } else {
                            holder.playing_now.setVisibility(View.GONE);
                            holder.itemLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_back));
                        }
                        holder.tvTopicName.setTextColor(ContextCompat.getColor(context, R.color.black));
                    }
                    boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                    if (isAdmn) {
                        holder.update_folder.setVisibility(GONE);
                        holder.threeDotsLay.setVisibility(VISIBLE);
                    } else {
                        holder.update_folder.setVisibility(GONE);
                        holder.threeDotsLay.setVisibility(GONE);
                    }
                }*/
            } else {
                Log.d("TAG", "getTypeNul: ");
                holder.folder_lay.setVisibility(GONE);
                holder.file_lay.setVisibility(VISIBLE);
                holder.tvTopicName.setText(videoList.get(position).getTitle());
                holder.locked_item_layout.setVisibility(View.GONE);
                Glide.with(context).load(videoList.get(position).getThumb()).into(holder.imgVideoIcon);
                holder.layout_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(position);
                    }
                });
                holder.tvTopicName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mItemClickListener.onItemClick(position);
                    }
                });
                holder.threeDotsLay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mEditClickListener.onEditClick(position, holder.threeDotsLay);
                    }
                });

                if (videoList.get(position).getIs_locked() == 1) {
                    holder.locked_item_layout.setVisibility(View.VISIBLE);
                } else holder.locked_item_layout.setVisibility(View.GONE);
                if (isFrom.equals("player")) {

                    if (videoList.get(position).isPlaying()) {
                        holder.playing_now.setVisibility(View.VISIBLE);
                        holder.itemLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorLightBlueGray));
                    } else {
                        holder.playing_now.setVisibility(View.GONE);
                        holder.itemLayout.setCardBackgroundColor(ContextCompat.getColor(context, R.color.card_back));
                    }
                    holder.tvTopicName.setTextColor(ContextCompat.getColor(context, R.color.black));
                }
                boolean isAdmn = PreferenceHandler.readBoolean(context, PreferenceHandler.ADMIN_LOGGED_IN, false);
                if (isAdmn) {
                    holder.update_folder.setVisibility(GONE);
                    holder.threeDotsLay.setVisibility(VISIBLE);
                } else {
                    holder.update_folder.setVisibility(GONE);
                    holder.threeDotsLay.setVisibility(GONE);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        return videoList.size();

    }


    public void setUpdatedList(List<DatumVideo> videoList) {
        this.videoList = new ArrayList<>(videoList);
        dataListFull = new ArrayList<>(videoList);
        notifyDataSetChanged();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTopicName, playing_now;
        private final CardView itemLayout;
        private final CardView layout_img;
        private final ImageView imgVideoIcon;
        private final ImageView update_folder;
        private final ImageView update_folder_;
        private final View locked_item_layout;
        private final RelativeLayout threeDotsLay, threeDotsLayFolder, file_lay;
        private final LinearLayout folder_lay;
        private final CircleImageView new_item_dot;
        private final TextView tv_material_name;

        ViewHolder(View itemView) {
            super(itemView);
            tvTopicName = itemView.findViewById(R.id.tv_topic_name);
            imgVideoIcon = itemView.findViewById(R.id.img_video_icon);
            itemLayout = itemView.findViewById(R.id.item_layout);
            layout_img = itemView.findViewById(R.id.layout_img);
            playing_now = itemView.findViewById(R.id.tv_playing_now);
            locked_item_layout = itemView.findViewById(R.id.layout_locked_item);
            update_folder = itemView.findViewById(R.id.update_folder);
            threeDotsLay = itemView.findViewById(R.id.threeDotsLay);
            update_folder_ = itemView.findViewById(R.id.update_folder_);
            threeDotsLayFolder = itemView.findViewById(R.id.threeDotsLayFolder);
            folder_lay = itemView.findViewById(R.id.folder_lay);
            file_lay = itemView.findViewById(R.id.file_lay);
            new_item_dot = itemView.findViewById(R.id.new_item_dot);
            tv_material_name = itemView.findViewById(R.id.tv_material_name);
        }
    }


    /**
     * Method to intialize click listener for items from activity
     *
     * @param mItemClickListener listener
     */
    public void SetOnItemClickListener(final VideosAdapter.OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }
    public void SetOnEditClickListener(final VideosAdapter.OnEditClickListener mEditClickListener) {
        this.mEditClickListener = mEditClickListener;
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
            List<DatumVideo> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(dataListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (DatumVideo item : dataListFull) {
                    if (item.getTitle().toLowerCase().contains(filterPattern)) {
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
            videoList.clear();
            videoList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public List<DatumVideo> removeAt(int position) {
        videoList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, videoList.size());
        return videoList;
    }
}

