package dao;

import com.example.admin.rxjavatestapplication.MyRetroFit;
import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
import com.example.admin.rxjavatestapplication.helpers.CacheProvider;
import com.example.admin.rxjavatestapplication.helpers.CachedResult;
import com.example.admin.rxjavatestapplication.model.Album;
import com.example.admin.rxjavatestapplication.model.Images;
import com.example.admin.rxjavatestapplication.model.Item;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.example.admin.rxjavatestapplication.model.Tracks;
import com.google.common.collect.Iterables;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import javax.inject.Inject;

import dagger.ObjectGraph;
import dagger.Provides;
import rx.Observable;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.when;

/*
    Testy działją jeżeli wyrzucimy z @Inject SpotifyResponseDao - spotifyResponseObservable:
        - .compose(CacheSubject.behaviorRefCount(cacheProvider.<SpotifyResponse>getCacheCreatorForKey("posts", SpotifyResponse.class)))
        - .compose(MoreOperators.<SpotifyResponse>repeatOnError(MyAndroidSchedulers.NETWORK_SCHEDULER))
 */


@RunWith(MockitoJUnitRunner.class)
public class SpotifyResponseDaoTest extends TestCase {

    @Inject
    SpotifyResponseDao spotifyResponseDao;

    @Mock
    MyRetroFit myRetroFit;

    @Mock
    CacheProvider cacheProvider;

//    private TestScheduler testScheduler = Schedulers.test();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(myRetroFit.listTracks(0)).thenReturn(Observable.just(getSpotifyResponse()));
        when(myRetroFit.listTracks(50)).thenReturn(Observable.just(getSpotifyResponse2()));

        ObjectGraph.create(new Module()).inject(this);
    }

    @Test
    public void testSpotifyResponseDao_isNotNull() throws Exception {
        assert_().that(spotifyResponseDao).isNotNull();
    }

    @Test
    public void testSpotifyItemsObservable_checkItemsAreTheSame() throws Exception {
        final TestSubscriber<CachedResult> testSubscriber = new TestSubscriber<>();

        spotifyResponseDao.spotifyItemsObservable().subscribe(testSubscriber);

        assert_().that(testSubscriber.getOnNextEvents()).hasSize(1);
        assert_().that(testSubscriber.getOnNextEvents()
                .get(0).getResponse().getTracks().getItems().get(0).getItemName())
                .isEqualTo("nameItem");
    }

    @Test
    public void testClickedItemObservable_checkItemsAreTheSame() throws Exception {
        final TestSubscriber<CachedResult> testSubscriber = new TestSubscriber<>();

        spotifyResponseDao.clickedItemObservable("50").subscribe(testSubscriber);

        assert_().that(testSubscriber.getOnNextEvents()).hasSize(1);
        assert_().that(testSubscriber.getOnNextEvents()
                .get(0).getResponse().getTracks().getItems().get(0).getItemName())
                .isEqualTo("nameItem2");
    }

    @Test
    public void testLoadMore_IsWorking() throws Exception {
        final TestSubscriber<CachedResult> testSubscriber = new TestSubscriber<>();
        spotifyResponseDao.spotifyItemsObservable().subscribe(testSubscriber);

        spotifyResponseDao.loadMoreObserver().onNext(null);
        assert_().that(testSubscriber.getOnNextEvents()).hasSize(2);
        assert_().that(Iterables.getLast(testSubscriber.getOnNextEvents()).getResponse().getTracks().getItems()).hasSize(2);
    }

    private SpotifyResponse getSpotifyResponse() {
        final Images images = new Images("http://mojurl.com");
        final ArrayList<Images> albumList = new ArrayList<>();
        albumList.add(images);
        final Album album = new Album("nameAlbum", albumList);
        final Item item = new Item("nameItem", "idItem", "duration", "popularity", album);
        final ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        final Tracks tracks = new Tracks(itemList, "0", 567);

        return new SpotifyResponse(tracks);
    }

    private SpotifyResponse getSpotifyResponse2() {
        final Images images = new Images("http://mojurl2.com");
        final ArrayList<Images> albumList = new ArrayList<>();
        albumList.add(images);
        final Album album = new Album("nameAlbum2", albumList);
        final Item item = new Item("nameItem2", "idItem2", "duration2", "popularity2", album);
        final ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        final Tracks tracks = new Tracks(itemList, "50", 567);

        return new SpotifyResponse(tracks);
    }


    @dagger.Module(
            injects = {
                    SpotifyResponseDaoTest.class
            }
    )
    class Module {

        @Provides
        public SpotifyResponseDao providesSpotifyResponseDao() {
            return new SpotifyResponseDao(myRetroFit, Schedulers.immediate(), Schedulers.immediate(), cacheProvider);
        }
    }


}
