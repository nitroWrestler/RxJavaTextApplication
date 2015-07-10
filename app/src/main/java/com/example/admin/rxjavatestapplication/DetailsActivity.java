package com.example.admin.rxjavatestapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import javax.annotation.Nonnull;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.view.ViewActions;

import static com.google.common.base.Preconditions.checkNotNull;

public class DetailsActivity extends Activity{

    private DetailsPresenter detailsPresenter;

    private static final String EXTRA_ID = "EXTRA_ID";

    public static Intent getIntent(@Nonnull Context context, @Nonnull String id) {
        return new Intent(context, DetailsActivity.class).putExtra(EXTRA_ID, checkNotNull(id));
    }

    @InjectView(R.id.tvNameOfSong)
    TextView nameOfSong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final String id = checkNotNull(getIntent().getStringExtra(EXTRA_ID));

        ButterKnife.inject(this);

        detailsPresenter = MainApplication
                .fromApplication(getApplication())
                .objectGraph()
                .plus(new Module())
                .get(DetailsPresenter.class);

        final DetailsPresenter.DetailsPresenterFromId presenterFromId =
                detailsPresenter.getPresenter(id);

        presenterFromId.nameObservable()
                .subscribe(ViewActions.setText(nameOfSong));

    }

    @dagger.Module(
            injects = {
                    DetailsPresenter.class
            },
            addsTo = MainApplication.Module.class
    )
    class Module {}
}
