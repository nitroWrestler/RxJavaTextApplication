package com.example.admin.rxjavatestapplication;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.example.admin.rxjavatestapplication.schedulers.ObserveOnScheduler;
import com.example.admin.rxjavatestapplication.schedulers.SubscribeOnScheduler;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.functions.Action1;
import rx.observers.Observers;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

public class RetrofitPresenter {

    @Nonnull
    private final Subject<AdapterItem, AdapterItem> openDetailsSubject = PublishSubject.create();

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
        private final String text;

        public AdapterItem(@Nonnull String id,
                           @Nullable String text) {
            this.id = id;
            this.text = text;
        }

        @Nonnull
        public String getId() {
            return id;
        }

        @Nullable
        public String getText() {
            return text;
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

    public static interface Listener {

        void showProgress(boolean showProgress);

        void updateData(SpotifyResponse spotifyResponse);

        void showButtonView(boolean showButtonView);
    }
}




























