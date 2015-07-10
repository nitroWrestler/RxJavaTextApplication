package com.example.admin.rxjavatestapplication;


import com.example.admin.rxjavatestapplication.model.SpotifyResponse;

import retrofit.http.GET;
import rx.Observable;

public interface MyRetroFit {

//    https://api.spotify.com/v1/search?q=deorro&type=track&limit=5

    @GET("/v1/search?q=deorro&type=track&limit=5")
    Observable<SpotifyResponse> listTracks();
}
