package com.example.admin.rxjavatestapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.view.ViewActions;
import rx.functions.Action1;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

public class DetailsActivity extends BaseActivity{

    private DetailsPresenter detailsPresenter;

    private static final String EXTRA_ID = "EXTRA_ID";
    private static final String EXTRA_OFFSET = "EXTRA_OFFSET";

    public static Intent getIntent(@Nonnull Context context, @Nonnull String id, @Nonnull String offset) {
        return new Intent(context, DetailsActivity.class)
                .putExtra(EXTRA_ID, checkNotNull(id))
                .putExtra(EXTRA_OFFSET, checkNotNull(offset));
    }

    @InjectView(R.id.tvNameOfSong)
    TextView nameOfSong;
    @InjectView(R.id.tvIdOfSong)
    TextView idOfSong;
    @InjectView(R.id.tvDuration)
    TextView durationOfSong;
    @InjectView(R.id.tvPopularity)
    TextView popularityOfSong;
    @InjectView(R.id.ivCdCover)
    ImageView cdCoverImage;

    @Inject
    Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final String id = checkNotNull(getIntent().getStringExtra(EXTRA_ID));
        final String offset = checkNotNull(getIntent().getStringExtra(EXTRA_OFFSET));

        ButterKnife.inject(this);

        detailsPresenter = MainApplication
                .fromApplication(getApplication())
                .objectGraph()
                .plus(new Module())
                .get(DetailsPresenter.class);

        MainApplication
                .fromApplication(getApplication())
                .objectGraph()
                .plus(new Module())
                .inject(this);

        final DetailsPresenter.DetailsPresenterFromId presenterFromId =
                detailsPresenter.getPresenter(id, offset);

        presenterFromId.nameObservable()
                .compose(lifecycleMainObservable.<String>bindLifecycle())
                .subscribe(ViewActions.setText(nameOfSong));

        presenterFromId.idObservable()
                .compose(lifecycleMainObservable.<String>bindLifecycle())
                .subscribe(ViewActions.setText(idOfSong));

        presenterFromId.durationObservable()
                .compose(lifecycleMainObservable.<String>bindLifecycle())
                .subscribe(ViewActions.setText(durationOfSong));

        presenterFromId.popularityObservable()
                .compose(lifecycleMainObservable.<String>bindLifecycle())
                .subscribe(ViewActions.setText(popularityOfSong));

        presenterFromId.cdCoverImageObservable()
                .compose(lifecycleMainObservable.<String>bindLifecycle())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        picasso.load(s)
                                .centerCrop()
                                .fit()
                                .into(cdCoverImage);
                    }
                });
    }

    @dagger.Module(
            injects = {
                    DetailsPresenter.class,
                    DetailsActivity.class
            },
            addsTo = MainApplication.Module.class
    )
    class Module {}
}
