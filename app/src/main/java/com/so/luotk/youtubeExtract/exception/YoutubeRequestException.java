package com.so.luotk.youtubeExtract.exception;

public class YoutubeRequestException extends Exception {

    public YoutubeRequestException(String message) {
        super(message);
    }

    public YoutubeRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public YoutubeRequestException(Throwable cause) {
        super(cause);
    }
}
