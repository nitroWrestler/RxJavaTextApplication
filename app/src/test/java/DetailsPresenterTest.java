//import com.appunite.rx.ResponseOrError;
//import com.example.admin.rxjavatestapplication.DetailsPresenter;
//import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
//import com.example.admin.rxjavatestapplication.model.Album;
//import com.example.admin.rxjavatestapplication.model.Images;
//import com.example.admin.rxjavatestapplication.model.Item;
//import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
//import com.example.admin.rxjavatestapplication.model.Tracks;
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
//import dagger.ObjectGraph;
//import dagger.Provides;
//import rx.observers.TestObserver;
//import rx.subjects.ReplaySubject;
//
//import static com.google.common.truth.Truth.assert_;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.when;
//
//@RunWith(MockitoJUnitRunner.class)
//public class DetailsPresenterTest extends TestCase {
//
//    @Inject
//    DetailsPresenter presenter;
//
//    @Mock
//    SpotifyResponseDao spotifyResponseDao;
//
//    private ReplaySubject<ResponseOrError<SpotifyResponse>> spotifySubject = ReplaySubject.create();
//    private DetailsPresenter.DetailsPresenterFromId detailsPresenterFromId;
//
//    @Before
//    public void setUp() throws Exception {
//        MockitoAnnotations.initMocks(this);
//
//        when(spotifyResponseDao.clickedItemObservable(anyString())).thenReturn(spotifySubject);
//
//        ObjectGraph.create(new Module()).inject(this);
//    }
//
//    @Test
//    public void testAfterStart_presenterIsNotNull() throws Exception {
//        assert_().that(presenter).isNotNull();
//    }
//
//    @Test
//    public void testAfterSuccessDownload_nameOfTrackIsSet() throws Exception {
//        final TestObserver<String> title = new TestObserver<>();
//        detailsPresenterFromId = presenter.getPresenter("5", "0");
//
//        detailsPresenterFromId.nameObservable().subscribe(title);
//        spotifySubject.onNext(sampleData());
//
//        assert_().that(title.getOnNextEvents().get(0)).isEqualTo("Name of song:\nnameX");
//
//    }
//
//    @Test
//    public void testAfterSuccessDownload_idOfTrackIsSet() throws Exception {
//        final TestObserver<String> title = new TestObserver<>();
//        detailsPresenterFromId = presenter.getPresenter("5", "0");
//
//        detailsPresenterFromId.idObservable().subscribe(title);
//        spotifySubject.onNext(sampleData());
//
//        assert_().that(title.getOnNextEvents().get(0)).isEqualTo("Id of song:\n5");
//    }
//
//    @Test
//    public void testAfterSuccessDownload_durationOfTrackIsSet() throws Exception {
//        final TestObserver<String> title = new TestObserver<>();
//        detailsPresenterFromId = presenter.getPresenter("5", "0");
//
//        detailsPresenterFromId.durationObservable().subscribe(title);
//        spotifySubject.onNext(sampleData());
//
//        assert_().that(title.getOnNextEvents().get(0)).isEqualTo("Duration of song:\nduration");
//    }
//
//    @Test
//    public void testAfterSuccessDownload_popularityOfTrackIsSet() throws Exception {
//        final TestObserver<String> title = new TestObserver<>();
//        detailsPresenterFromId = presenter.getPresenter("5", "0");
//
//        detailsPresenterFromId.popularityObservable().subscribe(title);
//        spotifySubject.onNext(sampleData());
//
//        assert_().that(title.getOnNextEvents().get(0)).isEqualTo("Popularity of song:\npopularity");
//    }
//
//    @Test
//    public void testAfterSuccessDownload_cdCoverImageOfTrackIsSet() throws Exception {
//        final TestObserver<String> title = new TestObserver<>();
//        detailsPresenterFromId = presenter.getPresenter("5", "0");
//
//        detailsPresenterFromId.cdCoverImageObservable().subscribe(title);
//        spotifySubject.onNext(sampleData());
//
//        assert_().that(title.getOnNextEvents().get(0)).isEqualTo("http://mojurl.com");
//    }
//
//    private ResponseOrError<SpotifyResponse> sampleData() {
//        ArrayList<Images> imagesArrayList = new ArrayList<>();
//        imagesArrayList.add(new Images("http://mojurl.com"));
//
//        Item item = new Item("nameX", "5", "duration", "popularity", new Album("id", imagesArrayList));
//        ArrayList<Item> itemList = new ArrayList<>();
//        itemList.add(item);
//        Tracks tracks = new Tracks(itemList, "1");
//        return ResponseOrError.fromData(new SpotifyResponse(tracks));
//    }
//
//    @dagger.Module(
//            injects = {
//                    DetailsPresenterTest.class
//            }
//    )
//    class Module {
//
//        @Provides
//        public DetailsPresenter providesDetailsPresenter() {
//            return new DetailsPresenter(spotifyResponseDao);
//        }
//    }
//}
