package com.example.admin.rxjavatestapplication;


import com.appunite.rx.ObservableExtensions;
import com.appunite.rx.ResponseOrError;
import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
import com.example.admin.rxjavatestapplication.model.Item;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func1;
import rx.subjects.BehaviorSubject;

import static com.google.common.base.Preconditions.checkNotNull;

public class DetailsPresenter {

    @Nonnull
    private final SpotifyResponseDao spotifyResponseDao;

    @Inject
    public DetailsPresenter(@Nonnull final SpotifyResponseDao spotifyResponseDao) {
        this.spotifyResponseDao = spotifyResponseDao;
    }

    @Nonnull
    public DetailsPresenterFromId getPresenter(@Nonnull final String id,
                                               @Nonnull final String offset) {
        checkNotNull(id);
        checkNotNull(offset);
        return new DetailsPresenterFromId(id, offset);
    }

    @Nonnull
    public Observable<ResponseOrError<SpotifyResponse>> itemsDaoObservable(String offset) {
        return this.spotifyResponseDao.clickedItemObservable(offset);
    }

    public class DetailsPresenterFromId {

        @Nonnull
        private final BehaviorSubject<AdapterItemDetails> mRequestSubject = BehaviorSubject.create();

        @Nonnull
        private final Observable<ResponseOrError<String>> mNameObservable, mIdObservable, mDurationObservable,
                mPopularityObservable, mCdCoverImage;

        public DetailsPresenterFromId(@Nonnull final String id,
                                      @Nonnull final String offset) {

            itemsDaoObservable(offset)
                    .compose(ResponseOrError.<SpotifyResponse>onlySuccess())
                    .map(new Func1<SpotifyResponse, ImmutableList<AdapterItemDetails>>() {
                        @Override
                        public ImmutableList<AdapterItemDetails> call(SpotifyResponse spotifyResponse) {
                            return ImmutableList.copyOf(Lists.transform(spotifyResponse.getTracks().getItems(),
                                    new Function<Item, AdapterItemDetails>() {
                                        @Nullable
                                        @Override
                                        public AdapterItemDetails apply(@Nullable Item item) {
                                            assert item != null;
                                            return new AdapterItemDetails(
                                                    item.getId(),
                                                    item.getItemName(),
                                                    item.getDuration(),
                                                    item.getPopularity(),
                                                    item.getAlbum().getImages()
                                            );
                                        }
                                    }));
                        }
                    })
                    .flatMap(new Func1<ImmutableList<AdapterItemDetails>, Observable<AdapterItemDetails>>() {
                        @Override
                        public Observable<AdapterItemDetails> call(ImmutableList<AdapterItemDetails> adapterItemDetailsActivities) {
                            for (AdapterItemDetails adapterItemDetails : adapterItemDetailsActivities) {
                                if (adapterItemDetails.getId().equals(id)) {
                                    return Observable.just(adapterItemDetails);
                                }
                            }
                            return Observable.error(new Throwable());
                        }
                    })
                    .doOnCompleted(new Action0() {
                        @Override
                        public void call() {

                        }
                    })
                    .subscribe(mRequestSubject);

            mNameObservable = mRequestSubject
                    .compose(ResponseOrError.<AdapterItemDetails>toResponseOrErrorObservable())
                    .compose(ResponseOrError.flatMap(new Func1<AdapterItemDetails, Observable<ResponseOrError<String>>>() {
                        @Override
                        public Observable<ResponseOrError<String>> call(AdapterItemDetails adapterItemDetails) {
                            return Observable.just(ResponseOrError.fromData("Name of song:\n" + adapterItemDetails.getName()));
                        }
                    })).compose(ObservableExtensions.<ResponseOrError<String>>behaviorRefCount());

            mIdObservable = mRequestSubject
                    .compose(ResponseOrError.<AdapterItemDetails>toResponseOrErrorObservable())
                    .compose(ResponseOrError.flatMap(new Func1<AdapterItemDetails, Observable<ResponseOrError<String>>>() {
                        @Override
                        public Observable<ResponseOrError<String>> call(AdapterItemDetails adapterItemDetails) {
                            return Observable.just(ResponseOrError.fromData("Id of song:\n" + adapterItemDetails.getId()));
                        }
                    })).compose(ObservableExtensions.<ResponseOrError<String>>behaviorRefCount());

            mDurationObservable = mRequestSubject
                    .compose(ResponseOrError.<AdapterItemDetails>toResponseOrErrorObservable())
                    .compose(ResponseOrError.flatMap(new Func1<AdapterItemDetails, Observable<ResponseOrError<String>>>() {
                        @Override
                        public Observable<ResponseOrError<String>> call(AdapterItemDetails adapterItemDetails) {
                            return Observable.just(ResponseOrError.fromData("Duration of song:\n" + adapterItemDetails.getDuration_ms()));
                        }
                    })).compose(ObservableExtensions.<ResponseOrError<String>>behaviorRefCount());

            mPopularityObservable = mRequestSubject
                    .compose(ResponseOrError.<AdapterItemDetails>toResponseOrErrorObservable())
                    .compose(ResponseOrError.flatMap(new Func1<AdapterItemDetails, Observable<ResponseOrError<String>>>() {
                        @Override
                        public Observable<ResponseOrError<String>> call(AdapterItemDetails adapterItemDetails) {
                            return Observable.just(ResponseOrError.fromData("Popularity of song:\n" + adapterItemDetails.getPopularity()));
                        }
                    })).compose(ObservableExtensions.<ResponseOrError<String>>behaviorRefCount());

            mCdCoverImage = mRequestSubject
                    .compose(ResponseOrError.<AdapterItemDetails>toResponseOrErrorObservable())
                    .compose(ResponseOrError.flatMap(new Func1<AdapterItemDetails, Observable<ResponseOrError<String>>>() {
                        @Override
                        public Observable<ResponseOrError<String>> call(AdapterItemDetails adapterItemDetails) {
                            return Observable.just(ResponseOrError.fromData(adapterItemDetails.getUrlList().get(0).getUrl()));
                        }
                    })).compose(ObservableExtensions.<ResponseOrError<String>>behaviorRefCount());
        }

        public Observable<String> nameObservable() {
            return mNameObservable
                    .compose(ResponseOrError.<String>onlySuccess());
        }

        public Observable<String> idObservable() {
            return mIdObservable
                    .compose(ResponseOrError.<String>onlySuccess());
        }

        public Observable<String> durationObservable() {
            return mDurationObservable
                    .compose(ResponseOrError.<String>onlySuccess());
        }

        public Observable<String> popularityObservable() {
            return mPopularityObservable
                    .compose(ResponseOrError.<String>onlySuccess());
        }

        public Observable<String> cdCoverImageObservable() {
            return mCdCoverImage
                    .compose(ResponseOrError.<String>onlySuccess());
        }
    }
}