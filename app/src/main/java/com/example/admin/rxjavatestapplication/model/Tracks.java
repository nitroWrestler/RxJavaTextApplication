package com.example.admin.rxjavatestapplication.model;

import java.util.ArrayList;
import java.util.List;

public class Tracks {

    private String offset;

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public Tracks(List<Item> items, String offset) {
        this.items = items;
        this.offset = offset;
    }

    public String getOffset() {
        return offset;
    }

//        public Tracks(final Item item) {
//        items = new ArrayList<>();
//        items.add(item);
//    }
}
