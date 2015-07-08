package com.example.admin.rxjavatestapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import javax.annotation.Nonnull;

import static com.google.common.base.Preconditions.checkNotNull;

public class DetailsActivity extends Activity{

    private static final String EXTRA_ID = "EXTRA_ID";

    public static Intent getIntent(@Nonnull Context context, @Nonnull String id) {
        return new Intent(context, DetailsActivity.class).putExtra(EXTRA_ID, checkNotNull(id));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
    }
}
