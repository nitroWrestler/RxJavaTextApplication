package com.example.admin.rxjavatestapplication;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.appunite.rx.ObservableExtensions;
import com.appunite.rx.ResponseOrError;
import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
import com.example.admin.rxjavatestapplication.detector.SimpleDetector;
import com.example.admin.rxjavatestapplication.model.Item;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import rx.Observable;
import rx.Observer;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.observers.Observers;
import rx.subjects.PublishSubject;

public class RetrofitPresenter {


    @Nonnull
    private final PublishSubject<AdapterItem> openDetailsSubject = PublishSubject.create();
    @Nonnull
    private final Observable<ImmutableList<AdapterItem>> immutableListObservable;
    @Nonnull
    private final SpotifyResponseDao spotifyResponseDao;

    @Inject
    public RetrofitPresenter(@Nonnull final SpotifyResponseDao spotifyResponseDao) {
        this.spotifyResponseDao = spotifyResponseDao;

        immutableListObservable = itemsDaoObservable()
                .compose(ResponseOrError.<SpotifyResponse>onlySuccess())
                .flatMap(new Func1<SpotifyResponse, Observable<ImmutableList<AdapterItem>>>() {
                    @Override
                    public Observable<ImmutableList<AdapterItem>> call(SpotifyResponse spotifyResponse) {
                        return Observable.just(ImmutableList.copyOf(Lists.transform(spotifyResponse.getTracks().getItems(),
                                new Function<Item, AdapterItem>() {
                                    @Nullable
                                    @Override
                                    public AdapterItem apply(@Nullable Item item) {
                                        return new AdapterItem(
                                                item.getId(),
                                                item.getItemName()
                                        );
                                    }
                                })));
                    }
                })
                .compose(ObservableExtensions.<ImmutableList<AdapterItem>>behaviorRefCount());
    }

    @Nonnull
    public Observable<ImmutableList<AdapterItem>> listObservable() {
        return immutableListObservable;
    }

    @Nonnull
    public Observable<ResponseOrError<SpotifyResponse>> itemsDaoObservable() {
        return this.spotifyResponseDao.spotifyItemsObservable();
    }

    public Observable<AdapterItem> openDetailsObservable() {
        return openDetailsSubject;
    }

    public class AdapterItem implements SimpleDetector.Detectable<AdapterItem> {

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

        @Override
        public boolean matches(@Nonnull AdapterItem item) {
            return Objects.equal(id, item.id);
        }

        @Override
        public boolean same(@Nonnull AdapterItem item) {
            return equals(item);
        }
    }
}