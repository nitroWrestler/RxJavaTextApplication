package com.example.admin.rxjavatestapplication.model;

import android.support.annotation.NonNull;

import javax.annotation.Nonnull;

public class Item {

    private String name;

    private String duration_ms;

    private String popularity;

    private String id;

    private String fakeOffset;

    private Album album;

    @NonNull
    public Album getAlbum() {
        return album;
    }

    public String getItemName() {
        return name;
    }

    public String getDuration() {
        return duration_ms;
    }

    public String getPopularity() {
        return popularity;
    }

    public String getId() {
        return id;
    }

    public Item(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public Item(String name, String id,  String duration_ms, String popularity, Album album) {
        this.name = name;
        this.duration_ms = duration_ms;
        this.popularity = popularity;
        this.album = album;
        this.id = id;
    }

    public void setFakeOffset(String fakeOffset) {
        this.fakeOffset = fakeOffset;
    }

    public String getFakeOffset() {
        return fakeOffset;
    }
}
