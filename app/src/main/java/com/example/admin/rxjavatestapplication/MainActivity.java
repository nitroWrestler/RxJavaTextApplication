package com.example.admin.rxjavatestapplication;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.appunite.rx.android.MoreViewObservables;
import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
import com.example.admin.rxjavatestapplication.helpers.LoadMoreHelper;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import dagger.Provides;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainActivity extends BaseActivity {

    @InjectView(R.id.listView)
    RecyclerView recyclerView;
    @InjectView(R.id.progressBarMainActivity)
    View progressView;
    @InjectView(R.id.viewRefreshData)
    View buttonView;
    @InjectView(R.id.bRefreshData)
    Button bRefreshData;

    private RetrofitPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.inject(this);

        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        final MyListViewAdapter myListViewAdapter = new MyListViewAdapter();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(myListViewAdapter);

        presenter = MainApplication
                .fromApplication(getApplication())
                .objectGraph()
                .plus(new Module())
                .get(RetrofitPresenter.class);

        presenter.listObservable()
                .compose(lifecycleMainObservable.<ImmutableList<RetrofitPresenter.AdapterItem>>bindLifecycle())
                .subscribe(myListViewAdapter);

        presenter.openDetailsObservable()
                .compose(lifecycleMainObservable.<RetrofitPresenter.AdapterItem>bindLifecycle())
                .subscribe(startDetailsActivityAction(this));

        MoreViewObservables.scroll(recyclerView)
                .filter(LoadMoreHelper.mapToNeedLoadMore(layoutManager, myListViewAdapter))
                .compose(lifecycleMainObservable.bindLifecycle())
                .subscribe(presenter.loadMoreObserver());

    }

    @Nonnull
    private static Action1<RetrofitPresenter.AdapterItem> startDetailsActivityAction(final Activity activity) {
        return new Action1<RetrofitPresenter.AdapterItem>() {
            @Override
            public void call(RetrofitPresenter.AdapterItem adapterItem) {
                //noinspection unchecked
                final Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(activity)
                        .toBundle();
                ActivityCompat.startActivity(activity,
                        DetailsActivity.getIntent(activity, adapterItem.getId(), adapterItem.getOffset()),
                        bundle);
                Log.w("ADAPTERITEM", adapterItem.getId());
            }
        };
    }



    @dagger.Module(
            injects = {
                    RetrofitPresenter.class
            },
            addsTo = MainApplication.Module.class
    )
    class Module {
    }
}
