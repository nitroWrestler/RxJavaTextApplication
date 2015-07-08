import com.example.admin.rxjavatestapplication.MyRetroFit;
import com.example.admin.rxjavatestapplication.RetrofitPresenter;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import javax.inject.Inject;

import dagger.ObjectGraph;
import dagger.Provides;
import rx.Observable;
import rx.schedulers.Schedulers;

import static com.google.common.truth.Truth.assert_;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RetrofitPresenterTest extends TestCase {

    @Inject
    RetrofitPresenter presenter;

    @Mock
    RetrofitPresenter.Listener listener;

    @Mock
    MyRetroFit myRetroFit;

    private Observable<SpotifyResponse> spotifyResponse;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        spotifyResponse = Observable.just(new SpotifyResponse());

        ObjectGraph.create(new Module()).inject(this);
    }

    @Test
    public void testAfterStart_presenterIsNotNull() throws Exception {
        when(myRetroFit.listTracks()).thenReturn(spotifyResponse);
        assert_().that(presenter).isNotNull();
    }

    @Test
    public void testAfterStart_showProgressIsTrue() throws Exception {
        when(myRetroFit.listTracks()).thenReturn(spotifyResponse);
        presenter.register(listener);
        verify(listener).showProgress(true);
    }

    @Test
    public void testAfterStart_updateDataWorks_andAfterHideProgress() throws Exception {
        when(myRetroFit.listTracks()).thenReturn(spotifyResponse);
        presenter.register(listener);
        verify(listener).updateData(any(SpotifyResponse.class));
        verify(listener).showProgress(false);
        verify(listener, never()).showButtonView(true);
    }

    @Test
    public void testAfterStart_getError() throws Exception {
        when(myRetroFit.listTracks())
                .thenReturn(Observable.<SpotifyResponse>error(new RuntimeException()));
        presenter.register(listener);

        verify(listener).showProgress(true);
        verify(listener).showButtonView(true);
        verify(listener).showProgress(false);
    }

    @Test
    public void testAfterButtonClick_checkButtonWork() throws Exception {
        when(myRetroFit.listTracks())
                .thenReturn(Observable.<SpotifyResponse>error(new RuntimeException()));
        presenter.register(listener);
        when(myRetroFit.listTracks()).thenReturn(spotifyResponse);
        presenter.refreshClick();

        verify(listener).showButtonView(true);
        verify(listener).showButtonView(false);
    }

    @dagger.Module(
            injects = {
                    RetrofitPresenterTest.class
            }
    )
    class Module {

        @Provides
        public RetrofitPresenter providesRetrofitPresenter() {
            return new RetrofitPresenter(myRetroFit, Schedulers.immediate(), Schedulers.immediate());
        }

    }
}
