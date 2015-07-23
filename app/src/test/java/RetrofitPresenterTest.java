import com.appunite.rx.ResponseOrError;
import com.example.admin.rxjavatestapplication.RetrofitPresenter;
import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
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
import rx.observers.TestObserver;
import rx.subjects.ReplaySubject;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitPresenterTest extends TestCase {

    @Inject
    RetrofitPresenter presenter;

    @Mock
    SpotifyResponseDao spotifyResponseDao;

    private ReplaySubject<ResponseOrError<SpotifyResponse>> spotifySubject = ReplaySubject.create();

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

    @Nonnull
    private ResponseOrError<SpotifyResponse> sampleData() {
        final Item item = new Item("nameItem", "idItem");
        final ArrayList<Item> itemList = new ArrayList<>();
        itemList.add(item);
        final Tracks tracks = new Tracks(itemList, "50");
        return ResponseOrError.fromData(new SpotifyResponse(tracks));
    }

    @Test
    public void testBeforeDownload_doNotPropagateItems() throws Exception {
        final TestObserver<ImmutableList<RetrofitPresenter.AdapterItem>> items = new TestObserver<>();
        presenter.listObservable().subscribe(items);

        assert_().that(items.getOnNextEvents())
                .isEmpty();
    }

    @Test
    public void testAfterDownloadJSON_itemsArePropagated() throws Exception {
        final TestObserver<ImmutableList<RetrofitPresenter.AdapterItem>> items = new TestObserver<>();
        presenter.listObservable().subscribe(items);

        spotifySubject.onNext(sampleData());

        assert_().that(items.getOnNextEvents()).hasSize(1);
        assert_().that(items.getOnNextEvents().get(0).get(0).getName()).isEqualTo("nameItem");
        assert_().that(items.getOnNextEvents().get(0).get(0).getId()).isEqualTo("idItem");
    }

    @Test
    public void testAfterClickOnFirstItem_openDetails() throws Exception {
        // Subscribe to open details
        final TestObserver<RetrofitPresenter.AdapterItem> openDetails = new TestObserver<>();
        presenter.openDetailsObservable().subscribe(openDetails);

        // Download item
        final TestObserver<ImmutableList<RetrofitPresenter.AdapterItem>> items = new TestObserver<>();
        presenter.listObservable().subscribe(items);
        spotifySubject.onNext(sampleData());

        final RetrofitPresenter.AdapterItem itemClick = items.getOnNextEvents().get(0).get(0);

        // User click
        itemClick.clickObserver().onNext(null);

        // Verify if details opened
        assert_().that(openDetails.getOnNextEvents())
                .contains(itemClick);

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

//    @Test
//    public void testAfterStart_showProgressIsTrue() throws Exception {
//        when(myRetroFit.listTracks()).thenReturn(spotifyResponse);
//        presenter.register(listener);
//        verify(listener).showProgress(true);
//    }
//
//    @Test
//    public void testAfterStart_updateDataWorks_andAfterHideProgress() throws Exception {
//        when(myRetroFit.listTracks()).thenReturn(spotifyResponse);
//        presenter.register(listener);
//
//        assertEquals("name", getAdapterItem().get(0).getName());
//
//        verify(listener).showProgress(false);
//        verify(listener, never()).showButtonView(true);
//    }
//
//    private List<RetrofitPresenter.AdapterItem> getAdapterItem() {
//        verify(listener).updateData(spotifyResponseArgumentCaptor.capture());
//        return spotifyResponseArgumentCaptor.getValue();
//    }
//
//    @Test
//    public void testAfterStart_getError() throws Exception {
//        when(myRetroFit.listTracks())
//                .thenReturn(Observable.<SpotifyResponse>error(new RuntimeException()));
//        presenter.register(listener);
//
//        verify(listener).showProgress(true);
//        verify(listener).showButtonView(true);
//        verify(listener).showProgress(false);
//    }
//
//    @Test
//    public void testAfterButtonClick_checkButtonWork() throws Exception {
//        when(myRetroFit.listTracks())
//                .thenReturn(Observable.<SpotifyResponse>error(new RuntimeException()));
//        presenter.register(listener);
//        when(myRetroFit.listTracks()).thenReturn(spotifyResponse);
//        presenter.refreshClick();
//
//        verify(listener).showButtonView(true);
//        verify(listener).showButtonView(false);
//    }