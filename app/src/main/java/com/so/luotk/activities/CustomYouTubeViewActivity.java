package com.so.luotk.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.so.luotk.databinding.DialogUsbConnectedBinding;
import com.so.luotk.utils.Utilities;
import com.so.luotk.R;

import android.content.res.Configuration;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.youtube.player.YouTubePlayer.OnInitializedListener;
import static com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import static com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import static com.google.android.youtube.player.YouTubePlayer.Provider;

public class CustomYouTubeViewActivity extends YouTubeBaseActivity implements OnInitializedListener, View.OnClickListener {
    private static final String TAG = "CustomPlayerControlActivity";
    private YouTubePlayer mPlayer;

    private View mPlayButtonLayout, playerLayout;
    private TextView mPlayTimeTextView;

    private Handler mHandler = null;
    private SeekBar mSeekBar;
    private YouTubePlayerView youTubePlayerView;
    private ImageView imageViewPlayIcon;
    private String videoId;
    private Toolbar toolbar;
    private ImageView fullscreen_icon, forward_icon;
    int clickCount = 0;
    long startTime;
    private final boolean actionDownReceived = false;
    private BroadcastReceiver broadcast_reciever;
    private final int DELTA_TOLERANCE = 70;
    private final int actionDownX = -1;
    private final int actionDownY = -1;
    private boolean isFullScreen;
    private final int seekTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utilities.restrictScreenShot(this);
//      getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(R.layout.activity_custom_you_tube_view);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (getIntent() != null) {
            videoId = getIntent().getStringExtra("VideoLink");
        }
        setToolbar();
        mHandler = new Handler(Looper.myLooper());
        broadcast_reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (intent.getExtras().getBoolean("connected")) {
                    showDisconnectUsbDialog();
                }
            }
        };
        // Initializing YouTube player view
        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.initialize(getString(R.string.api_key_youtube_one)
                + getString(R.string.api_key_youtube_two)
                + getString(R.string.api_key_youtube_three), this);
        //Add play button to explicitly play video in YouTubePlayerView
        mPlayButtonLayout = findViewById(R.id.video_control);
        imageViewPlayIcon = findViewById(R.id.play_video);
        imageViewPlayIcon.setBackgroundResource(R.drawable.baseline_pause_white_18dp);
        imageViewPlayIcon.setOnClickListener(this);
        mPlayTimeTextView = (TextView) findViewById(R.id.play_time);
        fullscreen_icon = findViewById(R.id.icon_full_screen);
        mSeekBar = findViewById(R.id.video_seekbar);
        mSeekBar.setOnSeekBarChangeListener(mVideoSeekBarChangeListener);
        playerLayout = findViewById(R.id.player_layout);
        playerLayout.setOnClickListener(this);
        fullscreen_icon.setOnClickListener(this);

    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        // toolbar.setTitle("Maths");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
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
        if (!isFinishing())
            dialog.show();
        dialog.getWindow().setLayout(MATCH_PARENT, WRAP_CONTENT);
        dialog.setCancelable(false);
    }


    @Override
    public void onInitializationFailure(Provider provider, YouTubeInitializationResult result) {
        Toast.makeText(this, "Failed to initialize.", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
        if (null == player) return;
        mPlayer = player;
        if (!wasRestored)
            try {
                if (videoId != null) {
                    if (videoId.contains("https://www.youtube.com/watch?v=")) {
                        videoId = videoId.replace("https://www.youtube.com/watch?v=", "");
                    } else if (videoId.contains("https://youtube.com/shorts/")){
                        String[] splitVideo = videoId.split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("shorts/");
                            videoId = splitU[1];
                        } else {
                            String[] splitUrl = videoId.split("shorts/");
                            videoId = splitUrl[1];
                        }
                    } else if (videoId.contains("https://youtube.com/live")){
                        String[] splitVideo = videoId.split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("live/");
                            videoId = splitU[1];
                        } else {
                            String[] splitUrl = videoId.split("live/");
                            videoId = splitUrl[1];
                        }
                    } else if (videoId.contains("https://www.youtube.com/live")){
                        String[] splitVideo = videoId.split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("live/");
                            videoId = splitU[1];
                        } else {
                            String[] splitUrl = videoId.split("live/");
                            videoId = splitUrl[1];
                        }
                    } else if (videoId.contains("https://www.youtube.com/shorts/")){
                        String[] splitVideo = videoId.split("\\?");
                        if (splitVideo.length > 1) {
                            String[] splitU = splitVideo[0].split("shorts/");
                            videoId = splitU[1];
                        } else {
                            String[] splitUrl = videoId.split("shorts/");
                            videoId = splitUrl[1];
                        }
                    }else {
                        videoId = videoId.replace("https://youtu.be/", "");
                    }
                    player.loadVideo(videoId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        player.setPlayerStyle(PlayerStyle.CHROMELESS);
        mPlayButtonLayout.setVisibility(View.VISIBLE);
        player.play();
        displayCurrentTime();
        // Add listeners to YouTubePlayer instance
        player.setPlayerStateChangeListener(mPlayerStateChangeListener);
        player.setPlaybackEventListener(mPlaybackEventListener);

    }

    PlaybackEventListener mPlaybackEventListener = new PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }

        @Override
        public void onPaused() {
           /* if (mPlayer != null)
                seekTime = mPlayer.getCurrentTimeMillis();*/
            mHandler.removeCallbacks(runnable);
        }

        @Override
        public void onPlaying() {
       /*     if (mPlayer != null && !mPlayer.isPlaying())
                mPlayer.play();*/
            mHandler.postDelayed(runnable, 100);
            displayCurrentTime();

        }

        @Override
        public void onSeekTo(int arg0) {
            //   mHandler.postDelayed(runnable, 100);
        }

        @Override
        public void onStopped() {
            mHandler.removeCallbacks(runnable);
        }
    };

    YouTubePlayer.PlayerStateChangeListener mPlayerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }

        @Override
        public void onLoaded(String arg0) {
        }

        @Override
        public void onLoading() {
        }

        @Override
        public void onVideoEnded() {
            CustomYouTubeViewActivity.this.finish();
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
        registerReceiver(broadcast_reciever, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        try {
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
        unregisterReceiver(broadcast_reciever);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.play_video:
                if (mPlayer != null)
                    if (!mPlayer.isPlaying()) {
                        try {
                            mPlayer.play();
                            imageViewPlayIcon.setBackgroundResource(R.drawable.baseline_pause_white_18dp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            mPlayer.pause();
                            imageViewPlayIcon.setBackgroundResource(R.drawable.baseline_play_arrow_white_18dp);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                break;
            case R.id.icon_full_screen:
                if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    toolbar.setVisibility(View.VISIBLE);
                    fullscreen_icon.setImageResource(R.drawable.ic_full_screen_white);
                    isFullScreen = false;
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    toolbar.setVisibility(View.GONE);
                    fullscreen_icon.setImageResource(R.drawable.ic_exit_white);
                    isFullScreen = true;
                }
                break;


        }

    }

    private void displayCurrentTime() {
        if (null == mPlayer) return;
        String formattedTime = formatTime(mPlayer.getDurationMillis() - mPlayer.getCurrentTimeMillis());
        mPlayTimeTextView.setText(formattedTime);
        int playPercent = getProgressPercentage(mPlayer.getCurrentTimeMillis(), mPlayer.getDurationMillis());
        mSeekBar.setProgress(playPercent);


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
                                // Toast.makeText(CustomYouTubeViewActivity.this, "Forwarded 10 sec", Toast.LENGTH_SHORT).show();
                            } else {
                                mPlayer.seekToMillis(mPlayer.getCurrentTimeMillis() - 10000);
                                mHandler.postDelayed(runnable, 100);
                                //   Toast.makeText(CustomYouTubeViewActivity.this, "Backwarded 10 sec", Toast.LENGTH_SHORT).show();

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

        return super.

                dispatchTouchEvent(ev);

    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        // Toast.makeText(this, "height" + String.valueOf(height), Toast.LENGTH_SHORT).show();

        RelativeLayout.LayoutParams playerParams = (RelativeLayout.LayoutParams) youTubePlayerView.getLayoutParams();
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            toolbar.setVisibility(View.GONE);
            playerParams.width = MATCH_PARENT;
            playerParams.height = height - 80;
            fullscreen_icon.setImageResource(R.drawable.ic_exit_white);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            isFullScreen = true;

        } else {
            toolbar.setVisibility(View.VISIBLE);
            playerParams.width = MATCH_PARENT;
            playerParams.height = WRAP_CONTENT;
            fullscreen_icon.setImageResource(R.drawable.ic_full_screen_white);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            isFullScreen = false;

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
