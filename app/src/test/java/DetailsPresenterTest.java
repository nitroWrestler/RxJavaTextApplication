import com.appunite.rx.ResponseOrError;
import com.example.admin.rxjavatestapplication.AdapterItemDetails;
import com.example.admin.rxjavatestapplication.DetailsPresenter;
import com.example.admin.rxjavatestapplication.MyRetroFit;
import com.example.admin.rxjavatestapplication.RetrofitPresenter;
import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
import com.example.admin.rxjavatestapplication.model.Album;
import com.example.admin.rxjavatestapplication.model.Images;
import com.example.admin.rxjavatestapplication.model.Item;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.example.admin.rxjavatestapplication.model.Tracks;
import com.google.common.collect.ImmutableList;

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
import rx.functions.Action1;
import rx.observers.TestObserver;
import rx.schedulers.Schedulers;
import rx.subjects.ReplaySubject;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DetailsPresenterTest extends TestCase {

    @Inject
    DetailsPresenter presenter;

    @Mock
    SpotifyResponseDao spotifyResponseDao;


    private ReplaySubject<ResponseOrError<SpotifyResponse>> spotifySubject = ReplaySubject.create();
    private DetailsPresenter.DetailsPresenterFromId detailsPresenterFromId;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);



        ObjectGraph.create(new Module()).inject(this);


//        ArrayList<Images> imagesArrayList = new ArrayList<>();
//        imagesArrayList.add(new Images("http://mojurl.com"));
//
//        Item item = new Item("name", "duration", "popularity", new Album("id", imagesArrayList));
//        Tracks tracks = new Tracks(item);
//        SpotifyResponse test = new SpotifyResponse(tracks);
//
//        spotifyResponse = Observable.just(test);

    }

    @Test
    public void testAfterStart_presenterIsNotNull() throws Exception {
        assert_().that(presenter).isNotNull();
    }

    @Test
    public void testAfterSuccessDownload_nameOfTrackIsSet() throws Exception {
        final TestObserver<String> title = setUpTests();

        detailsPresenterFromId.nameObservable().subscribe(title);

        assert_().that(title.getOnNextEvents()).isNotNull();
    }

    @Test
    public void testAfterSuccessDownload_idOfTrackIsSet() throws Exception {
        final TestObserver<String> title = setUpTests();

        detailsPresenterFromId.idObservable().subscribe(title);

        assert_().that(title.getOnNextEvents()).isNotNull();
    }

    @Test
    public void testAfterSuccessDownload_durationOfTrackIsSet() throws Exception {
        final TestObserver<String> title = setUpTests();

        detailsPresenterFromId.durationObservable().subscribe(title);

        assert_().that(title.getOnNextEvents()).isNotNull();
    }

    @Test
    public void testAfterSuccessDownload_popularityOfTrackIsSet() throws Exception {
        final TestObserver<String> title = setUpTests();

        detailsPresenterFromId.popularityObservable().subscribe(title);

        assert_().that(title.getOnNextEvents()).isNotNull();
    }

    @Test
    public void testAfterSuccessDownload_cdCoverImageOfTrackIsSet() throws Exception {
        final TestObserver<String> title = setUpTests();

        detailsPresenterFromId.cdCoverImageObservable().subscribe(title);

        assert_().that(title.getOnNextEvents()).isNotNull();
    }



    //Trzeba zrobić testy LoadMora i sprawdzic czy konkretnie w stringu jest nazwa z sampleData()
    @Test
    public void testTest() throws Exception {
        final TestObserver<ImmutableList<RetrofitPresenter.AdapterItem>> items = new TestObserver<>();

        spotifySubject.onNext(sampleData());

        assert_().that(items.getOnNextEvents()).hasSize(1);
        assert_().that(items.getOnNextEvents().get(0).get(0).getName()).isEqualTo("nameItem");
        assert_().that(items.getOnNextEvents().get(0).get(0).getId()).isEqualTo("idItem");
    }

    private TestObserver<String> setUpTests() {
        when(spotifyResponseDao.clickedItemObservable("1")).thenReturn(spotifySubject);
        detailsPresenterFromId = presenter.getPresenter("1", "1");
        return new TestObserver<>();
    }

    private ResponseOrError<SpotifyResponse> sampleData() {
        ArrayList<Images> imagesArrayList = new ArrayList<>();
        imagesArrayList.add(new Images("http://mojurl.com"));

        Item item = new Item("name", "duration", "popularity", new Album("id", imagesArrayList));
        ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        Tracks tracks = new Tracks(itemList, "1");
        return ResponseOrError.fromData(new SpotifyResponse(tracks));
    }

    @dagger.Module(
            injects = {
                    DetailsPresenterTest.class
            }
    )
    class Module {

        @Provides
        public DetailsPresenter providesDetailsPresenter() {
            return new DetailsPresenter(spotifyResponseDao);
        }
    }
}
