package com.so.luotk.adapter.adminrole;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.so.luotk.R;
import com.so.luotk.activities.CustomYouTubeViewActivity;
import com.so.luotk.activities.EasyVideoPlayer;
import com.so.luotk.activities.PlayerYoutubeActivity;
import com.so.luotk.activities.SingleIframeVideo;
import com.so.luotk.activities.SingleVideoYoutube;
import com.so.luotk.models.newmodels.YoutubeVideoModel;
import com.so.luotk.utils.PreferenceHandler;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class YouTubeHelpVideoAdapter extends RecyclerView.Adapter<YouTubeHelpVideoAdapter.ViewHolder>{
    private final ArrayList<YoutubeVideoModel> listdata;
    private final Context mContext;
    private String videoId = "";
    private long mLastClickTime=0;

    // RecyclerView recyclerView;
    public YouTubeHelpVideoAdapter(Context context, ArrayList<YoutubeVideoModel> listdata) {
        this.listdata = listdata;
        this.mContext = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.youtube_video_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final YoutubeVideoModel myListData = listdata.get(position);
        holder.video_title.setText(myListData.getDescription());
        String urlYoutubeImage = "https://img.youtube.com/vi/" + myListData.getVideoCode() +"/0.jpg";

        Glide.with(mContext).load(urlYoutubeImage).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.youtube_img);
        holder.mainLayout.setOnClickListener(v -> {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            videoId = "";
            getYoutubeId(myListData.getVideoUrl());
            if (PreferenceHandler.readString(mContext, PreferenceHandler.YOUTUBE_SPEED, "0").equals("4")) {
                Intent intent = new Intent(mContext, EasyVideoPlayer.class);
                intent.putExtra("currentposition", position);
                intent.putExtra(PreferenceHandler.IS_FROM, "youtubeOwm");
                intent.putExtra("currentTime", 0L);
                intent.putExtra("quality", "1");
                intent.putExtra("videoID", myListData.getVideoUrl());
                if (!videoId.isEmpty()) {
                    intent.putExtra("VideoLink", videoId);
                } else {
                    if (myListData.getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                    } else if (myListData.getVideoUrl().contains("https://www.youtube.com/shorts")){
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/shorts/", ""));
                    } else {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://youtu.be/", ""));
                    }

                }
                mContext.startActivity(intent);
            } else if (PreferenceHandler.readString(mContext, PreferenceHandler.YOUTUBE_SPEED, "0").equals("3")) {
                Intent intent = new Intent(mContext, PlayerYoutubeActivity.class);
                intent.putExtra("currentposition", position);
                intent.putExtra(PreferenceHandler.IS_FROM, "youtubeOwm");
                intent.putExtra("currentTime", 0L);
                intent.putExtra("quality", "1");
                intent.putExtra("videoID", myListData.getVideoUrl());
                if (!videoId.isEmpty()) {
                    intent.putExtra("VideoLink", videoId);
                } else {
                    if (myListData.getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                    } else if (myListData.getVideoUrl().contains("https://www.youtube.com/shorts")){
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/shorts/", ""));
                    } else {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://youtu.be/", ""));
                    }

                }
                mContext.startActivity(intent);
            } else if (PreferenceHandler.readString(mContext, PreferenceHandler.YOUTUBE_SPEED, "0").equals("2")) {
                Intent intent = new Intent(mContext, SingleVideoYoutube.class);
                intent.putExtra("currentposition", position);
                intent.putExtra(PreferenceHandler.IS_FROM, "youtubeOwm");
                intent.putExtra("videoID", myListData.getVideoUrl());
                if (!videoId.isEmpty()) {
                    intent.putExtra("VideoLink", videoId);
                } else {
                    if (myListData.getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                    } else if (myListData.getVideoUrl().contains("https://www.youtube.com/shorts")){
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/shorts/", ""));
                    } else {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://youtu.be/", ""));
                    }

                }
                mContext.startActivity(intent);
            } else if (PreferenceHandler.readString(mContext, PreferenceHandler.YOUTUBE_SPEED, "0").equals("1")) {
                Intent intent = new Intent(mContext, SingleIframeVideo.class);
                intent.putExtra("currentposition", position);
                intent.putExtra(PreferenceHandler.IS_FROM, "youtubeOwm");
                intent.putExtra("videoID", myListData.getVideoUrl());
                if (!videoId.isEmpty()) {
                    intent.putExtra("VideoLink", videoId);
                } else {
                    if (myListData.getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                    } else if (myListData.getVideoUrl().contains("https://www.youtube.com/shorts")){
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/shorts/", ""));
                    } else {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://youtu.be/", ""));
                    }

                }
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, CustomYouTubeViewActivity.class);
                if (!videoId.isEmpty()) {
                    intent.putExtra("VideoLink", videoId);
                } else {
                    if (myListData.getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                    } else if (myListData.getVideoUrl().contains("https://www.youtube.com/shorts")){
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://www.youtube.com/shorts/", ""));
                    } else {
                        intent.putExtra("VideoLink", myListData.getVideoUrl().replace("https://youtu.be/", ""));
                    }

                }
                mContext.startActivity(intent);

            }

        });

       /* Glide.with(mContext).load(urlYoutubeImage).centerCrop().into(holder.youtube_img);
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        videoId = "";
        extractYTId(myListData.getVideoUrl());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, CustomYouTubeViewActivity.class);
                intent.putExtra("VideoLink", myListData.getVideoCode());
                mContext.startActivity(intent);
//                Toast.makeText(view.getContext(),"click on item: "+myListData.getDescription(),Toast.LENGTH_LONG).show();
            }
        });*/
    }

    public String extractYTId(String ytUrl) {
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);
        if (matcher.find()) {
            videoId = matcher.group();
        }
        return videoId;
    }

    public static String getYoutubeId(String url) {
        String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }/*from w  w  w.  j a  va  2 s .c om*/
        return null;
    }
    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView youtube_img;
        public TextView video_title;
        public MaterialCardView mainLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.youtube_img = (ImageView) itemView.findViewById(R.id.youtube_img);
            this.video_title = (TextView) itemView.findViewById(R.id.video_title);
            mainLayout = (MaterialCardView) itemView.findViewById(R.id.mainLayout);
        }
    }
}
