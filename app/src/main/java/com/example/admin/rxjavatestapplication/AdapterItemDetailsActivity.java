package com.example.admin.rxjavatestapplication;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class AdapterItemDetailsActivity {

    @Nonnull
    private final String id;
    @Nullable
    private final String name;

    public AdapterItemDetailsActivity(@Nonnull String id,
                                      @Nullable String name) {
        this.id = id;
        this.name = name;
    }

    @Nonnull
    public String getId() {
        return id;
    }

    @Nullable
    public String getName() {
        return name;
    }
}

