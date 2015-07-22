package com.example.admin.rxjavatestapplication;


import com.appunite.rx.ResponseOrError;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;

import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

public interface MyRetroFit {

//    https://api.spotify.com/v1/search?q=deorro&type=track&limit=15&offset=0

    @GET("/v1/search?q=deorro&type=track&limit=50")
    Observable<SpotifyResponse> listTracks(@Query("offset") int offset);
}
