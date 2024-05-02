package com.so.luotk.viewmodel;

public class AuthResource<T> {
    public final AuthStatus status;
    public final T data;
    public final String message;

    public AuthResource(AuthStatus status, T data, String message) {
        this.status = status;
        this.data = data;
        this.message = message;
    }

    public static <T> AuthResource<T> authenticated(T data) {
        return new AuthResource<T>(AuthStatus.AUTHENTICATED, data, null);
    }

    public static <T> AuthResource<T> error(String msg, T data) {
        return new AuthResource<T>(AuthStatus.ERROR, data, msg);
    }

    public static <T> AuthResource<T> loading(T data) {
        return new AuthResource<T>(AuthStatus.LOADING, data, null);
    }

    public static <T> AuthResource<T> not_authenticated() {
        return new AuthResource<T>(AuthStatus.NOT_AUTHENTICATED, null, null);
    }

    public static <T> AuthResource<T> extra(String message) {
        return new AuthResource<T>(AuthStatus.EXTRA, null, message);
    }

    public enum AuthStatus {AUTHENTICATED, ERROR, LOADING, NOT_AUTHENTICATED, EXTRA}

    public AuthStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
