package com.so.luotk.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.so.luotk.R;
import com.so.luotk.activities.CustomYouTubeViewActivity;
import com.so.luotk.activities.EasyVideoPlayer;
import com.so.luotk.activities.PlayerYoutubeActivity;
import com.so.luotk.activities.SingleIframeVideo;
import com.so.luotk.activities.SingleVideoYoutube;
import com.so.luotk.databinding.ItemMoreOptionsBinding;
import com.so.luotk.databinding.ItemPoupularVideoListBinding;
import com.so.luotk.models.video.DatumVideo;
import com.so.luotk.utils.PreferenceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PopularVideosAdapter extends RecyclerView.Adapter<PopularVideosAdapter.ViewHolder> {
    private List<DatumVideo> popularVideoResultList;
    private Context context;
    private String videoId = "";
    private long mLastClickTime=0;
    public PopularVideosAdapter() {
        popularVideoResultList = new ArrayList<>();
    }

    @NonNull
    @Override
    public PopularVideosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new PopularVideosAdapter.ViewHolder(ItemPoupularVideoListBinding.inflate(LayoutInflater.from(context), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PopularVideosAdapter.ViewHolder holder, int position) {
        if (popularVideoResultList.size() > 0) {
            DatumVideo result = popularVideoResultList.get(position);
            if (result.getTitle() != null)
                holder.binding.videoName.setText(result.getTitle());
            Glide.with(context).load(result.getThumb()).placeholder(R.drawable.no_image).error(R.drawable.no_image).into(holder.binding.videoThumbnail);
            holder.binding.cardLay.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                videoId = "";
                Log.d("gagag", "onBindViewHolder: " + popularVideoResultList.get(position).getVideoUrl());
                getYoutubeId(popularVideoResultList.get(position).getVideoUrl());
                if (PreferenceHandler.readString(context, PreferenceHandler.YOUTUBE_SPEED, "0").equals("4")) {
                    Intent intent = new Intent(context, EasyVideoPlayer.class);
                    intent.putExtra("currentposition", position);
                    intent.putExtra(PreferenceHandler.IS_FROM, "popular");
                    intent.putExtra("currentTime", 0L);
                    intent.putExtra("quality", "1");
                    intent.putExtra("videoID", popularVideoResultList.get(position).getId());
                    if (!videoId.isEmpty()) {
                        intent.putExtra("VideoLink", videoId);
                    } else {
                        if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                        }

                    }
                    context.startActivity(intent);
                } else if (PreferenceHandler.readString(context, PreferenceHandler.YOUTUBE_SPEED, "0").equals("3")) {
                    Intent intent = new Intent(context, PlayerYoutubeActivity.class);
                    intent.putExtra("currentposition", position);
                    intent.putExtra(PreferenceHandler.IS_FROM, "popular");
                    intent.putExtra("currentTime", 0L);
                    intent.putExtra("quality", "1");
                    intent.putExtra("videoID", popularVideoResultList.get(position).getId());
                    if (!videoId.isEmpty()) {
                        intent.putExtra("VideoLink", videoId);
                    } else {
                        if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                        }

                    }
                    context.startActivity(intent);
                } else if (PreferenceHandler.readString(context, PreferenceHandler.YOUTUBE_SPEED, "0").equals("2")) {
                    Intent intent = new Intent(context, SingleVideoYoutube.class);
                    intent.putExtra("currentposition", position);
                    intent.putExtra(PreferenceHandler.IS_FROM, "popular");
                    intent.putExtra("videoID", popularVideoResultList.get(position).getId());
                    if (!videoId.isEmpty()) {
                        intent.putExtra("VideoLink", videoId);
                    } else {
                        if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                        }

                    }
                    context.startActivity(intent);
                } else if (PreferenceHandler.readString(context, PreferenceHandler.YOUTUBE_SPEED, "0").equals("1")) {
                    Intent intent = new Intent(context, SingleIframeVideo.class);
                    intent.putExtra("currentposition", position);
                    intent.putExtra(PreferenceHandler.IS_FROM, "popular");
                    intent.putExtra("videoID", popularVideoResultList.get(position).getId());
                    if (!videoId.isEmpty()) {
                        intent.putExtra("VideoLink", videoId);
                    } else {
                        if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                        }

                    }
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, CustomYouTubeViewActivity.class);
                    if (!videoId.isEmpty()) {
                        intent.putExtra("VideoLink", videoId);
                    } else {
                        if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", ""));
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/shorts")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("shorts/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        }  else if (popularVideoResultList.get(position).getVideoUrl().contains("https://www.youtube.com/live")){

                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else if (popularVideoResultList.get(position).getVideoUrl().contains("https://youtube.com/live")){
                            String[] splitVideo = popularVideoResultList.get(position).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                intent.putExtra("VideoLink", splitU[1]);
                            } else {
                                String[] splitUrl = popularVideoResultList.get(position).getVideoUrl().split("live/");
                                intent.putExtra("VideoLink", splitUrl[1]);
                            }
                        } else {
                            intent.putExtra("VideoLink", popularVideoResultList.get(position).getVideoUrl().replace("https://youtu.be/", ""));
                        }

                    }
                    context.startActivity(intent);
                }

            });
        }

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
    public void updateList(List<DatumVideo> popularVideoResultList) {
        this.popularVideoResultList = popularVideoResultList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (popularVideoResultList == null)
            return 0;
        else if (popularVideoResultList.size() <= 8)
            return popularVideoResultList.size() ;
        else return 8;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemPoupularVideoListBinding binding;
        ItemMoreOptionsBinding footerbinding;

        public ViewHolder(@NonNull ItemPoupularVideoListBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public ViewHolder(@NonNull ItemMoreOptionsBinding binding) {
            super(binding.getRoot());
            this.footerbinding = binding;
        }
    }
}
