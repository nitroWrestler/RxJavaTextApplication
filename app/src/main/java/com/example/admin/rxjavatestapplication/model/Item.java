package com.example.admin.rxjavatestapplication.model;

import android.support.annotation.NonNull;

import javax.annotation.Nonnull;

public class Item {

    private String name;

    private String duration_ms;

    private String popularity;

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

    public Item(String name, Album album) {
        this.name = name;
        this.album = album;
    }

//    public Item(Album album) {
//        this.album = album;
//    }

    public Item(String name, String duration_ms, String popularity, Album album) {
        this.name = name;
        this.duration_ms = duration_ms;
        this.popularity = popularity;
        this.album = album;
    }
}
