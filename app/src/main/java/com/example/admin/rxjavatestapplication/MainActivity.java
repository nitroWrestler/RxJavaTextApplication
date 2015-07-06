package com.example.admin.rxjavatestapplication;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.example.admin.rxjavatestapplication.model.Item;
import com.example.admin.rxjavatestapplication.model.SpotifyResponse;
import com.example.admin.rxjavatestapplication.schedulers.ObserveOnScheduler;
import com.example.admin.rxjavatestapplication.schedulers.SubscribeOnScheduler;

import java.util.List;

import javax.inject.Singleton;

import dagger.Provides;
import retrofit.RestAdapter;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends Activity {

    private MyListViewAdapter myListViewAdapter;

    private RetrofitPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ListView listView = (ListView) findViewById(R.id.listView);
        final View progressView = findViewById(R.id.progressBarMainActivity);

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
            public void updateData(SpotifyResponse spotifyResponse) {
                List<Item> items = spotifyResponse.getTracks().getItems();
                myListViewAdapter.setData(items);
            }
        });

    }

    @dagger.Module(
            injects = {
                    RetrofitPresenter.class
            },
            addsTo = MainApplication.Module.class
    )
    class Module {

        @Provides
        @Singleton
        MyRetroFit provideRetroFit() {
            return new RestAdapter.Builder()
                    .setEndpoint("https://api.spotify.com")
                    .setLogLevel(RestAdapter.LogLevel.FULL)
                    .build()
                    .create(MyRetroFit.class);
        }

        @Provides
        @Singleton
        @ObserveOnScheduler Scheduler provideObserveOnScheduler() {
            return AndroidSchedulers.mainThread();
        }

        @Provides
        @Singleton
        @SubscribeOnScheduler Scheduler provideSubscribeOnScheduler() {
            return Schedulers.io();
        }
    }
}
