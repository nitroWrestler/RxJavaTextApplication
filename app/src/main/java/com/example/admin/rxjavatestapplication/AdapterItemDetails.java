package com.example.admin.rxjavatestapplication;

import com.example.admin.rxjavatestapplication.model.Images;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AdapterItemDetails {

    @Nonnull
    private final String id;
    @Nullable
    private final String name;
    @Nullable
    private final String duration_ms;
    @Nullable
    private final String popularity;
    @Nullable
    private List urlImage;

    public AdapterItemDetails(@Nonnull String id,
                              @Nullable String name,
                              @Nullable String duration_ms,
                              @Nullable String popularity,
                              @Nullable List<Images> urlImage) {
        this.id = id;
        this.name = name;
        this.duration_ms = duration_ms;
        this.popularity = popularity;
        this.urlImage = urlImage;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Nullable
    public String getName() {
        return name;
    }

    @Nonnull
    public String getDuration_ms() {
        return duration_ms;
    }

    @Nonnull
    public String getPopularity() {
        return popularity;
    }

    @Nonnull
    List<Images> getUrlList() {
        return urlImage;
    }
}

