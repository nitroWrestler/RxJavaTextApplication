import com.example.admin.rxjavatestapplication.DetailsPresenter;
import com.example.admin.rxjavatestapplication.MyRetroFit;
import com.example.admin.rxjavatestapplication.model.Album;
import com.example.admin.rxjavatestapplication.model.Images;
import com.example.admin.rxjavatestapplication.model.Item;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.example.admin.rxjavatestapplication.model.Tracks;

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
import rx.observers.TestObserver;
import rx.schedulers.Schedulers;

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
    MyRetroFit myRetroFit;

    private DetailsPresenter.DetailsPresenterFromId detailsPresenterFromId;

    private Observable<SpotifyResponse> spotifyResponse;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        ArrayList<Images> imagesArrayList = new ArrayList<>();
        imagesArrayList.add(new Images("http://mojurl.com"));

        Item item = new Item("name", "duration", "popularity", new Album("id", imagesArrayList));
        Tracks tracks = new Tracks(item);
        SpotifyResponse test = new SpotifyResponse(tracks);

        spotifyResponse = Observable.just(test);

        ObjectGraph.create(new Module()).inject(this);
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

    private TestObserver<String> setUpTests() {
        when(myRetroFit.listTracks()).thenReturn(spotifyResponse);
        detailsPresenterFromId = presenter.getPresenter("1");
        return new TestObserver<>();
    }

    @dagger.Module(
            injects = {
                    DetailsPresenterTest.class
            }
    )
    class Module {

        @Provides
        public DetailsPresenter providesDetailsPresenter() {
            return new DetailsPresenter(myRetroFit, Schedulers.immediate(), Schedulers.immediate());
        }
    }
}
