package com.alaka_ala.florafilm.sys.kp_api;

import java.io.Serializable;

public class Genre implements Serializable {

    public Genre(String genre) {
        this.genre = genre;
    }

    public String getGenre() {
        return genre;
    }

    private final String genre;

}
