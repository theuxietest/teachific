package com.so.luotk.models.youtubeEx;

import android.os.Parcelable;

import com.so.luotk.models.youtubeEx.newModels.AdaptiveFormatsItem;
import com.so.luotk.models.youtubeEx.youtube.playerResponse.Cipher;

import java.io.Serializable;

public abstract class StreamItem implements Parcelable, Serializable {

    protected String extension;
    protected String codec;
    protected int bitrate;
    protected int iTag;
    protected String url;
    protected int averageBitrate;
    protected Integer approxDurationMs;
    protected Cipher cipher;

    protected StreamItem(String extension, String codec, int bitrate, int iTag, String url, int averageBitrate, int approxDurationMs) {
        this.extension = extension;
        this.codec = codec;
        this.bitrate = bitrate;
        this.iTag = iTag;
        this.url = url;
        this.averageBitrate = averageBitrate;
        this.approxDurationMs = approxDurationMs;
    }

    protected StreamItem(AdaptiveFormatsItem adaptiveStream) {
        String[] mimeTypeArr = adaptiveStream.getMimeType().split("[/;]");
        extension = mimeTypeArr[1];
        codec = mimeTypeArr[2].split("=")[1].replaceAll("\"", "");
        url = adaptiveStream.getUrl();
        iTag = adaptiveStream.getItag();
        bitrate = adaptiveStream.getBitrate();
        averageBitrate = adaptiveStream.getAverageBitrate();
        String rawDuration = adaptiveStream.getApproxDurationMs();
        approxDurationMs = rawDuration == null ? 0 : Integer.valueOf(rawDuration);
        cipher = adaptiveStream.getCipher();
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getCodec() {
        return codec;
    }

    public void setCodec(String codec) {
        this.codec = codec;
    }

    public int getBitrate() {
        return bitrate;
    }

    public void setBitrate(int bitrate) {
        this.bitrate = bitrate;
    }

    public int getAverageBitrate() {
        return averageBitrate;
    }

    public void setAverageBitrate(int averageBitrate) {
        this.averageBitrate = averageBitrate;
    }

    public int getiTag() {
        return iTag;
    }

    public void setiTag(int iTag) {
        this.iTag = iTag;
    }

    public String getUrl() {
        if (url == null && getCipher() != null) {
            url = String.format("%s&%s=%s", getCipher().getUrl(), getCipher().getSp(), getCipher().getS());
        }
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getApproxDurationMs() {
        return approxDurationMs;
    }

    public void setApproxDurationMs(int approxDurationMs) {
        this.approxDurationMs = approxDurationMs;
    }

    public Cipher getCipher() {
        return cipher;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StreamItem that = (StreamItem) o;

        if (bitrate != that.bitrate) return false;
        if (iTag != that.iTag) return false;
        if (averageBitrate != that.averageBitrate) return false;
        if (extension != null ? !extension.equals(that.extension) : that.extension != null)
            return false;
        if (codec != null ? !codec.equals(that.codec) : that.codec != null) return false;
        if (url != null ? !url.equals(that.url) : that.url != null) return false;
        return approxDurationMs != null ? approxDurationMs.equals(that.approxDurationMs) : that.approxDurationMs == null;
    }

    @Override
    public int hashCode() {
        int result = extension != null ? extension.hashCode() : 0;
        result = 31 * result + (codec != null ? codec.hashCode() : 0);
        result = 31 * result + bitrate;
        result = 31 * result + iTag;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + averageBitrate;
        result = 31 * result + (approxDurationMs != null ? approxDurationMs.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StreamItem{" +
                "extension='" + extension + '\'' +
                ", codec='" + codec + '\'' +
                ", bitrate=" + bitrate +
                ", averageBitrate=" + averageBitrate +
                ", iTag=" + iTag +
                ", url='" + url + '\'' +
                ", approxDurationMs=" + approxDurationMs +
                '}';
    }
}
