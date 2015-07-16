package com.example.admin.rxjavatestapplication.model;

public class SpotifyResponse {

    private Tracks tracks;

    public Tracks getTracks() {
        return tracks;
    }

    public SpotifyResponse(Tracks tracks) {
        this.tracks = tracks;
    }

}
