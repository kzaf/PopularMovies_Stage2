package com.example.popularmovies2.models;

public class Trailer {

    private String name;
    private String type;

    public Trailer(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
