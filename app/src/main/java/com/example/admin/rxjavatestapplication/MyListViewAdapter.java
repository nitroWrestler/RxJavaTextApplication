package com.example.admin.rxjavatestapplication;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.admin.rxjavatestapplication.model.Album;
import com.example.admin.rxjavatestapplication.model.Item;
import com.google.common.collect.ImmutableList;

import java.util.List;

public class MyListViewAdapter extends BaseAdapter{

    private List<Item> mItems = ImmutableList.of();

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
    }


}
