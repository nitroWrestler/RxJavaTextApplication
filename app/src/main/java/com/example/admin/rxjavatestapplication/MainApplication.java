package com.example.admin.rxjavatestapplication;

import android.app.Application;

import com.example.admin.rxjavatestapplication.schedulers.ObserveOnScheduler;
import com.example.admin.rxjavatestapplication.schedulers.SubscribeOnScheduler;

import javax.annotation.Nonnull;
import javax.inject.Singleton;

import dagger.ObjectGraph;
import dagger.Provides;
import retrofit.RestAdapter;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainApplication extends Application {

    private ObjectGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();

        objectGraph = ObjectGraph.create(new Module());
        objectGraph.inject(this);
    }

    @Nonnull
    public static MainApplication fromApplication(@Nonnull Application application) {
        return (MainApplication) application;
    }

    @Nonnull
    public ObjectGraph objectGraph() { return objectGraph; }

    @dagger.Module(
            injects = MainApplication.class,
            library = true
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
        @ObserveOnScheduler
        Scheduler provideObserveOnScheduler() {
            return AndroidSchedulers.mainThread();
        }

        @Provides
        @Singleton
        @SubscribeOnScheduler
        Scheduler provideSubscribeOnScheduler() {
            return Schedulers.io();
        }
    }
}
