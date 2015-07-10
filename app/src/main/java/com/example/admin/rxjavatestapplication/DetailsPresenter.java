package com.example.admin.rxjavatestapplication;


import com.example.admin.rxjavatestapplication.model.Item;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.example.admin.rxjavatestapplication.schedulers.ObserveOnScheduler;
import com.example.admin.rxjavatestapplication.schedulers.SubscribeOnScheduler;
import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

import static com.google.common.base.Preconditions.checkNotNull;

public class DetailsPresenter {


    private final MyRetroFit myRetroFit;
    private final Scheduler observeOnScheduler;
    private final Scheduler subscribeOnScheduler;
    Observable<List<AdapterItemDetailsActivity>> mListObservable;

    @Inject
    public DetailsPresenter(@Nonnull MyRetroFit retroFit,
                            @ObserveOnScheduler Scheduler observeOnScheduler,
                            @SubscribeOnScheduler Scheduler subscribeOnScheduler) {
        myRetroFit = retroFit;
        this.observeOnScheduler = observeOnScheduler;
        this.subscribeOnScheduler = subscribeOnScheduler;

        mListObservable = myRetroFit.listTracks()
                .map(new Func1<SpotifyResponse, List<AdapterItemDetailsActivity>>() {
                    @Override
                    public List<AdapterItemDetailsActivity> call(SpotifyResponse spotifyResponse) {
                        return Lists.transform(spotifyResponse.getTracks().getItems(),
                                new Function<Item, AdapterItemDetailsActivity>() {
                                    @Nullable
                                    @Override
                                    public AdapterItemDetailsActivity apply(@Nullable Item item) {
                                        return new AdapterItemDetailsActivity(item.getAlbum().getId(), item.getAlbum().getName());
                                    }
                                });
                    }
                })
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    @Nonnull
    public DetailsPresenterFromId getPresenter(@Nonnull final String id) {
        checkNotNull(id);
        return new DetailsPresenterFromId(id);
    }

    public class DetailsPresenterFromId {

        private final String id;
        private final Observable<String> nameObservable;
        private final BehaviorSubject<AdapterItemDetailsActivity> mRequestSubject = BehaviorSubject.create();
        private final Observable<String> mNameObservable;

        public DetailsPresenterFromId(@Nonnull final String id) {
            this.id = id;

            mListObservable
                    .flatMap(new Func1<List<AdapterItemDetailsActivity>, Observable<AdapterItemDetailsActivity>>() {
                        @Override
                        public Observable<AdapterItemDetailsActivity> call(List<AdapterItemDetailsActivity> adapterItemDetailsActivities) {
                            for (AdapterItemDetailsActivity adapterItemDetailsActivity : adapterItemDetailsActivities) {
                                if (adapterItemDetailsActivity.getId().equals(id)) {
                                    return Observable.just(adapterItemDetailsActivity);
                                }
                            }
                            return Observable.error(new Throwable());
                        }
                    })
                    .subscribe(mRequestSubject);

            mNameObservable = mRequestSubject
                    .flatMap(new Func1<AdapterItemDetailsActivity, Observable<String>>() {
                        @Override
                        public Observable<String> call(AdapterItemDetailsActivity adapterItemDetailsActivity) {
                            return Observable.just(adapterItemDetailsActivity.getName());
                        }
                    });
            nameObservable = Observable.just(id);
        }

        public Observable<String> nameObservable() {
            return mNameObservable;
        }


    }


}
