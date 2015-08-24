package com.example.admin.rxjavatestapplication.helpers;

import android.support.annotation.Nullable;

import com.example.admin.rxjavatestapplication.model.SpotifyResponse;

public class CachedResult {
    private final @Nullable
    SpotifyResponse response;
    private final @Nullable Throwable throwable;

    public CachedResult(@Nullable SpotifyResponse response, @Nullable Throwable throwable) {
        this.response = response;
        this.throwable = throwable;
    }

    @Nullable
    public SpotifyResponse getResponse() {
        return response;
    }

    @Nullable
    public Throwable getThrowable() {
        return throwable;
    }
}
