package com.so.luotk.activities;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.google.android.exoplayer2.util.Assertions.checkArgument;
import static com.google.android.exoplayer2.util.Assertions.checkNotNull;
import static com.google.android.exoplayer2.util.Assertions.checkState;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.MediaMetadata;
import com.google.android.exoplayer2.util.Util;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.so.luotk.customviews.exoplayer.IntentUtil;
import com.so.luotk.customviews.exoplayer.PlayerActivity;
import com.so.luotk.databinding.ActivityPlayerYoutubeBinding;
import com.so.luotk.databinding.DialogUsbConnectedBinding;
import com.so.luotk.models.youtubeEx.AdaptiveAudioStream;
import com.so.luotk.models.youtubeEx.newModels.VideoPlayerConfig;
import com.so.luotk.models.youtubeEx.youtube.playerResponse.MuxedStream;
import com.so.luotk.utils.PreferenceHandler;
import com.so.luotk.utils.Utilities;
import com.so.luotk.youtubeExtract.JExtractorCallback;
import com.so.luotk.youtubeExtract.YoutubeJExtractor;
import com.so.luotk.youtubeExtract.exception.YoutubeRequestException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayerYoutubeActivity extends AppCompatActivity {
    private static final String TAG = "PlayerYoutubeActivity";
    private String videoId;
    private ActivityPlayerYoutubeBinding binding;
    private int currentMode;
    private BroadcastReceiver broadcast_reciever;
    private String id, isFrom, quality ;
    private boolean connected;
    static boolean active = false;
    private Handler handler;
    private Runnable runnable;
    private long currentTime = 0;
    private YoutubeJExtractor youtubeJExtractor;
    private List<MuxedStream> qualityArray = new ArrayList<>();
    private ArrayList<String> qualityUrl = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Utilities.restrictScreenShot(this);
        Utilities.restrictKeepScreenOn(this);
        binding = ActivityPlayerYoutubeBinding.inflate(getLayoutInflater());
        overridePendingTransition(0, 0);
        Utilities.setUpStatusBar(this);
        Utilities.setLocale(this);
        setContentView(binding.getRoot());

        broadcast_reciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                if (intent.getExtras().getBoolean("connected")) {
                   /* showDisconnectUsbDialog();
                    connected = true;*/
                }
            }
        };
        if (getIntent() != null) {
            videoId = getIntent().getStringExtra("VideoLink");
            isFrom = getIntent().getStringExtra(PreferenceHandler.IS_FROM);
            id = getIntent().getStringExtra("videoID");
            quality = getIntent().getStringExtra("quality");
            try {
                currentTime = getIntent().getLongExtra("currentTime",0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        youtubeJExtractor = new YoutubeJExtractor();
        currentMode = getResources().getConfiguration().uiMode;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);

    }

    private void initializePlayer(String STREAM_URL, boolean playVideo) {

        Log.d(TAG, "initializePlayer: " + STREAM_URL);
        new AsyncTaskExample().execute(STREAM_URL);
    }

    private static final class PlaylistHolder {

        public final String title;
        public final List<MediaItem> mediaItems;

        private PlaylistHolder(String title, List<MediaItem> mediaItems) {
            checkArgument(!mediaItems.isEmpty());
            this.title = title;
            this.mediaItems = Collections.unmodifiableList(new ArrayList<>(mediaItems));
        }
    }
    private static boolean isNonNullAndChecked(@Nullable MenuItem menuItem) {
        // Temporary workaround for layouts that do not inflate the options menu.
        return menuItem != null && menuItem.isChecked();
    }
    private static final class PlaylistGroup {

        public final String title;
        public final List<PlaylistHolder> playlists;

        public PlaylistGroup(String title) {
            this.title = title;
            this.playlists = new ArrayList<>();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        active = true;
        binding.progressBar.setVisibility(View.VISIBLE);
        try {
            registerReceiver(broadcast_reciever, new IntentFilter("android.hardware.usb.action.USB_STATE"));
            if (!connected) {
                Log.d(TAG, "runVideoId: " + videoId);
                handler = new Handler(Looper.myLooper());
                runnable = new Runnable() {
                    @Override
                    public void run() {

                        new ExtractAsync().execute(videoId);
//                        runVideo(videoId);
                    }
                };
                new ExtractAsync().execute(videoId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void runVideo(String youtubeLink) {
        Log.d(TAG, "runVideo: " + youtubeLink);
        youtubeJExtractor.extract(youtubeLink, new JExtractorCallback() {
            @Override
            public void onSuccess(VideoPlayerConfig videoData) {
                Log.d(TAG, "onExtractionDone: " + videoData.getVideoDetails().isLiveContent());
                /*if (videoData.getVideoDetails().isLiveContent()) {
                    String dashManifest = videoData.getStreamingData().getHlsManifestUrl();
                    initializePlayer(dashManifest, false);
                    // or use HLS manifest via getHlsManifestUrl() method
                }
                else {*/

                if (!connected) {
                    if (active) {
                        List<MuxedStream> muxedStream = videoData.getStreamingData().getMuxedStreams();
                        List<AdaptiveAudioStream> adaptiveStream = videoData.getStreamingData().getAdaptiveAudioStreams();

                        for (MuxedStream muxeddata : muxedStream) {
                            qualityArray.add(muxeddata);
                        }
                        String url = "";
                        if (muxedStream.size() > 1) {
                            if (quality.equals("1")) {
                                url = muxedStream.get(0).getUrl();
                            } else {
                                url = muxedStream.get(1).getUrl();
                            }
                        } else {
                            url = muxedStream.get(0).getUrl();
                        }

                        initializePlayer(url, false);
                    }
                }
//                }

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
        unregisterReceiver(broadcast_reciever);
        handler.removeCallbacks(runnable);
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


    private void readPlaylistGroups(JsonReader reader, List<PlaylistGroup> groups)
            throws IOException {
        reader.beginArray();
        while (reader.hasNext()) {
            readPlaylistGroup(reader, groups);
        }
        reader.endArray();
    }

    private void readPlaylistGroup(JsonReader reader, List<PlaylistGroup> groups)
            throws IOException {
        String groupName = "";
        ArrayList<PlaylistHolder> playlistHolders = new ArrayList<>();

        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "name":
                    groupName = reader.nextString();
                    break;
                case "samples":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        playlistHolders.add(readEntry(reader, false));
                    }
                    reader.endArray();
                    break;
                case "_comment":
                    reader.nextString(); // Ignore.
                    break;
                default:
                    throw new IOException("Unsupported name: " + name, /* cause= */ null);
            }
        }
        reader.endObject();

        PlaylistGroup group = getGroup(groupName, groups);
        group.playlists.addAll(playlistHolders);
    }

    private PlaylistHolder readEntry(JsonReader reader, boolean insidePlaylist) throws IOException {
        Uri uri = null;
        String extension = null;
        String title = null;
        ArrayList<PlaylistHolder> children = null;
        Uri subtitleUri = null;
        String subtitleMimeType = null;
        String subtitleLanguage = null;
        UUID drmUuid = null;
        String drmLicenseUri = null;
        ImmutableMap<String, String> drmLicenseRequestHeaders = ImmutableMap.of();
        boolean drmSessionForClearContent = false;
        boolean drmMultiSession = false;
        boolean drmForceDefaultLicenseUri = false;
        MediaItem.ClippingConfiguration.Builder clippingConfiguration =
                new MediaItem.ClippingConfiguration.Builder();

        MediaItem.Builder mediaItem = new MediaItem.Builder();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "name":
                    title = reader.nextString();
                    break;
                case "uri":
                    uri = Uri.parse(reader.nextString());
                    break;
                case "extension":
                    extension = reader.nextString();
                    break;
                case "clip_start_position_ms":
                    clippingConfiguration.setStartPositionMs(reader.nextLong());
                    break;
                case "clip_end_position_ms":
                    clippingConfiguration.setEndPositionMs(reader.nextLong());
                    break;
                case "ad_tag_uri":
                    mediaItem.setAdsConfiguration(
                            new MediaItem.AdsConfiguration.Builder(Uri.parse(reader.nextString())).build());
                    break;
                case "drm_scheme":
                    drmUuid = Util.getDrmUuid(reader.nextString());
                    break;
                case "drm_license_uri":
                case "drm_license_url": // For backward compatibility only.
                    drmLicenseUri = reader.nextString();
                    break;
                case "drm_key_request_properties":
                    Map<String, String> requestHeaders = new HashMap<>();
                    reader.beginObject();
                    while (reader.hasNext()) {
                        requestHeaders.put(reader.nextName(), reader.nextString());
                    }
                    reader.endObject();
                    drmLicenseRequestHeaders = ImmutableMap.copyOf(requestHeaders);
                    break;
                case "drm_session_for_clear_content":
                    drmSessionForClearContent = reader.nextBoolean();
                    break;
                case "drm_multi_session":
                    drmMultiSession = reader.nextBoolean();
                    break;
                case "drm_force_default_license_uri":
                    drmForceDefaultLicenseUri = reader.nextBoolean();
                    break;
                case "subtitle_uri":
                    subtitleUri = Uri.parse(reader.nextString());
                    break;
                case "subtitle_mime_type":
                    subtitleMimeType = reader.nextString();
                    break;
                case "subtitle_language":
                    subtitleLanguage = reader.nextString();
                    break;
                case "playlist":
                    checkState(!insidePlaylist, "Invalid nesting of playlists");
                    children = new ArrayList<>();
                    reader.beginArray();
                    while (reader.hasNext()) {
                        children.add(readEntry(reader, /* insidePlaylist= */ true));
                    }
                    reader.endArray();
                    break;
                default:
                    throw new IOException("Unsupported attribute name: " + name, /* cause= */ null);
            }
        }
        reader.endObject();

        if (children != null) {
            List<MediaItem> mediaItems = new ArrayList<>();
            for (int i = 0; i < children.size(); i++) {
                mediaItems.addAll(children.get(i).mediaItems);
            }
            return new PlaylistHolder(title, mediaItems);
        } else {
            @Nullable
            String adaptiveMimeType =
                    Util.getAdaptiveMimeTypeForContentType(Util.inferContentType(uri, extension));
            mediaItem
                    .setUri(uri)
                    .setMediaMetadata(new MediaMetadata.Builder().setTitle(title).build())
                    .setMimeType(adaptiveMimeType)
                    .setClippingConfiguration(clippingConfiguration.build());
            if (drmUuid != null) {
                mediaItem.setDrmConfiguration(
                        new MediaItem.DrmConfiguration.Builder(drmUuid)
                                .setLicenseUri(drmLicenseUri)
                                .setLicenseRequestHeaders(drmLicenseRequestHeaders)
                                .forceSessionsForAudioAndVideoTracks(drmSessionForClearContent)
                                .setMultiSession(drmMultiSession)
                                .setForceDefaultLicenseUri(drmForceDefaultLicenseUri)
                                .build());
            } else {
                checkState(drmLicenseUri == null, "drm_uuid is required if drm_license_uri is set.");
                checkState(
                        drmLicenseRequestHeaders.isEmpty(),
                        "drm_uuid is required if drm_key_request_properties is set.");
                checkState(
                        !drmSessionForClearContent,
                        "drm_uuid is required if drm_session_for_clear_content is set.");
                checkState(!drmMultiSession, "drm_uuid is required if drm_multi_session is set.");
                checkState(
                        !drmForceDefaultLicenseUri,
                        "drm_uuid is required if drm_force_default_license_uri is set.");
            }
            if (subtitleUri != null) {
                MediaItem.SubtitleConfiguration subtitleConfiguration =
                        new MediaItem.SubtitleConfiguration.Builder(subtitleUri)
                                .setMimeType(
                                        checkNotNull(
                                                subtitleMimeType,
                                                "subtitle_mime_type is required if subtitle_uri is set."))
                                .setLanguage(subtitleLanguage)
                                .build();
                mediaItem.setSubtitleConfigurations(ImmutableList.of(subtitleConfiguration));
            }
            return new PlaylistHolder(title, Collections.singletonList(mediaItem.build()));
        }
    }

    private PlaylistGroup getGroup(String groupName, List<PlaylistGroup> groups) {
        for (int i = 0; i < groups.size(); i++) {
            if (Util.areEqual(groupName, groups.get(i).title)) {
                return groups.get(i);
            }
        }
        PlaylistGroup group = new PlaylistGroup(groupName);
        groups.add(group);
        return group;
    }

    public class AsyncTaskExample extends AsyncTask<String, String, String>{
        String codeee = "200";
        String streamUrl = "";
        @Override
        protected String doInBackground(String... strings) {
            streamUrl = strings[0];
            try {
                URL url = new URL(streamUrl);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                codeee = String.valueOf(connection.getResponseCode());

            } catch (IOException e) {
                e.printStackTrace();
            }


            return codeee;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.equals("403")) {
                Intent intent = new Intent(PlayerYoutubeActivity.this, SingleIframeVideo.class);
                intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
                intent.putExtra("videoID", id);
                intent.putExtra("VideoLink", videoId);
                startActivity(intent);
                finish();
                return;
            } else {
                runNextEvent(streamUrl);
            }
        }
    }

    public class ExtractAsync extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... strings) {
            try {
                runVideo(strings[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "done";
        }
    }

    public void runNextEvent(String STREAM_URL){
        String extension = null;
        String title = null;

        String drmLicenseUri = null;
        ImmutableMap<String, String> drmLicenseRequestHeaders = ImmutableMap.of();
        boolean drmSessionForClearContent = false;
        boolean drmMultiSession = false;
        boolean drmForceDefaultLicenseUri = false;
        MediaItem.ClippingConfiguration.Builder clippingConfiguration =
                new MediaItem.ClippingConfiguration.Builder();
        MediaItem.Builder mediaItem = new MediaItem.Builder();
        String adaptiveMimeType =
                Util.getAdaptiveMimeTypeForContentType(Util.inferContentType(Uri.parse(STREAM_URL), extension));
        mediaItem
                .setUri(STREAM_URL)
                .setMediaMetadata(new MediaMetadata.Builder().setTitle("ff").build())
                .setMimeType(adaptiveMimeType)
                .setClippingConfiguration(clippingConfiguration.build());

        checkState(drmLicenseUri == null, "drm_uuid is required if drm_license_uri is set.");
        checkState(
                drmLicenseRequestHeaders.isEmpty(),
                "drm_uuid is required if drm_key_request_properties is set.");
        checkState(
                !drmSessionForClearContent,
                "drm_uuid is required if drm_session_for_clear_content is set.");
        checkState(!drmMultiSession, "drm_uuid is required if drm_multi_session is set.");
        checkState(
                !drmForceDefaultLicenseUri,
                "drm_uuid is required if drm_force_default_license_uri is set.");

        PlaylistHolder playlistHolder = new PlaylistHolder(title, Collections.singletonList(mediaItem.build()));
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PreferenceHandler.IS_FROM, isFrom);
        intent.putExtra("videoID", id);
        intent.putExtra("qualityArray", new Gson().toJson(qualityArray));
        intent.putExtra("quality", quality);
        intent.putExtra("VideoLink", videoId);
        intent.putExtra("currentTime", currentTime);
//        intent.putExtra(IntentUtil.PREFER_EXTENSION_DECODERS_EXTRA,isNonNullAndChecked(preferExtensionDecodersMenuItem));
        IntentUtil.addToIntent(playlistHolder.mediaItems, intent);
        startActivity(intent);
        finish();
    }
}