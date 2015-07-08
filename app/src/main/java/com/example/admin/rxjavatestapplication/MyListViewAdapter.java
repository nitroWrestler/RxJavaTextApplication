package com.example.admin.rxjavatestapplication;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.admin.rxjavatestapplication.model.Item;
import com.google.common.collect.ImmutableList;

import java.util.List;

import javax.annotation.Nonnull;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observable;
import rx.Observer;
import rx.android.view.OnClickEvent;
import rx.android.view.ViewObservable;
import rx.functions.Func1;

public class MyListViewAdapter extends RecyclerView.Adapter<MyListViewAdapter.MyViewHolder> {

    private List<Item> mItems = ImmutableList.of();

    @Nonnull
    private final Context mContext;
    @Nonnull
    private Observer<String> mSelectedItemObserver;

    public MyListViewAdapter(@Nonnull Context mContext, @Nonnull Observer<String> selectedItemObserver) {
        this.mContext = mContext;
        mSelectedItemObserver = selectedItemObserver;
    }

    public void setData(List<Item> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @InjectView(android.R.id.text1)
        TextView mTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.inject(this, itemView);


        }

        public void bindData(final Item item) {
            mTextView.setText(item.getAlbum().getName());
            ViewObservable.clicks(itemView)
                    .flatMap(new Func1<OnClickEvent, Observable<String>>() {
                        @Override
                        public Observable<String> call(OnClickEvent onClickEvent) {
                            return Observable.just(item.getAlbum().getId());
                        }
                    })
                    .subscribe(mSelectedItemObserver);
        }
    }


    @Override
    public MyListViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final View view = LayoutInflater.from(mContext)
                .inflate(android.R.layout.simple_list_item_1, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyListViewAdapter.MyViewHolder viewHolder, int position) {
        viewHolder.bindData(mItems.get(position));
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

/*    private List<Item> mItems = ImmutableList.of();

    @NonNull
    private final Context mContext;

    public MyListViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater
                    .from(mContext)
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
        }
        Item item = (Item) getItem(position);
        Album album = item.getAlbum();
        final TextView textView = (TextView) convertView.findViewById(android.R.id.text1);
        textView.setText(album.getName());
        Log.w("pozycja", String.valueOf(position));

        return convertView;
    }

    public void setData(List<Item> items) {
        ImmutableList<Item> newList = ImmutableList.<Item>builder()
                .addAll(mItems)
                .addAll(items)
                .build();
        mItems = newList;
        notifyDataSetChanged();
    }*/
}
