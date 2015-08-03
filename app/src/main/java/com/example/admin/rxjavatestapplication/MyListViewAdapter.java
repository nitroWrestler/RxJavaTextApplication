package com.example.admin.rxjavatestapplication;


import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.rxjavatestapplication.detector.ChangesDetector;
import com.example.admin.rxjavatestapplication.detector.SimpleDetector;
import com.google.common.collect.ImmutableList;
import com.squareup.picasso.Picasso;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(@Nonnull RetrofitPresenter.AdapterItem item);

    public abstract void recycle();
}

public class MyListViewAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        Action1<ImmutableList<RetrofitPresenter.AdapterItem>>, ChangesDetector.ChangesAdapter {

    @Nonnull
    private final ChangesDetector<RetrofitPresenter.AdapterItem, RetrofitPresenter.AdapterItem> changesDetector;
    @Nonnull
    private final Picasso picasso;
    @Nonnull
    private List<RetrofitPresenter.AdapterItem> mItems = ImmutableList.of();

    @Inject
    public MyListViewAdapter(@Nonnull final Picasso picasso) {
        this.picasso = picasso;
        this.changesDetector = new ChangesDetector<>(new SimpleDetector<RetrofitPresenter.AdapterItem>());
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_main_items_cell, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        holder.bind(mItems.get(position));
    }

    @Override
    public void onViewRecycled(BaseViewHolder holder) {
        super.onViewRecycled(holder);
        holder.recycle();
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void call(ImmutableList<RetrofitPresenter.AdapterItem> adapterItems) {
        mItems = ImmutableList.copyOf(adapterItems);
        changesDetector.newData(this, adapterItems, false);
    }

    public class MyViewHolder extends BaseViewHolder {

        @InjectView(R.id.ivPreviewImageView)
        ImageView mImageView;
        @InjectView(R.id.tvNameOfSongNearImageView)
        TextView mTextView;
        @InjectView(R.id.layoutOneItemCell)
        View layoutItemCell;

        private CompositeSubscription subscription;

        public MyViewHolder(@Nonnull View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(@Nonnull RetrofitPresenter.AdapterItem item) {

            picasso.load(item.getPreviewImageUrl())
                    .into(mImageView);
            String b = item.getName() + ", Offset: " + item.getOffset();
            mTextView.setText(b);

            subscription = new CompositeSubscription(ViewObservable.clicks(layoutItemCell)
                            .map(new Func1<OnClickEvent, ImageView>() {
                                @Override
                                public ImageView call(OnClickEvent onClickEvent) {
                                    return mImageView;
                                }
                            })
                            .subscribe(item.clickObserver())
            );
        }

        @Override
        public void recycle() {
            subscription.unsubscribe();
        }


    }

}
