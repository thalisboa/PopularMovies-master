package br.com.thaislisboa.popularmovies.domain.model;

import java.io.Serializable;


public class Review implements Serializable {

    private String name;
    private String comment;

    Review(String name, String comment) {
        this.name = name;
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public String getComment() {
        return comment;
    }
}
