package com.example.admin.rxjavatestapplication.dao;

import com.appunite.rx.ResponseOrError;
import com.example.admin.rxjavatestapplication.MyRetroFit;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.example.admin.rxjavatestapplication.schedulers.ObserveOnScheduler;
import com.example.admin.rxjavatestapplication.schedulers.SubscribeOnScheduler;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Scheduler;

//@Singleton
public class SpotifyResponseDao {

    @Nonnull
    Observable<ResponseOrError<SpotifyResponse>> spotifyResponseObservable;

    private final MyRetroFit myRetroFit;
    private final Scheduler observeOnScheduler;
    private final Scheduler subscribeOnScheduler;

    @Inject
    public SpotifyResponseDao(@Nonnull MyRetroFit retroFit,
                              @ObserveOnScheduler Scheduler observeOnScheduler,
                              @SubscribeOnScheduler Scheduler subscribeOnScheduler) {
        myRetroFit = retroFit;
        this.observeOnScheduler = observeOnScheduler;
        this.subscribeOnScheduler = subscribeOnScheduler;

        spotifyResponseObservable = myRetroFit.listTracks(0)
                .compose(ResponseOrError.<SpotifyResponse>toResponseOrErrorObservable())
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    @Nonnull
    public Observable<ResponseOrError<SpotifyResponse>> spotifyItemsObservable() {
        return spotifyResponseObservable;
    }
}


//                .map(new Func1<SpotifyResponse, ImmutableList<RetrofitPresenter.AdapterItem>>() {
//                    @Override
//                    public ImmutableList<RetrofitPresenter.AdapterItem> call(SpotifyResponse spotifyResponse) {
//                        return ImmutableList.copyOf(Lists.transform(spotifyResponse.getTracks().getItems(),
//                                new Function<Item, RetrofitPresenter.AdapterItem>() {
//                                    @Nullable
//                                    @Override
//                                    public RetrofitPresenter.AdapterItem apply(@Nullable Item item) {
//                                        return new RetrofitPresenter.AdapterItem(
//                                                item.getAlbum().getId(),
//                                                item.getItemName());
//                                    }
//                                }));
//                    }
//                })
//                .compose(ResponseOrError.<ImmutableList<RetrofitPresenter.AdapterItem>>toResponseOrErrorObservable())
//                .subscribeOn(subscribeOnScheduler)
//                .observeOn(observeOnScheduler);
