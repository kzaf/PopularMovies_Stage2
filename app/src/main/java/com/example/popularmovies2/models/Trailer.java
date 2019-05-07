package com.example.popularmovies2.models;

public class Trailer {

    private String name;
    private String type;
    private String key;

    public Trailer(String name, String type, String key) {
        this.name = name;
        this.type = type;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }
}
