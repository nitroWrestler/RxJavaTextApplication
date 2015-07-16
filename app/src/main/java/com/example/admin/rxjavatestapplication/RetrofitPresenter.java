package com.example.admin.rxjavatestapplication;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.appunite.rx.ResponseOrError;
import com.example.admin.rxjavatestapplication.model.Item;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.example.admin.rxjavatestapplication.schedulers.ObserveOnScheduler;
import com.example.admin.rxjavatestapplication.schedulers.SubscribeOnScheduler;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.subjects.PublishSubject;

public class RetrofitPresenter {


    @Nonnull
    private final PublishSubject<AdapterItem> openDetailsSubject = PublishSubject.create();

    private Listener listener;
    private final MyRetroFit myRetroFit;
    private final Scheduler observeOnScheduler;
    private final Scheduler subscribeOnScheduler;

    @Inject
    public RetrofitPresenter(@Nonnull MyRetroFit retroFit,
                             @ObserveOnScheduler Scheduler observeOnScheduler,
                             @SubscribeOnScheduler Scheduler subscribeOnScheduler) {
        myRetroFit = retroFit;
        this.observeOnScheduler = observeOnScheduler;
        this.subscribeOnScheduler = subscribeOnScheduler;
    }

    public void register(@Nonnull final Listener listener) {
        this.listener = listener;

        listTracksPresenter();
    }

    public void listTracksPresenter() {
        listener.showProgress(true);
        myRetroFit.listTracks()
                .map(new Func1<SpotifyResponse, List<AdapterItem>>() {
                    @Override
                    public List<AdapterItem> call(SpotifyResponse spotifyResponse) {
                        return Lists.transform(spotifyResponse.getTracks().getItems(),
                                new Function<Item, AdapterItem>() {
                                    @Nullable
                                    @Override
                                    public AdapterItem apply(@Nullable Item item) {
                                        return new AdapterItem(
                                                item.getAlbum().getId(),
                                                item.getItemName());
                                    }
                                });
                    }
                })
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .subscribe(new Action1<List<AdapterItem>>() {
                    @Override
                    public void call(List<AdapterItem> spotifyResponse) {
                        listener.updateData(spotifyResponse);
                        listener.showProgress(false);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        listener.showButtonView(true);
                        listener.showProgress(false);
                    }
                });
    }

    public void refreshClick() {
        listener.showButtonView(false);
        listTracksPresenter();
    }

    @Nonnull
    public Observable<AdapterItem> openDetailsObservable() {
        return openDetailsSubject;
    }

    public class AdapterItem {

        @Nonnull
        private final String id;
        @Nullable
        private final String name;

        public AdapterItem(@Nonnull String id,
                           @Nullable String name) {
            this.id = id;
            this.name = name;
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
        public Observer<Object> clickObserver() {
            return Observers.create(new Action1<Object>() {
                @Override
                public void call(Object o) {
                    openDetailsSubject.onNext(AdapterItem.this);
                }
            });
        }
    }

    public interface Listener {

        void showProgress(boolean showProgress);

        void updateData(List<AdapterItem> spotifyResponse);

        void showButtonView(boolean showButtonView);
    }
}