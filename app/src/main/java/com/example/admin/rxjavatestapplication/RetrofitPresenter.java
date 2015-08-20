package com.example.admin.rxjavatestapplication;

import android.util.Log;
import android.widget.ImageView;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;

import com.appunite.rx.ObservableExtensions;
import com.appunite.rx.ResponseOrError;
import com.appunite.rx.functions.BothParams;
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
    private final PublishSubject<BothParams<RetrofitPresenter.AdapterItem, ImageView>> openDetailsSubject = PublishSubject.create();
    @Nonnull
    private final Observable<ImmutableList<AdapterItem>> immutableListObservable;
    @Nonnull
    private final SpotifyResponseDao spotifyResponseDao;

    @Inject
    public RetrofitPresenter(@Nonnull final SpotifyResponseDao spotifyResponseDao) {
        this.spotifyResponseDao = spotifyResponseDao;

        immutableListObservable = itemsDaoObservable()
//                .compose(ResponseOrError.<SpotifyResponse>onlySuccess())
                .flatMap(new Func1<SpotifyResponseDao.CachedResult, Observable<ImmutableList<AdapterItem>>>() {
                    @Override
                    public Observable<ImmutableList<AdapterItem>> call(final SpotifyResponseDao.CachedResult spotifyResponse) {
                        return Observable.just(ImmutableList.copyOf(Lists.transform(spotifyResponse.getResponse().getTracks().getItems(),
                                new Function<Item, AdapterItem>() {
                                    @Nullable
                                    @Override
                                    public AdapterItem apply(@Nullable Item item) {
                                        if (item.getFakeOffset() == null) {
                                            item.setFakeOffset(spotifyResponse.getResponse().getTracks().getOffset());
                                        }
                                        return new AdapterItem(
                                                item.getId(),
                                                item.getItemName(),
                                                item.getFakeOffset(),
                                                item.getAlbum().getImages().get(0).getUrl()
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
    public Observable<SpotifyResponseDao.CachedResult> itemsDaoObservable() {
        return this.spotifyResponseDao.spotifyItemsObservable();
    }

    public Observable<BothParams<RetrofitPresenter.AdapterItem, ImageView>> openDetailsObservable() {
        return openDetailsSubject;
    }

    public Observer<Object> loadMoreObserver() {
        return this.spotifyResponseDao.loadMoreObserver();
    }

    public class AdapterItem implements SimpleDetector.Detectable<AdapterItem> {

        @Nonnull
        private final String id;
        @Nullable
        private final String name;
        @Nonnull
        private final String offset;
        @Nonnull
        private final String previewImageUrl;

        public AdapterItem(@Nonnull String id,
                           @Nullable String name,
                           @Nonnull String offset,
                           @Nonnull String previewImageUrl) {
            this.id = id;
            this.name = name;
            this.offset = offset;
            this.previewImageUrl = previewImageUrl;
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
        public String getOffset() {
            return offset;
        }

        @Nonnull
        public String getPreviewImageUrl() {
            return previewImageUrl;
        }

        @Nonnull
        public Observer<ImageView> clickObserver() {
            return Observers.create(new Action1<ImageView>() {
                @Override
                public void call(ImageView imageViewCdCover) {
                    openDetailsSubject.onNext(new BothParams<>(AdapterItem.this, imageViewCdCover));
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AdapterItem that = (AdapterItem) o;
            return Objects.equal(id, that.id) &&
                    Objects.equal(name, that.name) &&
                    Objects.equal(offset, that.offset) &&
                    Objects.equal(previewImageUrl, that.previewImageUrl);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id, name, offset, previewImageUrl);
        }
    }
}