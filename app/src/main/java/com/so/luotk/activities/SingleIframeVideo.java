package com.so.luotk.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBarListener;
import com.so.luotk.R;
import com.so.luotk.client.MyClient;
import com.so.luotk.databinding.ActivitySingleIframeVideoBinding;
import com.so.luotk.databinding.DialogUsbConnectedBinding;
import com.so.luotk.models.output.ServiceResponse;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.youtubeExtract.YoutubeJExtractor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import timber.log.Timber;

public class SingleIframeVideo extends AppCompatActivity {

    private static final String TAG = "CustomActivity";
    private Handler mHandler = null;
    private String videoId;
    private boolean isFullScreen = false;

    ActivitySingleIframeVideoBinding binding;
    private final int currentPosition = 0;
    private int currentMode;
    private BroadcastReceiver broadcast_reciever;
    private Dialog dialog;
    private String id, isFrom;
    private boolean isViewSaved = false;
    private boolean isOverlay;
    private boolean connected;
    TextView full_text, speed_text;
    ImageView full_icon;
    private RecyclerView horizontal_recycler_view;
    private ArrayList<SppedModel> horizontalList;
    private CustomAdapter horizontalAdapter;
    ConstraintLayout customLay;
    private TextView titleVideo;
    static boolean active = false;
    private long currentTime = 0;
    private String lastQualityValue = "";
    BottomSheetDialog dialogBottom;
    private YoutubeJExtractor youtubeJExtractor;
    private DataSource.Factory mediaDataSourceFactory;
    private int trySourceError = 0;
    private boolean belowDialogOpen = false;
    private Handler handler;
    private Handler handlershowLay;


    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayerViewThis;
    private YouTubePlayerSeekBar youTubeSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utilities.restrictScreenShot(this);
        binding = ActivitySingleIframeVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        broadcast_reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (intent.getExtras().getBoolean("connected")) {
                    showDisconnectUsbDialog();
                    connected = true;
                }
            }
        };
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (getIntent() != null) {
            videoId = getIntent().getStringExtra("VideoLink");
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            id = getIntent().getStringExtra("videoID");
        }

        mHandler = new Handler(Looper.myLooper());
        handler = new Handler(Looper.myLooper());
        handlershowLay = new Handler(Looper.myLooper());

        youTubePlayerView = findViewById(R.id.youtube_player_view);
        youTubePlayerView.enterFullScreen();
        youTubeSeekBar = findViewById(R.id.youTubeSeekBar);

        binding.noClickLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.customLay.getVisibility() == View.VISIBLE) {
                    binding.customLay.setVisibility(View.GONE);
                } else {
//                    binding.customLay.setVisibility(View.VISIBLE);
                    showLayout();
                }
            }
        });
      /*  youTubePlayerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speedRecyler.getVisibility() == View.VISIBLE) {
                    speedRecyler.setVisibility(View.GONE);
                }
            }
        });*/

        initYouTubePlayerView(videoId);
        currentMode = getResources().getConfiguration().uiMode;


//        full_text = playerView.findViewById(R.id.full_text);
//        speed_text = playerView.findViewById(R.id.speed_text);
//        full_icon = playerView.findViewById(R.id.full_icon);
//        speedRecyler = playerView.findViewById(R.id.speedRecyler);
//        titleVideo = playerView.findViewById(R.id.titleVideo);

//        horizontal_recycler_view = (RecyclerView) playerView.findViewById(R.id.recycleHorizontal);
        horizontalList = new ArrayList<>();

        horizontalList.add(new SppedModel("1", "0.25x"));
        horizontalList.add(new SppedModel("2", "0.5x"));
        horizontalList.add(new SppedModel("3", "1x"));
        horizontalList.add(new SppedModel("4", "1.5x"));
        horizontalList.add(new SppedModel("5", "2x"));


        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.speedRecyler.getVisibility() == View.VISIBLE) {
                    binding.speedRecyler.setVisibility(View.GONE);
                }
                onBackPressed();
            }
        });
        binding.speedLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.speedRecyler.getVisibility() == View.VISIBLE) {
                    binding.speedRecyler.setVisibility(View.GONE);
                }
                binding.speedRecyler.setVisibility(View.VISIBLE);
                showLayout();
            }
        });




    }

    private void initYouTubePlayerView(String videoId) {
//        youTubePlayerView.inflateCustomPlayerUi(R.layout.ayp_empty_layout);

        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(0)
                .rel(0)
                .ivLoadPolicy(3)
                .ccLoadPolicy(0)
                .build();

        getLifecycle().addObserver(youTubePlayerView);

        youTubePlayerView.initialize(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                /*CustomPlayerUiController customPlayerUiController = new CustomPlayerUiController(SingleIframeVideo.this, customPlayerUi, youTubePlayer, youTubePlayerView);
                youTubePlayer.addListener(customPlayerUiController);*/

//                binding.customLay.setVisibility(View.VISIBLE);

                showLayout();
                binding.backgrounLay.setVisibility(View.VISIBLE);

                binding.progressBar.setVisibility(View.GONE);
                youTubePlayerViewThis = youTubePlayer;
                youTubePlayer.addListener(youTubeSeekBar);
                youTubeSeekBar.setYoutubePlayerSeekBarListener(new YouTubePlayerSeekBarListener() {
                    @Override
                    public void seekTo(float time) {
                        Log.d(TAG, "seekTo: " + time);
                        youTubePlayer.seekTo(time);
                    }

                });

                setButtonsForPlayer(youTubePlayer);
                horizontalAdapter = new CustomAdapter(horizontalList, SingleIframeVideo.this, youTubePlayer);
                LinearLayoutManager horizontalLayoutManagaer
                        = new LinearLayoutManager(SingleIframeVideo.this, LinearLayoutManager.HORIZONTAL, false);
                binding.recycleHorizontal.setLayoutManager(horizontalLayoutManagaer);
                binding.recycleHorizontal.setAdapter(horizontalAdapter);

//                youTubePlayer.loadVideo(videoId, 0);
                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer, getLifecycle(),
                        videoId,0f
                );
            }

            @Override
            public void onStateChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlayerState state) {
                super.onStateChange(youTubePlayer, state);
                if (state == PlayerConstants.PlayerState.ENDED) {
                    if (isFullScreen){
                        finish();
                    } else {
                        onBackPressed();
                    }
                }
                if (!isViewSaved) {
                    updateProgressBar();
                }
            }

            @Override
            public void onPlaybackQualityChange(@NotNull YouTubePlayer youTubePlayer, @NotNull PlayerConstants.PlaybackQuality playbackQuality) {
                super.onPlaybackQualityChange(youTubePlayer, playbackQuality);
                Log.d(TAG, "onPlaybackQualityChange: " + playbackQuality);
            }

        }, true, iFramePlayerOptions);

    }

    private void updateProgressBar() {
        try {
            Log.d(TAG, "updateProgressBar: " +isViewSaved +" : "+ isFrom);
            if (youTubeSeekBar != null) {
                Log.d(TAG, "updateProgressBar1: " +Float.parseFloat(youTubeSeekBar.getVideoCurrentTimeTextView().getText().toString().replace(":", ".")));
                if ((Float.parseFloat(youTubeSeekBar.getVideoCurrentTimeTextView().getText().toString().replace(":", ".")) >= 0.21)) {
                    if (!isViewSaved && isFrom.equalsIgnoreCase("course")) {
                        isViewSaved = true;
                        new MyClient(SingleIframeVideo.this).hitSetVideoView(id, (content, error) -> {
                            if (content != null) {
                                ServiceResponse response = (ServiceResponse) content;
                                isViewSaved = response.getResult() != null && response.getResult().equalsIgnoreCase("Views saved");
                                Log.d(TAG, "updateProgressBar: " + isViewSaved);
                                handler.removeCallbacks(updateProgressAction);
                            } else
                                handler.removeCallbacks(updateProgressAction);
                            Timber.e("onPlaying: " + error);
                        });

                    }

                }
            }
            // Remove scheduled updates.
            handler = new Handler(Looper.myLooper());
            handler.removeCallbacks(updateProgressAction);
            handler.postDelayed(updateProgressAction, 1000);
            // Schedule an update if necessary.


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final Runnable updateProgressAction = new Runnable() {
        @Override
        public void run() {
            if (!isViewSaved) {
                updateProgressBar();
            }
        }
    };

    private void setButtonsForPlayer(final YouTubePlayer youTubePlayerVal) {
        binding.fwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.speedRecyler.getVisibility() == View.VISIBLE) {
                    binding.speedRecyler.setVisibility(View.GONE);
                }


                Log.e(TAG, "onClick: " + youTubeSeekBar.getVideoCurrentTimeTextView().getText());
                try {
                    String value = youTubeSeekBar.getVideoCurrentTimeTextView().getText().toString().replace(":", ".");
                    if (Float.parseFloat(value) >= 1.0){
                        String[] splitTime = value.split("\\.");
                        float timeValMinute = Float.parseFloat(splitTime[0]) *  60f;
                        float timeValSecond = Float.parseFloat(splitTime[1]) +  10.0f;
                        float totalTime = timeValMinute + timeValSecond;
                        youTubePlayerVal.seekTo(totalTime);
                    } else {
                        Log.e(TAG, "onClick: "+ "in " + value);
                        String[] splitTime = value.split("\\.");
                        float timeVal = Float.parseFloat(splitTime[1])  + 10.0f;
                        youTubePlayerVal.seekTo(timeVal);
                    }
                    /*float timeVal = Float.parseFloat(youTubeSeekBar.getVideoCurrentTimeTextView().getText().toString().replace(":", "."))  + 10.0f;
                    Log.d(TAG, "onClick@: " + timeVal);
                    youTubePlayerVal.seekTo(timeVal);*/
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
//                youTubePlayerViewThis.seekTo(youTubeSeekBar.getVideoCurrentTimeTextView().getText());
//                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);
                showLayout();
            }
        });
        binding.rew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.speedRecyler.getVisibility() == View.VISIBLE) {
                    binding.speedRecyler.setVisibility(View.GONE);
                }
                String value = youTubeSeekBar.getVideoCurrentTimeTextView().getText().toString().replace(":", ".");
                if (Float.parseFloat(value) >= 1.0){
                    String[] splitTime = value.split("\\.");
                    float timeValMinute = Float.parseFloat(splitTime[0]) *  60f;
                    float timeValSecond = Float.parseFloat(splitTime[1]) -  10.0f;
                    float totalTime = timeValMinute + timeValSecond;
                    youTubePlayerVal.seekTo(totalTime);
                } else {
                    Log.e(TAG, "onClick: "+ "in " + value);
                    String[] splitTime = value.split("\\.");
                    float timeVal = Float.parseFloat(splitTime[1])  - 10.0f;
                    if (timeVal < 0){
                        youTubePlayerVal.seekTo(0);
                    } else {
                        youTubePlayerVal.seekTo(timeVal);
                    }
                }
                showLayout();
            }
        });

        binding.fullLay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onClick(View view) {
                if (binding.speedRecyler.getVisibility() == View.VISIBLE) {
                    binding.speedRecyler.setVisibility(View.GONE);
                }
                Log.d(TAG, "FullonClick: "+ "");
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                }

                showLayout();
            }
        });


        binding.exoPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (binding.speedRecyler.getVisibility() == View.VISIBLE) {
                        binding.speedRecyler.setVisibility(View.GONE);
                    }
                    youTubePlayerVal.play();
                    binding.exoPlay.setVisibility(View.GONE);
                    binding.exoPause.setVisibility(View.VISIBLE);
                    showLayout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.exo_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.speedRecyler.getVisibility() == View.VISIBLE) {
                    binding.speedRecyler.setVisibility(View.GONE);
                }
                youTubePlayerVal.pause();
                binding.exoPlay.setVisibility(View.VISIBLE);
                binding.exoPause.setVisibility(View.GONE);
                showLayout();
            }
        });
    }


    private final Runnable runnable = new Runnable() {

        @Override
        public void run() {
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        Log.d(TAG, "onStart: " + videoId + " : "+ currentTime);
        try {
            registerReceiver(broadcast_reciever, new IntentFilter("android.hardware.usb.action.USB_STATE"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed: " + isFullScreen);
        if (isFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            finish();
        }

    }

    /*@Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // Checks the orientation of the screen
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youTubePlayerView.enterFullScreen();
        }
        else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){
            youTubePlayerView.exitFullScreen();
        }
    }*/

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinishing() && dialog != null && dialog.isShowing())
            dialog.dismiss();
        try {
            unregisterReceiver(broadcast_reciever);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mHandler.removeCallbacks(runnable);
        try {
            handlershowLay.removeCallbacks(runnableShowLay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            handler.removeCallbacks(updateProgressAction);

            handler.removeCallbacksAndMessages(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*@Override
    protected void onStop() {
        super.onStop();
        if (!isFinishing() && dialog != null && dialog.isShowing())
            dialog.dismiss();
        mHandler.removeCallbacks(runnable);
        try {
            handlershowLay.removeCallbacks(runnableShowLay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            handler.removeCallbacks(updateProgressAction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("", "tag %s" + newConfig);
        if (currentMode == newConfig.uiMode) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                isFullScreen = true;
                binding.fullText.setText(getString(R.string.exit_full_screem));
                binding.fullIcon.setImageDrawable(getDrawable(R.drawable.ayp_ic_fullscreen_exit_24dp));

            } else {
                isFullScreen = false;
                binding.fullText.setText(getString(R.string.full_screem));
                binding.fullIcon.setImageDrawable(getDrawable(R.drawable.ayp_ic_fullscreen_24dp));

            }
        } else {

            Intent intent = new Intent(this, SingleIframeVideo.class);
            intent.putExtra("VideoLink", videoId);
            startActivity(intent);
            finish();
        }


    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
        private final ArrayList<SppedModel> dataSet;
        private YouTubePlayer youTubePlayerAdapter;
        Context mContext;

        public CustomAdapter(ArrayList<SppedModel> dataSet, SingleIframeVideo SingleVideoYoutube, YouTubePlayer youTubePlayer) {
            this.mContext = SingleVideoYoutube;
            this.youTubePlayerAdapter = youTubePlayer;
            this.dataSet = dataSet;

        }


        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView textViewName;

            public MyViewHolder(View itemView) {
                super(itemView);
                this.textViewName = (TextView) itemView.findViewById(R.id.speed_item);
                //this.textViewVersion = (TextView) itemView.findViewById(R.id.textViewVersion);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SppedModel sppedModel = dataSet.get(getPosition());
                        if (sppedModel.getId().equals("1")) {
                            binding.speedText.setText("Speed 0.25x");
                            youTubePlayerAdapter.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_0_25);
//                            youTubePlayerAdapter.setPlaybackQuality(PlayerConstants.PlaybackQuality.SMALL);
                        } else if (sppedModel.getId().equals("2")) {
                            binding.speedText.setText("Speed 0.5x");
                            youTubePlayerAdapter.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_0_5);

                        } else if (sppedModel.getId().equals("3")) {
                            binding.speedText.setText("Speed");
                            youTubePlayerAdapter.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_1);

                        } else if (sppedModel.getId().equals("4")) {
                            binding.speedText.setText("Speed 1.5x");
                            youTubePlayerAdapter.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_1_5);

                        } else if (sppedModel.getId().equals("5")) {
                            binding.speedText.setText("Speed 2x");
                            youTubePlayerAdapter.setPlaybackRate(PlayerConstants.PlaybackRate.RATE_2);
//                            youTubePlayerAdapter.setPlaybackQuality(PlayerConstants.PlaybackQuality.HD1080);

                        }

                        if (binding.speedRecyler.getVisibility() == View.VISIBLE) {
                            binding.speedRecyler.setVisibility(View.GONE);
                        }
                        showLayout();
                    }
                });
            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.speed_lay, parent, false);

            //view.setOnClickListener(MainActivity.myOnClickListener);

            MyViewHolder myViewHolder = new MyViewHolder(view);
            return myViewHolder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

            TextView textViewName = holder.textViewName;
            textViewName.setText(dataSet.get(listPosition).getName());
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }


    }

    private class SppedModel {
        String id;
        String name;

        public SppedModel(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
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

    public void showLayout(){
        binding.customLay.setVisibility(View.VISIBLE);
        handlershowLay.removeCallbacks(runnableShowLay);
        handlershowLay.postDelayed(runnableShowLay, 15000);
    }

    Runnable runnableShowLay = new Runnable() {
        @Override
        public void run() {
            if (binding.customLay.getVisibility() == View.VISIBLE) {
                binding.customLay.setVisibility(View.GONE);
            }
        }
    };


}