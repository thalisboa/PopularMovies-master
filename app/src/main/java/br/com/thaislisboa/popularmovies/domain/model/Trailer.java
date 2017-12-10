package br.com.thaislisboa.popularmovies.domain.model;

import java.io.Serializable;

public class Trailer implements Serializable{

    private String name;
    private String keyYouTube;

    Trailer(String name, String keyYouTube) {
        this.name = name;
        this.keyYouTube = keyYouTube;
    }

    public String getName()
    {
        return name;
    }

    public String getKeyYouTube() {
        return keyYouTube;
    }

    public String getYouTube(){
        return "http://www.youtube.com/watch?v=".concat(getKeyYouTube());

    }
}
