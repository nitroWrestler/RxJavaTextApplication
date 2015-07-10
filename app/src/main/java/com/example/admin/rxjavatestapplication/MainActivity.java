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

import java.util.List;

import javax.annotation.Nonnull;

import rx.functions.Action1;

public class MainActivity extends Activity {

    private MyListViewAdapter myListViewAdapter;

    private RetrofitPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView listView = (RecyclerView) findViewById(R.id.listView);
        listView.setLayoutManager(new LinearLayoutManager(this));

        final View progressView = findViewById(R.id.progressBarMainActivity);
        final View buttonView = findViewById(R.id.viewRefreshData);
        final Button bRefreshData = (Button) findViewById(R.id.bRefreshData);

        myListViewAdapter = new MyListViewAdapter(this);
        listView.setAdapter(myListViewAdapter);

        presenter = MainApplication
                .fromApplication(getApplication())
                .objectGraph()
                .plus(new Module())
                .get(RetrofitPresenter.class);

        presenter.register(new RetrofitPresenter.Listener() {
            @Override
            public void showProgress(boolean showProgress) {
                if (showProgress) progressView.setVisibility(View.VISIBLE);
                else progressView.setVisibility(View.GONE);
            }

            @Override
            public void updateData(List<RetrofitPresenter.AdapterItem> spotifyResponse) {
                List<RetrofitPresenter.AdapterItem> items = spotifyResponse;
                myListViewAdapter.setData(items);
                Log.w("ITEMS", items.toString());
            }

            @Override
            public void showButtonView(boolean showButtonView) {
                if (showButtonView) buttonView.setVisibility(View.VISIBLE);
                else buttonView.setVisibility(View.GONE);
            }
        });

        presenter.openDetailsObservable().subscribe(startDetailsActivityAction(this));

        bRefreshData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.refreshClick();
            }
        });
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
                        DetailsActivity.getIntent(activity, adapterItem.getId()),
                        bundle);
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
