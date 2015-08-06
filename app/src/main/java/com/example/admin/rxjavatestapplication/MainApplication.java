package com.example.admin.rxjavatestapplication;

import android.app.Application;
import android.content.Context;

import com.example.admin.rxjavatestapplication.design.TabFragment;
import com.example.admin.rxjavatestapplication.schedulers.ObserveOnScheduler;
import com.example.admin.rxjavatestapplication.schedulers.SubscribeOnScheduler;
import com.squareup.picasso.Picasso;

import javax.annotation.Nonnull;
import javax.inject.Named;
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
    public class Module {

        @Provides
        @Singleton
        @Named("Application")
        Context provideContext() {
            return getApplicationContext();
        }

        @Provides
        @Singleton
        MyRetroFit provideRetroFit() {
            return new RestAdapter.Builder()
                    .setEndpoint("https://api.spotify.com")
                    .setLogLevel(RestAdapter.LogLevel.BASIC)
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

        @Provides
        @Singleton
        Picasso providePicasso(@Named("Application") Context context) {
            return new Picasso.Builder(context)
                    .indicatorsEnabled(BuildConfig.DEBUG)
                    .loggingEnabled(BuildConfig.DEBUG)
                    .build();
        }
    }
}
