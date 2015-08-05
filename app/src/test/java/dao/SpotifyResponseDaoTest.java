//package dao;
//
//import com.appunite.rx.ResponseOrError;
//import com.example.admin.rxjavatestapplication.MyRetroFit;
//import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
//import com.example.admin.rxjavatestapplication.model.Album;
//import com.example.admin.rxjavatestapplication.model.Images;
//import com.example.admin.rxjavatestapplication.model.Item;
//import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
//import com.example.admin.rxjavatestapplication.model.Tracks;
//import com.google.common.collect.Iterables;
//
//import junit.framework.TestCase;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.mockito.runners.MockitoJUnitRunner;
//
//import java.util.ArrayList;
//
//import javax.inject.Inject;
//
//import dagger.Module;
//import dagger.ObjectGraph;
//import dagger.Provides;
//import rx.Observable;
//import rx.Subscriber;
//import rx.observers.TestObserver;
//import rx.schedulers.Schedulers;
//
//import static com.google.common.truth.Truth.assert_;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class SpotifyResponseDaoTest extends TestCase {
//
//    @Inject
//    SpotifyResponseDao spotifyResponseDao;
//
//    @Mock
//    MyRetroFit myRetroFit;
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//
//        when(myRetroFit.listTracks(0)).thenReturn(Observable.just(getSpotifyResponse()));
//        when(myRetroFit.listTracks(50)).thenReturn(Observable.just(getSpotifyResponse2()));
//
//        ObjectGraph.create(new Module()).inject(this);
//    }
//
//    @Test
//    public void testSpotifyResponseDao_isNotNull() throws Exception {
//        assert_().that(spotifyResponseDao).isNotNull();
//    }
//
//    @Test
//    public void testSpotifyItemsObservable_checkItemsAreTheSame() throws Exception {
//        final TestObserver<ResponseOrError<SpotifyResponse>> testObserver = new TestObserver<>();
//
//        spotifyResponseDao.spotifyItemsObservable().subscribe(testObserver);
//
//        assert_().that(testObserver.getOnNextEvents()).hasSize(1);
//        assert_().that(testObserver.getOnNextEvents()
//                .get(0).data().getTracks().getItems().get(0).getItemName())
//                .isEqualTo("nameItem");
//    }
//
//    @Test
//    public void testClickedItemObservable_checkItemsAreTheSame() throws Exception {
//        final TestObserver<ResponseOrError<SpotifyResponse>> testObserver = new TestObserver<>();
//
//        spotifyResponseDao.clickedItemObservable("50").subscribe(testObserver);
//
//        assert_().that(testObserver.getOnNextEvents()).hasSize(1);
//        assert_().that(testObserver.getOnNextEvents()
//                .get(0).data().getTracks().getItems().get(0).getItemName())
//                .isEqualTo("nameItem2");
//    }
//
//    @Test
//    public void testLoadMore_IsWorking() throws Exception {
//        final TestObserver<ResponseOrError<SpotifyResponse>> testObserver = new TestObserver<>();
//        spotifyResponseDao.spotifyItemsObservable().subscribe(testObserver);
//
//        spotifyResponseDao.loadMoreObserver().onNext(null);
//        assert_().that(testObserver.getOnNextEvents()).hasSize(2);
//        assert_().that(Iterables.getLast(testObserver.getOnNextEvents()).data().getTracks().getItems()).hasSize(2);
//    }
//
//    private SpotifyResponse getSpotifyResponse() {
//        final Images images = new Images("http://mojurl.com");
//        final ArrayList<Images> albumList = new ArrayList<>();
//        albumList.add(images);
//        final Album album = new Album("nameAlbum", albumList);
//        final Item item = new Item("nameItem", "idItem", "duration", "popularity", album);
//        final ArrayList<Item> itemList = new ArrayList<>();
//        itemList.add(item);
//        final Tracks tracks = new Tracks(itemList, "0");
//
//        return new SpotifyResponse(tracks);
//    }
//
//    private SpotifyResponse getSpotifyResponse2() {
//        final Images images = new Images("http://mojurl2.com");
//        final ArrayList<Images> albumList = new ArrayList<>();
//        albumList.add(images);
//        final Album album = new Album("nameAlbum2", albumList);
//        final Item item = new Item("nameItem2", "idItem2", "duration2", "popularity2", album);
//        final ArrayList<Item> itemList = new ArrayList<>();
//        itemList.add(item);
//        final Tracks tracks = new Tracks(itemList, "50");
//
//        return new SpotifyResponse(tracks);
//    }
//
//
//    @dagger.Module(
//            injects = {
//                    SpotifyResponseDaoTest.class
//            }
//    )
//    class Module {
//
//        @Provides
//        public SpotifyResponseDao providesSpotifyResponseDao() {
//            return new SpotifyResponseDao(myRetroFit, Schedulers.immediate(), Schedulers.immediate());
//        }
//    }
//
//
//}
