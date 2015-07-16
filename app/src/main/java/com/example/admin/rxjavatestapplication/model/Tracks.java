package com.example.admin.rxjavatestapplication.model;

import java.util.ArrayList;
import java.util.List;

public class Tracks {

    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public Tracks(final Item item) {
        items = new ArrayList<>();
        items.add(item);
    }
}
