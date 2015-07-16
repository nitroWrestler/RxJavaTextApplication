package com.example.admin.rxjavatestapplication.model;

import java.util.List;

public class Album {

    private String id;

    private String name;

    private List<Images> images;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public List<Images> getImages() {
        return images;
    }

    public Album(String name) {
        this.name = name;
    }

    public Album(String name, List<Images> images) {
        this.name = name;
        this.images = images;
    }
}
