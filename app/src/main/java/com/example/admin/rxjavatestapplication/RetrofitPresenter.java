package com.example.admin.rxjavatestapplication;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import android.util.Log;

import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.example.admin.rxjavatestapplication.schedulers.ObserveOnScheduler;
import com.example.admin.rxjavatestapplication.schedulers.SubscribeOnScheduler;

import rx.Scheduler;
import rx.functions.Action1;

public class RetrofitPresenter {

    private Listener listener;
    private final MyRetroFit myRetroFit;
    private final Scheduler observeOnScheduler;
    private final Scheduler subscribeOnScheduler;

    @Inject
    public RetrofitPresenter(MyRetroFit retroFit,
                             @ObserveOnScheduler Scheduler observeOnScheduler,
                             @SubscribeOnScheduler Scheduler subscribeOnScheduler) { // TODO pass schedulers
        myRetroFit = retroFit;
        this.observeOnScheduler = observeOnScheduler;
        this.subscribeOnScheduler = subscribeOnScheduler;
    }

    public void register(@Nonnull final Listener listener) {
        this.listener = listener;

        listener.showProgress(true);

        listTracks();
    }

    public void listTracks() {
        myRetroFit.listTracks()
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .subscribe(new Action1<SpotifyResponse>() {
                    @Override
                    public void call(SpotifyResponse spotifyResponse) {
                        listener.updateData(spotifyResponse);
                        listener.showProgress(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e("Error", "bind data", throwable);
                    }
                });
    }

    public static interface Listener {

        void showProgress(boolean showProgress);

        void updateData(SpotifyResponse spotifyResponse);
    }

}
