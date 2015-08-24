import android.widget.ImageView;

import com.appunite.rx.functions.BothParams;
import com.example.admin.rxjavatestapplication.RetrofitPresenter;
import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
import com.example.admin.rxjavatestapplication.helpers.CachedResult;
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

import javax.annotation.Nonnull;
import javax.inject.Inject;

import dagger.ObjectGraph;
import dagger.Provides;
import rx.observers.TestSubscriber;
import rx.subjects.ReplaySubject;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitPresenterTest extends TestCase {

    @Inject
    RetrofitPresenter presenter;

    @Mock
    SpotifyResponseDao spotifyResponseDao;

    private ReplaySubject<CachedResult> spotifySubject = ReplaySubject.create();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        when(spotifyResponseDao.spotifyItemsObservable()).thenReturn(spotifySubject);

        ObjectGraph.create(new Module()).inject(this);
    }

    @Test
    public void testAfterStart_presenterIsNotNull() throws Exception {
        assert_().that(presenter).isNotNull();
    }

    @Test
    public void testBeforeDownload_doNotPropagateItems() throws Exception {
        final TestSubscriber<ImmutableList<RetrofitPresenter.AdapterItem>> items = new TestSubscriber<>();
        presenter.listObservable().subscribe(items);

        assert_().that(items.getOnNextEvents())
                .isEmpty();
    }

    @Test
    public void testAfterDownloadJSON_itemsArePropagated() throws Exception {
        final TestSubscriber<ImmutableList<RetrofitPresenter.AdapterItem>> items = new TestSubscriber<>();
        presenter.listObservable().subscribe(items);

        spotifySubject.onNext(sampleData());

        items.assertNoErrors();
        assert_().that(items.getOnNextEvents()).hasSize(1);
        assert_().that(items.getOnNextEvents().get(0).get(0).getName()).isEqualTo("nameX");
        assert_().that(items.getOnNextEvents().get(0).get(0).getId()).isEqualTo("5");
    }

    @Test
    public void testAfterClickOnFirstItem_openDetails() throws Exception {
        // Subscribe to open details
        final TestSubscriber<BothParams<RetrofitPresenter.AdapterItem, ImageView>> openDetails = new TestSubscriber<>();
        presenter.openDetailsObservable().subscribe(openDetails);

        // Download item
        final TestSubscriber<ImmutableList<RetrofitPresenter.AdapterItem>> items = new TestSubscriber<>();
        presenter.listObservable().subscribe(items);

        spotifySubject.onNext(sampleData());
        final RetrofitPresenter.AdapterItem itemClick = items.getOnNextEvents().get(0).get(0);

        // User click
        itemClick.clickObserver().onNext(null);

        // Verify if details opened
        assert_().that(openDetails.getOnNextEvents().get(0).param1()).isEqualTo(itemClick);
    }

    @Nonnull
    private CachedResult sampleData() {
        ArrayList<Images> imagesArrayList = new ArrayList<>();
        imagesArrayList.add(new Images("http://mojurl.com"));

        Item item = new Item("nameX", "5", "duration", "popularity", new Album("id", imagesArrayList));
        final ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        final Tracks tracks = new Tracks(itemList, "50", 567);
        final SpotifyResponse spotifyResponse = new SpotifyResponse(tracks);
        return new CachedResult(spotifyResponse, null);
    }

    @dagger.Module(
            injects = {
                    RetrofitPresenterTest.class
            }
    )
    class Module {

        @Provides
        public RetrofitPresenter providesRetrofitPresenter() {
            return new RetrofitPresenter(spotifyResponseDao);
        }

    }
}