package com.so.luotk.models.videoData;

import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;

public class SpeedModel {
    String id;
    String name;
    PlayerConstants.PlaybackRate playbackRate;
    boolean isSelected = false;

    public SpeedModel(String id, String name, PlayerConstants.PlaybackRate playbackRate, boolean isSelected) {
        this.id = id;
        this.name = name;
        this.playbackRate = playbackRate;
        this.isSelected = isSelected;
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

    public PlayerConstants.PlaybackRate getPlaybackRate() {
        return playbackRate;
    }

    public void setPlaybackRate(PlayerConstants.PlaybackRate playbackRate) {
        this.playbackRate = playbackRate;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
