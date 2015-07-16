package com.example.admin.rxjavatestapplication;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.view.ViewObservable;
import rx.subscriptions.CompositeSubscription;

public class MyListViewAdapter extends RecyclerView.Adapter<MyListViewAdapter.MyViewHolder> {

    private List<RetrofitPresenter.AdapterItem> mItems = ImmutableList.of();

    @Nonnull
    private final Context mContext;

    public MyListViewAdapter(@Nonnull Context mContext) {
        this.mContext = mContext;
    }

    public void setData(List<RetrofitPresenter.AdapterItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public MyListViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(mContext)
                .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyListViewAdapter.MyViewHolder viewHolder, int position) {
        viewHolder.bind(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private CompositeSubscription subscription;

        @InjectView(android.R.id.text1)
        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);
        }

        public void bind(@Nonnull RetrofitPresenter.AdapterItem item) {
            mTextView.setText(item.getName());
            subscription = new CompositeSubscription(
                ViewObservable.clicks(mTextView).subscribe(item.clickObserver())
            );
        }
    }
}
