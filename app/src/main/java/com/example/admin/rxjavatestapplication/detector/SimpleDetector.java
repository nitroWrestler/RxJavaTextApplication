package com.example.admin.rxjavatestapplication.detector;

import javax.annotation.Nonnull;

public class SimpleDetector<T extends SimpleDetector.Detectable<T>> implements ChangesDetector.Detector<T, T> {

    public static interface Detectable<T> {
        public boolean matches(@Nonnull T item);
        public boolean same(@Nonnull T item);
    }

    @Nonnull
    @Override
    public T apply(@Nonnull T item) {
        return item;
    }

    @Override
    public boolean matches(@Nonnull T item, @Nonnull T newOne) {
        return item.matches(newOne);
    }

    @Override
    public boolean same(@Nonnull T item, @Nonnull T newOne) {
        return item.same(newOne);
    }
}
