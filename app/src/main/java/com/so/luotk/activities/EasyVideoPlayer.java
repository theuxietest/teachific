package com.so.luotk.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.options.IFramePlayerOptions;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerUtils;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBar;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.ui.views.YouTubePlayerSeekBarListener;
import com.so.luotk.R;
import com.so.luotk.adapter.VideoQualityAdapter;
import com.so.luotk.adapter.VideoSpeedAdapter;
import com.so.luotk.client.MyClient;
import com.so.luotk.databinding.ActivityEasyVideoPlayerBinding;
import com.so.luotk.databinding.DialogUsbConnectedBinding;
import com.so.luotk.models.output.ServiceResponse;
import com.so.luotk.models.videoData.QualityModel;
import com.so.luotk.models.videoData.SpeedModel;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.youtubeExtract.YoutubeJExtractor;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Objects;

public class EasyVideoPlayer extends AppCompatActivity {

    private static final String TAG = "CustomActivity";
    private Handler mHandler = null;
    private String videoId;
    private boolean isFullScreen = false;

    ActivityEasyVideoPlayerBinding binding;
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
    private ArrayList<SpeedModel> speedListOriginal;
    private ArrayList<SpeedModel> speedListCopy;
    private ArrayList<QualityModel> qualityListOriginal;
    private ArrayList<QualityModel> qualityListCopy;
    private VideoQualityAdapter qualityAdapter;
    private VideoSpeedAdapter speedAdapter;
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
    private Handler handler, handlerWait;
    private Handler handlershowLay;
    private Runnable runnableWait;
    public static String selectQuality = "0", selectSpeed = "3";
    BottomSheetDialog dialogQuality,  dialogSpeed;


    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer youTubePlayerViewThis;
    private YouTubePlayerSeekBar youTubeSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Utilities.restrictScreenShot(this);
        binding = ActivityEasyVideoPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        broadcast_reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (intent.getExtras().getBoolean("connected")) {
                   /* showDisconnectUsbDialog();
                    connected = true;*/
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
        handlerWait = new Handler(Looper.myLooper());

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



        currentMode = getResources().getConfiguration().uiMode;
        speedListOriginal = new ArrayList<>();
        speedListCopy = new ArrayList<>();
        qualityListOriginal = new ArrayList<>();
        qualityListCopy = new ArrayList<>();

        speedListOriginal.add(new SpeedModel("1", "0.25x",PlayerConstants.PlaybackRate.RATE_0_25, false));
        speedListOriginal.add(new SpeedModel("2", "0.5x",PlayerConstants.PlaybackRate.RATE_0_5, false));
        speedListOriginal.add(new SpeedModel("3", "1x",PlayerConstants.PlaybackRate.RATE_1, true));
        speedListOriginal.add(new SpeedModel("4", "1.5x",PlayerConstants.PlaybackRate.RATE_1_5, false));
        speedListOriginal.add(new SpeedModel("5", "2x",PlayerConstants.PlaybackRate.RATE_2, false));

        qualityListOriginal.add(new QualityModel("0", "DEFAULT", true, "auto"));
        qualityListOriginal.add(new QualityModel("1", "SMALL", false, "tiny"));
        qualityListOriginal.add(new QualityModel("2", "MEDIUM", false, "small"));
        qualityListOriginal.add(new QualityModel("3", "LARGE", false, "large"));
        qualityListOriginal.add(new QualityModel("4", "HD720", false, "hd720"));
        qualityListOriginal.add(new QualityModel("5", "HD1080", false, "hd1080"));


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
                /*if (binding.speedRecyler.getVisibility() == View.VISIBLE) {
                    binding.speedRecyler.setVisibility(View.GONE);
                }
                binding.speedRecyler.setVisibility(View.VISIBLE);
                showLayout();*/
                BottomSpeedDialog(selectSpeed);
            }
        });
        youTubePlayerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        youTubePlayerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return false;
            }
        });


        binding.exoTrackSelectionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogQuality.show();
            }
        });
        BottomQualityDialog(selectQuality);
        initYouTubePlayerView(videoId);
    }

    private void BottomQualityDialog(String selectedQuality) {
        qualityListCopy.clear();
        dialogQuality = new BottomSheetDialog(this, R.style.DialogStyle);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_dialog_quality, null);
        Objects.requireNonNull(dialogQuality.getWindow())
                .setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
        dialogQuality.setContentView(sheetView);
        dialogQuality.setCanceledOnTouchOutside(true);
        dialogQuality.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });



        RecyclerView recyclerView = sheetView.findViewById(R.id.qualtyRecycler);
        qualityAdapter = new VideoQualityAdapter(qualityListOriginal, EasyVideoPlayer.this, youTubePlayerViewThis, selectedQuality);
        recyclerView.setLayoutManager(new LinearLayoutManager(EasyVideoPlayer.this));
        recyclerView.setAdapter(qualityAdapter);


        qualityAdapter.SetOnItemClickListener(new VideoQualityAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                qualityListCopy.clear();
                QualityModel qualityModel = qualityListOriginal.get(position);
                for (int i = 0; i < qualityListOriginal.size(); i++) {
                    if (qualityListOriginal.get(i).getId().equals(qualityModel.getId())) {
                        QualityModel bqualityModel = new QualityModel(qualityListOriginal.get(i).getId(), qualityListOriginal.get(i).getName(), true, qualityListOriginal.get(i).getValue());
                        qualityListCopy.add(bqualityModel);
                        youTubePlayerViewThis.setPlaybackQuality(qualityListOriginal.get(i).getValue());
                        selectQuality = qualityListOriginal.get(i).getId();
                    } else {
                        QualityModel bqualityModel = new QualityModel(qualityListOriginal.get(i).getId(), qualityListOriginal.get(i).getName(), false, qualityListOriginal.get(i).getValue());
                        qualityListCopy.add(bqualityModel);
                    }
                    if (i == (qualityListOriginal.size() - 1)){
                        qualityListOriginal.clear();
                        qualityListOriginal.addAll(qualityListCopy);
                        qualityAdapter.notifyDataSetChanged();
                    }
                }

                dialogQuality.dismiss();
            }
        });
    }
    private void BottomSpeedDialog(String selectedSpeed) {
        speedListCopy.clear();
        dialogSpeed = new BottomSheetDialog(this, R.style.DialogStyle);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_dialog_quality, null);
        Objects.requireNonNull(dialogSpeed.getWindow())
                .setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
        dialogSpeed.setContentView(sheetView);
        dialogSpeed.setCanceledOnTouchOutside(true);
        dialogSpeed.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });

        RecyclerView recyclerView = sheetView.findViewById(R.id.qualtyRecycler);
        speedAdapter = new VideoSpeedAdapter(speedListOriginal, EasyVideoPlayer.this, youTubePlayerViewThis, selectedSpeed);
        recyclerView.setLayoutManager(new LinearLayoutManager(EasyVideoPlayer.this));
        recyclerView.setAdapter(speedAdapter);
        dialogSpeed.show();

        speedAdapter.SetOnItemClickListener(new VideoSpeedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                speedListCopy.clear();
                SpeedModel speedModel = speedListOriginal.get(position);
                for (int i = 0; i < speedListOriginal.size(); i++) {
                    if (speedListOriginal.get(i).getId().equals(speedModel.getId())) {
                        SpeedModel bspeedModel = new SpeedModel(speedListOriginal.get(i).getId(), speedListOriginal.get(i).getName(), speedListOriginal.get(i).getPlaybackRate(), true);
                        speedListCopy.add(bspeedModel);
                        youTubePlayerViewThis.setPlaybackRate(speedListOriginal.get(i).getPlaybackRate());
                        selectSpeed = speedListOriginal.get(i).getId();
                    } else {
                        SpeedModel bspeedModel = new SpeedModel(speedListOriginal.get(i).getId(), speedListOriginal.get(i).getName(), speedListOriginal.get(i).getPlaybackRate(), false);
                        speedListCopy.add(bspeedModel);
                    }
                    if (i == (speedListOriginal.size() - 1)){
                        speedListOriginal.clear();
                        speedListOriginal.addAll(speedListCopy);
                        speedAdapter.notifyDataSetChanged();
                    }
                }
                dialogSpeed.dismiss();
            }
        });

    }

    private void initYouTubePlayerView(String videoId) {
        IFramePlayerOptions iFramePlayerOptions = new IFramePlayerOptions.Builder()
                .controls(0)
                .build();

        getLifecycle().addObserver(youTubePlayerView);

        YouTubePlayerListener listener = new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                Log.d(TAG, "onReady: Call");
                youTubePlayerViewThis = youTubePlayer;
                youTubePlayer.addListener(youTubeSeekBar);
                youTubeSeekBar.setYoutubePlayerSeekBarListener(new YouTubePlayerSeekBarListener() {
                    @Override
                    public void seekTo(float time) {
                        youTubePlayer.seekTo(time);
                    }

                });
                setButtonsForPlayer(youTubePlayer);
                YouTubePlayerUtils.loadOrCueVideo(
                        youTubePlayer,
                        getLifecycle(),
                        videoId,
                        0f
                );
                handlerWait.removeCallbacks(runnableWait);
                runnableWait = new Runnable() {
                    @Override
                    public void run() {
                        showLayout();
                        binding.backgrounLay.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                };
                handlerWait.postDelayed(runnableWait, 1500);

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
            public void onPlaybackRateChange(YouTubePlayer youTubePlayer, PlayerConstants.PlaybackRate playbackRate) {
                String playbackSpeed = "Playback speed: ";
                Toast.makeText(EasyVideoPlayer.this, playbackRate + " --  " + playbackSpeed  , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPlaybackQualityChange(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlaybackQuality playbackQuality) {
                super.onPlaybackQualityChange(youTubePlayer, playbackQuality);
            }

            @Override
            public void onError(@NonNull YouTubePlayer youTubePlayer, @NonNull PlayerConstants.PlayerError error) {
                super.onError(youTubePlayer, error);
            }
        };

        youTubePlayerView.initialize(listener, iFramePlayerOptions);
    }

    private void updateProgressBar() {
        try {
            if (youTubeSeekBar != null) {
                if ((Float.parseFloat(youTubeSeekBar.getVideoCurrentTimeTextView().getText().toString().replace(":", ".")) >= 0.21)) {
                    if (!isViewSaved && isFrom.equalsIgnoreCase("course")) {
                        isViewSaved = true;
                        new MyClient(EasyVideoPlayer.this).hitSetVideoView(id, (content, error) -> {
                            if (content != null) {
                                ServiceResponse response = (ServiceResponse) content;
                                isViewSaved = response.getResult() != null && response.getResult().equalsIgnoreCase("Views saved");
                                handler.removeCallbacks(updateProgressAction);
                            } else
                                handler.removeCallbacks(updateProgressAction);
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
                        Log.e(TAG, "onClick1Arra: " +  new Gson().toJson(splitTime));
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
                    Log.e(TAG, "onClick1Arra: " +  new Gson().toJson(splitTime));
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
        try {

            binding.exoPause.performClick();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        try {
            binding.exoPlay.performClick();
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