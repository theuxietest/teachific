package com.so.luotk.youtubeExtract;

import com.so.luotk.models.youtubeEx.newModels.VideoPlayerConfig;
import com.so.luotk.youtubeExtract.exception.YoutubeRequestException;

public interface JExtractorCallback {

    void onSuccess(VideoPlayerConfig videoData);

    void onNetworkException(YoutubeRequestException e);

    void onError(Exception exception);
}
