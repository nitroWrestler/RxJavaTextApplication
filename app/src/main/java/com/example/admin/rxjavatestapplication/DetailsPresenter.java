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

    @Inject
    public DetailsPresenter(@Nonnull MyRetroFit retroFit,
                            @ObserveOnScheduler Scheduler observeOnScheduler,
                            @SubscribeOnScheduler Scheduler subscribeOnScheduler) {
        myRetroFit = retroFit;
        this.observeOnScheduler = observeOnScheduler;
        this.subscribeOnScheduler = subscribeOnScheduler;
    }

    @Nonnull
    public DetailsPresenterFromId getPresenter(@Nonnull final String id) {
        checkNotNull(id);
        return new DetailsPresenterFromId(id);
    }

    public class DetailsPresenterFromId {

        private final BehaviorSubject<AdapterItemDetails> mRequestSubject = BehaviorSubject.create();
        private final Observable<String> mNameObservable, mIdObservable, mDurationObservable,
                mPopularityObservable, mCdCoverImage;

        public DetailsPresenterFromId(@Nonnull final String id) {

            myRetroFit.listTracks()
                    .map(new Func1<SpotifyResponse, List<AdapterItemDetails>>() {
                        @Override
                        public List<AdapterItemDetails> call(SpotifyResponse spotifyResponse) {
                            return Lists.transform(spotifyResponse.getTracks().getItems(),
                                    new Function<Item, AdapterItemDetails>() {
                                        @Nullable
                                        @Override
                                        public AdapterItemDetails apply(@Nullable Item item) {
                                            assert item != null;
                                            return new AdapterItemDetails(
                                                    item.getAlbum().getId(),
                                                    item.getItemName(),
                                                    item.getDuration(),
                                                    item.getPopularity(),
                                                    item.getAlbum().getImages()
                                            );
                                        }
                                    });
                        }
                    })
                    .subscribeOn(subscribeOnScheduler)
                    .observeOn(observeOnScheduler)
                    .flatMap(new Func1<List<AdapterItemDetails>, Observable<AdapterItemDetails>>() {
                        @Override
                        public Observable<AdapterItemDetails> call(List<AdapterItemDetails> adapterItemDetailsActivities) {
                            for (AdapterItemDetails adapterItemDetails : adapterItemDetailsActivities) {
                                if (adapterItemDetails.getId().equals(id)) {
                                    return Observable.just(adapterItemDetails);
                                }
                            }
                            return Observable.error(new Throwable());
                        }
                    })
                    .subscribe(mRequestSubject);

            mNameObservable = mRequestSubject
                    .flatMap(new Func1<AdapterItemDetails, Observable<String>>() {
                        @Override
                        public Observable<String> call(AdapterItemDetails adapterItemDetails) {
                            return Observable.just("Name of song:\n" + adapterItemDetails.getName());
                        }
                    });

            mIdObservable = mRequestSubject
                    .flatMap(new Func1<AdapterItemDetails, Observable<String>>() {
                        @Override
                        public Observable<String> call(AdapterItemDetails adapterItemDetails) {
                            return Observable.just("Id of song:\n" + adapterItemDetails.getId());
                        }
                    });

            mDurationObservable = mRequestSubject
                    .flatMap(new Func1<AdapterItemDetails, Observable<String>>() {
                                 @Override
                                 public Observable<String> call(AdapterItemDetails adapterItemDetails) {
                                     return Observable.just("Duration of song:\n" + adapterItemDetails.getDuration_ms());
                                 }
                             }
                    );

            mPopularityObservable = mRequestSubject
                    .flatMap(new Func1<AdapterItemDetails, Observable<String>>() {
                        @Override
                        public Observable<String> call(AdapterItemDetails adapterItemDetails) {
                            return Observable.just("Popularity of song:\n" + adapterItemDetails.getPopularity());
                        }
                    });

            mCdCoverImage = mRequestSubject
                    .flatMap(new Func1<AdapterItemDetails, Observable<String>>() {
                        @Override
                        public Observable<String> call(AdapterItemDetails adapterItemDetails) {
                            return Observable.just(adapterItemDetails.getUrlList().get(0).getUrl());
                        }
                    });
        }

        public Observable<String> nameObservable() {
            return mNameObservable;
        }

        public Observable<String> idObservable() {
            return mIdObservable;
        }

        public Observable<String> durationObservable() {
            return mDurationObservable;
        }

        public Observable<String> popularityObservable() {
            return mPopularityObservable;
        }

        public Observable<String> cdCoverImageObservable() {
            return mCdCoverImage;
        }
    }


}
