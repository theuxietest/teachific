package com.so.luotk.youtubeExtract.exception;

public class SignatureDecryptionException extends Exception {

    public SignatureDecryptionException(String message) {
        super(message);
    }

    public SignatureDecryptionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SignatureDecryptionException(Throwable cause) {
        super(cause);
    }
}
