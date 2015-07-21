package com.example.admin.rxjavatestapplication;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.rxjavatestapplication.detector.ChangesDetector;
import com.example.admin.rxjavatestapplication.detector.SimpleDetector;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;
import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.view.ViewObservable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

abstract class BaseViewHolder extends RecyclerView.ViewHolder {

    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind (@Nonnull RetrofitPresenter.AdapterItem item);

    public abstract void recycle();
}

public class MyListViewAdapter extends RecyclerView.Adapter<BaseViewHolder> implements
        Action1<ImmutableList<RetrofitPresenter.AdapterItem>>, ChangesDetector.ChangesAdapter {

    @Nonnull
    private final ChangesDetector<RetrofitPresenter.AdapterItem, RetrofitPresenter.AdapterItem> changesDetector;
    @Nonnull
    private ImmutableList<RetrofitPresenter.AdapterItem> mItems = ImmutableList.of();

    @Inject
    public MyListViewAdapter() {
        this.changesDetector = new ChangesDetector<>(new SimpleDetector<RetrofitPresenter.AdapterItem>());
    }

//    public void setData(ImmutableList<RetrofitPresenter.AdapterItem> items) {
//        mItems = items;
//        notifyDataSetChanged();
//    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
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
        this.mItems = adapterItems;
        changesDetector.newData(this, adapterItems, false);
    }

    public class MyViewHolder extends BaseViewHolder {

        @InjectView(android.R.id.text1)
        TextView mTextView;

        private CompositeSubscription subscription;

        public MyViewHolder(@Nonnull View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(@Nonnull RetrofitPresenter.AdapterItem item) {
            mTextView.setText(item.getName());
            subscription = new CompositeSubscription(
                ViewObservable.clicks(mTextView).subscribe(item.clickObserver())
            );
        }

        @Override
        public void recycle() {
            subscription.unsubscribe();
        }
    }
}
