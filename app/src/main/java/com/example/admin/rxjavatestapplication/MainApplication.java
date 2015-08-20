package com.example.admin.rxjavatestapplication;

import android.app.Application;
import android.content.Context;

import com.appunite.rx.subjects.CacheSubject;
import com.example.admin.rxjavatestapplication.dao.SpotifyResponseDao;
import com.example.admin.rxjavatestapplication.gson.AndroidUnderscoreNamingStrategy;
import com.example.admin.rxjavatestapplication.gson.ImmutableListDeserializer;
import com.example.admin.rxjavatestapplication.helpers.CacheProvider;
import com.example.admin.rxjavatestapplication.helpers.DiskCacheCreator;
import com.example.admin.rxjavatestapplication.schedulers.ObserveOnScheduler;
import com.example.admin.rxjavatestapplication.schedulers.SubscribeOnScheduler;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.lang.reflect.Type;

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

        @Provides
        @Singleton
        Gson getGson() {
            return new GsonBuilder()
                    .registerTypeAdapter(ImmutableList.class, new ImmutableListDeserializer())
                    .setFieldNamingStrategy(new AndroidUnderscoreNamingStrategy())
                    .create();
        }

        @Provides
        @Singleton
        CacheProvider getCacheProvider(final @Named("Application") Context context, final Gson gson) {
            return new CacheProvider() {
                @Nonnull
                @Override
                public <T> CacheSubject.CacheCreator<T> getCacheCreatorForKey(@Nonnull String key, @Nonnull Type type) {
                    return new DiskCacheCreator<>(gson, type, new File(context.getCacheDir(), key + ".txt"));
                }
            };
        }
    }
}
