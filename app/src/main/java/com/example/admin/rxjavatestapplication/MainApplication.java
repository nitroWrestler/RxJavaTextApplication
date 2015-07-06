package com.example.admin.rxjavatestapplication;

import android.app.Application;

import javax.annotation.Nonnull;

import dagger.ObjectGraph;

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


    }
}
