package com.so.luotk.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.so.luotk.R;
import com.so.luotk.adapter.VideosAdapter;
import com.so.luotk.client.MyClient;
import com.so.luotk.databinding.ActivityNewCustomPlayerBinding;
import com.so.luotk.databinding.DialogUsbConnectedBinding;
import com.so.luotk.models.output.ServiceResponse;
import com.so.luotk.models.video.DatumVideo;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.youtube.player.YouTubePlayer.ErrorReason.UNAUTHORIZED_OVERLAY;

public class NewCustomPlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener, View.OnClickListener {
    private static final String TAG = "CustomPlayerControlActivity";
    private YouTubePlayer mPlayer;
    private View mPlayButtonLayout, playerLayout;
    private TextView mPlayTimeTextView;
    private Handler mHandler = null;
    private SeekBar mSeekBar;
    private YouTubePlayerView youTubePlayerView;
    private ImageView imageViewPlayIcon;
    private String videoId, video_idFrom;
    private Toolbar toolbar;
    private TextView toolbarCustomTitle;

    private ImageView fullscreen_icon, forward_icon;
    int clickCount = 0;
    long startTime;
    private ActivityNewCustomPlayerBinding binding;
    private boolean isFullScreen, isAutoPlay;
    private VideosAdapter videoAdapter;
    private List<DatumVideo> videoList, videoListType;
    private List<String> videoIdsList;
    private int currentPosition = 0, currentMode;
    private BroadcastReceiver broadcast_reciever;
    private Dialog dialog;
    private String id, isFrom;
    private boolean isViewSaved;
    private boolean isOverlay;
    private boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // attaching layout xml
        Utilities.restrictScreenShot(this);
// getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        binding = ActivityNewCustomPlayerBinding.inflate(getLayoutInflater());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this); Utilities.setLocale(this);
        setContentView(binding.getRoot());

        broadcast_reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (intent.getExtras().getBoolean("connected")) {
                    showDisconnectUsbDialog();
                    connected = true;
                }
            }
        };

        currentMode = getResources().getConfiguration().uiMode;
        videoList = new ArrayList<>();
        videoListType = new ArrayList<>();
        videoIdsList = new ArrayList<>();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (getIntent() != null) {
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            video_idFrom = getIntent().getStringExtra("videoID");
            videoId = getIntent().getStringExtra("VideoLink");
            if (isFrom.equalsIgnoreCase("home")) {
                videoList = (List<DatumVideo>) getIntent().getSerializableExtra("videolist");
                currentPosition = getIntent().getIntExtra("currentposition", 0);
            } else {
                videoListType = (List<DatumVideo>) getIntent().getSerializableExtra("videolist");
                for (DatumVideo datumVideo : videoListType) {
                    if (!datumVideo.getType().equalsIgnoreCase("folder")) {
                        videoList.add(datumVideo);
                    }
                }
                for (int i = 0; i < videoList.size(); i++) {
                    if (videoList.get(i).getId().equalsIgnoreCase(video_idFrom)) {
                        currentPosition = i;
                    }
                }
            }
            id = videoList.get(currentPosition).getId();

        }


        setToolbar();
        mHandler = new Handler(Looper.myLooper());

        youTubePlayerView = binding.youtubePlayerView;
        youTubePlayerView.initialize(getString(R.string.api_key_youtube_one)
                + getString(R.string.api_key_youtube_two)
                + getString(R.string.api_key_youtube_three), this);

        mPlayButtonLayout = binding.videoControl;
        imageViewPlayIcon = binding.playVideo;
        imageViewPlayIcon.setImageResource(R.drawable.ic_pause);
        imageViewPlayIcon.setOnClickListener(this);
        mPlayTimeTextView = binding.playTime;
        fullscreen_icon = binding.iconFullScreen;
        mSeekBar = binding.videoSeekbar;
        mSeekBar.setOnSeekBarChangeListener(mVideoSeekBarChangeListener);
        playerLayout = findViewById(R.id.player_layout);
        playerLayout.setOnClickListener(this);
        fullscreen_icon.setOnClickListener(this);
        setUpListAdapter();
        if (videoList != null && videoList.size() > 0)
            getVideoIds();
        binding.switchAutoplay.setOnCheckedChangeListener((buttonView, isChecked) -> {
            isAutoPlay = isChecked;

        });

    }

    private void showDisconnectUsbDialog() {
        DialogUsbConnectedBinding dialogUsbConnectedBinding = DialogUsbConnectedBinding.inflate(LayoutInflater.from(this));
        Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogUsbConnectedBinding.getRoot());
        dialogUsbConnectedBinding.txtGotIt.setOnClickListener(v -> {
            if (!isFinishing()) {
                dialog.dismiss();
                finish();
            }
        });
        try {
            if (!isFinishing())
                dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.setCancelable(false);
    }

    public static boolean isConnected(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        return intent.getExtras().getBoolean("connected");
    }

    public void getVideoIds() {
        int size = videoList.size();
        for (int i = 0; i < size; i++) {
            Log.d("TAG", "getVideoIds: "+videoList.get(i).getVideoUrl());
            if (isFrom.equalsIgnoreCase("home")) {
                String vId = extractYTId(videoList.get(i).getVideoUrl());
                if (vId.isEmpty()) {
                    if (videoList.get(i).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                        vId = videoList.get(i).getVideoUrl().replace("https://www.youtube.com/watch?v=", "");
                    } else if (videoList.get(i).getVideoUrl().contains("https://youtube.com/shorts")){
                        String[] splitVideo = videoList.get(i).getVideoUrl().split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("shorts/");
                            Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                            vId = splitU[1];
                        } else {
                            String[] splitUrl = videoList.get(i).getVideoUrl().split("shorts/");
                            vId = splitUrl[1];
                        }
                    } else if (videoList.get(i).getVideoUrl().contains("https://www.youtube.com/shorts")){
                        String[] splitVideo = videoList.get(i).getVideoUrl().split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("shorts/");
                            Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                            vId = splitU[1];
                        } else {
                            String[] splitUrl = videoList.get(i).getVideoUrl().split("shorts/");
                            vId = splitUrl[1];
                        }
                    } else if (videoList.get(i).getVideoUrl().contains("https://www.youtube.com/live")){
                        String[] splitVideo = videoList.get(i).getVideoUrl().split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("live/");
                            Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                            vId = splitU[1];
                        } else {
                            String[] splitUrl = videoList.get(i).getVideoUrl().split("live/");
                            vId = splitUrl[1];
                        }
                    } else if (videoList.get(i).getVideoUrl().contains("https://youtube.com/live")){
                        String[] splitVideo = videoList.get(i).getVideoUrl().split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("live/");
                            Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                            vId = splitU[1];
                        } else {
                            String[] splitUrl = videoList.get(i).getVideoUrl().split("live/");
                            vId = splitUrl[1];
                        }
                    } else {
                        vId = videoList.get(i).getVideoUrl().replace("https://youtu.be/", "");
                    }
                }
                videoIdsList.add(vId);
            } else {
                if (!videoList.get(i).getType().equalsIgnoreCase("folder")) {
                    String vId = extractYTId(videoList.get(i).getVideoUrl());
                    if (vId.isEmpty()) {
                        if (videoList.get(i).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                            vId = videoList.get(i).getVideoUrl().replace("https://www.youtube.com/watch?v=", "");
                        } else if (videoList.get(i).getVideoUrl().contains("https://youtube.com/shorts")){
                            String[] splitVideo = videoList.get(i).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                                vId = splitU[1];
                            } else {
                                String[] splitUrl = videoList.get(i).getVideoUrl().split("shorts/");
                                vId = splitUrl[1];
                            }
                        } else if (videoList.get(i).getVideoUrl().contains("https://www.youtube.com/shorts")){
                            String[] splitVideo = videoList.get(i).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("shorts/");
                                Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                                vId = splitU[1];
                            } else {
                                String[] splitUrl = videoList.get(i).getVideoUrl().split("shorts/");
                                vId = splitUrl[1];
                            }
                        } else if (videoList.get(i).getVideoUrl().contains("https://youtube.com/live")){
                            String[] splitVideo = videoList.get(i).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                                vId = splitU[1];
                            } else {
                                String[] splitUrl = videoList.get(i).getVideoUrl().split("live/");
                                vId = splitUrl[1];
                            }
                        } else if (videoList.get(i).getVideoUrl().contains("https://www.youtube.com/live")){
                            String[] splitVideo = videoList.get(i).getVideoUrl().split("\\?");
                            if (splitVideo.length > 1) {
                                String[] splitU = splitVideo[0].split("live/");
                                Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                                vId = splitU[1];
                            } else {
                                String[] splitUrl = videoList.get(i).getVideoUrl().split("live/");
                                vId = splitUrl[1];
                            }
                        } else {
                            vId = videoList.get(i).getVideoUrl().replace("https://youtu.be/", "");
                        }
                    }
                    videoIdsList.add(vId);
                }
            }
        }
    }

    private void setUpListAdapter() {
        binding.videosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.videosRecyclerView.smoothScrollToPosition(currentPosition);
        if (videoList != null && !videoList.isEmpty())
            videoAdapter = new VideosAdapter(this, videoList, "player");
        binding.videosRecyclerView.setAdapter(videoAdapter);
        videoAdapter.SetOnItemClickListener(position -> {
            if (!(videoList.get(position).getIs_locked() == 1)) {
                videoId = extractYTId(videoList.get(position).getVideoUrl());
                currentPosition = position;
                videoList.get(position).setPlaying(true);
                int size = videoList.size();
                for (int i = 0; i < size; i++) {
                    if (i != position) {
                        videoList.get(i).setPlaying(false);
                    }
                }
                videoAdapter.notifyDataSetChanged();
                if (videoId.isEmpty()) {
                    if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/watch?v=")) {
                        videoId = videoList.get(position).getVideoUrl().replace("https://www.youtube.com/watch?v=", "");
                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/shorts/")){
                        Log.d(TAG, "setUpListAdapterVvdd: ");
                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("shorts/");
                            Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                            videoId = splitU[1];
                        } else {
                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                            videoId = splitUrl[1];
                        }
                    } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/shorts/")){
                        Log.d(TAG, "setUpListAdapterVvdd: ");
                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("shorts/");
                            Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                            videoId = splitU[1];
                        } else {
                            String[] splitUrl = videoList.get(position).getVideoUrl().split("shorts/");
                            videoId = splitUrl[1];
                        }
                    } else if (videoList.get(position).getVideoUrl().contains("https://youtube.com/live/")){
                        Log.d(TAG, "setUpListAdapterVvdd: ");
                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("live/");
                            Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                            videoId = splitU[1];
                        } else {
                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                            videoId = splitUrl[1];
                        }
                    } else if (videoList.get(position).getVideoUrl().contains("https://www.youtube.com/live/")){
                        Log.d(TAG, "setUpListAdapterVvdd: ");
                        String[] splitVideo = videoList.get(position).getVideoUrl().split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("live/");
                            Log.d(TAG, "setUpListAdapter: " + splitU[1]);
                            videoId = splitU[1];
                        } else {
                            String[] splitUrl = videoList.get(position).getVideoUrl().split("live/");
                            videoId = splitUrl[1];
                        }
                    } else {
                        videoId = videoList.get(position).getVideoUrl().replace("https://youtu.be/", "");
                    }
                }
                if (mPlayer != null && !videoId.isEmpty()) {
                    mPlayer.loadVideo(videoId);
                    mPlayer.play();
                    displayCurrentTime();
                }
            } else Utilities.makeToast(this, "Content is locked");
        });
    }

    public String extractYTId(String ytUrl) {
        String videoId = "";
        String pattern = "(?<=watch\\?v=|/videos/|embed\\/)[^#\\&\\?]*";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(ytUrl);
        if (matcher.find()) {
            videoId = matcher.group();
        }
        return videoId;
    }

    private void setToolbar() {
        toolbar = binding.toolbar;
        toolbarCustomTitle = findViewById(R.id.tv_toolbar_title);
        toolbar.setNavigationIcon(Utilities.setNavigationIconColorBlack(this));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (null == player) return;
        mPlayer = player;
        if (!connected)
            if (!wasRestored)
                if (videoId != null) {
                    try {
                        player.loadVideo(videoId);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

        player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
        mPlayButtonLayout.setVisibility(View.VISIBLE);
        player.play();
        displayCurrentTime();
        // Add listeners to YouTubePlayer instance
        player.setPlayerStateChangeListener(mPlayerStateChangeListener);
        player.setPlaybackEventListener(mPlaybackEventListener);


    }

    YouTubePlayer.PlaybackEventListener mPlaybackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
            Log.e("TAG", "onBuffering: " + arg0);
        }

        @Override
        public void onPaused() {
            Log.e("TAG", "onPaused: ");
           /* if (mPlayer != null)
                seekTime = mPlayer.getCurrentTimeMillis();*/

            mHandler.removeCallbacks(runnable);
        }

        @Override
        public void onPlaying() {
            Log.e("TAG", "onPlaying: ");
            mHandler.postDelayed(runnable, 100);
            displayCurrentTime();


        }

        @Override
        public void onSeekTo(int arg0) {
            //   mHandler.postDelayed(runnable, 100);
        }

        @Override
        public void onStopped() {
            Log.e("TAG", "onStopped: ");
            if (isOverlay && !connected) {
                // mHandler.postDelayed(runnable, 100);
                if (mPlayer != null && !mPlayer.isPlaying())
                    mPlayer.play();
            } else
                mHandler.removeCallbacks(runnable);
        }
    };

    YouTubePlayer.PlayerStateChangeListener mPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
            Log.e("TAG", "onError: " + arg0);
            if (arg0.equals(UNAUTHORIZED_OVERLAY)) {
                isOverlay = true;
            }
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
            Log.e("TAG", "onLoading: ");

        }

        @Override
        public void onVideoEnded() {
            if (!isAutoPlay)
                NewCustomPlayerActivity.this.finish();
            else {
                videoList.get(currentPosition).setPlaying(false);
                if (currentPosition < videoIdsList.size() - 1)
                    currentPosition = currentPosition + 1;
                else if (currentPosition == videoIdsList.size() - 1)
                    currentPosition = 0;
                if (mPlayer != null && !(videoList.get(currentPosition).getIs_locked() == 1)) {
                    mPlayer.loadVideo(videoIdsList.get(currentPosition));
                    mPlayer.play();
                    videoList.get(currentPosition).setPlaying(true);
                    videoAdapter.notifyDataSetChanged();
                    id = videoList.get(currentPosition).getId();
                } else NewCustomPlayerActivity.this.finish();
            }
        }

        @Override
        public void onVideoStarted() {
            displayCurrentTime();
        }
    };

    SeekBar.OnSeekBarChangeListener mVideoSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser && mPlayer != null) {
                long lengthPlayed = (mPlayer.getDurationMillis() * progress) / 100;
                mPlayer.seekToMillis((int) lengthPlayed);
            }


        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {


        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        try {
            registerReceiver(broadcast_reciever, new IntentFilter("android.hardware.usb.action.USB_STATE"));
            if (mPlayer != null && !mPlayer.isPlaying()) {
                mPlayer.play();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinishing() && dialog != null && dialog.isShowing())
            dialog.dismiss();
        unregisterReceiver(broadcast_reciever);
        mHandler.removeCallbacks(runnable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_video:
                try {
                    if (mPlayer != null)
                        if (!mPlayer.isPlaying()) {
                            mPlayer.play();
                            imageViewPlayIcon.setImageResource(R.drawable.ic_pause);
                        } else {
                            mPlayer.pause();
                            imageViewPlayIcon.setImageResource(R.drawable.ic_play_button);
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.icon_full_screen:
                if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    toolbar.setVisibility(View.VISIBLE);
                    fullscreen_icon.setImageResource(R.drawable.ic_full_screen);
                    isFullScreen = false;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    toolbar.setVisibility(View.GONE);
                    fullscreen_icon.setImageResource(R.drawable.ic_exit);
                    isFullScreen = true;
                }
                break;
         /*   case R.id.img_settings:
                openDialog();
                break;*/


        }
    }

/*    private void openDialog() {
        DialogAutoPlayBinding dialogAutoPlayBinding = DialogAutoPlayBinding.inflate(LayoutInflater.from(this));
        Dialog dialog = new Dialog(this);
        dialog.setContentView(dialogAutoPlayBinding.getRoot());
        dialog.show();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_corners_drawable);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
    }*/

    private void displayCurrentTime() {
        if (null == mPlayer) return;
        String formattedTime = formatTime(mPlayer.getDurationMillis() - mPlayer.getCurrentTimeMillis());
        mPlayTimeTextView.setText(formattedTime);
        int playPercent = getProgressPercentage(mPlayer.getCurrentTimeMillis(), mPlayer.getDurationMillis());
        mSeekBar.setProgress(playPercent);
        if (mPlayer != null && mPlayer.isPlaying()) {
            long current_time = mPlayer.getCurrentTimeMillis();

            if (current_time >= 20000 && !isViewSaved && isFrom.equalsIgnoreCase("course")) {
                isViewSaved = true;
                new MyClient(NewCustomPlayerActivity.this).hitSetVideoView(id, (content, error) -> {
                    if (content != null) {
                        ServiceResponse response = (ServiceResponse) content;
                        isViewSaved = response.getResult() != null && response.getResult().equalsIgnoreCase("Views saved");
                    } else
                        Timber.e("onPlaying: " + error);
                });

            }
        }


    }

    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;
        return (hours == 0 ? "00:" : hours + ":") + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    public int getProgressPercentage(long currentDuration, long totalDuration) {
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage = (((double) currentSeconds) / totalSeconds) * 100;

        // return percentage
        return percentage.intValue();
    }

    private final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            displayCurrentTime();
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {


            case MotionEvent.ACTION_UP:
                clickCount++;
                int x2 = (int) ev.getX();
                int y2 = (int) ev.getY();

                int dy = x2 - y2;

                if (mPlayer != null) {
                    if (clickCount == 1) {
                        startTime = System.currentTimeMillis();
                    } else if (clickCount == 2) {
                        long duration = System.currentTimeMillis() - startTime;
                        if (duration <= 500) {
                            if (ev.getX() > width / 2) {
                                mPlayer.seekToMillis(mPlayer.getCurrentTimeMillis() + 10000);
                                mHandler.postDelayed(runnable, 100);
                                // Toast.makeText(NewCustomPlayerActivity.this, "Forwarded 10 sec", Toast.LENGTH_SHORT).show();
                            } else {
                                mPlayer.seekToMillis(mPlayer.getCurrentTimeMillis() - 10000);
                                mHandler.postDelayed(runnable, 100);
                                //   Toast.makeText(NewCustomPlayerActivity.this, "Backwarded 10 sec", Toast.LENGTH_SHORT).show();

                            }
                            clickCount = 0;
                            duration = 0;
                        } else {
                            clickCount = 1;
                            startTime = System.currentTimeMillis();
                        }
                        break;
                    }
                }
        }

        return super.dispatchTouchEvent(ev);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("", "tag %s" + newConfig);
        if (currentMode == newConfig.uiMode) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            int width = displayMetrics.widthPixels;
            RelativeLayout.LayoutParams playerParams = (RelativeLayout.LayoutParams) youTubePlayerView.getLayoutParams();
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                toolbar.setVisibility(View.GONE);
                binding.videosRecyclerView.setVisibility(View.GONE);
                playerParams.width = MATCH_PARENT;
                playerParams.height = height - 80;
                fullscreen_icon.setImageResource(R.drawable.ic_exit);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
                isFullScreen = true;

            } else {
                toolbar.setVisibility(View.VISIBLE);
                binding.videosRecyclerView.setVisibility(View.VISIBLE);
                playerParams.width = MATCH_PARENT;
                playerParams.height = WRAP_CONTENT;
                fullscreen_icon.setImageResource(R.drawable.ic_full_screen);
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
                isFullScreen = false;

            }
        } else {

            Intent intent = new Intent(NewCustomPlayerActivity.this, NewCustomPlayerActivity.class);
            intent.putExtra("VideoLink", videoId);
            intent.putExtra("videolist", (Serializable) videoList);
            intent.putExtra("currentPosition", currentPosition);
            intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
            startActivity(intent);
            finish();
          /*  startActivity(getIntent());
            finish();*/
        }


    }

    @Override
    public void onBackPressed() {

        if (isFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }

    }
}