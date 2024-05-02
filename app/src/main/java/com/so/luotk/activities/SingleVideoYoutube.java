package com.so.luotk.activities;

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
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.so.luotk.R;
import com.so.luotk.client.MyClient;
import com.so.luotk.databinding.ActivitySingleVideoYoutubeBinding;
import com.so.luotk.databinding.DialogUsbConnectedBinding;
import com.so.luotk.models.output.ServiceResponse;
import com.so.luotk.models.youtubeEx.AdaptiveAudioStream;
import com.so.luotk.models.youtubeEx.newModels.VideoPlayerConfig;
import com.so.luotk.models.youtubeEx.youtube.playerResponse.MuxedStream;
import com.so.luotk.models.youtubeEx.newModels.YoutubeDataModel;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.youtubeExtract.JExtractorCallback;
import com.so.luotk.youtubeExtract.YoutubeJExtractor;
import com.so.luotk.youtubeExtract.exception.YoutubeRequestException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import timber.log.Timber;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;

public class SingleVideoYoutube extends AppCompatActivity{
    private DefaultTrackSelector trackSelector;
    PlayerView playerView;
    SimpleExoPlayer simpleExoPlayer;
    private static final String TAG = "CustomActivity";
    private Handler mHandler = null;
    private String videoId;
    private ActivitySingleVideoYoutubeBinding binding;
    private boolean isFullScreen;
    private int currentMode;
    private BroadcastReceiver broadcast_reciever;
    private Dialog dialog;
    private String id, isFrom;
    private boolean isViewSaved;
    private boolean connected;
    TextView full_text, speed_text;
    ImageView full_icon;
    private RecyclerView horizontal_recycler_view;
    private ArrayList<SppedModel> horizontalList;
    private CustomAdapter horizontalAdapter;
    private LinearLayout speedRecyler;
    ConstraintLayout customLay;
    private TextView titleVideo;
    static boolean active = false;
    private long currentTime = 0;
    private List<MuxedStream> qualityArray = new ArrayList<>();
    private List<YoutubeDataModel> qualityArrayInApp = new ArrayList<>();
    private String lastQualityValue = "";
    BottomSheetDialog dialogBottom;
    private Handler handler;
    private YoutubeJExtractor youtubeJExtractor;
    private int trySourceError = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // attaching layout xml
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Utilities.restrictScreenShot(this);
        binding = ActivitySingleVideoYoutubeBinding.inflate(getLayoutInflater());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        broadcast_reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (intent.getExtras().getBoolean("connected")) {
                    showDisconnectUsbDialog();
                    connected = true;
                }
            }
        };

        binding.progressBar.setVisibility(View.VISIBLE);
        youtubeJExtractor = new YoutubeJExtractor();
        currentMode = getResources().getConfiguration().uiMode;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        if (getIntent() != null) {
            videoId = getIntent().getStringExtra("VideoLink");
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            id = getIntent().getStringExtra("videoID");
        }

        mHandler = new Handler(Looper.myLooper());
        playerView = findViewById(R.id.exoPlayerView);
        customLay = playerView.findViewById(R.id.customLay);

        playerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (speedRecyler.getVisibility() == View.VISIBLE) {
                    speedRecyler.setVisibility(View.GONE);
                }
            }
        });

        ImageView farwordBtn = playerView.findViewById(R.id.fwd);
        ImageView rewBtn = playerView.findViewById(R.id.rew);
        ImageView setting = playerView.findViewById(R.id.exo_track_selection_view);
        ImageView back_arrow = playerView.findViewById(R.id.back_arrow);

        RelativeLayout full_lay = playerView.findViewById(R.id.full_lay);
        RelativeLayout speed_lay = playerView.findViewById(R.id.speed_lay);
        full_text = playerView.findViewById(R.id.full_text);
        speed_text = playerView.findViewById(R.id.speed_text);
        full_icon = playerView.findViewById(R.id.full_icon);
        speedRecyler = playerView.findViewById(R.id.speedRecyler);
        titleVideo = playerView.findViewById(R.id.titleVideo);

        horizontal_recycler_view = (RecyclerView) playerView.findViewById(R.id.recycleHorizontal);
        horizontalList = new ArrayList<>();

        horizontalList.add(new SppedModel("1", "0.25x"));
        horizontalList.add(new SppedModel("2", "0.5x"));
        horizontalList.add(new SppedModel("3", "1x"));
        horizontalList.add(new SppedModel("4", "1.5x"));
        horizontalList.add(new SppedModel("5", "2x"));
        horizontalAdapter = new CustomAdapter(horizontalList, SingleVideoYoutube.this);
        LinearLayoutManager horizontalLayoutManagaer
                = new LinearLayoutManager(SingleVideoYoutube.this, LinearLayoutManager.HORIZONTAL, false);
        horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
        horizontal_recycler_view.setAdapter(horizontalAdapter);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        speed_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speedRecyler.setVisibility(View.VISIBLE);
            }
        });

        farwordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() + 10000);

            }
        });
        rewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                long num = simpleExoPlayer.getCurrentPosition() - 10000;
                if (num < 0) {
                    simpleExoPlayer.seekTo(0);
                } else {
                    simpleExoPlayer.seekTo(simpleExoPlayer.getCurrentPosition() - 10000);
                }
            }
        });

        full_lay.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SourceLockedOrientationActivity")
            @Override
            public void onClick(View view) {


                int orientation = SingleVideoYoutube.this.getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        });


        findViewById(R.id.exo_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    simpleExoPlayer.play();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        findViewById(R.id.exo_pause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpleExoPlayer.pause();
            }
        });

        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
            }
        });
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomDialogQuality(qualityArrayInApp);
            }
        });
        dialogBottom = new BottomSheetDialog(this, R.style.DialogStyle);
    }

    private void initializePlayer(String STREAM_URL, boolean playVideo) {
        /*TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);*/
        simpleExoPlayer = new SimpleExoPlayer.Builder(SingleVideoYoutube.this)/*.setTrackSelector(trackSelector)*/.build();
        playerView = findViewById(R.id.exoPlayerView);
        playerView.setPlayer(simpleExoPlayer);
        Log.d("TAG", "onExtractionDone: " + STREAM_URL);
        MediaItem mediaItem = MediaItem.fromUri(STREAM_URL);
        simpleExoPlayer.addMediaItem(mediaItem);
        simpleExoPlayer.prepare();
        if (currentTime == 0) {
            simpleExoPlayer.play();
        }
        simpleExoPlayer.seekTo(currentTime);
        if (playVideo) {
            simpleExoPlayer.play();
        }
        simpleExoPlayer.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Player.Listener.super.onPlayerError(error);
                try {
                    if (trySourceError < 5) {
                        runVideo(videoId);
                        trySourceError = trySourceError + 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onIsLoadingChanged(boolean isLoading) {
                Player.Listener.super.onIsLoadingChanged(isLoading);
            }

            @Override
            public void onPlaybackStateChanged(int playbackState) {
                Player.Listener.super.onPlaybackStateChanged(playbackState);
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    onBackPressed();
                }
                if (!isViewSaved) {
                    updateProgressBar();
                }
            }

        });

    }
    private void updateProgressBar() {
        try {
            if (simpleExoPlayer != null) {
                if ((simpleExoPlayer.getCurrentPosition() / 1000) >= 21) {
                    long current_time = simpleExoPlayer.getCurrentPosition();
                    if (current_time >= 20000 && !isViewSaved && isFrom.equalsIgnoreCase("course")) {
                        isViewSaved = true;
                        new MyClient(SingleVideoYoutube.this).hitSetVideoView(id, (content, error) -> {
                            if (content != null) {
                                ServiceResponse response = (ServiceResponse) content;
                                isViewSaved = response.getResult() != null && response.getResult().equalsIgnoreCase("Views saved");
                                Log.d(TAG, "updateProgressBar: " + isViewSaved);
                                handler.removeCallbacks(updateProgressAction);
                            } else
                                Timber.e("onPlaying: " + error);
                        });

                    }

                }
            }
            // Remove scheduled updates.
            handler = new Handler(Looper.myLooper());
            handler.removeCallbacks(updateProgressAction);
            // Schedule an update if necessary.
            int playbackState = simpleExoPlayer == null ? Player.STATE_IDLE : simpleExoPlayer.getPlaybackState();
            if (playbackState != Player.STATE_IDLE && playbackState != Player.STATE_ENDED) {
                handler.postDelayed(updateProgressAction, 1000);
            }
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
    protected void releasePlayer() {
        if (simpleExoPlayer != null) {
            simpleExoPlayer.release();
            simpleExoPlayer = null;
            trackSelector = null;
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
        if (simpleExoPlayer != null) {
            currentTime = simpleExoPlayer.getCurrentPosition();
        }
        releasePlayer();
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
        binding.progressBar.setVisibility(View.VISIBLE);
        try {
            registerReceiver(broadcast_reciever, new IntentFilter("android.hardware.usb.action.USB_STATE"));
            if (!connected) {
                runVideo(videoId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }


    @Override
    public void onBackPressed() {

        Log.d(TAG, "onBackPressed: " + isFullScreen);
        try {
            if (dialogBottom.isShowing()) {
                dialogBottom.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (isFullScreen) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            finish();
        }

    }
    public void runVideo(String youtubeLink) {
        Log.d(TAG, "runVideo: " + youtubeLink);
        youtubeJExtractor.extract(youtubeLink, new JExtractorCallback() {
            @Override
            public void onSuccess(VideoPlayerConfig videoData) {

                Log.d(TAG, "onExtractionDone: " + active);
                if (!connected) {
                    if (active) {
                        List<AdaptiveAudioStream> audioStream = videoData.getStreamingData().getAdaptiveAudioStreams();
                        Log.d(TAG, "onSuccess: " + audioStream.size());
                        List<MuxedStream> muxedStream = videoData.getStreamingData().getMuxedStreams();
                        qualityArray.clear();
                        qualityArrayInApp.clear();
                        for (int i = 0; i < muxedStream.size(); i++) {
                            if (lastQualityValue != null) {
                                if (lastQualityValue.equals("") || !lastQualityValue.equals(muxedStream.get(i).getQualityLabel())) {
                                    MuxedStream modelYT = muxedStream.get(i);
                                    lastQualityValue = modelYT.getQualityLabel();
                                    YoutubeDataModel youtubeDataModel = new YoutubeDataModel();
                                    modelYT.setUrl(modelYT.getUrl());
                                    youtubeDataModel.setUrl(modelYT.getUrl());
                                    modelYT.setQuality(modelYT.getQuality());
                                    youtubeDataModel.setQuality(modelYT.getQuality());
                                    modelYT.setQualityLabel(modelYT.getQualityLabel());
                                    youtubeDataModel.setQualityLabel(modelYT.getQualityLabel());
                                    if (i == 0) {
                                        youtubeDataModel.setSelected(true);
                                    }
                                    if (modelYT.getQualityLabel() != null) {
                                        qualityArray.add(modelYT);
                                        qualityArrayInApp.add(youtubeDataModel);
                                    }
                                    Log.d(TAG, "onExtractionDone: " + modelYT.getQualityLabel() + " : " + modelYT.getUrl());
                                }
                            }
                        }

                        String url = muxedStream.get(0).getUrl();
                        initializePlayer(url, false);
                        customLay.setVisibility(View.VISIBLE);
                        binding.progressBar.setVisibility(View.GONE);
                        if (videoData.getVideoDetails().getTitle() != null) {
                            titleVideo.setVisibility(View.VISIBLE);
                            titleVideo.setText(videoData.getVideoDetails().getTitle());
                        } else {
                            titleVideo.setVisibility(View.GONE);
                        }
                    }
                }
            }

            @Override
            public void onNetworkException(YoutubeRequestException e) {
                Log.d("TAG", "onNetworkException: " + e.getMessage());
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d("TAG", "onError: " + e.getMessage());
            }
        });
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
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("", "tag %s" + newConfig);
        if (currentMode == newConfig.uiMode) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                isFullScreen = true;
                full_text.setText(getString(R.string.exit_full_screem));
                full_icon.setImageDrawable(getDrawable(R.drawable.ayp_ic_fullscreen_exit_24dp));

            } else {
                isFullScreen = false;
                full_text.setText(getString(R.string.full_screem));
                full_icon.setImageDrawable(getDrawable(R.drawable.ayp_ic_fullscreen_24dp));

            }
        } else {

            Intent intent = new Intent(SingleVideoYoutube.this, SingleVideoYoutube.class);
            intent.putExtra("VideoLink", videoId);
            startActivity(intent);
            finish();
        }


    }

    private class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
        private final ArrayList<SppedModel> dataSet;
        Context mContext;

        public CustomAdapter(ArrayList<SppedModel> dataSet, SingleVideoYoutube SingleVideoYoutube) {
            this.mContext = SingleVideoYoutube;
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
                            speed_text.setText("Speed 0.25x");
                            PlaybackParameters param = new PlaybackParameters(0.25f);
                            simpleExoPlayer.setPlaybackParameters(param);

                        } else if (sppedModel.getId().equals("2")) {
                            speed_text.setText("Speed 0.5x");
                            PlaybackParameters param = new PlaybackParameters(0.5f);
                            simpleExoPlayer.setPlaybackParameters(param);

                        } else if (sppedModel.getId().equals("3")) {
                            speed_text.setText("Speed");
                            PlaybackParameters param = new PlaybackParameters(1f);
                            simpleExoPlayer.setPlaybackParameters(param);

                        } else if (sppedModel.getId().equals("4")) {
                            speed_text.setText("Speed 1.5x");
                            PlaybackParameters param = new PlaybackParameters(1.5f);
                            simpleExoPlayer.setPlaybackParameters(param);

                        } else if (sppedModel.getId().equals("5")) {
                            speed_text.setText("Speed 2x");
                            PlaybackParameters param = new PlaybackParameters(2f);
                            simpleExoPlayer.setPlaybackParameters(param);

                        }

                        speedRecyler.setVisibility(View.GONE);

                    }
                });
            }
        }


        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.speed_lay, parent, false);

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


    public void BottomDialogQuality(List<YoutubeDataModel> adativeStream) {

        RecyclerView qualtyRecycler;
        dialogBottom = new BottomSheetDialog(this, R.style.DialogStyle);
        View sheetView = getLayoutInflater().inflate(R.layout.bottom_dialog_quality, null);
        Objects.requireNonNull(dialogBottom.getWindow())
                .setSoftInputMode(SOFT_INPUT_STATE_VISIBLE);
        dialogBottom.setContentView(sheetView);
        dialogBottom.setCanceledOnTouchOutside(true);
        dialogBottom.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                View bottomSheetInternal = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                BottomSheetBehavior.from(bottomSheetInternal).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        qualtyRecycler = sheetView.findViewById(R.id.qualtyRecycler);

        QualityAdapterInner adapter = new QualityAdapterInner(adativeStream);
        qualtyRecycler.setHasFixedSize(true);
        qualtyRecycler.setLayoutManager(new LinearLayoutManager(this));
        qualtyRecycler.setAdapter(adapter);

        dialogBottom.show();

    }

    private class QualityAdapterInner extends RecyclerView.Adapter<QualityAdapterInner.ViewHolder>{
        private List<YoutubeDataModel> listData;

        public QualityAdapterInner(List<YoutubeDataModel> listData) {
            this.listData = listData;
        }
        @Override
        public QualityAdapterInner.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            View listItem= layoutInflater.inflate(R.layout.list_item, parent, false);
            QualityAdapterInner.ViewHolder viewHolder = new QualityAdapterInner.ViewHolder(listItem);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(QualityAdapterInner.ViewHolder holder, int position) {
            final YoutubeDataModel myListData = listData.get(position);
            holder.textView.setText(myListData.getQualityLabel());
            if (myListData.isSelected()) {
                holder.radioButton.setChecked(true);
            } else {
                holder.radioButton.setChecked(false);
            }
            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogBottom.dismiss();
                    Log.d(TAG, "onClick: " + myListData.isSelected());
                    if (!myListData.isSelected()) {
                        for (int i = 0; i < listData.size(); i++) {
                            if (listData.get(i).getQualityLabel().equals(myListData.getQualityLabel())) {
                                listData.get(i).setSelected(true);
                            } else {
                                listData.get(i).setSelected(false);
                            }
                        }
                        simpleExoPlayer.stop();
                        currentTime = simpleExoPlayer.getCurrentPosition();
                        String url = myListData.getUrl();
                        notifyDataSetChanged();
                        initializePlayer(url, true);
                    }
                }
            });
        }


        @Override
        public int getItemCount() {
            return listData.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView textView;
            public RelativeLayout relativeLayout;
            public RadioButton radioButton;
            public ViewHolder(View itemView) {
                super(itemView);
                this.textView = (TextView) itemView.findViewById(R.id.textView);
                relativeLayout = (RelativeLayout)itemView.findViewById(R.id.relativeLayout);
                radioButton = (RadioButton) itemView.findViewById(R.id.radioCheck);
            }
        }
    }
}